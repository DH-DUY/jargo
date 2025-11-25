package com.jargo.app.models;

/**
 * Vocabulary model - Từ vựng
 */
public class Vocabulary {
    private String vocabularyId;       // ID từ vựng
    private String lessonId;           // ID bài học
    private String word;               // Từ tiếng Anh
    private String pronunciation;      // Phiên âm IPA
    private String partOfSpeech;       // Từ loại (noun, verb, adj...)
    private String meaning;            // Nghĩa tiếng Việt
    private String definition;         // Định nghĩa tiếng Anh
    private String exampleEn;          // Ví dụ tiếng Anh
    private String exampleVi;          // Ví dụ tiếng Việt
    private String imageUrl;           // URL hình ảnh
    private String audioUrl;           // URL audio phát âm
    private String level;              // Cấp độ (beginner/intermediate/professional)


    // Phương thức khởi tạo rỗng (bắt buộc cho Firebase)
    public Vocabulary() {
    }

    // Phương thức lấy giá trị
     public String getVocabularyId() {
        return vocabularyId;
    }

    public String getWord() {
        return word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }

    public String getExampleEn() {
        return exampleEn;
    }

    public String getExampleVi() {
        return exampleVi;
    }

    public String getLevel() {
        return level;
    }

    // Phương thức thiết lập giá trị
        public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setExampleEn(String exampleEn) {
        this.exampleEn = exampleEn;
    }

    public void setExampleVi(String exampleVi) {
        this.exampleVi = exampleVi;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
