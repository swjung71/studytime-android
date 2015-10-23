# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Thomas\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

#-dontwarn org.simpleframework.xml.**

-keep class com.igaworks.** { *; }
-dontwarn com.igaworks.**

-dontshrink

-keep public class org.simpleframework.** { *; }
-keep class org.simpleframework.xml.** { *; }
-keep class org.simpleframework.xml.core.** { *; }
-keep class org.simpleframework.xml.util.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes ElementList, Root
-keepattributes Element
-keepattributes SourceFile,LineNumberTable

-dontwarn javax.xml.stream.XMLInputFactory
-dontwarn javax.xml.stream.XMLEventReader
-dontwarn javax.xml.stream.XMLEvent
-dontwarn javax.xml.stream.events.XMLEvent
-dontwarn javax.xml.stream.events.Attribute
-dontwarn javax.xml.stream.Attribute
-dontwarn javax.xml.stream.Location
-dontwarn javax.xml.stream.events.StartElement
-dontwarn javax.xml.stream.events.Characters

-keep class com.android.volley.** {*;}
-keep interface com.android.volley.** {*;}

-keep class kr.co.digitalanchor.studytime.model.** {*;}
-keep interface kr.co.digitalanchor.studytime.model.** {*;}

-libraryjars ../app/libs/3rdparty_login_library_android_4.1.4.jar
-keep public class com.nhn.android.naverlogin.** {
       public protected *;
}


