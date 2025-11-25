package com.jargo.app;

import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;

/**
 * Helper class for showing Material Design Snackbars with custom styling
 */
public class SnackbarHelper {

    // Colors for different states
    private static final int COLOR_SUCCESS = 0xFF4CAF50; // Green
    private static final int COLOR_ERROR = 0xFFF44336;   // Red
    private static final int COLOR_INFO = 0xFF2196F3;    // Blue
    private static final int COLOR_WARNING = 0xFFFF9800; // Orange

    /**
     * Show success Snackbar (green)
     */
    public static void showSuccess(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        customizeSnackbar(snackbar, COLOR_SUCCESS);
        snackbar.show();
    }

    /**
     * Show success Snackbar with action button
     */
    public static void showSuccess(View view, String message, String actionText, View.OnClickListener action) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        customizeSnackbar(snackbar, COLOR_SUCCESS);
        snackbar.setAction(actionText, action);
        snackbar.setActionTextColor(0xFFFFFFFF);
        snackbar.show();
    }

    /**
     * Show error Snackbar (red)
     */
    public static void showError(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        customizeSnackbar(snackbar, COLOR_ERROR);
        snackbar.show();
    }

    /**
     * Show error Snackbar with retry action
     */
    public static void showError(View view, String message, View.OnClickListener retryAction) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        customizeSnackbar(snackbar, COLOR_ERROR);
        snackbar.setAction("Thử lại", retryAction);
        snackbar.setActionTextColor(0xFFFFFFFF);
        snackbar.show();
    }

    /**
     * Show info Snackbar (blue)
     */
    public static void showInfo(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        customizeSnackbar(snackbar, COLOR_INFO);
        snackbar.show();
    }

    /**
     * Show warning Snackbar (orange)
     */
    public static void showWarning(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        customizeSnackbar(snackbar, COLOR_WARNING);
        snackbar.show();
    }

    /**
     * Customize Snackbar appearance
     */
    private static void customizeSnackbar(Snackbar snackbar, int backgroundColor) {
        // Set background color
        snackbar.setBackgroundTint(backgroundColor);
        
        // Set text color to white
        snackbar.setTextColor(0xFFFFFFFF);
        
        // Add elevation for modern look
        View snackbarView = snackbar.getView();
        snackbarView.setElevation(8f);
        
        // Add rounded corners
        snackbarView.setTranslationZ(8f);
    }

    /**
     * Show custom Snackbar with icon prefix
     */
    public static void showWithIcon(View view, String icon, String message, int backgroundColor) {
        String fullMessage = icon + "  " + message;
        Snackbar snackbar = Snackbar.make(view, fullMessage, Snackbar.LENGTH_SHORT);
        customizeSnackbar(snackbar, backgroundColor);
        snackbar.show();
    }

    // Convenience methods with icons
    public static void showSuccessWithIcon(View view, String message) {
        showWithIcon(view, "✓", message, COLOR_SUCCESS);
    }

    public static void showErrorWithIcon(View view, String message) {
        showWithIcon(view, "✗", message, COLOR_ERROR);
    }

    public static void showInfoWithIcon(View view, String message) {
        showWithIcon(view, "ℹ", message, COLOR_INFO);
    }

    public static void showWarningWithIcon(View view, String message) {
        showWithIcon(view, "⚠", message, COLOR_WARNING);
    }
}
