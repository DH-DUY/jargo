package com.jargo.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jargo.app.models.Topic;
import com.jargo.app.models.Progress;
import com.jargo.app.repositories.TopicRepository;
import com.jargo.app.repositories.ProgressRepository;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.List;
import java.util.Map;

/**
 * HomeViewModel - ViewModel cho HomeFragment
 * Quản lý danh sách topics và progress tracking
 */
public class HomeViewModel extends ViewModel {

    private final TopicRepository topicRepository;
    private final ProgressRepository progressRepository;
    
    // LiveData cho topics
    private final MutableLiveData<List<Topic>> topicsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Progress>> progressMapLiveData = new MutableLiveData<>();
    
    // LiveData cho states
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    // LiveData cho user info
    private final MutableLiveData<String> userFieldLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalXPLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> streakLiveData = new MutableLiveData<>(0);

    public HomeViewModel(SharedPrefsManager prefsManager) {
        this.topicRepository = TopicRepository.getInstance();
        this.progressRepository = ProgressRepository.getInstance(prefsManager);
    }

    // Getters cho LiveData
    public LiveData<List<Topic>> getTopics() {
        return topicsLiveData;
    }

    public LiveData<Map<String, Progress>> getProgressMap() {
        return progressMapLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getUserField() {
        return userFieldLiveData;
    }

    public LiveData<Integer> getTotalXP() {
        return totalXPLiveData;
    }

    public LiveData<Integer> getStreak() {
        return streakLiveData;
    }

    /**
     * Load topics theo field của user
     */
    public void loadTopicsByField(String fieldId) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        topicRepository.getTopicsByField(fieldId, new TopicRepository.TopicCallback() {
            @Override
            public void onSuccess(List<Topic> topics) {
                topicsLiveData.postValue(topics);
                isLoadingLiveData.postValue(false);
                
                // Load progress sau khi có topics
                loadAllProgress();
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                isLoadingLiveData.postValue(false);
            }
        });
    }

    /**
     * Load tất cả topics
     */
    public void loadAllTopics() {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        topicRepository.getAllTopics(new TopicRepository.TopicCallback() {
            @Override
            public void onSuccess(List<Topic> topics) {
                topicsLiveData.postValue(topics);
                isLoadingLiveData.postValue(false);
                
                // Load progress sau khi có topics
                loadAllProgress();
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                isLoadingLiveData.postValue(false);
            }
        });
    }

    /**
     * Load progress của user
     */
    public void loadAllProgress() {
        progressRepository.getAllProgress(new ProgressRepository.AllProgressCallback() {
            @Override
            public void onSuccess(Map<String, Progress> progressMap) {
                progressMapLiveData.postValue(progressMap);
            }

            @Override
            public void onError(String error) {
                // Không cần báo lỗi nghiêm trọng cho progress
                // User có thể chưa có progress nào
            }
        });
    }

    /**
     * Set user field (cho filtering)
     */
    public void setUserField(String fieldId) {
        userFieldLiveData.setValue(fieldId);
        loadTopicsByField(fieldId);
    }

    /**
     * Update XP và streak
     */
    public void updateUserStats(int xp, int streak) {
        totalXPLiveData.setValue(xp);
        streakLiveData.setValue(streak);
    }

    /**
     * Refresh data
     */
    public void refresh() {
        String currentField = userFieldLiveData.getValue();
        if (currentField != null) {
            loadTopicsByField(currentField);
        } else {
            loadAllTopics();
        }
    }
}
