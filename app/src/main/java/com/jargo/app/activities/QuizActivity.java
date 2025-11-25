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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.jargo.app.R;
import com.jargo.app.models.Quiz;
import com.jargo.app.repositories.QuizRepository;
import com.jargo.app.utils.Constants;
import java.util.List;

/**
 * QuizActivity - Màn hình làm quiz
 */
public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private RadioGroup radioGroupOptions;
    private EditText etFillBlank;
    private Button btnCheckAnswer;
    private Button btnNextQuestion;
    private ProgressBar progressBar;
    private View loadingView;

    private QuizRepository quizRepository;
    private List<Quiz> quizzes;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalQuestions = 0;

    private String lessonId;
    private String lessonTitle;
    private int vocabularyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizRepository = QuizRepository.getInstance();

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

        // Button listeners
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnNextQuestion.setOnClickListener(v -> nextQuestion());

        // Load quizzes
        loadQuizzes();
    }

    /**
     * Load danh sách quiz
     */
    private void loadQuizzes() {
        loadingView.setVisibility(View.VISIBLE);

        quizRepository.getQuizzesByLesson(lessonId, true, new QuizRepository.QuizCallback() {
            @Override
            public void onSuccess(List<Quiz> quizList) {
                loadingView.setVisibility(View.GONE);

                if (quizList.isEmpty()) {
                    Toast.makeText(QuizActivity.this,
                            "Chưa có quiz cho bài này",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                quizzes = quizList;
                totalQuestions = quizzes.size();
                showQuestion();
            }

            @Override
            public void onError(String error) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(QuizActivity.this,
                        "Lỗi: " + error,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Hiển thị câu hỏi hiện tại
     */
    private void showQuestion() {
        Quiz quiz = quizzes.get(currentQuestionIndex);

        // Question number
        tvQuestionNumber.setText(getString(R.string.quiz_question, currentQuestionIndex + 1, totalQuestions));

        // Question text
        String questionText = quiz.getQuestion();
        if (questionText == null || questionText.trim().isEmpty()) {
            questionText = "Câu hỏi đang được cập nhật...";
        }
        tvQuestion.setText(questionText);

        // Progress bar
        int progress = (int) (((currentQuestionIndex + 1) * 100.0) / totalQuestions);
        progressBar.setProgress(progress);

        // Reset UI
        btnCheckAnswer.setVisibility(View.VISIBLE);
        btnNextQuestion.setVisibility(View.GONE);
        radioGroupOptions.clearCheck();
        etFillBlank.setText("");

        // Setup question type
        if (Constants.QUIZ_TYPE_MULTIPLE_CHOICE.equals(quiz.getType())) {
            setupMultipleChoice(quiz);
        } else if (Constants.QUIZ_TYPE_FILL_BLANK.equals(quiz.getType())) {
            setupFillBlank(quiz);
        }
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
        Quiz quiz = quizzes.get(currentQuestionIndex);
        String userAnswer = getUserAnswer(quiz);

        if (userAnswer == null || userAnswer.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn hoặc nhập đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = checkCorrectAnswer(quiz, userAnswer);

        if (isCorrect) {
            correctAnswers++;
            Toast.makeText(this, "✅ Đúng rồi! +" + quiz.getXpReward() + " XP", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "❌ Sai rồi!\nĐáp án: " + quiz.getCorrectAnswer(), Toast.LENGTH_LONG).show();
        }

        // Show explanation if available
        if (quiz.getExplanation() != null && !quiz.getExplanation().isEmpty()) {
            tvQuestion.setText(tvQuestion.getText() + "\n\n💡 " + quiz.getExplanationVi());
        }

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
        currentQuestionIndex++;

        if (currentQuestionIndex < totalQuestions) {
            showQuestion();
        } else {
            // Quiz completed
            showResult();
        }
    }

    /**
     * Hiển thị kết quả
     */
    private void showResult() {
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
