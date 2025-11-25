package com.jargo.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPrefsManager - Quản lý SharedPreferences
 */
public class SharedPrefsManager {
    
    private static SharedPrefsManager instance;
    private final SharedPreferences prefs;
    
    private SharedPrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context);
        }
        return instance;
    }
    
    // Phương thức lưu dữ liệu
    public void saveString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }
    
    public void saveInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }
    
    public void saveBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }
    
    public void saveLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }
    
    // Phương thức lấy dữ liệu
    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }
    
    public long getLong(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }
    
    // Phương thức riêng cho người dùng
    public void saveUserId(String userId) {
        saveString(Constants.PREF_USER_ID, userId);
    }
    
    public String getUserId() {
        return getString(Constants.PREF_USER_ID, null);
    }
    
    public void saveUserName(String name) {
        saveString(Constants.PREF_USER_NAME, name);
    }
    
    public String getUserName() {
        return getString(Constants.PREF_USER_NAME, "");
    }
    
    public void saveUserEmail(String email) {
        saveString(Constants.PREF_USER_EMAIL, email);
    }
    
    public String getUserEmail() {
        return getString(Constants.PREF_USER_EMAIL, "");
    }
    
    public void saveUserField(String field) {
        saveString(Constants.PREF_USER_FIELD, field);
    }
    
    public String getUserField() {
        return getString(Constants.PREF_USER_FIELD, "");
    }
    
    public void saveUserLevel(String level) {
        saveString(Constants.PREF_USER_LEVEL, level);
    }
    
    public String getUserLevel() {
        return getString(Constants.PREF_USER_LEVEL, "");
    }
    
    public void setFirstLaunch(boolean isFirst) {
        saveBoolean(Constants.PREF_IS_FIRST_LAUNCH, isFirst);
    }
    
    public boolean isFirstLaunch() {
        return getBoolean(Constants.PREF_IS_FIRST_LAUNCH, true);
    }
    
    public void setLoggedIn(boolean isLoggedIn) {
        saveBoolean(Constants.PREF_IS_LOGGED_IN, isLoggedIn);
    }
    
    public boolean isLoggedIn() {
        return getBoolean(Constants.PREF_IS_LOGGED_IN, false);
    }
    
    // Phương thức xóa toàn bộ dữ liệu
    public void clearAll() {
        prefs.edit().clear().apply();
    }
    
    // Phương thức đăng xuất - xóa dữ liệu người dùng nhưng giữ cài đặt ứng dụng
    public void logout() {
        prefs.edit()
            .remove(Constants.PREF_USER_ID)
            .remove(Constants.PREF_USER_NAME)
            .remove(Constants.PREF_USER_EMAIL)
            .remove(Constants.PREF_USER_FIELD)
            .remove(Constants.PREF_USER_LEVEL)
            .putBoolean(Constants.PREF_IS_LOGGED_IN, false)
            .apply();
    }
}
