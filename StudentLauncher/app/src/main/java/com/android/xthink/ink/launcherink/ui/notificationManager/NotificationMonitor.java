
package com.android.xthink.ink.launcherink.ui.notificationManager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.receiver.NotificationReceiver;

import java.util.ArrayList;
import java.util.List;

public class NotificationMonitor extends NotificationListenerService {
    private static final String TAG = "NotificationMonitor";
    private static final String TAG_PRE = "[" + NotificationMonitor.class.getSimpleName() + "] ";
    private static final String DIALER_PACKAGE_NAME = "com.android.dialer";
    private static final String MESSAGE_PACKAGE_NAME = "com.android.mms";
    private static final String EMAIL_PACKAGE_NAME = "com.android.email";
    private static final int EVENT_UPDATE_CURRENT_NOS = 0;
    public static final String ACTION_NLS_CONTROL = "com.seven.notificationlistenerdemo.NLSCONTROL";
    public static List<StatusBarNotification[]> mCurrentNotifications = new ArrayList<StatusBarNotification[]>();
    public static int mCurrentNotificationsCounts = 0;
    public static StatusBarNotification mPostedNotification;
    public static StatusBarNotification mRemovedNotification;
    private CancelNotificationReceiver mReceiver = new CancelNotificationReceiver();

    private Handler mMonitorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_UPDATE_CURRENT_NOS:
                    logNLS("EVENT_UPDATE_CURRENT_NOS");
                    updateCurrentNotifications();
                    break;
                default:
                    break;
            }
        }
    };

    class CancelNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action;
            if (intent != null && intent.getAction() != null) {
                action = intent.getAction();
                if (action.equals(ACTION_NLS_CONTROL)) {
                    String command = intent.getStringExtra("command");
                    if (TextUtils.equals(command, "cancel_last")) {
                        if (mCurrentNotifications != null && mCurrentNotificationsCounts >= 1) {
                            for (int i = mCurrentNotificationsCounts - 1; i >= 0; i--) {
                                StatusBarNotification sbnn = getCurrentNotifications()[i];
                                logNLS("i = " + i + " PackageName = " + sbnn.getPackageName() + " Key = " + sbnn.getKey());
                                if (sbnn.getPackageName().equals("android")) {
                                    continue;
                                } else {
                                    cancelNotification(sbnn.getKey());
                                    break;
                                }
                            }
                        }
                    } else if (TextUtils.equals(command, "cancel_all")) {
                        cancelAllNotifications();
                    } else if (command != null){
                        logNLS("cancelNotification key = " + command);
                        cancelNotification(command);
                    }
                }
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        logNLS("onCreate...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NLS_CONTROL);
        registerReceiver(mReceiver, filter);
        mMonitorHandler.sendMessage(mMonitorHandler.obtainMessage(EVENT_UPDATE_CURRENT_NOS));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // a.equals("b");
        logNLS("onBind...");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        updateCurrentNotifications();
        logNLS("onNotificationPosted...");
        logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        mPostedNotification = sbn;

        /*
         * Bundle extras = sbn.getNotification().extras; String
         * notificationTitle = extras.getString(Notification.EXTRA_TITLE);
         * Bitmap notificationLargeIcon = ((Bitmap)
         * extras.getParcelable(Notification.EXTRA_LARGE_ICON)); Bitmap
         * notificationSmallIcon = ((Bitmap)
         * extras.getParcelable(Notification.EXTRA_SMALL_ICON)); CharSequence
         * notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
         * CharSequence notificationSubText =
         * extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
         * Log.i("SevenNLS", "notificationTitle:"+notificationTitle);
         * Log.i("SevenNLS", "notificationText:"+notificationText);
         * Log.i("SevenNLS", "notificationSubText:"+notificationSubText);
         * Log.i("SevenNLS",
         * "notificationLargeIcon is null:"+(notificationLargeIcon == null));
         * Log.i("SevenNLS",
         * "notificationSmallIcon is null:"+(notificationSmallIcon == null));
         */
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        updateCurrentNotifications();
        logNLS("removed...");
        logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        mRemovedNotification = sbn;
    }

    private void updateCurrentNotifications() {
        MyLog.d(TAG, "NotificationMonitor updateCurrentNotifications");
        try {
            StatusBarNotification[] activeNos = getActiveNotifications();
            StatusBarNotification[] tempNos = new StatusBarNotification[activeNos.length];
            int i = 0;
            for(StatusBarNotification statusBarNotification : activeNos) {
                Notification notification = statusBarNotification.getNotification();
                String packagename = statusBarNotification.getPackageName();
//                String tag = statusBarNotification.getTag();
//                if (tag != null && tag.contains("com.android.fakeoemfeatures")) {
//                    continue;
//                }
                MyLog.d(TAG, "xxx Notification package name = " + packagename);
                if (resolveText(notification) != null
                        && resolveTitle(notification) != null
                        && !MESSAGE_PACKAGE_NAME.equals(packagename)
                        && !DIALER_PACKAGE_NAME.equals(packagename)) {
                    tempNos[i] = statusBarNotification;
                    i++;
                }
            }
            int showNum = i;
            StatusBarNotification[] showNos = new StatusBarNotification[i];
            for (int j = 0; j < showNum; j ++) {
                showNos[j] = tempNos[j];
            }
            if (mCurrentNotifications.size() == 0) {
                mCurrentNotifications.add(null);
            }
            mCurrentNotifications.set(0, showNos);
            mCurrentNotificationsCounts = showNum;
            sendUpdateNotificationBroadcast(this);
        } catch (Exception e) {
            logNLS("Should not be here!!");
            e.printStackTrace();
        }
    }

    private CharSequence resolveText(Notification notification) {
        CharSequence contentText = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        if (contentText == null) {
            contentText = notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }
        return contentText;
    }

    private CharSequence resolveTitle(Notification notification) {
        CharSequence titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        if (titleText == null) {
            titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE_BIG);
        }
        return titleText;
    }

    public static StatusBarNotification[] getCurrentNotifications() {
        if (mCurrentNotifications.size() == 0) {
            logNLS("mCurrentNotifications size is ZERO!!");
            return null;
        }
        return mCurrentNotifications.get(0);
    }

    private static void sendUpdateNotificationBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(NotificationReceiver.ACTION_UPDATE_NOTIIFICATION);
        context.sendBroadcast(intent);
    }

    private static void logNLS(Object object) {
        MyLog.i(TAG, TAG_PRE + object);
    }

}
