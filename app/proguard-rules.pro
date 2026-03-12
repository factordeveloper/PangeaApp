# =============================================================================
# ProGuard/R8 Rules para SDHexperience
# Optimizado para rendimiento y tamaño mínimo de APK
# =============================================================================

# -----------------------------------------------------------------------------
# Optimizaciones generales de R8
# -----------------------------------------------------------------------------
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

# Preservar información de línea para stack traces en producción
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# -----------------------------------------------------------------------------
# Kotlin
# -----------------------------------------------------------------------------
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# -----------------------------------------------------------------------------
# Jetpack Compose
# -----------------------------------------------------------------------------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# -----------------------------------------------------------------------------
# Coil (Image Loading)
# -----------------------------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**

# Keep ImageLoader factory
-keep class * implements coil.ImageLoaderFactory { *; }

# -----------------------------------------------------------------------------
# WebView
# -----------------------------------------------------------------------------
# No tenemos interfaces JavaScript, pero si se agregan en el futuro:
# -keepclassmembers class * {
#     @android.webkit.JavascriptInterface <methods>;
# }

# -----------------------------------------------------------------------------
# Application class
# -----------------------------------------------------------------------------
-keep class com.masin.pangea.SDHApplication { *; }

# -----------------------------------------------------------------------------
# Navigation Compose
# -----------------------------------------------------------------------------
-keep class * extends androidx.navigation.Navigator { *; }

# -----------------------------------------------------------------------------
# Lifecycle & ViewModel
# -----------------------------------------------------------------------------
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# -----------------------------------------------------------------------------
# Eliminación agresiva de código no usado
# -----------------------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# -----------------------------------------------------------------------------
# Material 3
# -----------------------------------------------------------------------------
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# -----------------------------------------------------------------------------
# OkHttp
# -----------------------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# -----------------------------------------------------------------------------
# Gson
# -----------------------------------------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }
-keep class * implements com.google.gson.JsonSerializer { *; }
-keep class * implements com.google.gson.JsonDeserializer { *; }
# Keep data classes used for JSON serialization
-keep class com.masin.pangea.data.remote.** { *; }

# Keep JavascriptInterface methods (WebView bridge)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}