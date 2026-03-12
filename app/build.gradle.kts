plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.masin.pangea"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.masin.pangeaapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // R8 habilitado para reducir tamaño del APK y mejorar rendimiento
            isMinifyEnabled = true
            // Shrink resources elimina recursos no utilizados
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Deshabilitar minify en debug para builds rápidos
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true
        // Deshabilitar BuildConfig si no se usa para reducir bytecode
        buildConfig = false
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
        }
        jniLibs {
            pickFirsts += listOf("lib/x86/libc++_shared.so", "lib/x86_64/libc++_shared.so",
                "lib/armeabi-v7a/libc++_shared.so", "lib/arm64-v8a/libc++_shared.so")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Core Splash Screen API - splash nativo optimizado
    implementation(libs.androidx.splashscreen)
    
    // Profile Installer - mejora rendimiento con Baseline Profiles
    implementation(libs.androidx.profileinstaller)
    
    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // WebKit for WebView
    implementation(libs.androidx.webkit)
    
    // Coil for image loading (GIF support)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // OkHttp for network requests (LIA chat)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}