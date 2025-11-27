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
import com.jargo.app.R;
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
}
