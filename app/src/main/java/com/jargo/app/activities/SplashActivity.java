package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

        // Delay 2 giây rồi chuyển màn hình
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, Constants.SPLASH_DELAY_MS);
    }

    /**
     * Phương thức điều hướng đến màn hình tiếp theo
     */
    private void navigateToNextScreen() {
        Intent intent;

        // Kiểm tra đã hoàn thành onboarding chưa
        if (prefsManager.isFirstLaunch()) {
            // Lần đầu mở app → đi đến OnboardingActivity
            intent = new Intent(this, OnboardingActivity.class);
        } else {
            // Đã hoàn thành onboarding → đi đến MainActivity
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish(); // Đóng SplashActivity để không quay lại được
    }
}
