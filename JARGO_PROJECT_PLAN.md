# TỔNG QUAN DỰ ÁN & KẾ HOẠCH THỰC HIỆN: JARGO APP

## 1. ĐỊNH DANH SẢN PHẨM

- **Tên:** JARGO
- **Khẩu hiệu (Tagline):** Chinh phục từ vựng chuyên ngành (Master your professional vocabulary)
- **Loại hình:** EdTech / Tiếng Anh chuyên ngành (ESP - English for Specific Purposes)
- **Mô hình cốt lõi:** Học theo bài học kiểu Duolingo dành cho chuyên ngành (IT, Y khoa, Kinh tế)
- **Đối tượng mục tiêu:** 
  - Sinh viên chuẩn bị vào nghề
  - Fresher mới ra trường
  - Người đi làm cần nâng cao tiếng Anh chuyên môn

---

## 2. TECH STACK & KIẾN TRÚC

### 2.1. Công nghệ sử dụng
- **Ngôn ngữ:** Java 8+
- **Platform:** Android Native (API 24+)
- **Architecture Pattern:** MVVM với LiveData & ViewModel
- **Database:** Firebase Realtime Database
- **Authentication:** Firebase Authentication (Email/Password, Google Sign-In)
- **Storage:** Firebase Storage (cho audio files)
- **UI Framework:** XML Layouts + Material Design 3
- **Audio Player:** ExoPlayer
- **Image Loading:** Glide
- **Navigation:** Fragment-based với Navigation Component
- **Dependency Injection:** Hilt (hoặc manual DI)

### 2.2. Cấu trúc Firebase Database
```
jargo-database/
├── users/
│   └── {userId}/
│       ├── name: String
│       ├── email: String
│       ├── field: String (IT|Medical|Economics)
│       ├── level: String (beginner|intermediate|professional)
│       ├── createdAt: Long (timestamp)
│       ├── totalXP: Integer
│       ├── streak: Integer
│       └── progress/
│           └── {topicId}/
│               ├── completed: Boolean
│               ├── score: Integer
│               ├── lastAccessed: Long
│               └── vocabularyMastered: Integer
│
├── fields/
│   ├── it/
│   │   ├── name: "Information Technology"
│   │   ├── icon: "ic_it"
│   │   └── description: "Công nghệ thông tin"
│   ├── medical/
│   │   ├── name: "Medical & Healthcare"
│   │   ├── icon: "ic_medical"
│   │   └── description: "Y tế & Chăm sóc sức khỏe"
│   └── economics/
│       ├── name: "Economics & Business"
│       ├── icon: "ic_economics"
│       └── description: "Kinh tế & Kinh doanh"
│
├── topics/
│   └── {topicId}/
│       ├── name: String
│       ├── fieldId: String
│       ├── level: String
│       ├── order: Integer
│       ├── lessonCount: Integer
│       ├── description: String
│       └── isLocked: Boolean
│
├── lessons/
│   └── {lessonId}/
│       ├── topicId: String
│       ├── title: String
│       ├── order: Integer
│       ├── vocabularyCount: Integer
│       └── vocabularies/
│           └── {vocabId}/
│               ├── word: String
│               ├── pronunciation: String (IPA)
│               ├── meaning: String (Vietnamese)
│               ├── example: String
│               ├── exampleTranslation: String
│               ├── audioUrl: String
│               └── imageUrl: String (optional)
│
└── quizzes/
    └── {quizId}/
        ├── lessonId: String
        ├── type: String (multiple_choice|fill_blank|listening)
        ├── question: String
        ├── options: List<String>
        ├── correctAnswer: Integer
        ├── explanation: String
        └── points: Integer
```

