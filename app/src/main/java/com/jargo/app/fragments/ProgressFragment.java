package com.jargo.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jargo.app.R;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * ProgressFragment - Hiển thị tiến độ học tập
 */
public class ProgressFragment extends Fragment {

    private TextView tvTotalXP;
    private TextView tvStreak;
    private TextView tvLessonsCompleted;
    private TextView tvVocabsLearned;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(requireContext());

        // Bind views
        tvTotalXP = view.findViewById(R.id.tvTotalXP);
        tvStreak = view.findViewById(R.id.tvStreak);
        tvLessonsCompleted = view.findViewById(R.id.tvLessonsCompleted);
        tvVocabsLearned = view.findViewById(R.id.tvVocabsLearned);

        // Load data (TODO: Từ Firebase)
        loadProgress();

        return view;
    }

    private void loadProgress() {
        // TODO: Load từ Firebase Progress node
        tvTotalXP.setText("0 XP");
        tvStreak.setText("0 ngày");
        tvLessonsCompleted.setText("0 bài học");
        tvVocabsLearned.setText("0 từ vựng");
    }
}
