package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jargo.app.R;
import com.jargo.app.adapters.VocabularyPagerAdapter;
import com.jargo.app.models.Vocabulary;
import com.jargo.app.repositories.VocabularyRepository;
import com.jargo.app.utils.Constants;
import java.util.List;

/**
 * LearningActivity - Màn hình học từ vựng với flashcards
 */
public class LearningActivity extends AppCompatActivity {

    private TextView tvProgress;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnComplete;
    private ProgressBar progressBar;
    private View loadingView;

    private VocabularyRepository vocabularyRepository;
    private List<Vocabulary> vocabularies;

    private String lessonId;
    private String lessonTitle;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        vocabularyRepository = VocabularyRepository.getInstance();

        // Get data from Intent
        lessonId = getIntent().getStringExtra(Constants.EXTRA_LESSON_ID);
        lessonTitle = getIntent().getStringExtra(Constants.EXTRA_LESSON_TITLE);

        // Bind views
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvProgress = findViewById(R.id.tvProgress);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnComplete = findViewById(R.id.btnComplete);
        progressBar = findViewById(R.id.progressBar);
        loadingView = findViewById(R.id.loadingView);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(lessonTitle);
        }

        // Load vocabularies
        loadVocabularies();

        // Button listeners
        btnPrevious.setOnClickListener(v -> previousCard());
        btnNext.setOnClickListener(v -> nextCard());
        btnComplete.setOnClickListener(v -> completeLesson());
    }

    /**
     * Load danh sách từ vựng
     */
    private void loadVocabularies() {
        loadingView.setVisibility(View.VISIBLE);

        vocabularyRepository.getVocabulariesByLesson(lessonId, new VocabularyRepository.VocabularyCallback() {
            @Override
            public void onSuccess(List<Vocabulary> vocabs) {
                loadingView.setVisibility(View.GONE);
                
                if (vocabs.isEmpty()) {
                    Toast.makeText(LearningActivity.this,
                            "Chưa có từ vựng trong bài này",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                vocabularies = vocabs;
                setupViewPager();
            }

            @Override
            public void onError(String error) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(LearningActivity.this,
                        "Lỗi: " + error,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Setup ViewPager với vocabularies
     */
    private void setupViewPager() {
        VocabularyPagerAdapter pagerAdapter = new VocabularyPagerAdapter(vocabularies);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Tab không có text, chỉ dùng làm indicator
        }).attach();

        // ViewPager page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                updateUI();
            }
        });

        updateUI();
    }

    /**
     * Update UI dựa vào position hiện tại
     */
    private void updateUI() {
        int total = vocabularies.size();
        int current = currentPosition + 1;

        // Progress text
        tvProgress.setText(getString(R.string.learning_progress, current, total));

        // Progress bar
        int progress = (int) ((current * 100.0) / total);
        progressBar.setProgress(progress);

        // Buttons
        btnPrevious.setEnabled(currentPosition > 0);
        btnNext.setVisibility(currentPosition < total - 1 ? View.VISIBLE : View.GONE);
        btnComplete.setVisibility(currentPosition == total - 1 ? View.VISIBLE : View.GONE);
    }

    /**
     * Chuyển đến card trước
     */
    private void previousCard() {
        if (currentPosition > 0) {
            viewPager.setCurrentItem(currentPosition - 1, true);
        }
    }

    /**
     * Chuyển đến card tiếp theo
     */
    private void nextCard() {
        if (currentPosition < vocabularies.size() - 1) {
            viewPager.setCurrentItem(currentPosition + 1, true);
        }
    }

    /**
     * Hoàn thành lesson
     */
    private void completeLesson() {
        // Hiển thị dialog xác nhận làm quiz
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hoàn thành bài học")
                .setMessage("Bạn đã học xong tất cả từ vựng!\nLàm quiz để kiếm XP nhé?")
                .setPositiveButton("Làm quiz", (dialog, which) -> openQuiz())
                .setNegativeButton("Để sau", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Mở QuizActivity
     */
    private void openQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(Constants.EXTRA_LESSON_ID, lessonId);
        intent.putExtra(Constants.EXTRA_LESSON_TITLE, lessonTitle);
        intent.putExtra(Constants.EXTRA_VOCABULARY_COUNT, vocabularies.size());
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
