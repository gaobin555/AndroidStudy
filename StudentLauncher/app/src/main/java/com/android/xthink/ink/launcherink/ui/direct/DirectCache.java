/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.direct;

import android.content.Context;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.SharePreferenceHelper;

/**
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/16
 */
public class DirectCache {
    private static DirectCache instance;
    private final SharePreferenceHelper mSharePreferenceHelper;

    private DirectCache(Context context) {
        mSharePreferenceHelper = SharePreferenceHelper.getInstance(context);
    }

    public static DirectCache getInstance(Context context) {
        if (instance == null) {
            instance = new DirectCache(context);
        }
        return instance;
    }

    /**
     * 保存请求成功的时间
     *
     * @param time 时间戳
     */
    public void saveRequestTime(long time) {
        mSharePreferenceHelper.setLongValue(InkConstants.SP_DIRECT_APP_LAST_TIME, time);
    }

    public void saveSysVersion(String sysVersion) {
        mSharePreferenceHelper.setStringValue(InkConstants.SP_DIRECT_APP_LAST_VERSION, sysVersion);
    }

    public String getLastSysVersion() {
        return mSharePreferenceHelper.getStringValue(InkConstants.SP_DIRECT_APP_LAST_VERSION);
    }

    public long getLastRequestTime() {
        return mSharePreferenceHelper.getLongValue(InkConstants.SP_DIRECT_APP_LAST_TIME);
    }

}

