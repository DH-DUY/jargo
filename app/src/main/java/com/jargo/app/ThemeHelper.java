package com.jargo.app;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Helper class for managing app theme (Light/Dark mode)
 */
public class ThemeHelper {
    
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    
    // Theme modes
    public static final int MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES;
    public static final int MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    
    /**
     * Apply saved theme on app start
     */
    public static void applyTheme(Context context) {
        int themeMode = getThemeMode(context);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
    
    /**
     * Set theme mode and save preference
     */
    public static void setThemeMode(Context context, int mode) {
        // Save preference
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
        
        // Apply immediately
        AppCompatDelegate.setDefaultNightMode(mode);
    }
    
    /**
     * Get current theme mode
     */
    public static int getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM); // Default: follow system
    }
    
    /**
     * Check if dark mode is currently active
     */
    public static boolean isDarkMode(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode 
            & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * Toggle between light and dark mode
     */
    public static void toggleTheme(Context context) {
        int currentMode = getThemeMode(context);
        int newMode;
        
        if (currentMode == MODE_DARK) {
            newMode = MODE_LIGHT;
        } else {
            newMode = MODE_DARK;
        }
        
        setThemeMode(context, newMode);
    }
    
    /**
     * Set to system default (follow device settings)
     */
    public static void setSystemDefault(Context context) {
        setThemeMode(context, MODE_SYSTEM);
    }
    
    /**
     * Get theme mode name for display
     */
    public static String getThemeModeName(Context context) {
        int mode = getThemeMode(context);
        
        if (mode == MODE_LIGHT) {
            return "Sáng";
        } else if (mode == MODE_DARK) {
            return "Tối";
        } else if (mode == MODE_SYSTEM) {
            return "Theo hệ thống";
        } else {
            return "Unknown";
        }
    }
}
