/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.multitask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.ui.home.MainActivity;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

/**
 * 请描述功能
 *
 * @author liyuyan
 * @version 1.0, 2017/8/3
 */

public class MultitaskHandler {

    private static final String TAG = "MultitaskHandler";

    private MainActivity mActivity;

    public MultitaskHandler(MainActivity activity) {
        mActivity = activity;
    }

    public void registerBroadcast() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction("action_multitask_page");
        mActivity.registerReceiver(mBroadcastReceiver, homeFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action_multitask_page")) {
                Intent mEpdHomeIntent = new Intent(Intent.ACTION_MAIN, null);
                mEpdHomeIntent.addCategory("com.yotadevices.intent.category.EPD_HOME");
                mEpdHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                int id = intent.getIntExtra("pageId", -1);
                MyLog.i(TAG, "onReceive: sendId:" + id);
                mEpdHomeIntent.putExtra("pageId", id);
                InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
                mActivity.startActivity(mEpdHomeIntent);
            }
        }
    };


    public void stopBroadcast() {
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }

}