### 2.3. Cấu trúc Project (Java)
```
jargo/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/jargo/app/
│   │   │   │   │
│   │   │   │   ├── models/              # Data Models (POJO)
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Field.java
│   │   │   │   │   ├── Topic.java
│   │   │   │   │   ├── Lesson.java
│   │   │   │   │   ├── Vocabulary.java
│   │   │   │   │   ├── Quiz.java
│   │   │   │   │   └── Progress.java
│   │   │   │   │
│   │   │   │   ├── viewmodels/          # MVVM ViewModels
│   │   │   │   │   ├── OnboardingViewModel.java
│   │   │   │   │   ├── HomeViewModel.java
│   │   │   │   │   ├── LearningViewModel.java
│   │   │   │   │   └── QuizViewModel.java
│   │   │   │   │
│   │   │   │   ├── repositories/        # Data Access Layer
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── TopicRepository.java
│   │   │   │   │   ├── LessonRepository.java
│   │   │   │   │   └── QuizRepository.java
│   │   │   │   │
│   │   │   │   ├── activities/          # Activities
│   │   │   │   │   ├── SplashActivity.java
│   │   │   │   │   ├── OnboardingActivity.java
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── LearningActivity.java
│   │   │   │   │   └── QuizActivity.java
│   │   │   │   │
│   │   │   │   ├── fragments/           # Fragments
│   │   │   │   │   ├── HomeFragment.java
│   │   │   │   │   ├── ProgressFragment.java
│   │   │   │   │   ├── ProfileFragment.java
│   │   │   │   │   ├── FieldSelectionFragment.java
│   │   │   │   │   └── LevelSelectionFragment.java
│   │   │   │   │
│   │   │   │   ├── adapters/            # RecyclerView Adapters
│   │   │   │   │   ├── OnboardingPagerAdapter.java
│   │   │   │   │   ├── FieldAdapter.java
│   │   │   │   │   ├── TopicAdapter.java
│   │   │   │   │   ├── VocabularyAdapter.java
│   │   │   │   │   └── QuizAdapter.java
│   │   │   │   │
│   │   │   │   ├── utils/               # Utilities
│   │   │   │   │   ├── Constants.java
│   │   │   │   │   ├── FirebaseManager.java
│   │   │   │   │   ├── SharedPrefsManager.java
│   │   │   │   │   ├── AudioPlayerManager.java
│   │   │   │   │   ├── ProgressCalculator.java
│   │   │   │   │   └── NetworkUtils.java
│   │   │   │   │
│   │   │   │   └── interfaces/          # Callbacks & Listeners
│   │   │   │       ├── OnTopicClickListener.java
│   │   │   │       ├── OnQuizAnswerListener.java
│   │   │   │       └── FirebaseCallback.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/              # XML Layouts
│   │   │   │   │   ├── activity_splash.xml
│   │   │   │   │   ├── activity_onboarding.xml
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_learning.xml
│   │   │   │   │   ├── fragment_home.xml
│   │   │   │   │   ├── fragment_progress.xml
│   │   │   │   │   ├── fragment_profile.xml
│   │   │   │   │   ├── item_field.xml
│   │   │   │   │   ├── item_topic.xml
│   │   │   │   │   └── item_vocabulary.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml       # Color palette
│   │   │   │   │   ├── strings.xml      # String resources
│   │   │   │   │   ├── themes.xml       # Material themes
│   │   │   │   │   └── dimens.xml       # Dimensions
│   │   │   │   │
│   │   │   │   ├── drawable/            # Icons & images
│   │   │   │   ├── menu/                # Menu resources
│   │   │   │   └── navigation/          # Navigation graph
│   │   │   │
│   │   │   ├── AndroidManifest.xml
│   │   │   └── google-services.json     # Firebase config
│   │   │
│   │   └── test/                        # Unit tests
│   │
│   └── build.gradle (app level)
│
├── build.gradle (project level)
└── settings.gradle
```

---

## 3. HỆ THỐNG GIAO DIỆN (DESIGN SYSTEM)

### 3.1. Màu sắc (colors.xml)
```xml
<resources>
    <!-- Primary Colors -->
    <color name="primary">#2563EB</color>              <!-- Xanh Hoàng Gia -->
    <color name="primary_dark">#1E40AF</color>
    <color name="primary_light">#3B82F6</color>
    
    <!-- Secondary Colors -->
    <color name="secondary">#0F172A</color>            <!-- Slate 900 -->
    <color name="secondary_light">#1E293B</color>
    
    <!-- Accent Colors -->
    <color name="accent">#F59E0B</color>               <!-- Vàng Hổ Phách -->
    <color name="accent_light">#FBBF24</color>
    
    <!-- Background -->
    <color name="background">#F8FAFC</color>           <!-- Slate 50 -->
    <color name="surface">#FFFFFF</color>
    
    <!-- Status Colors -->
    <color name="success">#22C55E</color>              <!-- Green -->
    <color name="error">#EF4444</color>                <!-- Red -->
    <color name="warning">#F59E0B</color>              <!-- Amber -->
    <color name="info">#3B82F6</color>                 <!-- Blue -->
    
    <!-- Text Colors -->
    <color name="text_primary">#0F172A</color>
    <color name="text_secondary">#64748B</color>       <!-- Slate 500 -->
    <color name="text_tertiary">#94A3B8</color>        <!-- Slate 400 -->
    <color name="text_white">#FFFFFF</color>
</resources>
```

