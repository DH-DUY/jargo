package com.jargo.app.models;

/**
 * Topic model - Chủ đề học tập
 */
public class Topic {
    private String topicId;
    private String name;
    private String nameEn;
    private String fieldId;
    private String level;
    private int order;
    private int orderIndex;
    private int lessonCount;
    private int totalVocabularies;
    private String description;
    private boolean isLocked;

    // Phương thức khởi tạo rỗng (bắt buộc cho Firebase)
    public Topic() {
    }

    // Phương thức khởi tạo đầy đủ
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

    // Phương thức lấy giá trị
    public String getTopicId() {
        return topicId;
    }

    public String getName() {
        return name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public int getOrderIndex() {
        return orderIndex > 0 ? orderIndex : order;
    }

    public int getTotalVocabularies() {
        return totalVocabularies;
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

    // Phương thức thiết lập giá trị
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setTotalVocabularies(int totalVocabularies) {
        this.totalVocabularies = totalVocabularies;
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

    // Phương thức tiện ích
    public void unlock() {
        this.isLocked = false;
    }

    public void lock() {
        this.isLocked = true;
    }
}
