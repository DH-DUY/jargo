package com.jargo.app.repositories;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.Lesson;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

/**
 * LessonRepository - Quản lý dữ liệu lessons từ Firebase
 */
public class LessonRepository {

    private static LessonRepository instance;
    private final FirebaseManager firebaseManager;

    private LessonRepository() {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    public static synchronized LessonRepository getInstance() {
        if (instance == null) {
            instance = new LessonRepository();
        }
        return instance;
    }

    /**
     * Load tất cả lessons của một topic
     */
    public void getLessonsByTopic(String topicId, LessonCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_LESSONS)
                .orderByChild("topicId")
                .equalTo(topicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Lesson> lessons = new ArrayList<>();
                        
                        for (DataSnapshot lessonSnapshot : snapshot.getChildren()) {
                            Lesson lesson = lessonSnapshot.getValue(Lesson.class);
                            if (lesson != null) {
                                lessons.add(lesson);
                            }
                        }
                        
                        callback.onSuccess(lessons);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Load 1 lesson theo ID
     */
    public void getLessonById(String lessonId, SingleLessonCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_LESSONS)
                .child(lessonId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Lesson lesson = snapshot.getValue(Lesson.class);
                        if (lesson != null) {
                            callback.onSuccess(lesson);
                        } else {
                            callback.onError("Lesson not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Callback cho danh sách lessons
     */
    public interface LessonCallback {
        void onSuccess(List<Lesson> lessons);
        void onError(String error);
    }

    /**
     * Callback cho 1 lesson
     */
    public interface SingleLessonCallback {
        void onSuccess(Lesson lesson);
        void onError(String error);
    }
}
