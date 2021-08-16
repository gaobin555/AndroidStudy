package com.android.xthink.ink.launcherink.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;


import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.utils.Tool;

import static com.android.xthink.ink.launcherink.constants.LauncherConstants.ACTION_REQUEST_WEATHER;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.EXTRA_WEATHER;

public class AutoUpdateService extends Service {

    private static String TAG = "AutoUpdateService";

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        channel = new NotificationChannel(TAG, getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(getApplicationContext(), TAG).build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        requestWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 1000; // 每小时更新一次
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *  请求天气信息
     */
    private void requestWeather() {
        if(Tool.hasNetwork(this)) {
            Log.i(EXTRA_WEATHER, "xxx AutoUpdateService sendBroadcast:" + ACTION_REQUEST_WEATHER);
            Intent intent = new Intent(ACTION_REQUEST_WEATHER);
            sendBroadcast(intent);
        }
    }
}
