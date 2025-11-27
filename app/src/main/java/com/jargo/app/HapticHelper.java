package com.jargo.app;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;

/**
 * Helper class for providing haptic feedback
 */
public class HapticHelper {

    /**
     * Light tap feedback (button press)
     */
    public static void lightTap(View view) {
        view.performHapticFeedback(
            android.view.HapticFeedbackConstants.VIRTUAL_KEY,
            android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );
    }

    /**
     * Medium impact feedback (toggle, selection)
     */
    public static void mediumImpact(View view) {
        view.performHapticFeedback(
            android.view.HapticFeedbackConstants.KEYBOARD_TAP,
            android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );
    }

    /**
     * Heavy impact feedback (important action)
     */
    public static void heavyImpact(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrateCompat(vibrator, 50);
            }
        }
    }

    /**
     * Success vibration pattern (✓ animation)
     */
    public static void success(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 50, 50, 50}; // tick-tick pattern
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                vibrateCompat(vibrator, pattern, -1);
            }
        }
    }

    /**
     * Error vibration pattern (✗ animation)
     */
    public static void error(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 100, 50, 100}; // bzz-bzz pattern
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                vibrateCompat(vibrator, pattern, -1);
            }
        }
    }

    /**
     * Celebration vibration (confetti, achievement)
     */
    public static void celebration(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 30, 30, 30, 30, 50, 50, 100}; // crescendo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                vibrateCompat(vibrator, pattern, -1);
            }
        }
    }

    /**
     * Long press feedback
     */
    public static void longPress(View view) {
        view.performHapticFeedback(
            android.view.HapticFeedbackConstants.LONG_PRESS,
            android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );
    }

    /**
     * Selection change feedback (swipe, scroll snap)
     */
    public static void selectionChange(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(
                android.view.HapticFeedbackConstants.GESTURE_START
            );
        } else {
            lightTap(view);
        }
    }

    /**
     * Get Vibrator instance with proper API level handling
     */
    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return vibratorManager != null ? vibratorManager.getDefaultVibrator() : null;
        } else {
            return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    /**
     * Vibrate with compatibility for older APIs
     * Suppresses deprecation warning as we're handling it properly
     */
    @SuppressWarnings("deprecation")
    private static void vibrateCompat(Vibrator vibrator, long milliseconds) {
        vibrator.vibrate(milliseconds);
    }

    /**
     * Vibrate with pattern - compatibility for older APIs
     * Suppresses deprecation warning as we're handling it properly
     */
    @SuppressWarnings("deprecation")
    private static void vibrateCompat(Vibrator vibrator, long[] pattern, int repeat) {
        vibrator.vibrate(pattern, repeat);
    }
}
