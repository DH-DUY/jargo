package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.NotificationHelper;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * LoginActivity - Màn hình đăng nhập
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private View btnGoogleSignIn;
    private View btnLogin;
    private TextView tvRegisterLink, tvSkip;
    private View loadingView;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private SharedPrefsManager prefsManager;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseManager.getInstance().getAuth();
        prefsManager = SharedPrefsManager.getInstance(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Bind views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvSkip = findViewById(R.id.tvSkip);
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
        btnLogin.setOnClickListener(v -> login());
        tvRegisterLink.setOnClickListener(v -> goToRegister());
        tvSkip.setOnClickListener(v -> skipLogin());
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
            NotificationHelper.showError(this, "Google Sign-In thất bại", e.getMessage());
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
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Save user info
                            prefsManager.saveUserId(user.getUid());
                            prefsManager.saveUserEmail(user.getEmail());
                            if (user.getDisplayName() != null) {
                                prefsManager.saveUserName(user.getDisplayName());
                            }

                            NotificationHelper.showInfo(this, getString(R.string.login_success));
                            goToHome();
                        }
                    } else {
                        NotificationHelper.showError(this, "Xác thực thất bại", "Vui lòng thử lại");
                    }
                });
    }

    /**
     * Đăng nhập
     */
    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            NotificationHelper.showWarning(this, getString(R.string.login_error_empty));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            NotificationHelper.showWarning(this, getString(R.string.login_error_invalid_email));
            return;
        }

        // Show loading
        loadingView.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Firebase login
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loadingView.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Save user info
                            prefsManager.saveUserId(user.getUid());
                            prefsManager.saveUserEmail(user.getEmail());
                            if (user.getDisplayName() != null) {
                                prefsManager.saveUserName(user.getDisplayName());
                            }

                            NotificationHelper.showInfo(this, getString(R.string.login_success));
                            goToHome();
                        }
                    } else {
                        // Login failed
                        String errorMsg = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Đăng nhập thất bại";
                        NotificationHelper.showError(this, "Đăng nhập thất bại", errorMsg);
                    }
                });
    }

    /**
     * Chuyển sang Register
     */
    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Bỏ qua đăng nhập (Continue as Guest)
     */
    private void skipLogin() {
        goToHome();
    }

    /**
     * Đi đến Home
     */
    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
