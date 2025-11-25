package com.jargo.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jargo.app.R;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * LevelSelectionFragment - Màn hình chọn trình độ
 * Cho phép user chọn 1 trong 3 trình độ: Beginner, Intermediate, Professional
 */
public class LevelSelectionFragment extends Fragment {

    private RadioGroup radioGroupLevels;
    private SharedPrefsManager prefsManager;
    private String selectedLevel = Constants.LEVEL_BEGINNER; // Mặc định chọn Beginner

    public LevelSelectionFragment() {
        // Required empty public constructor
    }

    public static LevelSelectionFragment newInstance() {
        return new LevelSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_selection, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());

        // Khởi tạo views
        radioGroupLevels = view.findViewById(R.id.radioGroupLevels);

        // Mặc định chọn Beginner
        RadioButton rbBeginner = view.findViewById(R.id.rbBeginner);
        rbBeginner.setChecked(true);

        // Lắng nghe thay đổi lựa chọn
        radioGroupLevels.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBeginner) {
                selectedLevel = Constants.LEVEL_BEGINNER;
            } else if (checkedId == R.id.rbIntermediate) {
                selectedLevel = Constants.LEVEL_INTERMEDIATE;
            } else if (checkedId == R.id.rbProfessional) {
                selectedLevel = Constants.LEVEL_PROFESSIONAL;
            }

            // Lưu lựa chọn vào SharedPreferences
            prefsManager.saveUserLevel(selectedLevel);
        });

        return view;
    }
}
