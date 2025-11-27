package com.jargo.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jargo.app.models.Lesson;
import com.jargo.app.models.Vocabulary;
import com.jargo.app.repositories.LessonRepository;
import com.jargo.app.repositories.VocabularyRepository;
import java.util.List;

/**
 * LearningViewModel - ViewModel cho LearningActivity
 * Quản lý lesson và vocabulary learning flow
 */
public class LearningViewModel extends ViewModel {

    private final LessonRepository lessonRepository;
    private final VocabularyRepository vocabularyRepository;
    
    // LiveData cho lesson
    private final MutableLiveData<Lesson> lessonLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Vocabulary>> vocabulariesLiveData = new MutableLiveData<>();
    
    // LiveData cho learning state
    private final MutableLiveData<Integer> currentPositionLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalVocabulariesLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isCompletedLiveData = new MutableLiveData<>(false);
    
    // LiveData cho loading/error states
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LearningViewModel() {
        this.lessonRepository = LessonRepository.getInstance();
        this.vocabularyRepository = VocabularyRepository.getInstance();
    }

    // Getters cho LiveData
    public LiveData<Lesson> getLesson() {
        return lessonLiveData;
    }

    public LiveData<List<Vocabulary>> getVocabularies() {
        return vocabulariesLiveData;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPositionLiveData;
    }

    public LiveData<Integer> getTotalVocabularies() {
        return totalVocabulariesLiveData;
    }

    public LiveData<Boolean> isCompleted() {
        return isCompletedLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Load lesson và vocabularies
     */
    public void loadLesson(String lessonId) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        // Load lesson info
        lessonRepository.getLessonById(lessonId, new LessonRepository.SingleLessonCallback() {
            @Override
            public void onSuccess(Lesson lesson) {
                lessonLiveData.postValue(lesson);
                
                // Load vocabularies của lesson này
                loadVocabularies(lessonId);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                isLoadingLiveData.postValue(false);
            }
        });
    }

    /**
     * Load danh sách vocabularies
     */
    private void loadVocabularies(String lessonId) {
        vocabularyRepository.getVocabulariesByLesson(lessonId, new VocabularyRepository.VocabularyCallback() {
            @Override
            public void onSuccess(List<Vocabulary> vocabularies) {
                vocabulariesLiveData.postValue(vocabularies);
                totalVocabulariesLiveData.postValue(vocabularies.size());
                isLoadingLiveData.postValue(false);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                isLoadingLiveData.postValue(false);
            }
        });
    }

    /**
     * Navigate to next vocabulary
     */
    public void nextVocabulary() {
        Integer current = currentPositionLiveData.getValue();
        Integer total = totalVocabulariesLiveData.getValue();
        
        if (current != null && total != null) {
            if (current < total - 1) {
                currentPositionLiveData.setValue(current + 1);
            } else {
                // Đã học hết vocabularies
                isCompletedLiveData.setValue(true);
            }
        }
    }

    /**
     * Navigate to previous vocabulary
     */
    public void previousVocabulary() {
        Integer current = currentPositionLiveData.getValue();
        if (current != null && current > 0) {
            currentPositionLiveData.setValue(current - 1);
        }
    }

    /**
     * Jump to specific vocabulary
     */
    public void goToVocabulary(int position) {
        Integer total = totalVocabulariesLiveData.getValue();
        if (total != null && position >= 0 && position < total) {
            currentPositionLiveData.setValue(position);
        }
    }

    /**
     * Get progress percentage
     */
    public int getProgressPercentage() {
        Integer current = currentPositionLiveData.getValue();
        Integer total = totalVocabulariesLiveData.getValue();
        
        if (current != null && total != null && total > 0) {
            return ((current + 1) * 100) / total;
        }
        return 0;
    }

    /**
     * Reset learning state
     */
    public void reset() {
        currentPositionLiveData.setValue(0);
        isCompletedLiveData.setValue(false);
    }
}
