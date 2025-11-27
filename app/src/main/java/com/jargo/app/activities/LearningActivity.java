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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jargo.app.R;
import com.jargo.app.adapters.VocabularyPagerAdapter;
import com.jargo.app.models.Vocabulary;
import com.jargo.app.utils.Constants;
import com.jargo.app.viewmodels.LearningViewModel;
import com.jargo.app.viewmodels.ViewModelFactory;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.List;

/**
 * LearningActivity - Màn hình học từ vựng với flashcards
 * Sử dụng MVVM pattern với LearningViewModel
 */
public class LearningActivity extends AppCompatActivity {

    // ViewModel
    private LearningViewModel viewModel;
    
    // UI Components
    private TextView tvProgress;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnComplete;
    private ProgressBar progressBar;
    private View loadingView;

    // Data
    private List<Vocabulary> vocabularies;
    private String lessonId;
    private String lessonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

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

        // Setup ViewModel
        setupViewModel();

        // Load vocabularies
        viewModel.loadLesson(lessonId);

        // Button listeners
        btnPrevious.setOnClickListener(v -> viewModel.previousVocabulary());
        btnNext.setOnClickListener(v -> viewModel.nextVocabulary());
        btnComplete.setOnClickListener(v -> completeLesson());
    }

    /**
     * Setup ViewModel và LiveData observers
     */
    private void setupViewModel() {
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);
        ViewModelFactory factory = new ViewModelFactory(prefsManager);
        viewModel = new ViewModelProvider(this, factory).get(LearningViewModel.class);

        // Observe vocabularies
        viewModel.getVocabularies().observe(this, vocabs -> {
            if (vocabs != null && !vocabs.isEmpty()) {
                vocabularies = vocabs;
                setupViewPager();
            } else if (vocabs != null) {
                Toast.makeText(this, "Chưa có từ vựng trong bài này", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Observe loading state
        viewModel.isLoading().observe(this, isLoading -> {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Observe current position
        viewModel.getCurrentPosition().observe(this, position -> {
            if (position != null && vocabularies != null) {
                viewPager.setCurrentItem(position, true);
            }
        });

        // Observe completion
        viewModel.isCompleted().observe(this, isCompleted -> {
            if (isCompleted != null && isCompleted) {
                completeLesson();
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
                viewModel.goToVocabulary(position);
                updateUI();
            }
        });

        updateUI();
    }

    /**
     * Update UI dựa vào position hiện tại
     */
    private void updateUI() {
        Integer currentPos = viewModel.getCurrentPosition().getValue();
        Integer total = viewModel.getTotalVocabularies().getValue();
        
        if (currentPos == null || total == null || total == 0) {
            return;
        }
        
        int current = currentPos + 1;

        // Progress text
        tvProgress.setText(getString(R.string.learning_progress, current, total));

        // Progress bar
        int progress = viewModel.getProgressPercentage();
        progressBar.setProgress(progress);

        // Buttons
        btnPrevious.setEnabled(currentPos > 0);
        btnNext.setVisibility(currentPos < total - 1 ? View.VISIBLE : View.GONE);
        btnComplete.setVisibility(currentPos == total - 1 ? View.VISIBLE : View.GONE);
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
