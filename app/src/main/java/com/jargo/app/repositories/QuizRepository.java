package com.jargo.app.repositories;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.Quiz;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QuizRepository - Quản lý dữ liệu quiz từ Firebase
 */
public class QuizRepository {

    private static QuizRepository instance;
    private final FirebaseManager firebaseManager;

    private QuizRepository() {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    public static synchronized QuizRepository getInstance() {
        if (instance == null) {
            instance = new QuizRepository();
        }
        return instance;
    }

    /**
     * Load danh sách quiz theo lessonId
     */
    public void getQuizzesByLesson(String lessonId, boolean shuffle, QuizCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_QUIZZES)
                .orderByChild("lessonId")
                .equalTo(lessonId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Quiz> quizzes = new ArrayList<>();
                        
                        for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                            Quiz quiz = quizSnapshot.getValue(Quiz.class);
                            if (quiz != null) {
                                quizzes.add(quiz);
                            }
                        }
                        
                        // Shuffle nếu cần
                        if (shuffle && !quizzes.isEmpty()) {
                            Collections.shuffle(quizzes);
                        }
                        
                        callback.onSuccess(quizzes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Load 1 quiz theo ID
     */
    public void getQuizById(String quizId, SingleQuizCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_QUIZZES)
                .child(quizId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Quiz quiz = snapshot.getValue(Quiz.class);
                        if (quiz != null) {
                            callback.onSuccess(quiz);
                        } else {
                            callback.onError("Quiz not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Callback cho danh sách quizzes
     */
    public interface QuizCallback {
        void onSuccess(List<Quiz> quizzes);
        void onError(String error);
    }

    /**
     * Callback cho 1 quiz
     */
    public interface SingleQuizCallback {
        void onSuccess(Quiz quiz);
        void onError(String error);
    }
}
