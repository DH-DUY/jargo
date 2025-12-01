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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.repositories.LoginHistoryRepository;
import com.jargo.app.utils.FirebaseManager;
import com.jargo.app.utils.NotificationHelper;
import com.jargo.app.utils.SharedPrefsManager;
import com.jargo.app.activities.OnboardingActivity;

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
    private LoginHistoryRepository loginHistoryRepository;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseManager.getInstance().getAuth();
        prefsManager = SharedPrefsManager.getInstance(this);
        loginHistoryRepository = LoginHistoryRepository.getInstance();

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
            NotificationHelper.showWarning(this, "Google Sign-In thất bại: " + e.getMessage());
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
                            // Check blacklist first, then proceed
                            checkBlacklistAndProceed(user, "google");
                        }
                    } else {
                        // Log login failure
                        String tempUserId = etEmail.getText().toString(); // Fallback
                        loginHistoryRepository.logLogin(this, tempUserId, "google", false, null);
                        
                        NotificationHelper.showError(this, "Xác thực thất bại", "Vui lòng thử lại");
                    }
                });
    }

    /**
     * Đăng nhập
     */
    private void login() {
        String username = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();

        // Validate
        if (username.isEmpty() || password.isEmpty()) {
            NotificationHelper.showWarning(this, getString(R.string.login_error_empty));
            return;
        }

        // Validate username format (3-20 chars, alphanumeric + underscore)
        if (!isValidUsername(username)) {
            NotificationHelper.showWarning(this, getString(R.string.login_error_invalid_username));
            return;
        }

        // Convert username to fake email for Firebase Auth
        String fakeEmail = usernameToEmail(username);

        // Show loading
        loadingView.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Firebase login with fake email
        auth.signInWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener(this, task -> {
                    loadingView.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Check blacklist first, then proceed
                            checkBlacklistAndProceed(user, "email");
                        }
                    } else {
                        // Login failed - Log with username as fallback ID
                        loginHistoryRepository.logLogin(this, username, "username", false, null);
                        
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
        // Sign out khỏi Firebase để đảm bảo guest mode
        auth.signOut();
        
        // Đảm bảo không set isLoggedIn = true
        prefsManager.setLoggedIn(false);
        
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

    /**
     * Convert username to fake email for Firebase Auth
     */
    private String usernameToEmail(String username) {
        return username.toLowerCase() + "@jargo.app";
    }

    /**
     * Validate username format
     * - 3-20 characters
     * - Only lowercase letters, numbers, and underscore
     */
    private boolean isValidUsername(String username) {
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        return username.matches("^[a-z0-9_]+$");
    }

    /**
     * Check if user is blacklisted, then proceed with login or create new account
     */
    private void checkBlacklistAndProceed(FirebaseUser user, String loginMethod) {
        DatabaseReference blacklistRef = FirebaseManager.getInstance()
                .getDatabaseReference()
                .child("deletedUsers")
                .child(user.getUid());
        
        blacklistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User was deleted - allow creating new account
                    handleDeletedUserReturning(user, loginMethod);
                } else {
                    // User is ok - proceed with login
                    proceedWithLogin(user, loginMethod);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                // On error, allow login (fail-open)
                proceedWithLogin(user, loginMethod);
            }
        });
    }
    
    /**
     * Proceed with login after blacklist check passed
     */
    private void proceedWithLogin(FirebaseUser user, String loginMethod) {
        // Save user info
        prefsManager.saveUserId(user.getUid());
        prefsManager.saveUserEmail(user.getEmail());
        if (user.getDisplayName() != null) {
            prefsManager.saveUserName(user.getDisplayName());
        }
        
        // Mark as logged in (but don't set firstLaunch yet)
        prefsManager.setLoggedIn(true);

        // Log login success
        loginHistoryRepository.logLogin(this, user.getUid(), loginMethod, true, null);

        NotificationHelper.showInfo(this, getString(R.string.login_success));
        
        // Load user data from Firebase and decide where to go
        loadUserProfileAndNavigate(user.getUid());
    }
    
    /**
     * Handle deleted user returning - allow creating new account
     */
    private void handleDeletedUserReturning(FirebaseUser user, String loginMethod) {
        // Remove from blacklist
        FirebaseManager.getInstance()
                .getDatabaseReference()
                .child("deletedUsers")
                .child(user.getUid())
                .removeValue();
        
        // CRITICAL: Clear ALL local data first to avoid loading old cache
        prefsManager.clearAll();
        
        // Save basic info for new account
        prefsManager.saveUserId(user.getUid());
        prefsManager.saveUserEmail(user.getEmail());
        if (user.getDisplayName() != null) {
            prefsManager.saveUserName(user.getDisplayName());
        }
        
        // Mark as logged in but NOT finished onboarding (will go to onboarding)
        prefsManager.setLoggedIn(true);
        prefsManager.setFirstLaunch(true); // Trigger onboarding
        
        // Log login
        loginHistoryRepository.logLogin(this, user.getUid(), loginMethod, true, null);
        
        // Go directly to onboarding
        goToOnboarding();
    }
    
    /**
     * Go to onboarding
     */
    private void goToOnboarding() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Load user profile data and navigate to appropriate screen
     */
    private void loadUserProfileAndNavigate(String userId) {
        DatabaseReference userRef = FirebaseManager.getInstance()
                .getDatabaseReference()
                .child("users")
                .child(userId);
        
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User has data - existing user with profile
                    // Load field
                    String field = snapshot.child("field").getValue(String.class);
                    if (field != null && !field.isEmpty()) {
                        prefsManager.saveUserField(field);
                    }
                    
                    // Load level
                    String level = snapshot.child("level").getValue(String.class);
                    if (level != null && !level.isEmpty()) {
                        prefsManager.saveUserLevel(level);
                    }
                    
                    // User has completed onboarding before
                    prefsManager.setFirstLaunch(false);
                    
                    // Go to Home
                    goToHome();
                } else {
                    // User has NO data - new account or deleted account
                    // Need to go through onboarding
                    prefsManager.setFirstLaunch(true);
                    
                    // Redirect to onboarding
                    goToOnboarding();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                // On error, assume new user and go to onboarding
                prefsManager.setFirstLaunch(true);
                goToOnboarding();
            }
        });
    }
}
