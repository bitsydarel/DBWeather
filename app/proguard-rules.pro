# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/share/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
######################################DBWeather Proguard configuration##############################

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <methods>;
}

-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

-keep @interface com.google.android.gms.common.util.DynamiteApi
-keep public @com.google.android.gms.common.util.DynamiteApi class * {
  public <fields>;
  public <methods>;
}

-dontwarn android.security.NetworkSecurityPolicy

#Warning to skipp
-dontwarn sun.misc.**

#keep R static member for resources
-keepclassmembers class **.R$* {
    public static <fields>;
}

#Keep parcelable classes
-keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
}

#Keep searchView
-keep class android.support.v7.widget.SearchView

#Keep Glide Configuration
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Defautl android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep public class * extends android.app.Activity
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends com.dbeginc.dbweather.ui.BaseActivity
-keep public class * extends android.preference.Preference
-keep class io.reactivex.disposables.CompositeDisposable
-keep class org.simpleframework.xml.Serializer

#http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

#Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#Keep constructors members
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#Keep constructors members
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Keep Rest Classes
-keep class com.dbeginc.dbweather.models.api.** { *; }
-dontwarn com.dbeginc.dbweather.models.api.**

#Keep Database Classes
-keep class * extends android.database.sqlite.SQLiteOpenHelper { *; }
-dontwarn android.database.sqlite.SQLiteOpenHelper

#Keep Application Pojos Classes
-keep class com.dbeginc.dbweather.models.datatypes.** { *; }
-dontwarn com.dbeginc.dbweather.models.datatypes.**

#Keep Model data providers Classes
-keep class com.dbeginc.dbweather.models.provider.** { *; }
-dontwarn com.dbeginc.dbweather.models.provider.**

#Keep Ui Classes
-keep class com.dbeginc.dbweather.ui.** { *; }
-dontwarn com.dbeginc.dbweather.ui.**

# Retrofit 2.X
## https://square.github.io/retrofit/ ##

#-dontwarn retrofit2.**
#-keep class retrofit2.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}

-dontwarn okio.**
-dontwarn javax.annotation.**

# OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**

-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# Gson

# need to be skipped
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**

#Okio https://github.com/square/okio

-dontwarn com.google.api.client.googleapis**

-dontwarn com.google.errorprone.annotations.*