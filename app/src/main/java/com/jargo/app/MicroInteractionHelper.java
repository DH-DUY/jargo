package com.jargo.app;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Helper class for adding micro-interactions to views
 */
public class MicroInteractionHelper {

    /**
     * Add scale animation on touch (button press effect)
     */
    public static void addScaleEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    // Scale down
                    v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                    break;
                    
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    // Scale back to normal
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
                    break;
            }
            return false; // Don't consume the event
        });
    }

    /**
     * Add bounce animation on click
     */
    public static void addBounceEffect(View view) {
        view.setOnClickListener(v -> {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 0.9f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 0.9f);
            scaleDownX.setDuration(100);
            scaleDownY.setDuration(100);

            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);

            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(v, "scaleX", 1.05f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(v, "scaleY", 1.05f);
            scaleUpX.setDuration(100);
            scaleUpY.setDuration(100);

            AnimatorSet scaleUp = new AnimatorSet();
            scaleUp.play(scaleUpX).with(scaleUpY);

            ObjectAnimator scaleBackX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f);
            ObjectAnimator scaleBackY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f);
            scaleBackX.setDuration(100);
            scaleBackY.setDuration(100);

            AnimatorSet scaleBack = new AnimatorSet();
            scaleBack.play(scaleBackX).with(scaleBackY);

            AnimatorSet overall = new AnimatorSet();
            overall.play(scaleDown).before(scaleUp).before(scaleBack);
            overall.start();
        });
    }

    /**
     * Add shake animation (for errors)
     */
    public static void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 
            0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * Add pulse animation (draw attention)
     */
    public static void pulseView(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleX.setDuration(600);
        scaleY.setDuration(600);
        scaleX.setRepeatCount(2);
        scaleY.setRepeatCount(2);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
    }

    /**
     * Add rotation wiggle (for interactive elements)
     */
    public static void wiggleView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation",
            0f, -10f, 10f, -10f, 10f, -5f, 5f, 0f);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * Fade in animation
     */
    public static void fadeIn(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }

    /**
     * Fade out animation
     */
    public static void fadeOut(View view) {
        view.animate()
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> view.setVisibility(View.GONE))
            .start();
    }

    /**
     * Slide in from bottom
     */
    public static void slideInFromBottom(View view) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
            .translationY(0)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }

    /**
     * Slide out to bottom
     */
    public static void slideOutToBottom(View view) {
        view.animate()
            .translationY(view.getHeight())
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> view.setVisibility(View.GONE))
            .start();
    }

    /**
     * Add ripple effect programmatically (for older APIs)
     */
    public static void addRippleEffect(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.content.res.TypedArray typedArray = view.getContext().obtainStyledAttributes(
                new int[]{android.R.attr.selectableItemBackground});
            int drawable = typedArray.getResourceId(0, 0);
            typedArray.recycle();
            view.setBackgroundResource(drawable);
        }
    }

    /**
     * Success checkmark animation (scale + fade)
     */
    public static void showSuccessCheckmark(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(400)
            .setInterpolator(new OvershootInterpolator())
            .start();
    }
}
