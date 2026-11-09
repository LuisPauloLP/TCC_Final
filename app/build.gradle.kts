plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ged"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.ged"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("cz.adaptech.tesseract4android:tesseract4android:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.android.material:material:1.12.0")
}