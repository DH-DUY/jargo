package com.jargo.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * DebugChallenge model - Bài tập debug code
 * Core feature của Jargo - học từ vựng IT qua việc tìm lỗi trong code
 */
public class DebugChallenge {
    
    private String challengeId;
    private String topicId;
    private String topic;              // Tên topic (Git, React, etc.)
    private String difficulty;         // easy, medium, hard
    private String title;              // Tiêu đề challenge
    private String description;        // Mô tả ngắn
    
    // Code content
    private String codeSnippet;        // Code có bug
    private String language;           // java, javascript, python, etc.
    private int bugLineNumber;         // Dòng chứa bug (optional, for highlighting)
    
    // Question & Answer
    private String question;           // Câu hỏi
    private String questionVi;         // Câu hỏi tiếng Việt
    private List<String> options;      // Các lựa chọn
    private int correctAnswerIndex;    // Index của đáp án đúng (0-based)
    private String explanation;        // Giải thích đáp án (English)
    private String explanationVi;      // Giải thích tiếng Việt
    
    // Vocabulary
    private List<String> vocabularyTerms;  // Từ vựng học được từ challenge này
    private String correctCode;            // Code đã sửa đúng (optional)
    
    // Rewards
    private int xpReward;              // XP nhận được khi hoàn thành
    private int bonusXp;               // Bonus XP nếu trả lời đúng lần đầu
    
    // Metadata
    private int orderIndex;            // Thứ tự trong topic
    private boolean isLocked;          // Có bị khóa không
    private long createdAt;
    private long updatedAt;

    // Empty constructor for Firebase
    public DebugChallenge() {
        this.options = new ArrayList<>();
        this.vocabularyTerms = new ArrayList<>();
    }

    // Full constructor
    public DebugChallenge(String challengeId, String topicId, String topic, 
                          String difficulty, String title, String codeSnippet,
                          String language, String question, List<String> options,
                          int correctAnswerIndex, String explanation, int xpReward) {
        this.challengeId = challengeId;
        this.topicId = topicId;
        this.topic = topic;
        this.difficulty = difficulty;
        this.title = title;
        this.codeSnippet = codeSnippet;
        this.language = language;
        this.question = question;
        this.options = options != null ? options : new ArrayList<>();
        this.correctAnswerIndex = correctAnswerIndex;
        this.explanation = explanation;
        this.xpReward = xpReward;
        this.vocabularyTerms = new ArrayList<>();
    }

    // ==================== GETTERS ====================
    
    public String getChallengeId() {
        return challengeId;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public String getLanguage() {
        return language;
    }

    public int getBugLineNumber() {
        return bugLineNumber;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionVi() {
        return questionVi;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getCorrectAnswer() {
        if (options != null && correctAnswerIndex >= 0 && correctAnswerIndex < options.size()) {
            return options.get(correctAnswerIndex);
        }
        return null;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getExplanationVi() {
        return explanationVi;
    }

    public List<String> getVocabularyTerms() {
        return vocabularyTerms;
    }

    public String getCorrectCode() {
        return correctCode;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getBonusXp() {
        return bonusXp;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    // ==================== SETTERS ====================

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setBugLineNumber(int bugLineNumber) {
        this.bugLineNumber = bugLineNumber;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setQuestionVi(String questionVi) {
        this.questionVi = questionVi;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setExplanationVi(String explanationVi) {
        this.explanationVi = explanationVi;
    }

    public void setVocabularyTerms(List<String> vocabularyTerms) {
        this.vocabularyTerms = vocabularyTerms;
    }

    public void setCorrectCode(String correctCode) {
        this.correctCode = correctCode;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public void setBonusXp(int bonusXp) {
        this.bonusXp = bonusXp;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Kiểm tra đáp án có đúng không
     */
    public boolean isCorrectAnswer(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }

    /**
     * Kiểm tra đáp án có đúng không (by text)
     */
    public boolean isCorrectAnswer(String selectedAnswer) {
        if (selectedAnswer == null || options == null) return false;
        String correctAnswer = getCorrectAnswer();
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(selectedAnswer.trim());
    }

    /**
     * Lấy tổng XP (base + bonus nếu đúng lần đầu)
     */
    public int getTotalXp(boolean isFirstAttempt) {
        return isFirstAttempt ? xpReward + bonusXp : xpReward;
    }

    /**
     * Lấy màu difficulty
     */
    public String getDifficultyColor() {
        if (difficulty == null) return "#4CAF50"; // default green
        switch (difficulty.toLowerCase()) {
            case "easy":
                return "#4CAF50"; // Green
            case "medium":
                return "#FF9800"; // Orange
            case "hard":
                return "#F44336"; // Red
            default:
                return "#4CAF50";
        }
    }

    /**
     * Lấy icon cho language
     */
    public String getLanguageIcon() {
        if (language == null) return "💻";
        switch (language.toLowerCase()) {
            case "java":
                return "☕";
            case "javascript":
            case "js":
                return "🟨";
            case "python":
                return "🐍";
            case "kotlin":
                return "🟣";
            case "swift":
                return "🍎";
            case "cpp":
            case "c++":
                return "⚙️";
            case "html":
                return "🌐";
            case "css":
                return "🎨";
            case "sql":
                return "🗃️";
            case "git":
                return "📦";
            default:
                return "💻";
        }
    }

    public void addOption(String option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
    }

    public void addVocabularyTerm(String term) {
        if (this.vocabularyTerms == null) {
            this.vocabularyTerms = new ArrayList<>();
        }
        this.vocabularyTerms.add(term);
    }

    @Override
    public String toString() {
        return "DebugChallenge{" +
                "challengeId='" + challengeId + '\'' +
                ", topic='" + topic + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
