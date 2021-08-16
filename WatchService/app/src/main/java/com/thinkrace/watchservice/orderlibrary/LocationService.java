package com.thinkrace.watchservice.orderlibrary;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;

import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;
import com.xuhao.android.common.utils.SLog;

public class LocationService extends Service {
    private OrderUtil orderUtil;


    public static void pull(Context context) {
        context.startService(new Intent(context, LocationService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        orderUtil = OrderUtil.getInstance();
        BatteryBroadcastReciver reciver = new BatteryBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(reciver, intentFilter);
        SLog.e("onCreate 心跳服务已启动" );
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SLog.e("onStartCommand" );
        orderUtil.startSocket();
        orderUtil.setServiceStateStart();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        orderUtil.stopSocket();

        Intent service = new Intent(getApplicationContext(), LocationService.class);
        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startService(service);
    }

    public static int RESERVE_POWER_THRESHOLD = 5;
    public class BatteryBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                int level = intent.getIntExtra("level", 0);
                int total = intent.getIntExtra("scale", 100);
                float batteryLevel = (level*100)/total;
                SLog.e("当前电量："+ level + ", 总电量：" + total + ", 百分比：" + batteryLevel + "%");
                orderUtil.runReservePower(context, level);
            }
        }
    }
}
