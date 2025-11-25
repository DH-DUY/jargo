package com.jargo.app.models;

/**
 * User model - Thông tin người dùng
 */
public class User {
    private String userId;
    private String name;
    private String email;
    private String field;           // IT, Medical, Economics
    private String level;           // beginner, intermediate, professional
    private int totalXP;
    private int streak;
    private long createdAt;

    // Constructor rỗng (Firebase yêu cầu)
    public User() {
    }

    // Constructor đầy đủ
    public User(String userId, String name, String email, String field, String level) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.field = field;
        this.level = level;
        this.totalXP = 0;
        this.streak = 0;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getField() {
        return field;
    }

    public String getLevel() {
        return level;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public int getStreak() {
        return streak;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public void addXP(int xp) {
        this.totalXP += xp;
    }

    public void incrementStreak() {
        this.streak++;
    }

    public void resetStreak() {
        this.streak = 0;
    }
}
