package com.jargo.app.models;

/**
 * Topic model - Chủ đề học tập
 */
public class Topic {
    private String topicId;
    private String name;
    private String fieldId;
    private String level;
    private int order;
    private int lessonCount;
    private String description;
    private boolean isLocked;

    // Constructor rỗng
    public Topic() {
    }

    // Constructor đầy đủ
    public Topic(String topicId, String name, String fieldId, String level, 
                 int order, int lessonCount, String description, boolean isLocked) {
        this.topicId = topicId;
        this.name = name;
        this.fieldId = fieldId;
        this.level = level;
        this.order = order;
        this.lessonCount = lessonCount;
        this.description = description;
        this.isLocked = isLocked;
    }

    // Getters
    public String getTopicId() {
        return topicId;
    }

    public String getName() {
        return name;
    }

    public String getFieldId() {
        return fieldId;
    }

    public String getLevel() {
        return level;
    }

    public int getOrder() {
        return order;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLocked() {
        return isLocked;
    }

    // Setters
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    // Utility methods
    public void unlock() {
        this.isLocked = false;
    }

    public void lock() {
        this.isLocked = true;
    }
}
