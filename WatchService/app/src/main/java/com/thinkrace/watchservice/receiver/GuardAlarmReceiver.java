package com.thinkrace.watchservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thinkrace.watchservice.function.location.LocationUploadManager;


/**
 * Created by chenjia
 */

public class GuardAlarmReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "GuardAlarmReceiver";

    public static final String ACTION_SET_GUARD_ALARM_START = "android.intent.action.SET_GUARD_ALARM_START";
    public static final String ACTION_SET_GUARD_ALARM_END = "android.intent.action.SET_GUARD_ALARM_END";

    private long mCurrentLocationMode = 0;
    private LocationUploadManager mLocationUploadManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mLocationUploadManager = LocationUploadManager.instance();
        mCurrentLocationMode = mLocationUploadManager.getCurrentLocationMode();
        boolean isCancelGuardAlarm = intent.getBooleanExtra("isCancelGuardAlarm", false);
        Log.e(LOG_TAG,"intent.getAction() = " + intent.getAction() + ", isCancelGuardAlarm = " + isCancelGuardAlarm + ", mCurrentLocationMode = " + mCurrentLocationMode);
        if (intent.getAction().equals(ACTION_SET_GUARD_ALARM_START)) {
            if (isCancelGuardAlarm) {
                closeGuard();
            } else {
                openGuard();
            }
        } else if (intent.getAction().equals(ACTION_SET_GUARD_ALARM_END)) {
            closeGuard();
        }
    }

    public void openGuard() {
        mLocationUploadManager.parseInteraval(LocationUploadManager.EMERGENCY_TYPE);
    }

    public void closeGuard() {
        mLocationUploadManager.parseInteraval(LocationUploadManager.NORMAL_TYPE);
    }
}
