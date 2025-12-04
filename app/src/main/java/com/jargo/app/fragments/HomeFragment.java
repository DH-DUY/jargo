package com.jargo.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.jargo.app.R;
import com.jargo.app.activities.DebugChallengeActivity;
import com.jargo.app.activities.LessonListActivity;
import com.jargo.app.adapters.TopicAdapter;
import com.jargo.app.models.Topic;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.NotificationHelper;
import com.jargo.app.utils.SharedPrefsManager;
import com.jargo.app.viewmodels.HomeViewModel;
import com.jargo.app.viewmodels.ViewModelFactory;
import java.util.ArrayList;

/**
 * HomeFragment - Màn hình chính hiển thị danh sách topics
 * Sử dụng MVVM pattern với HomeViewModel
 */
public class HomeFragment extends Fragment implements TopicAdapter.OnTopicClickListener {

    // ViewModel
    private HomeViewModel viewModel;
    
    // UI Components
    private TopicAdapter topicAdapter;
    private TextView tvFieldName;
    private TextView tvUserName;
    private TextView tvXP;
    private TextView tvStreak;
    private View progressBar;
    private View emptyState;
    
    // Streak Card Components
    private MaterialCardView cardStreak;
    private TextView tvStreakMain;
    private TextView tvStreakMotivation;
    private TextView tvMilestoneTarget;
    private LinearProgressIndicator pbStreakMilestone;

    // Utils
    private SharedPrefsManager prefsManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo
        prefsManager = SharedPrefsManager.getInstance(requireContext());

        // Bind views
        tvFieldName = view.findViewById(R.id.tvFieldName);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvXP = view.findViewById(R.id.tvXP);
        tvStreak = view.findViewById(R.id.tvStreak);
        RecyclerView recyclerViewTopics = view.findViewById(R.id.recyclerViewTopics);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Bind Streak Card views
        cardStreak = view.findViewById(R.id.cardStreak);
        tvStreakMain = view.findViewById(R.id.tvStreakMain);
        tvStreakMotivation = view.findViewById(R.id.tvStreakMotivation);
        tvMilestoneTarget = view.findViewById(R.id.tvMilestoneTarget);
        pbStreakMilestone = view.findViewById(R.id.pbStreakMilestone);
        emptyState = view.findViewById(R.id.emptyState);

        // Setup RecyclerView
        recyclerViewTopics.setLayoutManager(new LinearLayoutManager(requireContext()));
        topicAdapter = new TopicAdapter(new ArrayList<>(), this);
        recyclerViewTopics.setAdapter(topicAdapter);

        // Setup ViewModel
        setupViewModel();

        // Load data
        loadUserInfo();
        loadData();
        
        // Load user stats
        viewModel.loadUserXP();
        viewModel.loadUserStreak();

        // Setup Debug Challenge button
        View btnStartDebug = view.findViewById(R.id.btnStartDebug);
        if (btnStartDebug != null) {
            btnStartDebug.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), DebugChallengeActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    /**
     * Setup ViewModel và LiveData observers
     */
    private void setupViewModel() {
        ViewModelFactory factory = new ViewModelFactory(prefsManager);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Observe topics
        viewModel.getTopics().observe(getViewLifecycleOwner(), topics -> {
            if (topics != null && !topics.isEmpty()) {
                // Sort theo orderIndex
                topics.sort((t1, t2) -> Integer.compare(t1.getOrderIndex(), t2.getOrderIndex()));
                topicAdapter.updateTopics(topics);
                emptyState.setVisibility(View.GONE);
            } else {
                topicAdapter.updateTopics(new ArrayList<>());
                emptyState.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                NotificationHelper.showError(requireActivity(), "Lỗi", error);
            }
        });

        // Observe user field
        viewModel.getUserField().observe(getViewLifecycleOwner(), field -> {
            if (field != null) {
                tvFieldName.setText(getFieldDisplayName(field));
            }
        });

        // Observe XP and Streak
        viewModel.getTotalXP().observe(getViewLifecycleOwner(), xp -> {
            if (xp != null) {
                tvXP.setText(getString(R.string.home_xp, xp));
            }
        });

        viewModel.getStreak().observe(getViewLifecycleOwner(), streak -> {
            if (streak != null) {
                tvStreak.setText(getString(R.string.home_streak, streak));
                // Update prominent Streak card
                updateStreakCard(streak);
            }
        });

        // Observe progress map để update progress bar
        viewModel.getProgressMap().observe(getViewLifecycleOwner(), progressMap -> {
            if (progressMap != null) {
                topicAdapter.updateProgress(progressMap);
            }
        });
    }

