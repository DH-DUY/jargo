package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.repositories.ProgressRepository;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.SharedPrefsManager;
import com.jargo.app.utils.StreakManager;
import com.jargo.app.utils.XPCalculator;

/**
 * ResultActivity - Hiển thị kết quả sau khi hoàn thành quiz
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvScore;
    private TextView tvAccuracy;
    private TextView tvXPEarned;
    private TextView tvStreak;
    private TextView tvCongrats;
    private ProgressBar progressBar;
    private View loadingView;

    private ProgressRepository progressRepository;
    private StreakManager streakManager;

    private String lessonId;
    private int vocabularyCount;
    private int quizScore;
    private int quizTotal;
    private int totalXP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        progressRepository = ProgressRepository.getInstance(prefsManager);
        streakManager = StreakManager.getInstance(this);

        // Get data from Intent
        lessonId = getIntent().getStringExtra(Constants.EXTRA_LESSON_ID);
        String lessonTitle = getIntent().getStringExtra(Constants.EXTRA_LESSON_TITLE);
        vocabularyCount = getIntent().getIntExtra(Constants.EXTRA_VOCABULARY_COUNT, 0);
        quizScore = getIntent().getIntExtra(Constants.EXTRA_QUIZ_SCORE, 0);
        quizTotal = getIntent().getIntExtra(Constants.EXTRA_QUIZ_TOTAL, 0);

        // Bind views
        tvScore = findViewById(R.id.tvScore);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvXPEarned = findViewById(R.id.tvXPEarned);
        tvStreak = findViewById(R.id.tvStreak);
        tvCongrats = findViewById(R.id.tvCongrats);
        progressBar = findViewById(R.id.progressBar);
        Button btnContinue = findViewById(R.id.btnContinue);
        loadingView = findViewById(R.id.loadingView);

        // Display results
        displayResults();

        // Save progress
        saveProgress();

        // Update streak
        updateStreak();

        // Button listener
        btnContinue.setOnClickListener(v -> goToHome());

        // Handle back press - force user to use Continue button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToHome();
            }
        });
    }

    /**
     * Hiển thị kết quả
     */
    private void displayResults() {
        // Score
        tvScore.setText(getString(R.string.result_score_format, quizScore, quizTotal));

        // Accuracy
        int accuracy = quizTotal > 0 ? (int) ((quizScore * 100.0) / quizTotal) : 0;
        tvAccuracy.setText(getString(R.string.result_accuracy_format, accuracy));

        // Progress bar
        progressBar.setMax(100);
        progressBar.setProgress(accuracy);

        // Calculate XP
        totalXP = XPCalculator.calculateLessonCompleteXP(vocabularyCount, quizScore, quizTotal);
        tvXPEarned.setText(getString(R.string.result_xp_format, totalXP));

        // Congratulations message
        if (accuracy == 100) {
            tvCongrats.setText(R.string.result_congrats_perfect);
        } else if (accuracy >= 80) {
            tvCongrats.setText(R.string.result_congrats_great);
        } else if (accuracy >= 60) {
            tvCongrats.setText(R.string.result_congrats_good);
        } else {
            tvCongrats.setText(R.string.result_congrats_keepgoing);
        }
    }

    /**
     * Lưu progress vào Firebase
     */
    private void saveProgress() {
        loadingView.setVisibility(View.VISIBLE);

        int accuracy = quizTotal > 0 ? (int) ((quizScore * 100.0) / quizTotal) : 0;

        progressRepository.saveLessonProgress(lessonId, accuracy, new ProgressRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                // Update user XP
                progressRepository.updateUserXP(totalXP, new ProgressRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        loadingView.setVisibility(View.GONE);
                        Toast.makeText(ResultActivity.this,
                                "Đã lưu tiến độ!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        loadingView.setVisibility(View.GONE);
                        Toast.makeText(ResultActivity.this,
                                "Lỗi lưu XP: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(ResultActivity.this,
                        "Lỗi lưu progress: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cập nhật streak
     */
    private void updateStreak() {
        streakManager.updateStreak(new StreakManager.StreakCallback() {
            @Override
            public void onSuccess(int newStreak, int xpEarned) {
                tvStreak.setText(getString(R.string.result_streak_format, newStreak));
                
                if (xpEarned > 0) {
                    totalXP += xpEarned;
                    tvXPEarned.setText(getString(R.string.result_xp_format, totalXP));
                    Toast.makeText(ResultActivity.this,
                            getString(R.string.result_streak_bonus, xpEarned),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                tvStreak.setText(getString(R.string.result_streak_format, 0));
            }
        });
    }

    /**
     * Quay về màn hình chính
     */
    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
