# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/share/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
-optimizationpasses 5
-verbose

##################### Things to not warn ######################################
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
# https://github.com/square/okhttp/issues/2230
-dontwarn okhttp3.**
-dontnote okhttp3.**
# https://github.com/square/okio#proguard
-dontnote okio.**
-dontwarn okio.**
-dontwarn com.bea.xml.**
# Needed by google-play-services when linking against an older platform version
-dontwarn com.google.android.gms.**
-dontnote com.google.android.gms.**
# bottom bar
-dontnote com.roughike.bottombar.**
-dontwarn com.roughike.bottombar.**
# simpleframework
-dontwarn org.simpleframework.xml.**
-keep class org.simpleframework.xml.** { *; }
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
#
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
##################################################################################
##################### Things to keep ######################################
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
# Retain declared checked exceptions for use by a Proxy instance.
# Retain generic type information for use by reflection by converters and adapters.
# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,Exceptions
# https://github.com/bumptech/glide/blob/master/library/proguard-rules.txt
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
##################################################################################