package com.jargo.app.models;

/**
 * Vocabulary model - Từ vựng
 */
public class Vocabulary {
    private String vocabId;
    private String word;
    private String pronunciation;      // IPA notation
    private String meaning;            // Vietnamese translation
    private String example;            // Example sentence in English
    private String exampleTranslation; // Example translation in Vietnamese
    private String audioUrl;           // Firebase Storage URL
    private String imageUrl;           // Optional image URL

    // Phương thức khởi tạo rỗng (bắt buộc cho Firebase)
    public Vocabulary() {
    }

    // Phương thức khởi tạo đầy đủ
    public Vocabulary(String vocabId, String word, String pronunciation, 
                      String meaning, String example, String exampleTranslation) {
        this.vocabId = vocabId;
        this.word = word;
        this.pronunciation = pronunciation;
        this.meaning = meaning;
        this.example = example;
        this.exampleTranslation = exampleTranslation;
    }

    // Phương thức lấy giá trị
    public String getVocabId() {
        return vocabId;
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

    public String getExample() {
        return example;
    }

    public String getExampleTranslation() {
        return exampleTranslation;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Phương thức thiết lập giá trị
    public void setVocabId(String vocabId) {
        this.vocabId = vocabId;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setExampleTranslation(String exampleTranslation) {
        this.exampleTranslation = exampleTranslation;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
