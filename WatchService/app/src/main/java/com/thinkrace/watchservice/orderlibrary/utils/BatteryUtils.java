package com.thinkrace.watchservice.orderlibrary.utils;
/*
 *  @项目名：  WatchService
 *  @包名：    com.thinkrace.watchservice.utils
 *  @文件名:   BatteryUtils
 *  @创建者:   win10
 *  @创建时间:  2018/4/13 17:01
 *  @描述：    TODO
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryUtils {

    public static String getBatteryLevel(Context paramContext) {
        Intent intent = paramContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int battery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if(battery < 10) {
            return "00"+battery;
        } else if(battery < 100){
            return "0"+battery;
        }
        return String.valueOf(battery);
    }
}
