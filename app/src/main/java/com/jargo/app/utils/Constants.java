package com.jargo.app.utils;

/**
 * Constants - Các hằng số dùng trong app
 */
public class Constants {
    
    // Firebase Database Paths
    public static final String DB_USERS = "users";
    public static final String DB_FIELDS = "fields";
    public static final String DB_TOPICS = "topics";
    public static final String DB_LESSONS = "lessons";
    public static final String DB_VOCABULARIES = "vocabularies";
    public static final String DB_QUIZZES = "quizzes";
    public static final String DB_PROGRESS = "progress";
    
    // SharedPreferences Keys
    public static final String PREF_NAME = "JargoPrefs";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_FIELD = "user_field";
    public static final String PREF_USER_LEVEL = "user_level";
    public static final String PREF_IS_FIRST_LAUNCH = "is_first_launch";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    
    // Intent Extras
    public static final String EXTRA_TOPIC_ID = "topic_id";
    public static final String EXTRA_TOPIC_NAME = "topic_name";
    public static final String EXTRA_LESSON_ID = "lesson_id";
    public static final String EXTRA_LESSON_TITLE = "lesson_title";
    public static final String EXTRA_FIELD_ID = "field_id";
    public static final String EXTRA_LEVEL = "level";
    public static final String EXTRA_VOCABULARY_COUNT = "vocabulary_count";
    public static final String EXTRA_QUIZ_SCORE = "quiz_score";
    public static final String EXTRA_QUIZ_TOTAL = "quiz_total";
    
    // Field Types
    public static final String FIELD_IT = "it";
    public static final String FIELD_MEDICAL = "medical";
    public static final String FIELD_ECONOMICS = "economics";
    
    // Level Types
    public static final String LEVEL_BEGINNER = "beginner";
    public static final String LEVEL_INTERMEDIATE = "intermediate";
    public static final String LEVEL_PROFESSIONAL = "professional";
    
    // Quiz Types
    public static final String QUIZ_TYPE_MULTIPLE_CHOICE = "multiple_choice";
    public static final String QUIZ_TYPE_FILL_BLANK = "fill_blank";
    public static final String QUIZ_TYPE_LISTENING = "listening";
    
    // XP Points
    public static final int XP_VOCABULARY_LEARNED = 5;
    public static final int XP_QUIZ_CORRECT = 10;
    public static final int XP_LESSON_COMPLETED = 50;
    public static final int XP_TOPIC_COMPLETED = 200;
    public static final int XP_DAILY_STREAK = 25;
    
    // UI Constants
    public static final int SPLASH_DELAY_MS = 3000;
    public static final int ANIMATION_DURATION_MS = 300;
    public static final int QUIZ_TIME_LIMIT_SECONDS = 30;
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_NAME_LENGTH = 2;
    
    // Request Codes
    public static final int RC_SIGN_IN = 9001;
    public static final int RC_PERMISSION_AUDIO = 9002;
    
    // Error Messages
    public static final String ERROR_NETWORK = "Không có kết nối mạng";
    public static final String ERROR_FIREBASE = "Lỗi kết nối Firebase";
    public static final String ERROR_LOAD_DATA = "Không thể tải dữ liệu";
    public static final String ERROR_INVALID_INPUT = "Dữ liệu nhập không hợp lệ";
    
    // Success Messages
    public static final String SUCCESS_SAVE = "Lưu thành công";
    public static final String SUCCESS_UPDATE = "Cập nhật thành công";
    public static final String SUCCESS_LOGIN = "Đăng nhập thành công";
}
