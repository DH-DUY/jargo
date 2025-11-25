package com.jargo.app.models;

/**
 * Progress model - Tiến độ học tập của user cho từng topic
 */
public class Progress {
    private String userId;
    private String topicId;
    private boolean completed;
    private int score;                  // Điểm số (%)
    private long lastAccessed;          // Timestamp lần truy cập cuối
    private int vocabularyMastered;     // Số từ vựng đã thuộc

    // Constructor rỗng
    public Progress() {
    }

    // Constructor đầy đủ
    public Progress(String userId, String topicId) {
        this.userId = userId;
        this.topicId = topicId;
        this.completed = false;
        this.score = 0;
        this.lastAccessed = System.currentTimeMillis();
        this.vocabularyMastered = 0;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getTopicId() {
        return topicId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getScore() {
        return score;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public int getVocabularyMastered() {
        return vocabularyMastered;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public void setVocabularyMastered(int vocabularyMastered) {
        this.vocabularyMastered = vocabularyMastered;
    }

    // Utility methods
    public void updateLastAccessed() {
        this.lastAccessed = System.currentTimeMillis();
    }

    public void incrementVocabularyMastered() {
        this.vocabularyMastered++;
    }

    public void markAsCompleted(int finalScore) {
        this.completed = true;
        this.score = finalScore;
        this.lastAccessed = System.currentTimeMillis();
    }

    public int getProgressPercentage(int totalVocabulary) {
        if (totalVocabulary == 0) return 0;
        return (vocabularyMastered * 100) / totalVocabulary;
    }
}
