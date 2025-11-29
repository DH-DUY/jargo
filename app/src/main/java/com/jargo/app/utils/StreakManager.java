package com.jargo.app.utils;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * StreakManager - Quản lý streak (số ngày học liên tục)
 */
public class StreakManager {

    private static final String TAG = "Jargo_StreakManager";
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
            Log.e(TAG, "updateStreak: User not logged in");
            callback.onError("User not logged in");
            return;
        }

        String today = dateFormat.format(new Date());
        Log.d(TAG, "updateStreak: Starting streak update for date: " + today);

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

                        Log.d(TAG, "Current streak: " + currentStreak + ", Last login: " + lastLoginDate);

                        int newStreak = calculateNewStreak(lastLoginDate, currentStreak, today);
                        int xpEarned = 0;

                        // Tính XP nếu streak tăng
                        if (newStreak > currentStreak) {
                            xpEarned = XPCalculator.calculateStreakXP(newStreak);
                            Log.i(TAG, "Streak increased! New: " + newStreak + ", XP earned: " + xpEarned);
                        } else if (newStreak < currentStreak) {
                            Log.w(TAG, "Streak reset from " + currentStreak + " to " + newStreak);
                        } else {
                            Log.d(TAG, "Streak unchanged: " + newStreak);
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
                                .addOnSuccessListener(aVoid -> {
                                    Log.i(TAG, "Streak saved successfully: " + finalNewStreak);
                                    callback.onSuccess(finalNewStreak, finalXpEarned);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to save streak: " + e.getMessage());
                                    callback.onError(e.getMessage());
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error in updateStreak: " + error.getMessage());
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Tính streak mới dựa vào last login date
     * Sử dụng Calendar API để tính chính xác số ngày
     */
    private int calculateNewStreak(String lastLoginDate, int currentStreak, String today) {
        if (lastLoginDate == null || lastLoginDate.isEmpty()) {
            // Lần đầu tiên đăng nhập
            Log.d(TAG, "First time login, streak = 1");
            return 1;
        }

        if (lastLoginDate.equals(today)) {
            // Đã đăng nhập hôm nay rồi
            Log.d(TAG, "Already logged in today, streak unchanged: " + currentStreak);
            return currentStreak;
        }

        // Tính số ngày chênh lệch với Calendar API
        try {
            Date lastDate = dateFormat.parse(lastLoginDate);
            Date todayDate = dateFormat.parse(today);

            if (lastDate != null && todayDate != null) {
                // Sử dụng Calendar để tính số ngày chính xác
                Calendar lastCal = Calendar.getInstance();
                lastCal.setTime(lastDate);
                lastCal.set(Calendar.HOUR_OF_DAY, 0);
                lastCal.set(Calendar.MINUTE, 0);
                lastCal.set(Calendar.SECOND, 0);
                lastCal.set(Calendar.MILLISECOND, 0);

                Calendar todayCal = Calendar.getInstance();
                todayCal.setTime(todayDate);
                todayCal.set(Calendar.HOUR_OF_DAY, 0);
                todayCal.set(Calendar.MINUTE, 0);
                todayCal.set(Calendar.SECOND, 0);
                todayCal.set(Calendar.MILLISECOND, 0);

                long diffInMillis = todayCal.getTimeInMillis() - lastCal.getTimeInMillis();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

                Log.d(TAG, "Days difference: " + diffInDays + " (Last: " + lastLoginDate + ", Today: " + today + ")");

                if (diffInDays == 1) {
                    // Đăng nhập ngày hôm qua → Streak tăng
                    int newStreak = currentStreak + 1;
                    Log.i(TAG, "Consecutive day! Streak: " + currentStreak + " → " + newStreak);
                    return newStreak;
                } else if (diffInDays > 1) {
                    // Đăng nhập cách > 1 ngày → Reset streak
                    Log.w(TAG, "Missed days! Streak reset: " + currentStreak + " → 1");
                    return 1;
                } else if (diffInDays == 0) {
                    // Same day (shouldn't happen due to check above)
                    return currentStreak;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating streak", e);
            e.printStackTrace();
        }

        Log.w(TAG, "Fallback to streak = 1");
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
