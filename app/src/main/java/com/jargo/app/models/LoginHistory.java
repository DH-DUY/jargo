package com.jargo.app.models;

/**
 * LoginHistory - Model lưu lịch sử đăng nhập của user
 */
public class LoginHistory {
    private String id;              // Unique ID (timestamp hoặc UUID)
    private String userId;          // User ID
    private long timestamp;         // Thời gian đăng nhập
    private String deviceInfo;      // Thông tin thiết bị
    private String loginMethod;     // Phương thức đăng nhập (email, google, etc)
    private String appVersion;      // Phiên bản app
    private boolean isSuccess;      // Đăng nhập thành công hay thất bại

    // Constructor rỗng (bắt buộc cho Firebase)
    public LoginHistory() {
    }

    public LoginHistory(String userId, long timestamp, String deviceInfo, String loginMethod, String appVersion, boolean isSuccess) {
        this.id = String.valueOf(timestamp); // Dùng timestamp làm ID
        this.userId = userId;
        this.timestamp = timestamp;
        this.deviceInfo = deviceInfo;
        this.loginMethod = loginMethod;
        this.appVersion = appVersion;
        this.isSuccess = isSuccess;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
