package com.android.xthink.ink.launcherink.common.utils;

/**
 * Created by liyuyan on 2017/3/7.
 */

public class TextUtils {

    /**
     * 安全的 String 返回
     *
     * @param prefix 默认字段
     * @param obj    拼接字段 (需检查)
     */
    public static String safeText(String prefix, String obj) {
        if (android.text.TextUtils.isEmpty(obj)) return "";
        return android.text.TextUtils.concat(prefix, obj).toString();
    }

    public static String safeText(String msg) {
        if (null == msg) {
            return "";
        }
        return safeText("", msg);
    }
}