### 3.2. Typography
- **Font:** Roboto (Android default)
- **Heading 1:** 32sp, Bold
- **Heading 2:** 24sp, Bold
- **Heading 3:** 20sp, SemiBold
- **Body:** 16sp, Regular
- **Caption:** 14sp, Regular
- **Small:** 12sp, Regular

### 3.3. Components chính
- **TopicCard:** CardView với gradient background, progress indicator
- **VocabularyCard:** Flashcard style với flip animation
- **ProgressBar:** Custom circular progress với XP display
- **QuizButton:** Material button với ripple effect
- **AudioButton:** Floating action button cho pronunciation

---

## 4. LUỒNG NGƯỜI DÙNG (USER FLOW)

### 4.1. Onboarding Flow
```
SplashActivity (2s)
    ↓
Check if user logged in?
    ├── YES → MainActivity (Home)
    └── NO  → OnboardingActivity
                ↓
            ViewPager2 với 3 screens:
            1. Welcome Screen (Logo + Tagline)
            2. Field Selection (IT, Medical, Economics)
            3. Level Selection (Beginner, Intermediate, Professional)
                ↓
            Firebase Auth (Anonymous hoặc Email)
                ↓
            Save user profile to Firebase
                ↓
            MainActivity (Home)
```

### 4.2. Home Screen
```
MainActivity
    ├── BottomNavigation
    │   ├── Home (HomeFragment)
    │   ├── Progress (ProgressFragment)
    │   └── Profile (ProfileFragment)
    │
    └── HomeFragment
        ├── Header (Welcome + Streak info)
        ├── Progress Card (Overall progress)
        └── Topics RecyclerView
            ├── Topic 1 (Locked/Unlocked)
            ├── Topic 2
            └── Topic 3 ...
```

### 4.3. Learning Flow
```
User clicks Topic → LearningActivity
    ↓
Load Lesson from Firebase
    ↓
ViewPager2 for Vocabularies:
    ├── Screen 1: Word + Image
    │   ├── Word (large text)
    │   ├── Pronunciation (IPA)
    │   ├── Audio button (ExoPlayer)
    │   └── Meaning + Example
    │
    ├── Screen 2: Next word...
    │
    └── Last Screen: Summary
            ↓
        QuizActivity
            ↓
        Multiple choice questions (10 questions)
            ↓
        Calculate score & XP
            ↓
        Update Firebase progress
            ↓
        Show result screen
            ↓
        Return to Home
```

---

## 5. KẾ HOẠCH PHÁT TRIỂN CHI TIẾT

### PHASE 1: SETUP & FOUNDATION (Tuần 1-2)

#### Bước 1.1: Khởi tạo Project
- [ ] Tạo Android project (Java, API 24+)
- [ ] Setup Firebase (Realtime Database, Auth, Storage)
- [ ] Cấu hình build.gradle với dependencies
- [ ] Thêm google-services.json
- [ ] Setup Material Design theme

**Deliverable:** Project skeleton có thể build thành công

#### Bước 1.2: Tạo Models & Constants
- [ ] User.java
- [ ] Field.java
- [ ] Topic.java
- [ ] Lesson.java
- [ ] Vocabulary.java
- [ ] Quiz.java
- [ ] Progress.java
- [ ] Constants.java (Firebase paths, keys)

**Deliverable:** Tất cả POJO models với getters/setters

#### Bước 1.3: Setup Design System
- [ ] colors.xml
- [ ] strings.xml
- [ ] themes.xml
- [ ] dimens.xml
- [ ] drawable resources (icons)

**Deliverable:** Design system hoàn chỉnh

---

### PHASE 2: ONBOARDING (Tuần 3-4)

#### Bước 2.1: SplashActivity
- [ ] Layout với logo JARGO
- [ ] Check authentication status
- [ ] Navigate to appropriate screen

**Files cần tạo:**
- `SplashActivity.java`
- `activity_splash.xml`

#### Bước 2.2: OnboardingActivity
- [ ] ViewPager2 implementation
- [ ] 3 onboarding screens
- [ ] Field selection (RecyclerView Grid)
- [ ] Level selection (RadioButton group)
- [ ] Firebase Auth integration
- [ ] Save user profile

