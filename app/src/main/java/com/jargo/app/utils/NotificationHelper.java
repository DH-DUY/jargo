package com.jargo.app.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.jargo.app.HapticHelper;
import com.jargo.app.R;

/**
 * NotificationHelper - Custom notification system
 * Hiển thị notification đẹp với animation thay vì Toast
 */
public class NotificationHelper {

    private static final int DURATION_MS = 2000; // 2 seconds
    
    /**
     * Show success notification (correct answer)
     */
    public static void showSuccess(Activity activity, String message, int xpReward) {
        showNotification(activity, message, xpReward, NotificationType.SUCCESS);
    }
    
    /**
     * Show error notification (wrong answer)
     */
    public static void showError(Activity activity, String message, String correctAnswer) {
        showNotification(activity, message + "\nĐáp án: " + correctAnswer, 0, NotificationType.ERROR);
    }
    
    /**
     * Show info notification
     */
    public static void showInfo(Activity activity, String message) {
        showNotification(activity, message, 0, NotificationType.INFO);
    }
    
    /**
     * Show warning notification
     */
    public static void showWarning(Activity activity, String message) {
        showNotification(activity, message, 0, NotificationType.WARNING);
    }
    
    /**
     * Notification types
     */
    private enum NotificationType {
        SUCCESS, ERROR, INFO, WARNING
    }
    
    /**
     * Show custom notification
     */
    private static void showNotification(Activity activity, String message, int xpReward, NotificationType type) {
        // Inflate layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View notificationView = inflater.inflate(R.layout.custom_notification, null);
        
        // Get root view
        ViewGroup rootView = activity.findViewById(android.R.id.content);
        
        // Setup notification container
        FrameLayout container = new FrameLayout(activity);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.gravity = Gravity.TOP;
        containerParams.topMargin = 80; // Below status bar + toolbar
        container.setLayoutParams(containerParams);
        container.addView(notificationView);
        
        // Setup views
        CardView card = notificationView.findViewById(R.id.notificationCard);
        ImageView ivIcon = notificationView.findViewById(R.id.ivIcon);
        TextView tvMessage = notificationView.findViewById(R.id.tvMessage);
        TextView tvXP = notificationView.findViewById(R.id.tvXP);
        ViewGroup background = (ViewGroup) card.getChildAt(0);
        
        // Set content
        tvMessage.setText(message);
        
        // Style based on notification type
        switch (type) {
            case SUCCESS:
                ivIcon.setImageResource(R.drawable.ic_check_circle);
                background.setBackgroundResource(R.drawable.notification_background_success);
                tvXP.setText("+" + xpReward + " XP");
                tvXP.setVisibility(View.VISIBLE);
                HapticHelper.success(activity);
                break;
                
            case ERROR:
                ivIcon.setImageResource(R.drawable.ic_error_circle);
                background.setBackgroundResource(R.drawable.notification_background_error);
                tvXP.setVisibility(View.GONE);
                HapticHelper.error(activity);
                break;
                
            case INFO:
                ivIcon.setImageResource(R.drawable.ic_info_circle);
                background.setBackgroundResource(R.drawable.notification_background_info);
                tvXP.setVisibility(View.GONE);
                HapticHelper.heavyImpact(activity);
                break;
                
            case WARNING:
                ivIcon.setImageResource(R.drawable.ic_warning_circle);
                background.setBackgroundResource(R.drawable.notification_background_warning);
                tvXP.setVisibility(View.GONE);
                HapticHelper.heavyImpact(activity);
                break;
        }
        
        // Add to root view
        rootView.addView(container);
        
        // Slide down animation
        Animation slideDown = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
        container.startAnimation(slideDown);
        
        // Auto dismiss after duration
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Slide up animation
            Animation slideUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    rootView.removeView(container);
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            container.startAnimation(slideUp);
        }, DURATION_MS);
    }
}
