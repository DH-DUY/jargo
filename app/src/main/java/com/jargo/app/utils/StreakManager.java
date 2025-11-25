package com.jargo.app.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * StreakManager - Quản lý streak (số ngày học liên tục)
 */
public class StreakManager {

    private static StreakManager instance;
    private final SharedPrefsManager prefsManager;
    private final FirebaseManager firebaseManager;
    private final SimpleDateFormat dateFormat;

    private StreakManager(Context context) {
        this.prefsManager = SharedPrefsManager.getInstance(context);
        this.firebaseManager = FirebaseManager.getInstance();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public static synchronized StreakManager getInstance(Context context) {
        if (instance == null) {
            instance = new StreakManager(context);
        }
        return instance;
    }

    /**
     * Cập nhật streak khi user hoàn thành activity
     */
    public void updateStreak(StreakCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        String today = dateFormat.format(new Date());

        // Load last login date và current streak
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_USERS)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastLoginDate = snapshot.child("lastLoginDate").getValue(String.class);
                        int currentStreak = 0;
                        if (snapshot.child("streak").exists()) {
                            currentStreak = snapshot.child("streak").getValue(Integer.class);
                        }

                        int newStreak = calculateNewStreak(lastLoginDate, currentStreak, today);
                        int xpEarned = 0;

                        // Tính XP nếu streak tăng
                        if (newStreak > currentStreak) {
                            xpEarned = XPCalculator.calculateStreakXP(newStreak);
                        }

                        // Final copy cho lambda
                        final int finalXpEarned = xpEarned;
                        final int finalNewStreak = newStreak;

                        // Update Firebase
                        firebaseManager.getDatabaseReference()
                                .child(Constants.DB_USERS)
                                .child(userId)
                                .child("lastLoginDate")
                                .setValue(today);

                        firebaseManager.getDatabaseReference()
                                .child(Constants.DB_USERS)
                                .child(userId)
                                .child("streak")
                                .setValue(newStreak)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(finalNewStreak, finalXpEarned))
                                .addOnFailureListener(e -> callback.onError(e.getMessage()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Tính streak mới dựa vào last login date
     */
    private int calculateNewStreak(String lastLoginDate, int currentStreak, String today) {
        if (lastLoginDate == null || lastLoginDate.isEmpty()) {
            // Lần đầu tiên đăng nhập
            return 1;
        }

        if (lastLoginDate.equals(today)) {
            // Đã đăng nhập hôm nay rồi
            return currentStreak;
        }

        // Tính số ngày chênh lệch
        try {
            Date lastDate = dateFormat.parse(lastLoginDate);
            Date todayDate = dateFormat.parse(today);

            if (lastDate != null && todayDate != null) {
                long diffInMillis = todayDate.getTime() - lastDate.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

                if (diffInDays == 1) {
                    // Đăng nhập ngày hôm qua → Streak tăng
                    return currentStreak + 1;
                } else if (diffInDays > 1) {
                    // Đăng nhập cách > 1 ngày → Reset streak
                    return 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1; // Default
    }

    /**
     * Load streak hiện tại từ Firebase
     */
    public void getCurrentStreak(StreakLoadCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_USERS)
                .child(userId)
                .child("streak")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int streak = 0;
                        if (snapshot.exists()) {
                            streak = snapshot.getValue(Integer.class);
                        }
                        callback.onSuccess(streak);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Check nếu streak sắp bị mất (chưa login hôm nay)
     */
    public void checkStreakStatus(StreakStatusCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onWarning(false);
            return;
        }

        String today = dateFormat.format(new Date());

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_USERS)
                .child(userId)
                .child("lastLoginDate")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastLoginDate = snapshot.getValue(String.class);
                        boolean isAtRisk = !today.equals(lastLoginDate);
                        callback.onWarning(isAtRisk);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onWarning(false);
                    }
                });
    }

    /**
     * Callbacks
     */
    public interface StreakCallback {
        void onSuccess(int newStreak, int xpEarned);
        void onError(String error);
    }

    public interface StreakLoadCallback {
        void onSuccess(int streak);
        void onError(String error);
    }

    public interface StreakStatusCallback {
        void onWarning(boolean isAtRisk);
    }
}
