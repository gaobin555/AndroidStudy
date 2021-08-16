package com.android.xthink.ink.launcherink.ui.notificationManager;

import android.app.Notification;

public interface NotificationManager {
    /**
     * @param notification 点击的通知
     */
    void onClickNotification(Notification notification, String key);
}