**Files cần tạo:**
- `OnboardingActivity.java`
- `OnboardingPagerAdapter.java`
- `FieldSelectionFragment.java`
- `LevelSelectionFragment.java`
- `activity_onboarding.xml`
- `fragment_field_selection.xml`
- `fragment_level_selection.xml`
- `item_field.xml`

#### Bước 2.3: Repositories & Firebase
- [ ] UserRepository.java
- [ ] FirebaseManager.java
- [ ] SharedPrefsManager.java (lưu user session)

**Deliverable:** Onboarding flow hoàn chỉnh, user data lưu vào Firebase

---

### PHASE 3: HOME SCREEN (Tuần 5-6)

#### Bước 3.1: MainActivity & Navigation
- [ ] MainActivity với BottomNavigationView
- [ ] Navigation Component setup
- [ ] 3 fragments (Home, Progress, Profile)

**Files cần tạo:**
- `MainActivity.java`
- `activity_main.xml`
- `navigation/nav_graph.xml`

#### Bước 3.2: HomeFragment
- [ ] HomeViewModel.java
- [ ] TopicRepository.java
- [ ] Load topics from Firebase based on user field & level
- [ ] RecyclerView cho topics
- [ ] TopicAdapter.java
- [ ] Progress tracking UI

**Files cần tạo:**
- `HomeFragment.java`
- `HomeViewModel.java`
- `TopicRepository.java`
- `TopicAdapter.java`
- `fragment_home.xml`
- `item_topic.xml`

#### Bước 3.3: ProgressFragment
- [ ] Hiển thị overall statistics
- [ ] XP & Streak display
- [ ] Completed topics list
- [ ] Charts (optional - sử dụng MPAndroidChart)

**Files cần tạo:**
- `ProgressFragment.java`
- `fragment_progress.xml`

#### Bước 3.4: ProfileFragment
- [ ] User info display
- [ ] Settings (change field, level)
- [ ] Logout functionality

**Files cần tạo:**
- `ProfileFragment.java`
- `fragment_profile.xml`

**Deliverable:** Home screen hoàn chỉnh với topic list

---

### PHASE 4: LEARNING MODE (Tuần 7-9)

#### Bước 4.1: LearningActivity Setup
- [ ] LearningActivity.java
- [ ] LearningViewModel.java
- [ ] LessonRepository.java
- [ ] Load lesson & vocabularies from Firebase

**Files cần tạo:**
- `LearningActivity.java`
- `LearningViewModel.java`
- `LessonRepository.java`
- `activity_learning.xml`

#### Bước 4.2: Vocabulary Learning UI
- [ ] ViewPager2 cho vocabulary cards
- [ ] VocabularyAdapter.java
- [ ] Card layout với word, pronunciation, meaning, example
- [ ] Audio player integration (ExoPlayer)
- [ ] AudioPlayerManager.java
- [ ] Swipe gestures

**Files cần tạo:**
- `VocabularyAdapter.java`
- `AudioPlayerManager.java`
- `item_vocabulary.xml`

#### Bước 4.3: Audio Integration
- [ ] Setup ExoPlayer
- [ ] Load audio from Firebase Storage
- [ ] Play/Pause controls
- [ ] Handle playback states

**Deliverable:** Vocabulary learning screen với audio playback

---

### PHASE 5: QUIZ SYSTEM (Tuần 10-11)

#### Bước 5.1: QuizActivity
- [ ] QuizActivity.java
- [ ] QuizViewModel.java
- [ ] QuizRepository.java
- [ ] Load quizzes from Firebase

**Files cần tạo:**
- `QuizActivity.java`
- `QuizViewModel.java`
- `QuizRepository.java`
- `activity_quiz.xml`

#### Bước 5.2: Quiz UI
- [ ] Multiple choice layout
- [ ] Answer selection
- [ ] Timer (optional)
- [ ] Score calculation
- [ ] Result screen

**Files cần tạo:**
- `fragment_quiz_question.xml`
- `fragment_quiz_result.xml`

#### Bước 5.3: Progress Update
- [ ] Update user progress in Firebase
- [ ] Calculate XP earned
- [ ] Update streak
- [ ] Unlock next topic logic

**Deliverable:** Quiz system hoàn chỉnh với progress tracking

---

### PHASE 6: POLISH & OPTIMIZATION (Tuần 12-13)

#### Bước 6.1: Error Handling
- [ ] Network error handling
- [ ] Firebase error handling
- [ ] User-friendly error messages
- [ ] Retry mechanisms

#### Bước 6.2: Loading States
- [ ] Progress dialogs
- [ ] Shimmer effects
- [ ] Empty states
- [ ] Error states

