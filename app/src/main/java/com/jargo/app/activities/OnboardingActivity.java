package com.jargo.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jargo.app.MainActivity;
import com.jargo.app.R;
import com.jargo.app.adapters.OnboardingPagerAdapter;
import com.jargo.app.utils.SharedPrefsManager;

/**
 * OnboardingActivity - Màn hình giới thiệu app và chọn chuyên ngành/trình độ
 * Sử dụng ViewPager2 với 3 fragments:
 * 1. Welcome - Chào mừng
 * 2. Field Selection - Chọn chuyên ngành
 * 3. Level Selection - Chọn trình độ
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnNext;
    private Button btnSkip;
    
    private OnboardingPagerAdapter pagerAdapter;
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        prefsManager = SharedPrefsManager.getInstance(this);

        // Khởi tạo views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        // Setup ViewPager2
        pagerAdapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Không cần text cho tabs, chỉ hiển thị dots
        }).attach();

        // Setup button listeners
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pagerAdapter.getItemCount() - 1) {
                // Chuyển đến trang tiếp theo
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                // Trang cuối cùng → Hoàn thành onboarding
                completeOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> completeOnboarding());

        // Theo dõi thay đổi trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtons(position);
            }
        });

        // Update buttons cho trang đầu tiên
        updateButtons(0);
    }

    /**
     * Cập nhật text và visibility của buttons theo trang hiện tại
     */
    private void updateButtons(int position) {
        if (position == pagerAdapter.getItemCount() - 1) {
            // Trang cuối cùng
            btnNext.setText(R.string.btn_finish);
            btnSkip.setVisibility(View.GONE);
        } else {
            // Các trang khác
            btnNext.setText(R.string.btn_next);
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hoàn thành onboarding và chuyển đến MainActivity
     */
    private void completeOnboarding() {
        // Đánh dấu đã hoàn thành onboarding
        prefsManager.setFirstLaunch(false);

        // Chuyển đến MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Phương thức public để fragments có thể gọi khi hoàn thành
     */
    public void onOnboardingComplete() {
        completeOnboarding();
    }
}
