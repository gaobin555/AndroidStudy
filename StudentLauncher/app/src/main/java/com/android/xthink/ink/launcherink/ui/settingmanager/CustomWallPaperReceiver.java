package com.android.xthink.ink.launcherink.ui.settingmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.utils.SettingsDBUtils;

/**
 * 自定义壁纸的广播接收器
 *
 * @author wanchi
 * @version 1.0, 2017/12/13
 */
public class CustomWallPaperReceiver extends BroadcastReceiver {

    private static final String TAG = "CustomWallPaperReceiver";

    public CustomWallPaperReceiver() {
        super();
        MyLog.i(TAG, "create ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String uriStr = intent.getStringExtra("imageUri");
        MyLog.i(TAG, "onReceive: " + uriStr);
        if (!TextUtils.isEmpty(uriStr)) {
            SettingsDBUtils.putSystemSettingValue(context, InkConstants.SYSTEM_CUSTOM_WALLPAPER, true);
            SettingsDBUtils.putSystemSettingValue(context, InkConstants.SYSTEM_PURE_LOCK, false);
            SettingsDBUtils.putSystemSettingValue(context, InkConstants.SYSTEM_WALLPAPER_PUSH, false);
        }
    }
}
