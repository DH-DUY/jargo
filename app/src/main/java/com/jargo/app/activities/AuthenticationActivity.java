package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * AuthenticationActivity - Màn hình chọn phương thức đăng nhập
 * Google Sign-In hoặc Continue as Guest
 */
public class AuthenticationActivity extends AppCompatActivity {

    private View btnGoogleSignIn;
    private View btnContinueAsGuest;
    private View btnEmailLogin;
    private View loadingView;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private SharedPrefsManager prefsManager;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        auth = FirebaseManager.getInstance().getAuth();
        prefsManager = SharedPrefsManager.getInstance(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Bind views
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnContinueAsGuest = findViewById(R.id.btnContinueAsGuest);
        btnEmailLogin = findViewById(R.id.btnEmailLogin);
        loadingView = findViewById(R.id.loadingView);

        // Setup Google Sign-In launcher
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleGoogleSignInResult(task);
                    } else {
                        loadingView.setVisibility(View.GONE);
                    }
                });

        // Button listeners
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        btnContinueAsGuest.setOnClickListener(v -> continueAsGuest());
        btnEmailLogin.setOnClickListener(v -> goToEmailLogin());
    }

    /**
     * Đăng nhập với Google
     */
    private void signInWithGoogle() {
        loadingView.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    /**
     * Xử lý kết quả Google Sign-In
     */
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            loadingView.setVisibility(View.GONE);
            Toast.makeText(this, "Google Sign-In thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xác thực với Firebase sử dụng Google token
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    loadingView.setVisibility(View.GONE);
                    
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Save user info
                            prefsManager.saveUserId(user.getUid());
                            prefsManager.saveUserEmail(user.getEmail());
                            if (user.getDisplayName() != null) {
                                prefsManager.saveUserName(user.getDisplayName());
                            }

                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            goToOnboarding();
                        }
                    } else {
                        Toast.makeText(this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Tiếp tục với tư cách khách
     */
    private void continueAsGuest() {
        // Guest ID đã được tạo trong SplashActivity
        goToOnboarding();
    }

    /**
     * Đi đến Email Login
     */
    private void goToEmailLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Đi đến Onboarding
     */
    private void goToOnboarding() {
        Intent intent;
        if (prefsManager.isFirstLaunch()) {
            intent = new Intent(this, OnboardingActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
