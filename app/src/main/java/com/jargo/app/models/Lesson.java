package com.jargo.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Lesson model - Bài học chứa nhiều từ vựng
 */
public class Lesson {
    private String lessonId;
    private String topicId;
    private String title;
    private int order;
    private int vocabularyCount;
    private List<Vocabulary> vocabularies;

    // Phương thức khởi tạo rỗng (bắt buộc cho Firebase)
    public Lesson() {
        this.vocabularies = new ArrayList<>();
    }

    // Phương thức khởi tạo đầy đủ
    public Lesson(String lessonId, String topicId, String title, int order) {
        this.lessonId = lessonId;
        this.topicId = topicId;
        this.title = title;
        this.order = order;
        this.vocabularyCount = 0;
        this.vocabularies = new ArrayList<>();
    }

    // Phương thức lấy giá trị
    public String getLessonId() {
        return lessonId;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public int getOrder() {
        return order;
    }

    public int getVocabularyCount() {
        return vocabularyCount;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    // Phương thức thiết lập giá trị
    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setVocabularyCount(int vocabularyCount) {
        this.vocabularyCount = vocabularyCount;
    }

    public void setVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
        this.vocabularyCount = vocabularies != null ? vocabularies.size() : 0;
    }

    // Phương thức tiện ích
    public void addVocabulary(Vocabulary vocabulary) {
        if (this.vocabularies == null) {
            this.vocabularies = new ArrayList<>();
        }
        this.vocabularies.add(vocabulary);
        this.vocabularyCount = this.vocabularies.size();
    }

    public Vocabulary getVocabularyAt(int index) {
        if (vocabularies != null && index >= 0 && index < vocabularies.size()) {
            return vocabularies.get(index);
        }
        return null;
    }
}
