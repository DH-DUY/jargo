package com.jargo.app;

import android.app.Application;

/**
 * JargoApplication - Application class để init app-wide settings
 */
public class JargoApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Apply theme khi app khởi động
        ThemeHelper.applyTheme(this);
    }
}
