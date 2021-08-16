# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\work\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

# 不使用大小写混合
-dontusemixedcaseclassnames
#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclasses
#-dontwarn 打开不看警告
# 混淆时记录日志
-verbose
-ignorewarning
#指定压缩级别
-optimizationpasses 5
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn javax.annotation.**
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
-dontwarn android.net.**
-dontwarn com.facebook.**
-dontwarn com.coolcloud.uac.android.**
-dontwarn com.yulong.appdata.**
-dontwarn org.greenrobot.eventbus.**
-dontwarn org.litepal.**

-keep class android.net.SSLCertificateSocketFactory{*;}
#-keep class de.greenrobot.event.**{ *; }
-keep class com.facebook.**{ *; }
-keep class com.facebook.infer.**{ *; }
-keep class com.coolcloud.uac.android.** { *; }
-keep class com.yulong.appdata.** { *; }
-keep class org.greenrobot.eventbus.** { *; }
-keep class org.litepal.** { *; }

#保持泛型
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepattributes Exceptions,InnerClasses
#基础组件不能混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.FragmentActivity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.widget.TextView
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.android.xthink.ink.launcherink.service.AutoUpdateService
-keep public class com.android.xthink.ink.launcherink.service.UpdateReceiver


#-----------keep httpclient -------------------
-keep class org.apache.** {
    public <fields>;
    public <methods>;
}

# gson
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

# OkHttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** {*;}
-keep interface com.squareup.okhttp.** {*;}
-dontwarn okio.**


#bean
-keep class com.android.xthink.ink.launcherink.common.network.user.** {*;}
-keepclassmembers class com.android.xthink.ink.launcherink.common.network.user.** { <fields>; }

-keep class com.android.xthink.ink.launcherink.init.bean.** {*;}
-keepclassmembers class com.android.xthink.ink.launcherink.init.bean.** { <fields>; }

-keep class com.android.xthink.ink.launcherink.common.network.direct.bean.** {*;}
-keepclassmembers class com.android.xthink.ink.launcherink.common.network.direct.bean.** { <fields>; }

-keep class com.android.xthink.ink.launcherink.ui.home.bean.db.** {*;}
-keepclassmembers class com.android.xthink.ink.launcherink.ui.home.bean.db.** { <fields>; }

-keep class com.android.xthink.ink.launcherink.ui.home.bean.gson.** {*;}
-keepclassmembers class com.android.xthink.ink.launcherink.ui.home.bean.gson.** { <fields>; }

#保持update javaBean不被混淆
-keep class com.android.xthink.ink.launcherink.ui.home.update.model.bean.** {*;}
-keepclassmembers class com.android.xthink.ink.launcherink.ui.home.update.model.bean.** { <fields>; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

#---eventBus start---
-keepclassmembers class com.android.xthink.ink.launcherink.ui.home.MainActivity{
    public void onEventMainThread(com.android.xthink.ink.launcherink.ui.edit.EditEvent);
}
#---eventBus end---



