package com.thinkrace.watchservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ClassForbiddenAlarmReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "ForbiddenAlarmReceiver";
    public static final String ACTION_SET_CLASSFORBIDDEN_ALARM_START = "android.intent.action.ACTION_SET_CLASSFORBIDDEN_ALARM_START";
    public static final String ACTION_SET_CLASSFORBIDDEN_ALARM_STOP = "android.intent.action.ACTION_SET_CLASSFORBIDDEN_ALARM_STOP";

    public static final String ACTION_CLASS_FORBIDDEN = "com.android.hotpeper.ACTION_CLASS_FORBIDDEN";
    public static final String CLASS_FORBIDDEN_STATE = "class_forbidden_state";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(LOG_TAG, "action = "  + action);

        Intent classForbidden = new Intent(ACTION_CLASS_FORBIDDEN);
        classForbidden.setFlags(intent.getFlags()| 0x01000000);

        if (ACTION_SET_CLASSFORBIDDEN_ALARM_START.equals(action)) {
            classForbidden.putExtra(CLASS_FORBIDDEN_STATE, 1);
            context.sendBroadcast(classForbidden);

        } else if (ACTION_SET_CLASSFORBIDDEN_ALARM_STOP.equals(action)) {
            classForbidden.putExtra(CLASS_FORBIDDEN_STATE, 0);
            context.sendBroadcast(classForbidden);
        }
    }
}
