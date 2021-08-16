package com.android.xthink.ink.launcherink.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.SimpleArrayMap;

import java.util.Set;

/**
 * @author liyuyan@X-Thinks.com
 * @version 1.0, 2016/12/22
 */
public class SharePreferenceHelper {
    private static SimpleArrayMap<String, SharePreferenceHelper> SP_UTILS_MAP = new SimpleArrayMap<>();
    public SharedPreferences preferences;

    private SharePreferenceHelper(Context context, String name) {
        if (name != null && name.length() > 0) {
            preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        } else {
            preferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        }
    }

    /**
     * 获取SP实例
     *
     * @param spName sp名
     * @return {@link SharePreferenceHelper}
     */
    public static SharePreferenceHelper getInstance(Context context, String spName) {
        if (isSpace(spName)) {
            spName = context.getApplicationContext().getPackageName();
        }
        SharePreferenceHelper spHelper = SP_UTILS_MAP.get(spName);
        if (spHelper == null) {
            spHelper = new SharePreferenceHelper(context, spName);
            SP_UTILS_MAP.put(spName, spHelper);
        }
        return spHelper;
    }

    public static SharePreferenceHelper getInstance(Context context) {
        return getInstance(context, null);
    }

    public void setStringValue(String key, String value) {
        this.preferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return this.preferences.getString(key, "");
    }

    public String getStringValue(String key, String defaultValue) {
        return this.preferences.getString(key, defaultValue);
    }

    public void setBooleanValue(String key, Boolean value) {
        this.preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key) {
        return this.preferences.getBoolean(key, false);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return this.preferences.getBoolean(key, defaultValue);
    }

    public void setLongValue(String key, long value) {
        this.preferences.edit().putLong(key, value).apply();
    }

    public long getLongValue(String key) {
        return this.preferences.getLong(key, 0L);
    }

    public void setIntValue(String key, int value) {
        this.preferences.edit().putInt(key, value).apply();
    }

    public int getIntValue(String key) {
        return this.preferences.getInt(key, -1);
    }

    public int getIntValue(String key, int defalutValue) {
        return this.preferences.getInt(key, defalutValue);
    }

    public void setFloatValue(String key, float value) {
        this.preferences.edit().putFloat(key, value).apply();
    }

    public float getFloatValue(String key, float value) {
        return this.preferences.getFloat(key, value);
    }

    public void setSetValue(String key, Set<String> values) {
        this.preferences.edit().putStringSet(key, values).apply();
    }

    public Set<String> getSetValue(String key) {
        return this.preferences.getStringSet(key, null);
    }

    public void removeValue(String key) {
        preferences.edit().remove(key).apply();
    }

    public void removeAllValues() {
        preferences.edit().clear().apply();
    }

    public boolean contains(String key) {
        return preferences.contains(key);
    }

    /**
     * 校验字符非空,且不包含空白字符
     *
     * @param s string
     * @return Boolean 是否非空
     */
    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
