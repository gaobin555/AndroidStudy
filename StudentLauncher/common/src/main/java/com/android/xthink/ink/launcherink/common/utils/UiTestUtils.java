package com.android.xthink.ink.launcherink.common.utils;

/**
 * ui自动化测试工具
 *
 * @author wanchi
 * @version 1.0, 2018/2/1
 */

public class UiTestUtils {
    private static final String TAG = "ui_automator";

    public static void tag(String content) {
        MyLog.i(TAG, "自动化测试(背屏桌面)：" + content);
    }
}
