package com.jargo.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.navigation.NavigationBarView;
import com.jargo.app.fragments.HomeFragment;
import com.jargo.app.fragments.ProgressFragment;
import com.jargo.app.fragments.ProfileFragment;

/**
 * MainActivity - Màn hình chính với Responsive Navigation
 * Mobile: Bottom Navigation | Tablet: Navigation Rail
 * Quản lý 3 fragments: Home, Progress, Profile
 */
public class MainActivity extends AppCompatActivity {

    private NavigationBarView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup navigation: Bottom Navigation (mobile) hoặc Navigation Rail (tablet)
        setupNavigation();

        // Load HomeFragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    /**
     * Setup navigation view dựa trên layout (responsive)
     * Mobile: BottomNavigationView
     * Tablet: NavigationRailView
     */
    private void setupNavigation() {
        // Try bottom navigation (mobile layout)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            navigationView = bottomNav;
        } else {
            // Try navigation rail (tablet layout)
            NavigationRailView navRail = findViewById(R.id.navigationRail);
            if (navRail != null) {
                navigationView = navRail;
            }
        }

        // Setup item selected listener
        if (navigationView != null) {
            navigationView.setOnItemSelectedListener(item -> {
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
