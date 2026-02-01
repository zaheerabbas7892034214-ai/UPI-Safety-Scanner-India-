# Add project specific ProGuard rules here.
-keep class com.upisafety.scanner.** { *; }
-keepclassmembers class com.upisafety.scanner.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Billing
-keep class com.android.billingclient.** { *; }

# CameraX
-keep class androidx.camera.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
