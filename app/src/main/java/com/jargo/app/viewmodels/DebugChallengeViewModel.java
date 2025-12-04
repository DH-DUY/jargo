package com.jargo.app.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jargo.app.models.DebugChallenge;
import com.jargo.app.repositories.DebugChallengeRepository;
import com.jargo.app.utils.SharedPrefsManager;

import java.util.List;

/**
 * DebugChallengeViewModel - ViewModel cho Debug Challenge mode
 */
public class DebugChallengeViewModel extends ViewModel {

    private static final String TAG = "DebugChallengeVM";

    // Repository
    private final DebugChallengeRepository repository;
    private final SharedPrefsManager prefsManager;

    // LiveData
    private final MutableLiveData<List<DebugChallenge>> challenges = new MutableLiveData<>();
    private final MutableLiveData<DebugChallenge> currentChallenge = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalChallenges = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> correctAnswers = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> wrongAnswers = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalXpEarned = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isCompleted = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public DebugChallengeViewModel(SharedPrefsManager prefsManager) {
        this.prefsManager = prefsManager;
        this.repository = DebugChallengeRepository.getInstance();
    }

    // ==================== GETTERS ====================

    public LiveData<DebugChallenge> getCurrentChallenge() {
        return currentChallenge;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public LiveData<Integer> getTotalChallenges() {
        return totalChallenges;
    }

    public LiveData<Integer> getCorrectAnswers() {
        return correctAnswers;
    }

    public LiveData<Integer> getWrongAnswers() {
        return wrongAnswers;
    }

    public LiveData<Integer> getTotalXpEarned() {
        return totalXpEarned;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<Boolean> isCompleted() {
        return isCompleted;
    }

    public LiveData<String> getError() {
        return error;
    }

    // ==================== LOAD DATA ====================

    /**
     * Load all challenges
     */
    public void loadAllChallenges() {
        isLoading.setValue(true);
        error.setValue(null);

        repository.getAllChallenges().observeForever(challengeList -> {
            isLoading.setValue(false);
            
            if (challengeList != null && !challengeList.isEmpty()) {
                challenges.setValue(challengeList);
                totalChallenges.setValue(challengeList.size());
                currentPosition.setValue(0);
                currentChallenge.setValue(challengeList.get(0));
                Log.d(TAG, "Loaded " + challengeList.size() + " challenges");
            } else {
                error.setValue("No challenges found");
                Log.w(TAG, "No challenges found");
            }
        });
    }

    /**
     * Load challenges by topic
     */
    public void loadChallengesByTopic(String topicId) {
        isLoading.setValue(true);
        error.setValue(null);

        repository.getChallengesByTopic(topicId).observeForever(challengeList -> {
            isLoading.setValue(false);
            
            if (challengeList != null && !challengeList.isEmpty()) {
                challenges.setValue(challengeList);
                totalChallenges.setValue(challengeList.size());
                currentPosition.setValue(0);
                currentChallenge.setValue(challengeList.get(0));
                Log.d(TAG, "Loaded " + challengeList.size() + " challenges for topic: " + topicId);
            } else {
                error.setValue("No challenges found for this topic");
                Log.w(TAG, "No challenges found for topic: " + topicId);
            }
        });
    }

    /**
     * Load challenges by difficulty
     */
    public void loadChallengesByDifficulty(String difficulty) {
        isLoading.setValue(true);
        error.setValue(null);

        repository.getChallengesByDifficulty(difficulty).observeForever(challengeList -> {
            isLoading.setValue(false);
            
            if (challengeList != null && !challengeList.isEmpty()) {
                challenges.setValue(challengeList);
                totalChallenges.setValue(challengeList.size());
                currentPosition.setValue(0);
                currentChallenge.setValue(challengeList.get(0));
            } else {
                error.setValue("No challenges found for this difficulty");
            }
        });
    }

    // ==================== GAME LOGIC ====================

    /**
     * Record correct answer
     */
    public void recordCorrectAnswer() {
        Integer current = correctAnswers.getValue();
        if (current == null) current = 0;
        correctAnswers.setValue(current + 1);

        // Add XP
        DebugChallenge challenge = currentChallenge.getValue();
        if (challenge != null) {
            int xp = challenge.getXpReward();
            Integer totalXp = totalXpEarned.getValue();
            if (totalXp == null) totalXp = 0;
            totalXpEarned.setValue(totalXp + xp);

            // Save to Firebase
            String userId = prefsManager.getUserId();
            if (userId != null) {
                repository.saveChallengeCompletion(userId, challenge.getChallengeId(), true, xp);
                repository.updateUserXP(userId, xp, null);
            }
        }

        Log.d(TAG, "Correct answer recorded. Total correct: " + (current + 1));
    }

    /**
     * Record wrong answer
     */
    public void recordWrongAnswer() {
        Integer current = wrongAnswers.getValue();
        if (current == null) current = 0;
        wrongAnswers.setValue(current + 1);

        // Save to Firebase (no XP for wrong answer)
        DebugChallenge challenge = currentChallenge.getValue();
        if (challenge != null) {
            String userId = prefsManager.getUserId();
            if (userId != null) {
                repository.saveChallengeCompletion(userId, challenge.getChallengeId(), false, 0);
            }
        }

        Log.d(TAG, "Wrong answer recorded. Total wrong: " + (current + 1));
    }

    /**
     * Move to next challenge
     */
    public void nextChallenge() {
        List<DebugChallenge> challengeList = challenges.getValue();
        Integer position = currentPosition.getValue();

        if (challengeList == null || position == null) return;

        int nextPosition = position + 1;

        if (nextPosition < challengeList.size()) {
            currentPosition.setValue(nextPosition);
            currentChallenge.setValue(challengeList.get(nextPosition));
            Log.d(TAG, "Moving to challenge " + (nextPosition + 1) + "/" + challengeList.size());
        } else {
            // All challenges completed
            isCompleted.setValue(true);
            Log.d(TAG, "All challenges completed!");
        }
    }

    /**
     * Get progress percentage
     */
    public int getProgressPercentage() {
        Integer current = currentPosition.getValue();
        Integer total = totalChallenges.getValue();

        if (current == null || total == null || total == 0) return 0;

        return (int) (((current + 1) / (float) total) * 100);
    }

    /**
     * Get accuracy percentage
     */
    public int getAccuracyPercentage() {
        Integer correct = correctAnswers.getValue();
        Integer wrong = wrongAnswers.getValue();

        if (correct == null) correct = 0;
        if (wrong == null) wrong = 0;

        int total = correct + wrong;
        if (total == 0) return 0;

        return (int) ((correct / (float) total) * 100);
    }

    /**
     * Reset state for new session
     */
    public void reset() {
        currentPosition.setValue(0);
        correctAnswers.setValue(0);
        wrongAnswers.setValue(0);
        totalXpEarned.setValue(0);
        isCompleted.setValue(false);
        error.setValue(null);

        List<DebugChallenge> challengeList = challenges.getValue();
        if (challengeList != null && !challengeList.isEmpty()) {
            currentChallenge.setValue(challengeList.get(0));
        }
    }
}
