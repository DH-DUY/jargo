package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.utils.Constants;
import com.jargo.app.utils.SharedPrefsManager;
import java.util.UUID;

/**
 * SplashActivity - Màn hình chào đầu tiên khi mở app
 * Hiển thị logo JARGO trong 2 giây, sau đó chuyển sang:
 * - OnboardingActivity nếu lần đầu mở app
 * - MainActivity nếu đã hoàn thành onboarding
 */
public class SplashActivity extends AppCompatActivity {

    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefsManager = SharedPrefsManager.getInstance(this);

        // Tạo Guest ID nếu chưa có
        if (prefsManager.getUserId() == null) {
            String guestId = UUID.randomUUID().toString();
            prefsManager.saveUserId(guestId);
        }
        
        // Start animations
        startAnimations();

        // Delay 2 giây rồi chuyển màn hình
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, Constants.SPLASH_DELAY_MS);
    }
    
    /**
     * Start all splash animations
     */
    private void startAnimations() {
        // Logo pulse animation
        ImageView logo = findViewById(R.id.ivLogo);
        Animation pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        logo.startAnimation(pulseAnim);
        
        // Tagline fade in
        TextView tagline = findViewById(R.id.tvTagline);
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim.setStartOffset(300); // Start after 300ms
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tagline.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                tagline.setAlpha(1.0f);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        tagline.startAnimation(fadeInAnim);
        
        // Animated dots with wave effect
        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);
        
        Animation bounceAnim1 = AnimationUtils.loadAnimation(this, R.anim.bounce_dot);
        Animation bounceAnim2 = AnimationUtils.loadAnimation(this, R.anim.bounce_dot);
        Animation bounceAnim3 = AnimationUtils.loadAnimation(this, R.anim.bounce_dot);
        
        // Stagger with 400ms delay for smooth wave effect
        bounceAnim1.setStartOffset(500);
        bounceAnim2.setStartOffset(900);  // 400ms after dot1
        bounceAnim3.setStartOffset(1300); // 400ms after dot2
        
        dot1.startAnimation(bounceAnim1);
        dot2.startAnimation(bounceAnim2);
        dot3.startAnimation(bounceAnim3);
    }

    /**
     * Phương thức điều hướng đến màn hình tiếp theo
     */
    private void navigateToNextScreen() {
        Intent intent;

        // Kiểm tra user đã đăng nhập chưa
        if (prefsManager.isLoggedIn()) {
            // Đã đăng nhập → Kiểm tra onboarding
            if (prefsManager.isFirstLaunch()) {
                intent = new Intent(this, OnboardingActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
        } else {
            // Chưa đăng nhập → Đi đến màn hình chọn phương thức auth
            intent = new Intent(this, AuthenticationActivity.class);
        }

        startActivity(intent);
        finish(); // Đóng SplashActivity để không quay lại được
    }
}
