/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 请描述功能
 *
 * @author liyuyan
 * @version 1.0, 2018/7/4
 */

public class TimeReceiver extends BroadcastReceiver {

    private ReceiveListener mReceiveListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.time_minute".equals(intent.getAction())) {
            if (mReceiveListener != null) {
                mReceiveListener.receiveMinuteTick();
            } else {
                Log.d("TimeReceiver", "mReceiveListener ==null");
            }
            Log.d("TimeReceiver", "ACTION_TIME_TICK");
        }
    }

    public interface ReceiveListener {
        void receiveMinuteTick();
    }

    public void setReceiveListener(ReceiveListener receiveListener) {
        mReceiveListener = receiveListener;
    }

}
