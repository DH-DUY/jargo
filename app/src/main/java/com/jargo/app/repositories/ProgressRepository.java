package com.jargo.app.repositories;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.Progress;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * ProgressRepository - Quản lý tiến độ học tập trong Firebase
 */
public class ProgressRepository {

    private static ProgressRepository instance;
    private final FirebaseManager firebaseManager;
    private final SharedPrefsManager prefsManager;

    private ProgressRepository(SharedPrefsManager prefsManager) {
        this.firebaseManager = FirebaseManager.getInstance();
        this.prefsManager = prefsManager;
    }

    public static synchronized ProgressRepository getInstance(SharedPrefsManager prefsManager) {
        if (instance == null) {
            instance = new ProgressRepository(prefsManager);
        }
        return instance;
    }

    /**
     * Lưu tiến độ hoàn thành lesson
     */
    public void saveLessonProgress(String lessonId, int quizScore, int vocabularyCount, SaveCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        Progress progress = new Progress();
        progress.setLessonId(lessonId);
        progress.setUserId(userId);
        progress.setCompleted(true);
        progress.setCompletedAt(System.currentTimeMillis());
        progress.setQuizScore(quizScore);
        progress.setVocabularyMastered(vocabularyCount);

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_PROGRESS)
                .child(userId)
                .child(lessonId)
                .setValue(progress)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Load tiến độ của 1 lesson
     */
    public void getLessonProgress(String lessonId, ProgressCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_PROGRESS)
                .child(userId)
                .child(lessonId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Progress progress = snapshot.getValue(Progress.class);
                        callback.onSuccess(progress);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Load tất cả progress của user
     */
    public void getAllProgress(AllProgressCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_PROGRESS)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Progress> progressMap = new HashMap<>();
                        
                        for (DataSnapshot progressSnapshot : snapshot.getChildren()) {
                            Progress progress = progressSnapshot.getValue(Progress.class);
                            if (progress != null) {
                                progressMap.put(progress.getLessonId(), progress);
                            }
                        }
                        
                        callback.onSuccess(progressMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Update XP của user
     */
    public void updateUserXP(int xpToAdd, SaveCallback callback) {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        firebaseManager.getDatabaseReference()
                .child(Constants.DB_USERS)
                .child(userId)
                .child("totalXP")
                .get()
                .addOnSuccessListener(snapshot -> {
                    int currentXP = 0;
                    if (snapshot.exists()) {
                        currentXP = snapshot.getValue(Integer.class);
                    }
                    
                    int newXP = currentXP + xpToAdd;
                    
                    firebaseManager.getDatabaseReference()
                            .child(Constants.DB_USERS)
                            .child(userId)
                            .child("totalXP")
                            .setValue(newXP)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Callbacks
     */
    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface ProgressCallback {
        void onSuccess(Progress progress);
        void onError(String error);
    }

    public interface AllProgressCallback {
        void onSuccess(Map<String, Progress> progressMap);
        void onError(String error);
    }
}
