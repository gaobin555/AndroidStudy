/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 反射方法静默安装APK
 *
 * @author liyuyan
 * @version 1.0, 2017/6/2
 */

public class PackageUtils {
    public static boolean silentInstall(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        File file = new File(apkPath);
        Uri uri = Uri.fromFile(file);
        Class<?> pmClz = packageManager.getClass();
        try {
            Class<?> aClass = Class.forName("android.app.PackageInstallObserver");
            Constructor<?> constructor = aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object installObserver = constructor.newInstance();
            Method installPackage = pmClz.getDeclaredMethod("installPackage", Uri.class, aClass, int.class, String.class);
            installPackage.setAccessible(true);
            installPackage.invoke(packageManager, uri, installObserver, 2, null);
            MyLog.e("version", "apkPath: " + apkPath + "-安装成功");
            return true;
        } catch (Exception e) {
            MyLog.e("version", e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public static String getActivity(Activity activity) {
        ActivityManager mActivityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String topActivityName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return topActivityName;
    }

}