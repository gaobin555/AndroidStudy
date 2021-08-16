package com.android.xthink.ink.launcherink.common.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * application工具集合
 * Created by wanchi on 2017/2/13.
 */

public class ApplicationUtils {

    /**
     * 创建一个Application，包名已经包含在Context中了。然后通过各种反射将application设置到Context中。
     *
     * @param context 包含指定包名的Context
     */
    public static void makePluginApplication(Context context) throws ClassNotFoundException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class contextImplClass = Class.forName("android.app.ContextImpl");
        Field packageInfoField = contextImplClass.getDeclaredField("mPackageInfo");
        packageInfoField.setAccessible(true);

        Object loadedWidgetApk = packageInfoField.get(context);
        Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
        Method makeApplicationMethod = loadedApkClass.getMethod("makeApplication", boolean.class, Instrumentation.class);

        Instrumentation instrumentation = getInstrumentation();
        Application application = (Application) makeApplicationMethod.invoke(loadedWidgetApk, false, instrumentation);

        Class contextWrapperClass = Class.forName("android.content.ContextWrapper");
        Field mBaseField = contextWrapperClass.getDeclaredField("mBase");
        mBaseField.setAccessible(true);
        mBaseField.set(application, context);
    }

    /**
     * 反射得到ActivityThread的Instrument对象。（ActivityThread类不可访问）
     *
     * @return 得到的Instrument
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Instrumentation getInstrumentation() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Field activityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        activityThreadField.setAccessible(true);
        Object activityThread = activityThreadField.get(activityThreadClass);

        Field instrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        return (Instrumentation) instrumentationField.get(activityThread);
    }

    public static void requestPermission(Activity activity, String permission,
                                         String[] requestPermission, int requestId) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            //系统应用不需要处理这个权限问题 // TODO: 2017/2/8
            ActivityCompat.requestPermissions(activity, requestPermission, requestId);
        }
    }

}
