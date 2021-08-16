package com.xuhao.android.libsocket.impl.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xuhao.android.libsocket.impl.exceptions.DogDeadException;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.bean.IPulse;
import com.xuhao.android.libsocket.sdk.client.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

/**
 * Created by xuhao on 2017/5/18.
 */

public class PulseManager implements IPulse {
    /**
     * 心跳事件
     */
    private static final int PULSE_WHAT = 0;
    /**
     * 喂养事件
     */
    private static final int FEED_WHAT = 1;
    /**
     * 数据包发送器
     */
    private IConnectionManager mManager;
    /**
     * 心跳数据包
     */
    private IPulseSendable mSendable;
    /**
     * 连接参数
     */
    private OkSocketOptions mOkOptions;
    /**
     * 当前频率
     */
    private long mCurrentFrequency;
    /**
     * 当前的线程模式
     */
    private OkSocketOptions.IOThreadMode mCurrentThreadMode;
    /**
     * 是否死掉
     */
    private boolean isDead = false;
    /**
     * 允许遗漏的次数
     */
    private int mLoseTimes = -1;

    public static final int ALARM_PULSE_SEND_REQUEST_CODE = 10001;
    public static final String ACTION_ALARM_PULSE_SEND = "android.intent.action.ALARM_PULSE_SEND";
    private AlarmManager mAlarmManager;
    private PulseAlarmReceiver mPulseAlarmReceiver;
    private static PulseManager mPulseManager;
    /**
     * 脉搏计时器
     */
    private Handler mPulseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDead) {
                return;
            }
            switch (msg.what) {
                /*case PULSE_WHAT: {
                    if (mManager != null && mSendable != null) {
                        if (mOkOptions.getPulseFeedLoseTimes() != -1
                                && ++mLoseTimes >= mOkOptions.getPulseFeedLoseTimes()) {
                            mManager.disconnect(
                                    new DogDeadException("you need feed dog on time,otherwise he will die"));
                        } else {
                            Log.i("PulseManager", "[IWAP03]client send pulse data");
                            mManager.send(mSendable);
                            pulse();
                        }
                    }
                    break;
                }*/
                case FEED_WHAT: {
                    if (getLoseTimes() >= 0) {
                        Log.e("PulseManager", "due to pulse data response timeout, disconnect and reconnection!!!");
                        disconnect();
                    }
                    break;
                }
            }
        }
    };

    public synchronized static PulseManager getInstance(IConnectionManager manager, OkSocketOptions okOptions) {
        if (mPulseManager == null) {
            mPulseManager = new PulseManager(manager, okOptions);
        }
        return mPulseManager;
    }

    PulseManager(IConnectionManager manager, OkSocketOptions okOptions) {
        mManager = manager;
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();

        mAlarmManager = (AlarmManager) OkSocket.getContext().getSystemService(Context.ALARM_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ALARM_PULSE_SEND);
        mPulseAlarmReceiver = new PulseAlarmReceiver();
        OkSocket.getContext().registerReceiver(mPulseAlarmReceiver, intentFilter);
        Log.i("PulseManager", "init PulseAlarmReceiver");
    }

    public IPulse setPulseSendable(IPulseSendable sendable) {
        if (sendable != null) {
            mSendable = sendable;
        }
        return this;
    }

    public IPulseSendable getPulseSendable() {
        return mSendable;
    }

    @Override
    public void pulse() {
        privateDead();
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            mCurrentFrequency = mOkOptions.getPulseFrequency();
            mCurrentFrequency = mCurrentFrequency < 1000 ? 1000 : mCurrentFrequency;//间隔最小为一秒
            /*if (mPulseHandler != null) {
                mPulseHandler.sendEmptyMessageDelayed(PULSE_WHAT, mCurrentFrequency);
            }*/

            if (mManager != null && mSendable != null) {
                Log.i("PulseManager", "mOkOptions.getPulseFeedLoseTimes = " + mOkOptions.getPulseFeedLoseTimes() + ", mLoseTimes = " + getLoseTimes());
                if (++mLoseTimes >= mOkOptions.getPulseFeedLoseTimes()) {
                    disconnect();
                } else {
                    Log.i("PulseManager", "[IWAP03]client send pulse data, mLoseTimes = " + getLoseTimes());
                    mManager.send(mSendable);
                    mPulseHandler.sendEmptyMessageDelayed(FEED_WHAT, 3000);
                    createPulseAlarm(mCurrentFrequency);
                }
            }
        }
    }

    @Override
    public void trigger() {
        privateDead();
        if (isDead) {
            return;
        }
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            /*if (mPulseHandler != null) {
                mPulseHandler.sendEmptyMessage(PULSE_WHAT);
            }*/
        }
    }

    public void dead() {
        mLoseTimes = 0;
        isDead = true;
        privateDead();
    }

    @Override
    public void feed() {
        //mPulseHandler.sendEmptyMessage(FEED_WHAT);
        if (mPulseHandler.hasMessages(FEED_WHAT)) {
            mPulseHandler.removeMessages(FEED_WHAT);
        }
        mLoseTimes = -1;
        Log.i("PulseManager", "mLoseTimes = "+ getLoseTimes() + ", feed success!!!");
    }

    private void privateDead() {
        /*if (mPulseHandler != null) {
            mPulseHandler.removeMessages(PULSE_WHAT);
        }*/
    }

    private int getLoseTimes() {
        return mLoseTimes;
    }

    private void disconnect() {
        mManager.disconnect(
                new DogDeadException("you need feed dog on time,otherwise he will die"));
        mLoseTimes = -1;
    }

    protected void setOkOptions(OkSocketOptions okOptions) {
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        /*if (mCurrentFrequency != mOkOptions.getPulseFrequency()) {
            pulse();
        }*/
    }

    public void createPulseAlarm(long currentFrequency) {
        Intent intent = new Intent(ACTION_ALARM_PULSE_SEND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(OkSocket.getContext(), ALARM_PULSE_SEND_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + currentFrequency, pendingIntent);
    }

    public class PulseAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("PulseManager", "receive ACTION_ALARM_PULSE_SEND");
            pulse();
        }
    }
}
