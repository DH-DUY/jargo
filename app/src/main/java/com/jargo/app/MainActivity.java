package com.jargo.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * MainActivity - Màn hình chính với Responsive Navigation
 * Mobile: Bottom Navigation | Tablet: Navigation Rail
 * Quản lý 3 fragments: Home, Progress, Profile
 * Sử dụng Navigation Component cho fragment navigation
 */
public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Navigation Component
        setupNavController();

        // Setup Bottom Navigation with NavController
        setupBottomNavigation();
    }

    /**
     * Setup NavController từ NavHostFragment
     */
    private void setupNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    /**
     * Setup Bottom Navigation hoặc Navigation Rail với NavController
     * NavigationUI tự động handle navigation
     */
    private void setupBottomNavigation() {
        if (navController == null) {
            return;
        }

        // Try bottom navigation (mobile layout)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            NavigationUI.setupWithNavController(bottomNav, navController);
            return;
        }

        // Try navigation rail (tablet layout)
        NavigationRailView navRail = findViewById(R.id.navigationRail);
        if (navRail != null) {
            NavigationUI.setupWithNavController(navRail, navController);
        }
    }

    /**
     * Support back navigation
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() 
                || super.onSupportNavigateUp();
    }
}
