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
    private String correctAnswer;      // Đáp án đúng (String)
    private String explanation;        // Giải thích đáp án
    private int points;                // Điểm cho câu hỏi này
    private int xpReward;
    private String explanationVi;
    private List<String> alternativeAnswers;

    // Phương thức khởi tạo rỗng (bắt buộc cho Firebase)
    public Quiz() {
        this.options = new ArrayList<>();
    }

    // Phương thức khởi tạo đầy đủ
    public Quiz(String quizId, String lessonId, String type, String question, 
                List<String> options, String correctAnswer, String explanation, int points) {
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.type = type;
        this.question = question;
        this.options = options != null ? options : new ArrayList<>();
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.points = points;
    }

    // Phương thức lấy giá trị
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getPoints() {
        return points;
    }

    public int getXpReward() { 
    return xpReward; 
    }
    public String getExplanationVi() { 
    return explanationVi; 
    }
    public List<String> getAlternativeAnswers() { 
    return alternativeAnswers; 
    }

    // Phương thức thiết lập giá trị
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

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setXpReward(int xpReward) { 
    this.xpReward = xpReward; 
    }
    public void setExplanationVi(String explanationVi) { 
    this.explanationVi = explanationVi; 
    }
    public void setAlternativeAnswers(List<String> alternativeAnswers) { 
    this.alternativeAnswers = alternativeAnswers; 
    }

    // Phương thức tiện ích
    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null || correctAnswer == null) return false;
        return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }

    public String getCorrectAnswerText() {
        return correctAnswer;
    }

    public void addOption(String option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
    }
}
