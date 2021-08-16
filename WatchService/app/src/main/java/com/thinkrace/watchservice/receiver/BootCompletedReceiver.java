package com.thinkrace.watchservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thinkrace.watchservice.orderlibrary.LocationService;
import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Administrator on 2018\4\26 0026.
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String ACTION_BOOT="android.intent.action.BOOT_COMPLETED";
    private static final String ACTION_SHUTDOWN="android.intent.action.ACTION_SHUTDOWN";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BOOT)) {
            Log.e("dmm","BOOT_COMPLETED");
            LocationService.pull(context);
        } else if (intent.getAction().equals(ACTION_SHUTDOWN)) {
            Log.e("dmm","ACTION_SHUTDOWN");
            HashMap<String, String> controllingApps = OrderUtil.getInstance().getControllingApps();
            if (controllingApps.size() > 0) {
                Iterator<Map.Entry<String, String>> iter = controllingApps.entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    OrderUtil.getInstance().recordAPTEFile((String)entry.getKey(), (String)entry.getValue(), "2"); //state默认为2，即该APP未处理完成
                }
            }
        }
    }
}
