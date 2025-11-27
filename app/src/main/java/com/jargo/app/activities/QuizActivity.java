package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.jargo.app.R;
import com.jargo.app.models.Quiz;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.NotificationHelper;
import com.jargo.app.utils.SharedPrefsManager;
import com.jargo.app.viewmodels.QuizViewModel;
import com.jargo.app.viewmodels.ViewModelFactory;
import java.util.List;

/**
 * QuizActivity - Màn hình làm quiz
 * Sử dụng MVVM pattern với QuizViewModel
 */
public class QuizActivity extends AppCompatActivity {

    // ViewModel
    private QuizViewModel viewModel;
    
    // UI Components
    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private RadioGroup radioGroupOptions;
    private EditText etFillBlank;
    private Button btnCheckAnswer;
    private Button btnNextQuestion;
    private ProgressBar progressBar;
    private View loadingView;

    // Data
    private List<Quiz> quizzes;
    private String lessonId;
    private String lessonTitle;
    private int vocabularyCount;
    private boolean isAnswerChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get data from Intent
        lessonId = getIntent().getStringExtra(Constants.EXTRA_LESSON_ID);
        lessonTitle = getIntent().getStringExtra(Constants.EXTRA_LESSON_TITLE);
        vocabularyCount = getIntent().getIntExtra(Constants.EXTRA_VOCABULARY_COUNT, 0);

