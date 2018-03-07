-keepattributes Exceptions,Signature,*Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * {
  public <init>(android.content.Context);
}

-keep public class com.apisense.bee.utils.** { public *; }

##############
# zbar specific
-keep class net.sourceforge.zbar.** { *; }
# End zbar
##############

##############
# apisense specific
-keep public class io.apisense.sdk.** { *; }
-keepclassmembernames class io.apisense.sdk.** { *; }
-keep public class io.apisense.** { *; }
-keepclassmembernames class io.apisense.** { *; }

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

## Retrofit
-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

## Ormlite
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

## Rhino
-keep class javax.script.** { *; }
-keep class com.sun.script.javascript.** { *; }
-keep class org.mozilla.javascript.** { *; }
-dontwarn org.mozilla.javascript.**
-dontwarn sun.**

## JDeferred
-dontwarn org.slf4j.**

## Visualization Module
-dontwarn com.google.maps.android.**
-dontwarn org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck

# Dagger: https://github.com/google/dagger/issues/645
-dontwarn com.google.errorprone.annotations.*

# End apisense
##############

# Removing Verbose and Debug Logs
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
}
