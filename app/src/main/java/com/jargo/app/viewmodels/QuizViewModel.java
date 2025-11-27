package com.jargo.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jargo.app.models.Quiz;
import com.jargo.app.repositories.QuizRepository;
import com.jargo.app.repositories.ProgressRepository;
import com.jargo.app.utils.SharedPrefsManager;
import com.jargo.app.utils.XPCalculator;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizViewModel - ViewModel cho QuizActivity
 * Quản lý quiz flow, scoring và progress tracking
 */
public class QuizViewModel extends ViewModel {

    private final QuizRepository quizRepository;
    private final ProgressRepository progressRepository;
    private final XPCalculator xpCalculator;
    
    // LiveData cho quiz
    private final MutableLiveData<List<Quiz>> quizzesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Quiz> currentQuizLiveData = new MutableLiveData<>();
    
    // LiveData cho quiz state
    private final MutableLiveData<Integer> currentPositionLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalQuizzesLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isCompletedLiveData = new MutableLiveData<>(false);
    
    // LiveData cho scoring
    private final MutableLiveData<Integer> correctAnswersLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> wrongAnswersLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> scoreLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> earnedXPLiveData = new MutableLiveData<>(0);
    
    // LiveData cho user answers
    private final MutableLiveData<List<Integer>> userAnswersLiveData = new MutableLiveData<>(new ArrayList<>());
    
    // LiveData cho loading/error states
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSavingProgressLiveData = new MutableLiveData<>(false);

    public QuizViewModel(SharedPrefsManager prefsManager) {
        this.quizRepository = QuizRepository.getInstance();
        this.progressRepository = ProgressRepository.getInstance(prefsManager);
        this.xpCalculator = new XPCalculator();
    }

    // Getters cho LiveData
    public LiveData<List<Quiz>> getQuizzes() {
        return quizzesLiveData;
    }

    public LiveData<Quiz> getCurrentQuiz() {
        return currentQuizLiveData;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPositionLiveData;
    }

    public LiveData<Integer> getTotalQuizzes() {
        return totalQuizzesLiveData;
    }

    public LiveData<Boolean> isCompleted() {
        return isCompletedLiveData;
    }

    public LiveData<Integer> getCorrectAnswers() {
        return correctAnswersLiveData;
    }

    public LiveData<Integer> getWrongAnswers() {
        return wrongAnswersLiveData;
    }

    public LiveData<Integer> getScore() {
        return scoreLiveData;
    }

    public LiveData<Integer> getEarnedXP() {
        return earnedXPLiveData;
    }

