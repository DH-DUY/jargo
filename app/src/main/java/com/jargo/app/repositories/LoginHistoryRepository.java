package com.jargo.app.repositories;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.LoginHistory;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LoginHistoryRepository - Quản lý lịch sử đăng nhập
 */
public class LoginHistoryRepository {

    private static final String TAG = "Jargo_LoginHistoryRepo";
    private static LoginHistoryRepository instance;
    private final FirebaseManager firebaseManager;

    private LoginHistoryRepository() {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    public static synchronized LoginHistoryRepository getInstance() {
        if (instance == null) {
            instance = new LoginHistoryRepository();
        }
        return instance;
    }

    /**
     * Lưu log đăng nhập
     */
    public void logLogin(Context context, String userId, String loginMethod, boolean isSuccess, LogCallback callback) {
        long timestamp = System.currentTimeMillis();
        String deviceInfo = getDeviceInfo();
        String appVersion = getAppVersion(context);

        LoginHistory loginHistory = new LoginHistory(userId, timestamp, deviceInfo, loginMethod, appVersion, isSuccess);

        Log.d(TAG, "Logging login - User: " + userId + ", Method: " + loginMethod + ", Success: " + isSuccess);

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_LOGIN_HISTORY)
                .child(userId)
                .child(loginHistory.getId())
                .setValue(loginHistory)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Login history saved successfully");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save login history: " + e.getMessage());
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                });
    }

    /**
     * Lấy lịch sử đăng nhập của user (giới hạn 50 records gần nhất)
     */
    public void getLoginHistory(String userId, HistoryCallback callback) {
        Log.d(TAG, "Loading login history for user: " + userId);

        Query query = firebaseManager.getDatabaseReference()
                .child(Constants.DB_LOGIN_HISTORY)
                .child(userId)
                .orderByChild("timestamp")
                .limitToLast(50); // Lấy 50 records mới nhất

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LoginHistory> historyList = new ArrayList<>();
                
                for (DataSnapshot historySnapshot : snapshot.getChildren()) {
                    LoginHistory history = historySnapshot.getValue(LoginHistory.class);
                    if (history != null) {
                        historyList.add(history);
                    }
                }

                // Sắp xếp theo thời gian giảm dần (mới nhất lên đầu)
                Collections.sort(historyList, (h1, h2) -> Long.compare(h2.getTimestamp(), h1.getTimestamp()));

                Log.i(TAG, "Loaded " + historyList.size() + " login history records");
                callback.onSuccess(historyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading login history: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });
    }

    /**
     * Xóa lịch sử đăng nhập cũ (giữ lại 100 records gần nhất)
     */
    public void cleanOldHistory(String userId, LogCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_LOGIN_HISTORY)
                .child(userId)
                .orderByChild("timestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        if (count > 100) {
                            int toDelete = (int) (count - 100);
                            int deleted = 0;
                            
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if (deleted < toDelete) {
                                    child.getRef().removeValue();
                                    deleted++;
                                } else {
                                    break;
                                }
                            }
                            
                            Log.i(TAG, "Cleaned " + deleted + " old login history records");
                        }
                        
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error cleaning history: " + error.getMessage());
                        if (callback != null) {
                            callback.onError(error.getMessage());
                        }
                    }
                });
    }

    /**
     * Lấy thông tin thiết bị
     */
    private String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")";
    }

    /**
     * Lấy phiên bản app
     */
    private String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName != null ? packageInfo.versionName : "1.0.0";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error getting app version", e);
            return "1.0.0";
        }
    }

    /**
     * Callbacks
     */
    public interface LogCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface HistoryCallback {
        void onSuccess(List<LoginHistory> historyList);
        void onError(String error);
    }
}
