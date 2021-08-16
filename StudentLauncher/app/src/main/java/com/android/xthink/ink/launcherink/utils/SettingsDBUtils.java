package com.android.xthink.ink.launcherink.utils;

import android.content.Context;
import android.provider.Settings;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;

/**
 * 正面系统设置读写值Utils
 *
 * @author liyuyan
 * @version 1.0, 2017/10/11
 */

public class SettingsDBUtils {

    /**
     * 写入系统设置Setting值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putSystemSettingValue(Context context, String key, boolean value) {
        Settings.Secure.putInt(context.getContentResolver(), key, value ? 1 : 0);
    }

    /**
     * 读取B屏存入的系统设置Setting值
     *
     * @param context
     * @param key
     */
    public static boolean getSystemSettingValue(Context context, String key) {
        int settingValue = 0;
        //通过Key读取系统设置Setting值
        if (key.equals(InkConstants.SYSTEM_PURE_LOCK)) {
            settingValue = Settings.Secure.getInt(context.getContentResolver(), key, 0);
        } else if (key.equals(InkConstants.SYSTEM_LOCK_NOTIFICATION)) {
            settingValue = Settings.Secure.getInt(context.getContentResolver(), key, 1);
        } else if (key.equals(InkConstants.SYSTEM_LOCK_ENCRYPT)) {
            settingValue = Settings.Secure.getInt(context.getContentResolver(), key, 0);
        } else if (key.equals(InkConstants.SYSTEM_SV_SHAKE)) {
            settingValue = Settings.Secure.getInt(context.getContentResolver(), key, 0);
        } else {
            settingValue = Settings.Secure.getInt(context.getContentResolver(), key, 0);
        }
        return settingValue == 1;
    }
}