        // Bind views
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        etFillBlank = findViewById(R.id.etFillBlank);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        progressBar = findViewById(R.id.progressBar);
        loadingView = findViewById(R.id.loadingView);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quiz: " + lessonTitle);
        }

        // Setup ViewModel
        setupViewModel();

        // Button listeners
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnNextQuestion.setOnClickListener(v -> nextQuestion());

        // Load quizzes
        viewModel.loadQuizzes(lessonId, true);
    }

    /**
     * Setup ViewModel và LiveData observers
     */
    private void setupViewModel() {
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        ViewModelFactory factory = new ViewModelFactory(prefsManager);
        viewModel = new ViewModelProvider(this, factory).get(QuizViewModel.class);

        // Observe quizzes
        viewModel.getQuizzes().observe(this, quizList -> {
            if (quizList != null && !quizList.isEmpty()) {
                quizzes = quizList;
                showQuestion();
            } else if (quizList != null) {
                NotificationHelper.showWarning(this, "Chưa có quiz cho bài này");
                finish();
            }
        });

        // Observe current quiz
        viewModel.getCurrentQuiz().observe(this, quiz -> {
            if (quiz != null) {
                displayQuiz(quiz);
            }
        });

        // Observe loading state
        viewModel.isLoading().observe(this, isLoading -> {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                NotificationHelper.showError(this, "Lỗi", error);
                finish();
            }
        });

        // Observe completion
        viewModel.isCompleted().observe(this, isCompleted -> {
            if (isCompleted != null && isCompleted) {
                showResult();
            }
        });

        // Observe current position for progress
        viewModel.getCurrentPosition().observe(this, position -> {
            if (position != null) {
                updateProgress();
            }
        });
    }


    /**
     * Hiển thị câu hỏi hiện tại
     */
    private void showQuestion() {
        Integer currentPos = viewModel.getCurrentPosition().getValue();
        if (currentPos != null && quizzes != null && currentPos < quizzes.size()) {
            Quiz quiz = quizzes.get(currentPos);
            displayQuiz(quiz);
        }
    }

    /**
     * Display quiz UI
     */
    private void displayQuiz(Quiz quiz) {
        Integer currentPos = viewModel.getCurrentPosition().getValue();
        Integer total = viewModel.getTotalQuizzes().getValue();
        
        if (currentPos == null || total == null) {
            return;
        }

        // Question number
        tvQuestionNumber.setText(getString(R.string.quiz_question, currentPos + 1, total));

        // Question text
        String questionText = quiz.getQuestion();
        if (questionText == null || questionText.trim().isEmpty()) {
            questionText = "Câu hỏi đang được cập nhật...";
        }
        tvQuestion.setText(questionText);

        // Reset UI
        isAnswerChecked = false;
        btnCheckAnswer.setVisibility(View.VISIBLE);
        btnNextQuestion.setVisibility(View.GONE);
        radioGroupOptions.clearCheck();
        etFillBlank.setText("");
        radioGroupOptions.setEnabled(true);
        etFillBlank.setEnabled(true);
        for (int i = 0; i < radioGroupOptions.getChildCount(); i++) {
            radioGroupOptions.getChildAt(i).setEnabled(true);
        }

        // Setup question type
        if (Constants.QUIZ_TYPE_MULTIPLE_CHOICE.equals(quiz.getType())) {
            setupMultipleChoice(quiz);
        } else if (Constants.QUIZ_TYPE_FILL_BLANK.equals(quiz.getType())) {
            setupFillBlank(quiz);
        }
        
        updateProgress();
    }

    /**
     * Update progress bar
     */
    private void updateProgress() {
        int progress = viewModel.getProgressPercentage();
        progressBar.setProgress(progress);
    }

    /**
     * Setup multiple choice question
     */
    private void setupMultipleChoice(Quiz quiz) {
        radioGroupOptions.setVisibility(View.VISIBLE);
        etFillBlank.setVisibility(View.GONE);

        radioGroupOptions.removeAllViews();

        List<String> options = quiz.getOptions();
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setId(View.generateViewId());
                radioButton.setText(options.get(i));
                radioButton.setTextSize(16);
                radioButton.setPadding(16, 16, 16, 16);
                radioGroupOptions.addView(radioButton);
            }
        }
    }

    /**
     * Setup fill blank question
     */
    private void setupFillBlank(Quiz quiz) {
        radioGroupOptions.setVisibility(View.GONE);
        etFillBlank.setVisibility(View.VISIBLE);
        etFillBlank.setHint("Nhập đáp án...");
    }

    /**
     * Kiểm tra đáp án
     */
    private void checkAnswer() {
        if (isAnswerChecked) {
            return;
        }
        
        Integer currentPos = viewModel.getCurrentPosition().getValue();
        if (currentPos == null || quizzes == null || currentPos >= quizzes.size()) {
            return;
        }
        
        Quiz quiz = quizzes.get(currentPos);
        String userAnswer = getUserAnswer(quiz);

        if (userAnswer == null || userAnswer.isEmpty()) {
            NotificationHelper.showWarning(this, "Vui lòng chọn hoặc nhập đáp án");
            return;
        }

        // Get selected option index for multiple choice
        int selectedIndex = -1;
        if (Constants.QUIZ_TYPE_MULTIPLE_CHOICE.equals(quiz.getType())) {
            int selectedId = radioGroupOptions.getCheckedRadioButtonId();
            for (int i = 0; i < radioGroupOptions.getChildCount(); i++) {
                if (radioGroupOptions.getChildAt(i).getId() == selectedId) {
                    selectedIndex = i;
                    break;
                }
            }
        }
        
        // Submit answer to ViewModel
        viewModel.submitAnswer(selectedIndex);
        
        boolean isCorrect = checkCorrectAnswer(quiz, userAnswer);

        if (isCorrect) {
            NotificationHelper.showSuccess(this, "Đúng rồi! Tuyệt vời!", quiz.getXpReward());
        } else {
            NotificationHelper.showError(this, "Sai rồi!", quiz.getCorrectAnswer());
        }

        // Show explanation if available
        if (quiz.getExplanation() != null && !quiz.getExplanation().isEmpty()) {
            tvQuestion.setText(tvQuestion.getText() + "\n\n💡 " + quiz.getExplanationVi());
        }

        isAnswerChecked = true;
        btnCheckAnswer.setVisibility(View.GONE);
        btnNextQuestion.setVisibility(View.VISIBLE);

        // Disable input
        radioGroupOptions.setEnabled(false);
        for (int i = 0; i < radioGroupOptions.getChildCount(); i++) {
            radioGroupOptions.getChildAt(i).setEnabled(false);
        }
        etFillBlank.setEnabled(false);
    }

    /**
     * Lấy đáp án user chọn
     */
    private String getUserAnswer(Quiz quiz) {
        if (Constants.QUIZ_TYPE_MULTIPLE_CHOICE.equals(quiz.getType())) {
            int selectedId = radioGroupOptions.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedButton = findViewById(selectedId);
                return selectedButton.getText().toString();
            }
        } else if (Constants.QUIZ_TYPE_FILL_BLANK.equals(quiz.getType())) {
            return etFillBlank.getText().toString().trim();
        }
        return null;
    }

    /**
     * Check xem đáp án có đúng không
     */
    private boolean checkCorrectAnswer(Quiz quiz, String userAnswer) {
        String correctAnswer = quiz.getCorrectAnswer().toLowerCase().trim();
        userAnswer = userAnswer.toLowerCase().trim();

        if (correctAnswer.equals(userAnswer)) {
            return true;
        }

        // Check alternative answers
        List<String> alternatives = quiz.getAlternativeAnswers();
        if (alternatives != null) {
            for (String alt : alternatives) {
                if (alt.toLowerCase().trim().equals(userAnswer)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Chuyển sang câu hỏi tiếp theo
     */
    private void nextQuestion() {
        viewModel.nextQuiz();
    }

    /**
     * Hiển thị kết quả
     */
    private void showResult() {
        Integer correctAnswers = viewModel.getCorrectAnswers().getValue();
        Integer totalQuestions = viewModel.getTotalQuizzes().getValue();
        
        if (correctAnswers == null || totalQuestions == null) {
            return;
        }
        
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.EXTRA_LESSON_ID, lessonId);
        intent.putExtra(Constants.EXTRA_LESSON_TITLE, lessonTitle);
        intent.putExtra(Constants.EXTRA_VOCABULARY_COUNT, vocabularyCount);
        intent.putExtra(Constants.EXTRA_QUIZ_SCORE, correctAnswers);
        intent.putExtra(Constants.EXTRA_QUIZ_TOTAL, totalQuestions);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
