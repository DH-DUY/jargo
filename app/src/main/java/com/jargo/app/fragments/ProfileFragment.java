package com.jargo.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jargo.app.R;
import com.jargo.app.activities.OnboardingActivity;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * ProfileFragment - Hiển thị thông tin cá nhân
 */
public class ProfileFragment extends Fragment {

    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvField;
    private TextView tvLevel;
    private Button btnLogout;

    private SharedPrefsManager prefsManager;
    private FirebaseManager firebaseManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());
        firebaseManager = FirebaseManager.getInstance();

        // Bind views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvField = view.findViewById(R.id.tvField);
        tvLevel = view.findViewById(R.id.tvLevel);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load user info
        loadUserInfo();

        // Logout button
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserInfo() {
        tvUserName.setText(prefsManager.getUserName());
        tvEmail.setText(prefsManager.getUserEmail());
        tvField.setText(getFieldName(prefsManager.getUserField()));
        tvLevel.setText(getLevelName(prefsManager.getUserLevel()));
    }

    private String getFieldName(String fieldId) {
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

    private String getLevelName(String levelId) {
        switch (levelId) {
            case Constants.LEVEL_BEGINNER:
                return getString(R.string.level_beginner);
            case Constants.LEVEL_INTERMEDIATE:
                return getString(R.string.level_intermediate);
            case Constants.LEVEL_PROFESSIONAL:
                return getString(R.string.level_professional);
            default:
                return "";
        }
    }

    private void logout() {
        // Clear data
        prefsManager.logout();
        firebaseManager.signOut();

        // Quay về Onboarding
        Intent intent = new Intent(requireContext(), OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
