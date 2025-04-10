import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

val cloudinaryCloudName = localProperties.getProperty("cloudinary.cloud_name") ?: ""
val cloudinaryApiKey = localProperties.getProperty("cloudinary.api_key") ?: ""
val cloudinaryApiSecret = localProperties.getProperty("cloudinary.api_secret") ?: ""
val cloudinaryUploadPreset = localProperties.getProperty("cloudinary.upload_preset") ?: ""
android {
    namespace = "com.forrestgump.ig"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.forrestgump.ig"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"$cloudinaryCloudName\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"$cloudinaryApiKey\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"$cloudinaryApiSecret\"")
        buildConfigField("String", "CLOUDINARY_UPLOAD_PRESET", "\"$cloudinaryUploadPreset\"")

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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.storage)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.play.services.cast.framework)
    implementation(libs.androidx.animation.android)
    implementation(libs.androidx.animation.android)
    implementation(fileTree(mapOf(
        "dir" to "D:\\THIRD-YEAR_SEMESTER-2\\Mobile\\Project\\ZalopayLib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dagger Hilt dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Jetpack Compose dependencies
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.coil.compose)
    implementation(libs.coil.video.decoder)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.okhttp) // Thư viện HTTP để gửi request
    implementation(libs.json) // Thư viện JSON để xử lý phản hồi

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.android.lottie.compose)

    // Firebase Authentication
    implementation(libs.firebase.auth)
    implementation(libs.android.lottie.compose)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.activity.compose.v180)
    implementation(libs.cloudinary.android)

    // Dependency cho Paging Library (runtime)
    implementation(libs.androidx.paging.runtime)

    // Dùng với Jetpack Compose:
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.compose.v100alpha18)
    implementation(libs.accompanist.swiperefresh)

    implementation(libs.firebase.functions.ktx)
    implementation(libs.coil.compose)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.play.services.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.v178)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.firebase.messaging.ktx)
}
