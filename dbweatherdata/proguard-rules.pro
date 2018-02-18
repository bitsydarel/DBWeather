# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class locationName to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

-optimizationpasses 5
-optimizations !class/unboxing/enum
-verbose

# keep repository classes
-keep public class com.dbeginc.dbweatherdata.implementations.repositories.**
#-renamesourcefileattribute SourceFile

# don't warn javax warning
-dontwarn javax.annotation.**

###################### NETWORK RULES ######################
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
# Gson specific classes
-dontwarn sun.misc.**
-dontwarn com.bea.xml.**
# simpleframework
-dontwarn org.simpleframework.xml.**
-keep class org.simpleframework.xml.Serializer
-keep class org.simpleframework.xml.** { *; }
-keepattributes ElementList, Root
# https://github.com/square/okhttp/issues/2230
-dontwarn okhttp3.**
-dontnote okhttp3.**
# https://github.com/square/okio#proguard
-dontwarn okio.**
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
####################################################################################
# Retain declared checked exceptions for use by a Proxy instance.
# Retain generic type information for use by reflection by converters and adapters.
# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,Exceptions

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**

# Needed by google-play-services when linking against an older platform version
-dontwarn com.google.android.gms.**
-dontnote com.google.android.gms.**
-dontnote com.google.firebase.database.**
# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path

# Suppress notes on LicensingServices
-dontnote **.ILicensingService

# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models. Modify to fit the structure
# of your app.
-keepclassmembers class com.dbeginc.dbweatherdata.proxies.remote.** {
  *;
}

-keepclassmembers class com.dbeginc.dbweatherdata.proxies.local.** {
  *;
}