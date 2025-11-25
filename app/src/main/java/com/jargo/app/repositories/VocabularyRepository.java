package com.jargo.app.repositories;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.models.Vocabulary;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

/**
 * VocabularyRepository - Quản lý dữ liệu từ vựng từ Firebase
 */
public class VocabularyRepository {

    private static VocabularyRepository instance;
    private final FirebaseManager firebaseManager;

    private VocabularyRepository() {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    public static synchronized VocabularyRepository getInstance() {
        if (instance == null) {
            instance = new VocabularyRepository();
        }
        return instance;
    }

    /**
     * Load danh sách từ vựng theo lessonId
     */
    public void getVocabulariesByLesson(String lessonId, VocabularyCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_VOCABULARIES)
                .orderByChild("lessonId")
                .equalTo(lessonId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Vocabulary> vocabularies = new ArrayList<>();
                        
                        for (DataSnapshot vocabSnapshot : snapshot.getChildren()) {
                            Vocabulary vocabulary = vocabSnapshot.getValue(Vocabulary.class);
                            if (vocabulary != null) {
                                vocabularies.add(vocabulary);
                            }
                        }
                        
                        callback.onSuccess(vocabularies);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Load 1 từ vựng theo ID
     */
    public void getVocabularyById(String vocabularyId, SingleVocabularyCallback callback) {
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_VOCABULARIES)
                .child(vocabularyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Vocabulary vocabulary = snapshot.getValue(Vocabulary.class);
                        if (vocabulary != null) {
                            callback.onSuccess(vocabulary);
                        } else {
                            callback.onError("Vocabulary not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Callback cho danh sách vocabularies
     */
    public interface VocabularyCallback {
        void onSuccess(List<Vocabulary> vocabularies);
        void onError(String error);
    }

    /**
     * Callback cho 1 vocabulary
     */
    public interface SingleVocabularyCallback {
        void onSuccess(Vocabulary vocabulary);
        void onError(String error);
    }
}
