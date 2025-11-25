package com.jargo.app.utils;

/**
 * XPCalculator - Tính toán điểm XP cho các hoạt động học tập
 */
public class XPCalculator {

    // XP rewards cho các hoạt động
    public static final int XP_LEARN_VOCABULARY = 5;
    public static final int XP_COMPLETE_LESSON = 20;
    public static final int XP_QUIZ_CORRECT_ANSWER = 10;
    public static final int XP_QUIZ_PERFECT_BONUS = 50;
    public static final int XP_DAILY_STREAK = 10;
    public static final int XP_WEEKLY_STREAK_BONUS = 25;

    /**
     * Tính XP cho việc học từ vựng
     */
    public static int calculateVocabularyXP(int vocabularyCount) {
        return vocabularyCount * XP_LEARN_VOCABULARY;
    }

    /**
     * Tính XP cho quiz
     */
    public static int calculateQuizXP(int correctAnswers, int totalQuestions) {
        int baseXP = correctAnswers * XP_QUIZ_CORRECT_ANSWER;
        
        // Bonus nếu làm đúng 100%
        if (correctAnswers == totalQuestions && totalQuestions > 0) {
            baseXP += XP_QUIZ_PERFECT_BONUS;
        }
        
        return baseXP;
    }

    /**
     * Tính XP cho streak
     */
    public static int calculateStreakXP(int streakDays) {
        int xp = 0;
        
        // Base XP cho streak
        if (streakDays > 0) {
            xp += XP_DAILY_STREAK;
        }
        
        // Bonus mỗi 7 ngày
        if (streakDays > 0 && streakDays % 7 == 0) {
            xp += XP_WEEKLY_STREAK_BONUS;
        }
        
        return xp;
    }

    /**
     * Tính tổng XP cho hoàn thành lesson (bao gồm vocab + quiz)
     */
    public static int calculateLessonCompleteXP(int vocabularyCount, 
                                                  int correctAnswers, 
                                                  int totalQuestions) {
        int vocabXP = calculateVocabularyXP(vocabularyCount);
        int quizXP = calculateQuizXP(correctAnswers, totalQuestions);
        int lessonXP = XP_COMPLETE_LESSON;
        
        return vocabXP + quizXP + lessonXP;
    }

    /**
     * Tính level dựa trên totalXP
     */
    public static int calculateLevel(int totalXP) {
        if (totalXP < 100) return 1;
        if (totalXP < 300) return 2;
        if (totalXP < 600) return 3;
        if (totalXP < 1000) return 4;
        if (totalXP < 1500) return 5;
        if (totalXP < 2500) return 6;
        if (totalXP < 4000) return 7;
        if (totalXP < 6000) return 8;
        if (totalXP < 9000) return 9;
        return 10; // Max level
    }

    /**
     * Tính XP cần để lên level tiếp theo
     */
    public static int getXPForNextLevel(int currentLevel) {
        switch (currentLevel) {
            case 1: return 100;
            case 2: return 300;
            case 3: return 600;
            case 4: return 1000;
            case 5: return 1500;
            case 6: return 2500;
            case 7: return 4000;
            case 8: return 6000;
            case 9: return 9000;
            default: return 0; // Max level reached
        }
    }

    /**
     * Tính % progress đến level tiếp theo
     */
    public static int calculateLevelProgress(int totalXP) {
        int currentLevel = calculateLevel(totalXP);
        int currentLevelXP = getXPForLevel(currentLevel);
        int nextLevelXP = getXPForNextLevel(currentLevel);
        
        if (nextLevelXP == 0) {
            return 100; // Max level
        }
        
        int xpInCurrentLevel = totalXP - currentLevelXP;
        int xpNeeded = nextLevelXP - currentLevelXP;
        
        return (int) ((xpInCurrentLevel * 100.0) / xpNeeded);
    }

    /**
     * Lấy XP tối thiểu cho level
     */
    private static int getXPForLevel(int level) {
        switch (level) {
            case 1: return 0;
            case 2: return 100;
            case 3: return 300;
            case 4: return 600;
            case 5: return 1000;
            case 6: return 1500;
            case 7: return 2500;
            case 8: return 4000;
            case 9: return 6000;
            case 10: return 9000;
            default: return 0;
        }
    }

    /**
     * Lấy tên level
     */
    public static String getLevelName(int level) {
        switch (level) {
            case 1: return "Mới bắt đầu";
            case 2: return "Sơ cấp";
            case 3: return "Trung cấp";
            case 4: return "Khá";
            case 5: return "Giỏi";
            case 6: return "Xuất sắc";
            case 7: return "Chuyên gia";
            case 8: return "Thạo";
            case 9: return "Bậc thầy";
            case 10: return "Huyền thoại";
            default: return "Unknown";
        }
    }
}
