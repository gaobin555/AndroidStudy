package com.android.xthink.ink.launcherink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.xthink.ink.launcherink.common.utils.MyLog;

public class NotificationReceiver extends BroadcastReceiver {

    private String TAG = "NotificationReceiver";
    public static final String ACTION_UPDATE_NOTIIFICATION = "action_new_notification";

    private NotificationOnListener mNotificationOnListener;

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_UPDATE_NOTIIFICATION)) {
            if(mNotificationOnListener != null) {
                //MyLog.d(TAG, "更新通知");
                mNotificationOnListener.updateNotification();
            }
        }
    }

    public void registerNotificationReceiver(Context context, NotificationOnListener notificationOnListener) {
        try {
            this.mNotificationOnListener = notificationOnListener;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_UPDATE_NOTIIFICATION);
            MyLog.d(TAG, "注册通知广播...");
            context.registerReceiver(this, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterNotificationReceiver(Context context) {
        try {
            context.unregisterReceiver(this);
            MyLog.d(TAG, "注销通知广播接收者...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface NotificationOnListener {
        public void updateNotification();
    }
}