package com.jargo.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jargo.app.R;
import com.jargo.app.ThemeHelper;
import com.jargo.app.activities.AuthenticationActivity;
import com.jargo.app.activities.LoginActivity;
import com.jargo.app.activities.LoginHistoryActivity;
import com.jargo.app.activities.OnboardingActivity;
import com.jargo.app.activities.RegisterActivity;
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
    private LinearLayout layoutGuestActions;
    private LinearLayout btnLoginHistory;
    private Button btnCreateAccount;
    private Button btnSignIn;
    private Button btnLogout;
    private Button btnDeleteAccount;
    private SwitchMaterial switchDarkMode;

    private SharedPrefsManager prefsManager;
    private FirebaseManager firebaseManager;
    private GoogleSignInClient googleSignInClient;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefsManager = SharedPrefsManager.getInstance(requireContext());
        firebaseManager = FirebaseManager.getInstance();
        
        // Configure Google Sign In client for sign out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Bind views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvField = view.findViewById(R.id.tvField);
        tvLevel = view.findViewById(R.id.tvLevel);
        layoutGuestActions = view.findViewById(R.id.layoutGuestActions);
        btnLoginHistory = view.findViewById(R.id.btnLoginHistory);
        btnCreateAccount = view.findViewById(R.id.btnCreateAccount);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // Load user info
        loadUserInfo();
        
        // Setup dark mode switch
        setupDarkModeSwitch();

        // Button listeners
        btnLoginHistory.setOnClickListener(v -> goToLoginHistory());
        btnCreateAccount.setOnClickListener(v -> goToRegister());
        btnSignIn.setOnClickListener(v -> goToLogin());
        btnLogout.setOnClickListener(v -> logout());
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());

        return view;
    }

    private void loadUserInfo() {
        // Check if user is authenticated
        boolean isAuthenticated = firebaseManager.isUserLoggedIn();
        
        if (isAuthenticated) {
            // Real user - show user info and logout button
            tvUserName.setText(prefsManager.getUserName());
            tvEmail.setText(prefsManager.getUserEmail());
            layoutGuestActions.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            btnDeleteAccount.setVisibility(View.VISIBLE);
            btnLoginHistory.setVisibility(View.VISIBLE);
        } else {
            // Guest user - show login/register buttons
            tvUserName.setText(R.string.profile_guest);
            tvEmail.setText(R.string.profile_create_account);
            layoutGuestActions.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            btnDeleteAccount.setVisibility(View.GONE);
            btnLoginHistory.setVisibility(View.GONE);
        }
        
        tvField.setText(getFieldName(prefsManager.getUserField()));
        String levelName = getLevelName(prefsManager.getUserLevel());
        tvLevel.setText(levelName.isEmpty() ? "Người mới" : levelName);
    }
    
    /**
     * Setup dark mode switch
     */
    private void setupDarkModeSwitch() {
        // Set initial state based on current theme
        int currentMode = ThemeHelper.getThemeMode(requireContext());
        switchDarkMode.setChecked(currentMode == AppCompatDelegate.MODE_NIGHT_YES);
        
        // Handle switch toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ThemeHelper.setThemeMode(requireContext(), AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                ThemeHelper.setThemeMode(requireContext(), AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
    
    /**
     * Đi đến màn hình Login History
     */
    private void goToLoginHistory() {
        Intent intent = new Intent(requireContext(), LoginHistoryActivity.class);
        startActivity(intent);
    }
    
    /**
     * Đi đến màn hình Login
     */
    private void goToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
    }
    
    /**
     * Đi đến màn hình Register
     */
    private void goToRegister() {
        Intent intent = new Intent(requireContext(), RegisterActivity.class);
        startActivity(intent);
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
        if (levelId == null || levelId.isEmpty()) {
            return "Người mới"; // Default level
        }
        switch (levelId) {
            case Constants.LEVEL_BEGINNER:
                return getString(R.string.level_beginner);
            case Constants.LEVEL_INTERMEDIATE:
                return getString(R.string.level_intermediate);
            case Constants.LEVEL_PROFESSIONAL:
                return getString(R.string.level_professional);
            default:
                return "Người mới";
        }
    }

    private void logout() {
        // Clear data
        prefsManager.logout();
        firebaseManager.signOut();

        // Quay về Authentication (không cần onboarding lại)
        Intent intent = new Intent(requireContext(), AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Hiển thị dialog xác nhận xóa tài khoản
     */
    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản?\n\n⚠️ Cảnh báo:\n• Tất cả dữ liệu học tập sẽ bị xóa vĩnh viễn\n• Không thể khôi phục lại")
                .setPositiveButton("Xóa tài khoản", (dialog, which) -> deleteAccount())
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Xóa tài khoản
     */
    private void deleteAccount() {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            return;
        }

        // Show loading
        AlertDialog loadingDialog = new AlertDialog.Builder(requireContext())
                .setMessage("Đang xóa tài khoản...")
                .setCancelable(false)
                .create();
        loadingDialog.show();

        // Đăng xuất Google trước để clear cache
        googleSignInClient.signOut();
        
        // Xóa dữ liệu từ Firebase
        deleteUserData(userId, () -> {
            // Xóa Firebase Auth account
            if (firebaseManager.getAuth().getCurrentUser() != null) {
                firebaseManager.getAuth().getCurrentUser().delete()
                        .addOnCompleteListener(task -> {
                            loadingDialog.dismiss();
                            
                            if (task.isSuccessful()) {
                                // Xóa local data
                                prefsManager.logout();
                                
                                // Thông báo thành công
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Thành công")
                                        .setMessage("Tài khoản đã được xóa")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            // Về màn hình Authentication
                                            Intent intent = new Intent(requireContext(), AuthenticationActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            requireActivity().finish();
                                        })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                // Lỗi khi xóa auth
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Lỗi")
                                        .setMessage("Không thể xóa tài khoản: " + task.getException().getMessage())
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        });
            } else {
                loadingDialog.dismiss();
                // Guest user - chỉ xóa local data
                prefsManager.logout();
                Intent intent = new Intent(requireContext(), AuthenticationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    /**
     * Xóa toàn bộ dữ liệu của user từ Firebase
     */
    private void deleteUserData(String userId, Runnable onComplete) {
        // Đếm số tác vụ xóa cần hoàn thành
        final int[] tasksRemaining = {4}; // 4 tasks: blacklist, users, progress, loginHistory
        
        Runnable checkComplete = () -> {
            tasksRemaining[0]--;
            if (tasksRemaining[0] == 0 && onComplete != null) {
                onComplete.run();
            }
        };

        // 1. Thêm vào blacklist để ngăn đăng nhập lại
        firebaseManager.getDatabaseReference()
                .child("deletedUsers")
                .child(userId)
                .setValue(System.currentTimeMillis())
                .addOnCompleteListener(task -> checkComplete.run());

        // 2. Xóa user node
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_USERS)
                .child(userId)
                .removeValue()
                .addOnCompleteListener(task -> checkComplete.run());

        // 3. Xóa progress
        firebaseManager.getDatabaseReference()
                .child(Constants.DB_PROGRESS)
                .child(userId)
                .removeValue()
                .addOnCompleteListener(task -> checkComplete.run());

        // 4. Xóa login history
        firebaseManager.getDatabaseReference()
                .child("loginHistory")
                .child(userId)
                .removeValue()
                .addOnCompleteListener(task -> checkComplete.run());
    }
}