    /**
     * Load thông tin user từ SharedPreferences
     */
    private void loadUserInfo() {
        String userName = prefsManager.getUserName();
        tvUserName.setText(userName != null ? userName : getString(R.string.home_default_username));
    }

    /**
     * Load data từ ViewModel
     */
    private void loadData() {
        String currentField = prefsManager.getUserField();
        if (currentField != null && !currentField.isEmpty()) {
            viewModel.setUserField(currentField);
        } else {
            viewModel.loadAllTopics();
        }
    }

    /**
     * Lấy tên hiển thị của field
     */
    private String getFieldDisplayName(String fieldId) {
        switch (fieldId) {
            case Constants.FIELD_IT:
                return getString(R.string.field_it);
            case Constants.FIELD_MEDICAL:
                return getString(R.string.field_medical);
            case Constants.FIELD_ECONOMICS:
                return getString(R.string.field_economics);
            default:
                return "";
        }
    }

    @Override
    public void onTopicClick(Topic topic) {
        if (topic.isLocked()) {
            NotificationHelper.showWarning(requireActivity(), "Chủ đề này đang bị khóa. Hoàn thành chủ đề trước đó để mở khóa!");
        } else {
            // Mở LessonListActivity
            Intent intent = new Intent(requireContext(), LessonListActivity.class);
            intent.putExtra(Constants.EXTRA_TOPIC_ID, topic.getTopicId());
            intent.putExtra(Constants.EXTRA_TOPIC_NAME, topic.getName());
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh user stats khi quay lại fragment
        // (sau khi complete quiz, XP/Streak đã được update)
        viewModel.loadUserXP();
        viewModel.loadUserStreak();
    }
    
    /**
     * Update Streak card with full gamification
     */
    private void updateStreakCard(int streak) {
        // Update main streak number
        tvStreakMain.setText(String.valueOf(streak));
        
        // Set motivational message based on streak
        tvStreakMotivation.setText(getStreakMotivation(streak));
        
        // Update milestone progress
        updateMilestoneProgress(streak);
        
        // Animate card on load if streak > 0
        if (streak > 0) {
            animateStreakCard();
        }
        
        // Check for milestone celebration
        checkMilestoneCelebration(streak);
        
        // Setup click listener for navigation
        setupStreakCardClick();
    }
    
    private String getStreakMotivation(int streak) {
        if (streak == 0) return "🌟 Bắt đầu chuỗi mới hôm nay!";
        if (streak == 1) return "✨ Tuyệt! Hãy tiếp tục nhé!";
        if (streak < 3) return "💪 Tuyệt vời! Tiếp tục streak!";
        if (streak < 7) return "🎯 Xuất sắc! Gần 1 tuần rồi!";
        if (streak == 7) return "🎉 Đỉnh cao! 1 tuần liên tục!";
        if (streak < 14) return "🔥 Tuyệt đỉnh! Đang on fire!";
        if (streak == 14) return "🏆 Huyền thoại! 2 tuần rồi!";
        if (streak < 30) return "⚡ Siêu phàm! Không thể cản!";
        if (streak == 30) return "👑 VÔ ĐỊCH! 1 tháng streak!";
        if (streak < 100) return "🌟 Truyền thuyết! " + streak + " ngày!";
        return "🔱 THẦN THOẠI! " + streak + " ngày liên tục!";
    }
    
    private void updateMilestoneProgress(int streak) {
        int[] milestones = {7, 14, 30, 60, 100, 365};
        int nextMilestone = 7;
        
        // Find next milestone
        for (int m : milestones) {
            if (streak < m) {
                nextMilestone = m;
                break;
            }
        }
        
        // Calculate progress percentage
        int previousMilestone = 0;
        for (int m : milestones) {
            if (m < nextMilestone) {
                previousMilestone = m;
            }
        }
        
        int range = nextMilestone - previousMilestone;
        int current = streak - previousMilestone;
        int progress = (int) ((current / (float) range) * 100);
        
        // Update UI
        pbStreakMilestone.setProgress(Math.min(progress, 100));
        tvMilestoneTarget.setText("🏆 " + nextMilestone + " ngày");
        
        // If reached max milestone, show special message
        if (streak >= 365) {
            tvMilestoneTarget.setText("🔱 MAX STREAK!");
            pbStreakMilestone.setProgress(100);
        }
    }
    
    private void animateStreakCard() {
        if (cardStreak == null) return;
        
        // Pulse animation
        cardStreak.animate()
            .scaleX(1.03f).scaleY(1.03f)
            .setDuration(300)
            .withEndAction(() -> {
                cardStreak.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(300)
                    .start();
            })
            .start();
    }
    
    private void checkMilestoneCelebration(int streak) {
        int[] milestones = {7, 14, 30, 60, 100, 365};
        
        // Check if current streak is exactly a milestone
        for (int milestone : milestones) {
            if (streak == milestone) {
                celebrateMilestone(milestone);
                break;
            }
        }
    }
    
    private void celebrateMilestone(int days) {
        // Haptic feedback
        try {
            com.jargo.app.HapticHelper.celebration(requireContext());
        } catch (Exception e) {
            // Haptic not available, skip
        }
        
        // Show celebration dialog
        String title = "🎉 Milestone Achieved!";
        String message;
        
        if (days == 7) {
            message = "Tuyệt vời! Bạn đã học 1 tuần liên tục!\n\nTiếp tục phát huy nhé! 💪";
        } else if (days == 14) {
            message = "Đỉnh cao! 2 tuần rồi đó!\n\nBạn đang on fire! 🔥";
        } else if (days == 30) {
            message = "VÔ ĐỊCH! 1 tháng liên tục!\n\nBạn là huyền thoại! 👑";
        } else if (days == 60) {
            message = "SIÊU PHÀM! 2 tháng rồi!\n\nKhông ai cản được! ⚡";
        } else if (days == 100) {
            message = "THẦN THOẠI! 100 ngày rồi!\n\nBạn là truyền thuyết sống! 🔱";
        } else if (days == 365) {
            message = "CẢ NĂM LIÊN TỤC!\n\nBạn đã đạt đến đỉnh cao tuyệt đối!\nKhông còn gì để nói ngoài... RESPECT! 🙏🔥👑";
        } else {
            message = "Awesome! " + days + " days streak!\n\nKeep going! 🎯";
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Tiếp tục! 💪", null)
            .setCancelable(true)
            .show();
    }
    
    private void setupStreakCardClick() {
        if (cardStreak != null) {
            cardStreak.setOnClickListener(v -> {
                // Navigate to Progress tab to see details
                try {
                    com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                        ((com.jargo.app.MainActivity) requireActivity())
                            .findViewById(R.id.bottomNavigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.progressFragment);
                    }
                } catch (Exception e) {
                    // Fallback: show streak details in dialog
                    showStreakDetails();
                }
            });
        }
    }
    
    private void showStreakDetails() {
        Integer streak = viewModel.getStreak().getValue();
        if (streak == null) streak = 0;
        
        String details = "Chuỗi học liên tục: " + streak + " ngày\n\n";
        
        if (streak > 0) {
            details += "Hãy tiếp tục duy trì streak để:\n";
            details += "• Nhận thêm XP mỗi ngày\n";
            details += "• Mở khóa achievement badges\n";
            details += "• Leo lên bảng xếp hạng";
        } else {
            details += "Hoàn thành bài học hôm nay để bắt đầu streak!";
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("🔥 Streak Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show();
    }
}
