plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.jargo.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jargo.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    // Cấu hình tên file APK output
    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "JARGO-${buildType.name}.apk"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Material Design
    implementation("com.google.android.material:material:1.13.0")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")
    
    // RecyclerView & ViewPager2
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    
    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    
    // Image Loading - Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")
    
    // Audio Player - ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    
    // JSON Processing
    implementation("com.google.code.gson:gson:2.13.2")
    
    // Shimmer Effect (loading)
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}