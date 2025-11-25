package com.jargo.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jargo.app.fragments.HomeFragment;
import com.jargo.app.fragments.ProgressFragment;
import com.jargo.app.fragments.ProfileFragment;

/**
 * MainActivity - Màn hình chính với Bottom Navigation
 * Quản lý 3 fragments: Home, Progress, Profile
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Load HomeFragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Xử lý Bottom Navigation item click
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_progress) {
                fragment = new ProgressFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            return fragment != null && loadFragment(fragment);
        });
    }

    /**
     * Load fragment vào container
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
