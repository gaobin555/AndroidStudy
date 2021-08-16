package com.android.xthink.ink.launcherink.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.manager.InkToastManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 冻结app相关
 * Created by mark.zhang on 2016/12/1.
 */
public class FreezeUtils {

    private static final String TAG = FreezeUtils.class.getName();
    public static final String THAW_ACTION = "com.zeusis.action.thwaApp";
    public static final String FREEZE_PARAMETER = "mPackageNamesList";
    /**
     * 冻结的app，在第一次点击以后，会加入到这个队列中。在此队列中的冻结的app，再次被点击时，会解冻。
     * key:包名packName,value:时间date（预备解冻有时效）.
     */
    private static Map<String, Long> sReadyToThawMap;

    public static boolean isAppFrozen(Context context, String pakName) {
        if (TextUtils.isEmpty(pakName))
            return false;
        boolean freezeFlag = false;
        ApplicationInfo appInfo;
        PackageManager pm = context.getPackageManager();
        try {
            appInfo = pm.getApplicationInfo(pakName, 0);
            if (pm.getApplicationEnabledSetting(pakName) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                freezeFlag = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return freezeFlag;
    }

    public static String getAppName(Context context, String pakName) {
        if (TextUtils.isEmpty(pakName))
            return "";
        ApplicationInfo appInfo;
        PackageManager pm = context.getPackageManager();
        try {
            appInfo = pm.getApplicationInfo(pakName, 0);
            return pm.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isSystemApp(Context context, String packageName) {
        boolean isSytemApp = false;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            isSytemApp = pInfo != null && ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, packageName + " not found");
            e.printStackTrace();
        }
        return isSytemApp;
    }

    /**
     * 新增预备解冻的app，当解冻的app在第一次点击以后，将其加入到这个队列中。
     *
     * @param packageName 包名
     */
    public static void addReadyToThaw(String packageName) {
        long date = System.currentTimeMillis();
        if (sReadyToThawMap == null) {
            sReadyToThawMap = new HashMap<>();
        }
        sReadyToThawMap.put(packageName, date);
    }

    /**
     * 移除预备解冻的app
     *
     * @param packageName 包名
     */
    public static void removeReadyToThaw(String packageName) {
        if (sReadyToThawMap != null) {
            sReadyToThawMap.remove(packageName);
        }
    }

    /**
     * 判断一个app是否预备解冻,时效是10分钟。
     *
     * @param packageName 指定app的包名
     * @return true，表示预备解冻了，点击app的时候可以解冻。false，还没有预备解冻，下一次点击的时候需要提示用户再次点击才能解冻。
     */
    public static boolean isReadyToThaw(String packageName) {
        if (sReadyToThawMap != null && sReadyToThawMap.containsKey(packageName)) {
            Long date = sReadyToThawMap.get(packageName);
            long currentTime = System.currentTimeMillis();
            if (date != null && currentTime - date < 1000 * 10 * 60) {
                return true;
            } else {
                // 失效的app取消掉。
                sReadyToThawMap.remove(packageName);
            }
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = false;
        } catch (PackageManager.NameNotFoundException e) {
            installed = true;
        }
        return installed;
    }

    private static BigDecimal parseApkSize(int size) {
        BigDecimal bd = new BigDecimal((double) size / (1024 * 1024));
        BigDecimal setScale = bd.setScale(2, BigDecimal.ROUND_DOWN);
        return setScale;
    }

    public static void thawApp(Context context, ArrayList<String> packageNamesList) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setAction(THAW_ACTION);
            intent.putStringArrayListExtra(FREEZE_PARAMETER, packageNamesList);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 处理app冻结的情况
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void handleAppFreeze(Context context, String packageName) {
        //app冻结的情况。
        if (isReadyToThaw(packageName)) {
            // 预备解冻的情况
            removeReadyToThaw(packageName);
            ArrayList<String> packageList = new ArrayList<>();
            packageList.add(packageName);
            thawApp(context, packageList);
        } else {
            // 第一次点击app，加入到预备解冻的队列中
            addReadyToThaw(packageName);
            String appName = getAppName(context, packageName);
            String tip = appName + context.getString(R.string.direct_freeze_tip);
            InkToastManager.showToastShort(context, tip);
        }
    }

}
