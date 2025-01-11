# Firebase: Mantener todas las clases de Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Retrofit: Mantener todas las clases de Retrofit
-keep class com.squareup.retrofit2.** { *; }
-dontwarn com.squareup.retrofit2.**

# Glide: Mantener todas las clases de Glide y las anotaciones de Glide
-keep class com.bumptech.glide.** { *; }
-keep class com.bumptech.glide.annotation.** { *; }
-keep class com.bumptech.glide.module.** { *; }
-dontwarn com.bumptech.glide.**

# Dagger: Mantener todas las clases de Dagger y las clases de inyección
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-dontwarn javax.annotation.**

# Room Database: Mantener todas las clases de Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Navigation Component: Mantener todas las clases de Navigation
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ML Kit: Mantener todas las clases de ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# WebRTC: Mantener todas las clases de WebRTC
-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**

# Signal Protocol: Mantener todas las clases del protocolo Signal
-keep class org.whispersystems.** { *; }
-dontwarn org.whispersystems.**

# AR Core: Mantener todas las clases de AR Core
-keep class com.google.ar.** { *; }
-dontwarn com.google.ar.**

# Timber (Logging): Mantener todas las clases de Timber para logging
-keep class timber.log.** { *; }
-dontwarn timber.log.**

# Evitar advertencias comunes
-dontnote okhttp3.**
-dontnote retrofit2.**
-dontnote androidx.**
-dontwarn javax.annotation.**

# Mantener atributos para reflexión
-keepattributes *Annotation*
-keepattributes Signature

# Mantener clases necesarias para reflexión en Kotlin
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Proguard rules for Multidex support
-keep class androidx.multidex.** { *; }
-keep class com.android.tools.fd.** { *; }
-dontwarn androidx.multidex.**

-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

-keep class org.signal.** { *; }
-keep class org.whispersystems.** { *; }
-keepclassmembers class * {
    @org.signal.** *;
}

-keep class com.google.android.gms.internal.** { *; }
-keepclassmembers class com.google.android.gms.internal.** {
    private static final java.lang.String TAG;
    private static final int *;
    static final int *;
    static final java.lang.String *;
}

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Protobuf
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**

# Signal
-keep class org.signal.** { *; }
-dontwarn org.signal.**

# Firebase In-App Messaging
-keep class com.google.firebase.inappmessaging.** { *; }
-keep class com.google.firebase.inappmessaging.display.** { *; }

# Protobuf
-keep class com.google.protobuf.** { *; }

# Signal
-keep class org.signal.** { *; }
-keep class org.whispersystems.** { *; }

# Timber
-dontwarn org.jetbrains.annotations.**
-keep class timber.log.Timber { *; }
