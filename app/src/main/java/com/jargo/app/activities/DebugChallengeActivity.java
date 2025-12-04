package com.jargo.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.jargo.app.R;
import com.jargo.app.models.DebugChallenge;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.NotificationHelper;
import com.jargo.app.utils.SharedPrefsManager;
import com.jargo.app.viewmodels.DebugChallengeViewModel;
import com.jargo.app.viewmodels.ViewModelFactory;
import com.jargo.app.views.CodeWebView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

/**
 * DebugChallengeActivity - Màn hình chơi Debug Challenge
 * User tìm lỗi trong code và chọn đáp án đúng
 */
public class DebugChallengeActivity extends AppCompatActivity {

    // ViewModel
    private DebugChallengeViewModel viewModel;

    // UI Components
    private MaterialToolbar toolbar;
    private TextView tvProgress;
    private LinearProgressIndicator progressBar;
    private Chip chipTopic;
    private Chip chipDifficulty;
    private TextView tvXpReward;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvLanguage;
    private CodeWebView codeWebView;
    private TextView tvQuestion;
    private TextView tvQuestionVi;
    private RadioGroup radioGroupOptions;
    private MaterialCardView cardExplanation;
    private TextView tvExplanation;
    private TextView tvExplanationVi;
    private ChipGroup chipGroupVocabulary;
    private View layoutVocabulary;
    private MaterialButton btnCheckAnswer;
    private MaterialButton btnNextChallenge;
    private View loadingView;

    // State
    private String topicId;
    private boolean isAnswerChecked = false;
    private int selectedOptionIndex = -1;
    private List<String> shuffledOptions;  // Options after shuffle
    private Map<Integer, Integer> shuffledToOriginalIndexMap;  // Map shuffled index to original index

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable immersive mode (hide navigation bar)
        enableImmersiveMode();
        
        setContentView(R.layout.activity_debug_challenge);

        // Get data from Intent
        topicId = getIntent().getStringExtra(Constants.EXTRA_TOPIC_ID);

        // Bind views
        bindViews();

        // Setup toolbar
        setupToolbar();

        // Setup ViewModel
        setupViewModel();

        // Setup listeners
        setupListeners();

        // Load challenges
        if (topicId != null && !topicId.isEmpty()) {
            viewModel.loadChallengesByTopic(topicId);
        } else {
            viewModel.loadAllChallenges();
        }
        
