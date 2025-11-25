package com.jargo.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

/**
 * Helper class for showing Lottie animations
 * Animations should be placed in app/src/main/res/raw/
 */
public class LottieHelper {

    /**
     * Show success animation (checkmark)
     * Download from: https://lottiefiles.com/animations/success
     */
    public static void showSuccess(Context context, ViewGroup container) {
        showAnimation(context, container, "success.json", false, 1500);
    }

    /**
     * Show celebration/confetti animation
     * Download from: https://lottiefiles.com/animations/confetti
     */
    public static void showCelebration(Context context, ViewGroup container) {
        showAnimation(context, container, "celebration.json", false, 3000);
    }

    /**
     * Show error animation (X mark or shake)
     * Download from: https://lottiefiles.com/animations/error
     */
    public static void showError(Context context, ViewGroup container) {
        showAnimation(context, container, "error.json", false, 1500);
    }

    /**
     * Show loading animation
     * Download from: https://lottiefiles.com/animations/loading
     */
    public static LottieAnimationView showLoading(Context context, ViewGroup container) {
        return showAnimation(context, container, "loading.json", true, 0);
    }

    /**
     * Show trophy/achievement animation
     * Download from: https://lottiefiles.com/animations/trophy
     */
    public static void showTrophy(Context context, ViewGroup container) {
        showAnimation(context, container, "trophy.json", false, 2500);
    }

    /**
     * Generic method to show Lottie animation
     */
    private static LottieAnimationView showAnimation(Context context, ViewGroup container, 
                                                     String fileName, boolean loop, long duration) {
        LottieAnimationView animationView = new LottieAnimationView(context);
        
        // Set animation from raw resource
        try {
            animationView.setAnimation("raw/" + fileName.replace(".json", ""));
        } catch (Exception e) {
            // Fallback - animation file not found
            // You can show a simple vector animation instead
            return null;
        }
        
        // Configure animation
        if (loop) {
            animationView.setRepeatCount(LottieDrawable.INFINITE);
        } else {
            animationView.setRepeatCount(0);
        }
        
        // Set size
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        animationView.setLayoutParams(params);
        
        // Add to container
        container.addView(animationView);
        
        // Play animation
        animationView.playAnimation();
        
        // Remove after duration (if not looping)
        if (!loop && duration > 0) {
            animationView.postDelayed(() -> {
                container.removeView(animationView);
            }, duration);
        }
        
        return animationView;
    }

    /**
     * Stop and remove loading animation
     */
    public static void hideLoading(LottieAnimationView animationView, ViewGroup container) {
        if (animationView != null) {
            animationView.cancelAnimation();
            container.removeView(animationView);
        }
    }

    /**
     * Show inline Lottie animation in an ImageView-like way
     */
    public static void showInline(LottieAnimationView lottieView, String fileName, boolean loop) {
        try {
            lottieView.setAnimation("raw/" + fileName.replace(".json", ""));
            lottieView.setRepeatCount(loop ? LottieDrawable.INFINITE : 0);
            lottieView.playAnimation();
        } catch (Exception e) {
            // Animation file not found
            e.printStackTrace();
        }
    }
}
