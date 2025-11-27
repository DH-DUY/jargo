package com.jargo.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.R;
import com.jargo.app.models.Progress;
import com.jargo.app.repositories.ProgressRepository;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.Map;

/**
 * ProgressFragment - Hiển thị tiến độ học tập
 */
public class ProgressFragment extends Fragment {

    private TextView tvTotalXP;
    private TextView tvStreak;
    private TextView tvLessonsCompleted;
    private TextView tvVocabsLearned;
    private ProgressBar loadingProgress;
    private View loadingView;

    private ProgressRepository progressRepository;
    private SharedPrefsManager prefsManager;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());
        progressRepository = ProgressRepository.getInstance(prefsManager);

        // Bind views
        tvTotalXP = view.findViewById(R.id.tvTotalXP);
        tvStreak = view.findViewById(R.id.tvStreak);
        tvLessonsCompleted = view.findViewById(R.id.tvLessonsCompleted);
        tvVocabsLearned = view.findViewById(R.id.tvVocabsLearned);
        loadingView = view.findViewById(R.id.loadingView);

        // Load data từ Firebase
        loadProgress();
        loadUserXP();

        return view;
    }

    /**
     * Load progress data từ Firebase
     */
    private void loadProgress() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        progressRepository.getAllProgress(new ProgressRepository.AllProgressCallback() {
            @Override
            public void onSuccess(Map<String, Progress> progressMap) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                // Count completed lessons
                int completedLessons = 0;
                int totalVocabs = 0;

                for (Progress progress : progressMap.values()) {
                    if (progress.isCompleted()) {
                        completedLessons++;
                    }
                    totalVocabs += progress.getVocabularyMastered();
                }

                // Update UI
                tvLessonsCompleted.setText(completedLessons + " bài học");
                tvVocabsLearned.setText(totalVocabs + " từ vựng");
                
                // Streak (TODO: implement from StreakManager)
                tvStreak.setText("0 ngày");
            }

            @Override
            public void onError(String error) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                
                // Show default values on error
                tvLessonsCompleted.setText("0 bài học");
                tvVocabsLearned.setText("0 từ vựng");
                tvStreak.setText("0 ngày");
            }
        });
    }

    /**
     * Load user XP từ Firebase
     */
    private void loadUserXP() {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            tvTotalXP.setText("0 XP");
            return;
        }

        FirebaseManager.getInstance()
                .getDatabaseReference()
                .child(Constants.DB_USERS)
                .child(userId)
                .child("totalXP")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer xp = snapshot.getValue(Integer.class);
                        if (xp != null) {
                            tvTotalXP.setText(xp + " XP");
                        } else {
                            tvTotalXP.setText("0 XP");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvTotalXP.setText("0 XP");
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data khi quay lại fragment
        loadProgress();
        loadUserXP();
    }
}