#### Bước 6.3: Animations & Transitions
- [ ] Activity transitions
- [ ] Fragment transitions
- [ ] Card flip animations
- [ ] Button ripple effects

#### Bước 6.4: Offline Support
- [ ] Cache data locally
- [ ] Sync when online
- [ ] Offline indicators

**Deliverable:** App polished và production-ready

---

### PHASE 7: TESTING & DEPLOYMENT (Tuần 14)

#### Bước 7.1: Testing
- [ ] Unit tests cho ViewModels
- [ ] Integration tests cho Repositories
- [ ] UI tests (Espresso)
- [ ] Manual testing

#### Bước 7.2: Data Population
- [ ] Tạo sample data cho Firebase
- [ ] IT vocabulary set
- [ ] Medical vocabulary set
- [ ] Economics vocabulary set
- [ ] Audio files upload

#### Bước 7.3: Documentation
- [ ] README.md
- [ ] User guide
- [ ] Developer documentation

#### Bước 7.4: Deployment
- [ ] Generate signed APK
- [ ] Prepare Play Store assets
- [ ] Submit to Play Store (optional)

**Deliverable:** App hoàn thiện sẵn sàng release

---

## 6. DEPENDENCIES CHÍNH (build.gradle)

### Project-level build.gradle
```gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}

plugins {
    id 'com.android.application' version '8.1.2' apply false
}
```

### App-level build.gradle
```gradle
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.jargo.app'
    compileSdk 34

    defaultConfig {
        applicationId "com.jargo.app"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX Core
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.core:core:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-database'
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-storage'
    implementation 'com.google.firebase:firebase-analytics'
    
    // Material Design
    implementation 'com.google.android.material:material:1.11.0'
    
    // RecyclerView & ViewPager2
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
    
    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-runtime:2.7.0'
    
    // Navigation Component
    implementation 'androidx.navigation:navigation-fragment:2.7.6'
    implementation 'androidx.navigation:navigation-ui:2.7.6'
    
    // Image Loading - Glide
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
    
    // Audio Player - ExoPlayer
    implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
    
    // JSON Processing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Shimmer Effect (loading)
    implementation 'com.facebook.shimmer:shimmer:0.5.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

---

## 7. TIMELINE TỔN KẾT

| Phase | Duration | Tasks | Status |
|-------|----------|-------|--------|
| **Phase 1** | Tuần 1-2 | Setup project, Models, Design System | Pending |
| **Phase 2** | Tuần 3-4 | Onboarding flow, Firebase Auth | Pending |
| **Phase 3** | Tuần 5-6 | Home screen, Navigation, Topic list | Pending |
| **Phase 4** | Tuần 7-9 | Learning mode, Audio player | Pending |
| **Phase 5** | Tuần 10-11 | Quiz system, Progress tracking | Pending |
| **Phase 6** | Tuần 12-13 | Polish, Optimization, Animations | Pending |
| **Phase 7** | Tuần 14 | Testing, Data population, Deployment | Pending |

**Tổng thời gian:** 14 tuần (3.5 tháng)

---

## 8. FEATURES TƯƠNG LAI (POST-MVP)

- [ ] Social features (leaderboard, friends)
- [ ] Daily challenges
- [ ] Push notifications for reminders
- [ ] Offline mode hoàn chỉnh
- [ ] AI-powered pronunciation feedback
- [ ] Speaking practice
- [ ] Writing exercises
- [ ] Premium subscription
- [ ] More fields (Law, Engineering, etc.)
- [ ] Multi-language support

---

## 9. GHI CHÚ KỸ THUẬT

### 9.1. Firebase Security Rules
```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "fields": {
      ".read": true,
      ".write": false
    },
    "topics": {
      ".read": true,
      ".write": false
    },
    "lessons": {
      ".read": true,
      ".write": false
    },
    "quizzes": {
      ".read": true,
      ".write": false
    }
  }
}
```

### 9.2. Performance Tips
- Sử dụng ViewHolder pattern cho RecyclerView
- Lazy loading cho images với Glide
- Cache Firebase data locally
- Sử dụng ProGuard cho release build
- Optimize image sizes
- Preload audio files

### 9.3. Best Practices
- Follow MVVM architecture strictly
- Use LiveData for reactive UI updates
- Handle configuration changes properly
- Implement proper error handling
- Add logging for debugging
- Write clean, documented code

---

**Document Version:** 1.0  
**Last Updated:** November 25, 2025  
**Author:** JARGO Development Team
