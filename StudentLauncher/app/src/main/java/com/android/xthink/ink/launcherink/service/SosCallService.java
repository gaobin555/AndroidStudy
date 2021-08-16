package com.android.xthink.ink.launcherink.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseCallState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import static com.android.xthink.ink.launcherink.ui.toolsmanager.ToolsManagerActivity.sosNumbers;

public class SosCallService extends Service {
    private static final String TAG = SosCallService.class.getSimpleName();

    private static int LastCallState = 0;

    private static final int SOS_CALL_NEXT = 0;

    private static int DialingSOS = 0;

    private static final String[] MCCMNC_TABLE_TYPE_CT = {"45502", "46003", "46011", "46012", "46013"};

    private boolean isCtCard = false;

    TelephonyManager mTelephonyManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SOS_CALL_NEXT:
                    callPhone();
                    break;
            }
        }
    };

    public SosCallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d( TAG, "gaobin onStartCommand...startId = " + startId);
        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String operator = mTelephonyManager.getNetworkOperator();
        isCtCard = isCTCard(operator);
        Log.d( TAG, "gaobin isCTCard = " + isCtCard + ", sosNumbers.length = " + sosNumbers.length);
        if (DialingSOS < sosNumbers.length) {
            setTellisten();
            callPhone();
        } else {
            stopSelf();
            DialingSOS = 0;
        }

        return Service.START_NOT_STICKY;
    }

    private void callPhone(){
        if (!TextUtils.isEmpty(sosNumbers[DialingSOS])) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + sosNumbers[DialingSOS]);
            Log.i(TAG, "gaobin Call : " + sosNumbers[DialingSOS]);
            intent.setData(data);
            startActivity(intent);
            DialingSOS++;
        } else {
            DialingSOS++;
            handler.sendEmptyMessage(SOS_CALL_NEXT);
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "gaobin onCreate...");
        super.onCreate();

        NotificationChannel channel = new NotificationChannel("sos", "sos", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return;
        manager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, "sos")
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .build();
        startForeground(101, notification);
    }

    private void setTellisten() {
        mTelephonyManager.listen(new PhoneStateListener(){
            /**
             * 当有精确通话状态时回调
             * Callback invoked when precise device call state changes
             * @hide 隐藏api,给系统app使用的
             */
            //@Override
            public void onPreciseCallStateChanged(PreciseCallState callState) {
                //当有精确通话状态时回调
                int FCallState = callState.getForegroundCallState();
                Log.d(TAG,"xxx gaobin callState = " + callState.toString());
                if(FCallState == 1) {
                    DialingSOS = 0;
                    stopSelf();
                } else if ((FCallState == 7 && LastCallState == 4) || (FCallState == 0 && LastCallState == 4)) {
                    if (DialingSOS < sosNumbers.length) {
                        handler.sendEmptyMessage(SOS_CALL_NEXT);
                    } else {
                        DialingSOS = 0;
                        stopSelf();
                    }
                }
                LastCallState = FCallState;
            }
        }, 0x00000800);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d( TAG, "gaobin onDestroy...");
    }

    private static boolean isCTCard(String mccmnc) {
        if (!TextUtils.isEmpty(mccmnc)) {
            Log.d(TAG, "isCTCard, simOperator =" + mccmnc);
            for (String str : MCCMNC_TABLE_TYPE_CT) {
                if (str.equals(mccmnc)) {
                    return true;
                }
            }
        }
        return false;
    }
}
