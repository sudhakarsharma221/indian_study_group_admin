plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.indiastudygroupadmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.indiastudygroupadmin"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            buildConfigField(
                "String", "API_BASE_URL", "\"https://indian-study-group-pqyn.onrender.com/\""
            )
            buildConfigField("String", "PINCODE_BASE_URL", "\"https://api.postalpincode.in/\"")
            buildConfigField("String", "EMAIL_URL", "\"https://send.api.mailtrap.io/\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField(
                "String", "API_BASE_URL", "\"https://indian-study-group-pqyn.onrender.com/\""
            )
            buildConfigField("String", "PINCODE_BASE_URL", "\"https://api.postalpincode.in/\"")
            buildConfigField("String", "EMAIL_URL", "\"https://send.api.mailtrap.io/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //ShimmerEffect
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //Image Slider
    implementation("com.github.dangiashish:Auto-Image-Slider:1.0.4")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    //Lottie Animation
    implementation("com.airbnb.android:lottie:6.1.0")

    //Retrofit Dependencies
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Circle View
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //ShimmerEffect
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //Refresh Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    implementation("com.google.android.play:integrity:1.3.0")

    //OTP VIEW
    implementation("io.github.chaosleung:pinview:1.4.4")
}