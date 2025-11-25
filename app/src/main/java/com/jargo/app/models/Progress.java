package com.jargo.app.models;

/**
 * Progress model - Tiến độ học tập của user cho từng topic
 */
public class Progress {
    private String userId;
    private String topicId;
    private String lessonId;            // ID bài học
    private boolean completed;
    private int score;                  // Điểm số (%)
    private int quizScore;              // Điểm quiz
    private long lastAccessed;          // Timestamp lần truy cập cuối
    private long completedAt;           // Timestamp hoàn thành
    private int vocabularyMastered;     // Số từ vựng đã thuộc
    private int xp;                     // XP earned

    // Phương thức khởi tạo rỗng (bắt buộc cho Firebase)
    public Progress() {
    }

    // Phương thức khởi tạo đầy đủ
    public Progress(String userId, String topicId) {
        this.userId = userId;
        this.topicId = topicId;
        this.completed = false;
        this.score = 0;
        this.lastAccessed = System.currentTimeMillis();
        this.vocabularyMastered = 0;
    }

    // Phương thức lấy giá trị
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

    public String getLessonId() {
        return lessonId;
    }

    public int getQuizScore() {
        return quizScore;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public int getXp() {
        return xp;
    }

    // Phương thức thiết lập giá trị
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

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public void setQuizScore(int quizScore) {
        this.quizScore = quizScore;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    // Phương thức tiện ích
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