    public LiveData<List<Integer>> getUserAnswers() {
        return userAnswersLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> isSavingProgress() {
        return isSavingProgressLiveData;
    }

    /**
     * Load quizzes cho lesson
     */
    public void loadQuizzes(String lessonId, boolean shuffle) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        
        quizRepository.getQuizzesByLesson(lessonId, shuffle, new QuizRepository.QuizCallback() {
            @Override
            public void onSuccess(List<Quiz> quizzes) {
                quizzesLiveData.postValue(quizzes);
                totalQuizzesLiveData.postValue(quizzes.size());
                
                // Set quiz đầu tiên
                if (!quizzes.isEmpty()) {
                    currentQuizLiveData.postValue(quizzes.get(0));
                }
                
                // Initialize user answers list
                List<Integer> answers = new ArrayList<>();
                for (int i = 0; i < quizzes.size(); i++) {
                    answers.add(-1); // -1 = chưa trả lời
                }
                userAnswersLiveData.postValue(answers);
                
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
     * Submit answer cho quiz hiện tại
     */
    public void submitAnswer(int answerIndex) {
        Integer currentPos = currentPositionLiveData.getValue();
        List<Integer> answers = userAnswersLiveData.getValue();
        List<Quiz> quizzes = quizzesLiveData.getValue();
        
        if (currentPos != null && answers != null && quizzes != null) {
            // Save user's answer
            answers.set(currentPos, answerIndex);
            userAnswersLiveData.setValue(answers);
            
            // Check if answer is correct
            Quiz currentQuiz = quizzes.get(currentPos);
            if (currentQuiz.getCorrectAnswer() == answerIndex) {
                correctAnswersLiveData.setValue(correctAnswersLiveData.getValue() + 1);
            } else {
                wrongAnswersLiveData.setValue(wrongAnswersLiveData.getValue() + 1);
            }
        }
    }

    /**
     * Navigate to next quiz
     */
    public void nextQuiz() {
        Integer current = currentPositionLiveData.getValue();
        Integer total = totalQuizzesLiveData.getValue();
        List<Quiz> quizzes = quizzesLiveData.getValue();
        
        if (current != null && total != null && quizzes != null) {
            if (current < total - 1) {
                int nextPos = current + 1;
                currentPositionLiveData.setValue(nextPos);
                currentQuizLiveData.setValue(quizzes.get(nextPos));
            } else {
                // Đã hoàn thành tất cả quiz
                completeQuiz();
            }
        }
    }

    /**
     * Navigate to previous quiz
     */
    public void previousQuiz() {
        Integer current = currentPositionLiveData.getValue();
        List<Quiz> quizzes = quizzesLiveData.getValue();
        
        if (current != null && current > 0 && quizzes != null) {
            int prevPos = current - 1;
            currentPositionLiveData.setValue(prevPos);
            currentQuizLiveData.setValue(quizzes.get(prevPos));
        }
    }

    /**
     * Complete quiz và calculate score
     */
    private void completeQuiz() {
        Integer correct = correctAnswersLiveData.getValue();
        Integer total = totalQuizzesLiveData.getValue();
        
        if (correct != null && total != null && total > 0) {
            // Calculate score (0-100)
            int score = (correct * 100) / total;
            scoreLiveData.setValue(score);
            
            // Calculate earned XP
            int xp = xpCalculator.calculateQuizXP(score, total);
            earnedXPLiveData.setValue(xp);
            
            isCompletedLiveData.setValue(true);
        }
    }

    /**
     * Save progress to Firebase
     */
    public void saveProgress(String lessonId, SaveProgressCallback callback) {
        Integer score = scoreLiveData.getValue();
        if (score == null) {
            callback.onError("No score available");
            return;
        }
        
        isSavingProgressLiveData.setValue(true);
        
        progressRepository.saveLessonProgress(lessonId, score, new ProgressRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                // Update user XP
                Integer xp = earnedXPLiveData.getValue();
                if (xp != null && xp > 0) {
                    progressRepository.updateUserXP(xp, new ProgressRepository.SaveCallback() {
                        @Override
                        public void onSuccess() {
                            isSavingProgressLiveData.postValue(false);
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String error) {
                            isSavingProgressLiveData.postValue(false);
                            callback.onError(error);
                        }
                    });
                } else {
                    isSavingProgressLiveData.postValue(false);
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(String error) {
                isSavingProgressLiveData.postValue(false);
                callback.onError(error);
            }
        });
    }

    /**
     * Get progress percentage
     */
    public int getProgressPercentage() {
        Integer current = currentPositionLiveData.getValue();
        Integer total = totalQuizzesLiveData.getValue();
        
        if (current != null && total != null && total > 0) {
            return ((current + 1) * 100) / total;
        }
        return 0;
    }

    /**
     * Check if answer is correct
     */
    public boolean isAnswerCorrect(int quizIndex, int answerIndex) {
        List<Quiz> quizzes = quizzesLiveData.getValue();
        if (quizzes != null && quizIndex >= 0 && quizIndex < quizzes.size()) {
            return quizzes.get(quizIndex).getCorrectAnswer() == answerIndex;
        }
        return false;
    }

    /**
     * Callback cho save progress
     */
    public interface SaveProgressCallback {
        void onSuccess();
        void onError(String error);
    }
}
