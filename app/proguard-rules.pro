# QR Guard ProGuard Rules

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Keep Room entities
-keep class com.example.qrguard.data.local.** { *; }

# Keep ZXing
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
