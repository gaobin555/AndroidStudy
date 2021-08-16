/*
 * Copyright 2016-2017 HiveBox.
 */

package com.android.xthink.ink.launcherink.base;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;


/**
 * 将Service变为优先级别最高的前台进程.
 */

public abstract class FGService extends Service {

    private static Notification createNotification(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new Notification.Builder(context)
                    .setContentTitle("rpc demo!")
                    .setContentText("demo content！")
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setWhen(System.currentTimeMillis())
                    .build();
        } else {
            return new Notification();
        }
    }

    protected abstract int getParentPid();

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(getParentPid(), new Notification());
        } else {
            startForeground(getParentPid(), createNotification(this.getApplicationContext()));
            //启动一个内部服务
            Intent intent = new Intent(this, InnerService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("pid", getParentPid());
            intent.putExtras(bundle);
            startService(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public static class InnerService extends Service {

        @Override
        public void onCreate() {
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Bundle bundle = intent.getExtras();
            int pid = bundle.getInt("pid");
            startForeground(pid, createNotification(this.getApplicationContext()));
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            stopForeground(true);
        }
    }


}