        // Setup modern back press handling
        setupBackPressHandling();
    }
    
    private void setupBackPressHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Confirm exit if in progress
                Integer current = viewModel.getCurrentPosition().getValue();
                if (current != null && current > 0 && !isAnswerChecked) {
                    NotificationHelper.showWarning(
                        DebugChallengeActivity.this, 
                        "Progress will be lost if you exit now"
                    );
                }
                // Always allow back if user confirms
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        chipTopic = findViewById(R.id.chipTopic);
        chipDifficulty = findViewById(R.id.chipDifficulty);
        tvXpReward = findViewById(R.id.tvXpReward);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvLanguage = findViewById(R.id.tvLanguage);
        codeWebView = findViewById(R.id.codeWebView);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionVi = findViewById(R.id.tvQuestionVi);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        cardExplanation = findViewById(R.id.cardExplanation);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvExplanationVi = findViewById(R.id.tvExplanationVi);
        chipGroupVocabulary = findViewById(R.id.chipGroupVocabulary);
        layoutVocabulary = findViewById(R.id.layoutVocabulary);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnNextChallenge = findViewById(R.id.btnNextChallenge);
        loadingView = findViewById(R.id.loadingView);

        // Setup CodeWebView
        codeWebView.setDarkTheme(true);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Debug Challenge");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModel() {
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        ViewModelFactory factory = new ViewModelFactory(prefsManager);
        viewModel = new ViewModelProvider(this, factory).get(DebugChallengeViewModel.class);

        // Observe current challenge
        viewModel.getCurrentChallenge().observe(this, this::displayChallenge);

        // Observe loading state
        viewModel.isLoading().observe(this, isLoading -> {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                NotificationHelper.showError(this, "Error", error);
            }
        });

        // Observe completion
        viewModel.isCompleted().observe(this, isCompleted -> {
            if (isCompleted != null && isCompleted) {
                showCompletionResult();
            }
        });

        // Observe progress
        viewModel.getCurrentPosition().observe(this, position -> updateProgress());
        viewModel.getTotalChallenges().observe(this, total -> updateProgress());
    }

    private void setupListeners() {
        // Radio group selection
        radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isAnswerChecked) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (group.getChildAt(i).getId() == checkedId) {
                        selectedOptionIndex = i;
                        break;
                    }
                }
                btnCheckAnswer.setEnabled(true);
            }
        });

        // Check answer button
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());

        // Next challenge button
        btnNextChallenge.setOnClickListener(v -> nextChallenge());
    }

    private void displayChallenge(DebugChallenge challenge) {
        if (challenge == null) return;

        // Reset state
        isAnswerChecked = false;
        selectedOptionIndex = -1;
        btnCheckAnswer.setVisibility(View.VISIBLE);
        btnCheckAnswer.setEnabled(false);
        btnNextChallenge.setVisibility(View.GONE);
        cardExplanation.setVisibility(View.GONE);
        radioGroupOptions.clearCheck();

        // Topic & Difficulty
        chipTopic.setText(challenge.getTopic());
        chipDifficulty.setText(capitalize(challenge.getDifficulty()));
        setDifficultyChipColor(challenge.getDifficulty());

        // XP Reward
        tvXpReward.setText("+" + challenge.getXpReward() + " XP");

        // Title & Description
        tvTitle.setText(challenge.getTitle());
        if (challenge.getDescription() != null && !challenge.getDescription().isEmpty()) {
            tvDescription.setText(challenge.getDescription());
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        // Language
        String langIcon = challenge.getLanguageIcon();
        String langName = capitalize(challenge.getLanguage());
        tvLanguage.setText(langIcon + " " + langName);

        // Code display
        if (challenge.getBugLineNumber() > 0) {
            codeWebView.setBugLineNumber(challenge.getBugLineNumber());
        }
        codeWebView.displayCodeWithLineNumbers(challenge.getCodeSnippet(), challenge.getLanguage());

        // Question
        tvQuestion.setText(challenge.getQuestion());
        if (challenge.getQuestionVi() != null && !challenge.getQuestionVi().isEmpty()) {
            tvQuestionVi.setText(challenge.getQuestionVi());
            tvQuestionVi.setVisibility(View.VISIBLE);
        } else {
            tvQuestionVi.setVisibility(View.GONE);
        }

        // Options - Shuffle để đáp án không cùng vị trí
        List<String> originalOptions = challenge.getOptions();
        if (originalOptions != null && !originalOptions.isEmpty()) {
            // Create list with indices
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < originalOptions.size(); i++) {
                indices.add(i);
            }
            
            // Shuffle indices
            Collections.shuffle(indices);
            
            // Create shuffled options and mapping
            shuffledOptions = new ArrayList<>();
            shuffledToOriginalIndexMap = new HashMap<>();
            for (int i = 0; i < indices.size(); i++) {
                int originalIndex = indices.get(i);
                shuffledOptions.add(originalOptions.get(originalIndex));
                shuffledToOriginalIndexMap.put(i, originalIndex);
            }
            
            setupOptions(shuffledOptions);
        }

        // Update progress
        updateProgress();
    }

    private void setupOptions(List<String> options) {
        radioGroupOptions.removeAllViews();

        if (options == null || options.isEmpty()) return;

        for (int i = 0; i < options.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(options.get(i));
            radioButton.setTextSize(15);
            radioButton.setPadding(16, 24, 16, 24);
            radioButton.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            radioButton.setLayoutParams(params);

            radioGroupOptions.addView(radioButton);
        }
    }

    private void checkAnswer() {
        if (isAnswerChecked || selectedOptionIndex < 0) return;

        DebugChallenge challenge = viewModel.getCurrentChallenge().getValue();
        if (challenge == null || shuffledToOriginalIndexMap == null) return;

        isAnswerChecked = true;
        
        // Map shuffled index to original index
        int originalSelectedIndex = shuffledToOriginalIndexMap.get(selectedOptionIndex);
        boolean isCorrect = challenge.isCorrectAnswer(originalSelectedIndex);
        
        // Find which shuffled index corresponds to the correct answer
        int correctShuffledIndex = -1;
        for (Map.Entry<Integer, Integer> entry : shuffledToOriginalIndexMap.entrySet()) {
            if (entry.getValue() == challenge.getCorrectAnswerIndex()) {
                correctShuffledIndex = entry.getKey();
                break;
            }
        }

        // Disable radio buttons
        for (int i = 0; i < radioGroupOptions.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroupOptions.getChildAt(i);
            rb.setEnabled(false);

            // Highlight correct/wrong answers (using shuffled indices)
            if (i == correctShuffledIndex) {
                rb.setTextColor(ContextCompat.getColor(this, R.color.success_green));
                rb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle, 0);
            } else if (i == selectedOptionIndex && !isCorrect) {
                rb.setTextColor(ContextCompat.getColor(this, R.color.error_red));
                rb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cancel, 0);
            }
        }

        // Record result
        if (isCorrect) {
            viewModel.recordCorrectAnswer();
            NotificationHelper.showSuccess(this, "Correct! 🎉", challenge.getXpReward());
        } else {
            viewModel.recordWrongAnswer();
            NotificationHelper.showError(this, "Wrong!", challenge.getCorrectAnswer());
        }

        // Show explanation
        showExplanation(challenge);

        // Update buttons
        btnCheckAnswer.setVisibility(View.GONE);
        btnNextChallenge.setVisibility(View.VISIBLE);
    }

    private void showExplanation(DebugChallenge challenge) {
        cardExplanation.setVisibility(View.VISIBLE);

        // Explanation text
        if (challenge.getExplanation() != null && !challenge.getExplanation().isEmpty()) {
            tvExplanation.setText(challenge.getExplanation());
            tvExplanation.setVisibility(View.VISIBLE);
        } else {
            tvExplanation.setVisibility(View.GONE);
        }

        // Vietnamese explanation
        if (challenge.getExplanationVi() != null && !challenge.getExplanationVi().isEmpty()) {
            tvExplanationVi.setText(challenge.getExplanationVi());
            tvExplanationVi.setVisibility(View.VISIBLE);
        } else {
            tvExplanationVi.setVisibility(View.GONE);
        }

        // Vocabulary terms
        List<String> vocabTerms = challenge.getVocabularyTerms();
        if (vocabTerms != null && !vocabTerms.isEmpty()) {
            layoutVocabulary.setVisibility(View.VISIBLE);
            chipGroupVocabulary.removeAllViews();

            for (String term : vocabTerms) {
                Chip chip = new Chip(this);
                chip.setText(term);
                chip.setChipBackgroundColorResource(R.color.chip_vocabulary_bg);
                chip.setTextColor(ContextCompat.getColor(this, R.color.chip_vocabulary_text));
                chip.setClickable(false);
                chipGroupVocabulary.addView(chip);
            }
        } else {
            layoutVocabulary.setVisibility(View.GONE);
        }
    }

    private void nextChallenge() {
        viewModel.nextChallenge();
    }

    private void updateProgress() {
        Integer current = viewModel.getCurrentPosition().getValue();
        Integer total = viewModel.getTotalChallenges().getValue();

        if (current != null && total != null && total > 0) {
            tvProgress.setText((current + 1) + "/" + total);
            int progress = (int) (((current + 1) / (float) total) * 100);
            progressBar.setProgress(progress);
        }
    }

    private void showCompletionResult() {
        Integer correct = viewModel.getCorrectAnswers().getValue();
        Integer total = viewModel.getTotalChallenges().getValue();
        Integer totalXp = viewModel.getTotalXpEarned().getValue();

        if (correct == null) correct = 0;
        if (total == null) total = 0;
        if (totalXp == null) totalXp = 0;

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.EXTRA_QUIZ_SCORE, correct);
        intent.putExtra(Constants.EXTRA_QUIZ_TOTAL, total);
        intent.putExtra(Constants.EXTRA_XP_EARNED, totalXp);
        intent.putExtra(Constants.EXTRA_MODE, "debug_challenge");
        startActivity(intent);
        finish();
    }

    private void setDifficultyChipColor(String difficulty) {
        if (difficulty == null) return;

        int colorRes;
        switch (difficulty.toLowerCase()) {
            case "easy":
                colorRes = R.color.difficulty_easy;
                break;
            case "medium":
                colorRes = R.color.difficulty_medium;
                break;
            case "hard":
                colorRes = R.color.difficulty_hard;
                break;
            default:
                colorRes = R.color.difficulty_easy;
        }
        chipDifficulty.setChipBackgroundColorResource(colorRes);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Enable immersive mode to hide system navigation bar
     */
    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }


        super.onBackPressed();
    }
}
