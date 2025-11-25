package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * RegisterActivity - Màn hình đăng ký tài khoản
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnGoogleSignUp;
    private Button btnRegister;
    private TextView tvLoginLink;
    private View loadingView;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private SharedPrefsManager prefsManager;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseManager.getInstance().getAuth();
        prefsManager = SharedPrefsManager.getInstance(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Bind views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
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
        btnGoogleSignUp.setOnClickListener(v -> signUpWithGoogle());
        btnRegister.setOnClickListener(v -> register());
        tvLoginLink.setOnClickListener(v -> goToLogin());
    }

    /**
     * Đăng ký với Google
     */
    private void signUpWithGoogle() {
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
            Toast.makeText(this, "Google Sign-Up thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                            goToHome();
                        }
                    } else {
                        Toast.makeText(this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Đăng ký tài khoản
     */
    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || 
            TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, R.string.register_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() < Constants.MIN_NAME_LENGTH) {
            Toast.makeText(this, R.string.register_error_name_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.login_error_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, R.string.register_error_password_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.register_error_password_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        loadingView.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Firebase register
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Register success - update display name
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        loadingView.setVisibility(View.GONE);
                                        btnRegister.setEnabled(true);

                                        if (profileTask.isSuccessful()) {
                                            // Save user info
                                            prefsManager.saveUserId(user.getUid());
                                            prefsManager.saveUserName(name);
                                            prefsManager.saveUserEmail(email);

                                            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                                            goToHome();
                                        }
                                    });
                        }
                    } else {
                        // Register failed
                        loadingView.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        
                        String errorMsg = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Đăng ký thất bại";
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Chuyển sang Login
     */
    private void goToLogin() {
        finish();
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
