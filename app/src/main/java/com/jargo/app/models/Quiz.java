package com.jargo.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Quiz model - Câu hỏi trắc nghiệm
 */
public class Quiz {
    private String quizId;
    private String lessonId;
    private String type;               // multiple_choice, fill_blank, listening
    private String question;
    private List<String> options;      // Các đáp án lựa chọn
    private int correctAnswer;         // Index của đáp án đúng (0-based)
    private String explanation;        // Giải thích đáp án
    private int points;                // Điểm cho câu hỏi này

    // Constructor rỗng
    public Quiz() {
        this.options = new ArrayList<>();
    }

    // Constructor đầy đủ
    public Quiz(String quizId, String lessonId, String type, String question, 
                List<String> options, int correctAnswer, String explanation, int points) {
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.type = type;
        this.question = question;
        this.options = options != null ? options : new ArrayList<>();
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.points = points;
    }

    // Getters
    public String getQuizId() {
        return quizId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getPoints() {
        return points;
    }

    // Setters
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // Utility methods
    public boolean isCorrect(int userAnswer) {
        return userAnswer == correctAnswer;
    }

    public String getCorrectAnswerText() {
        if (options != null && correctAnswer >= 0 && correctAnswer < options.size()) {
            return options.get(correctAnswer);
        }
        return null;
    }

    public void addOption(String option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
    }
}
