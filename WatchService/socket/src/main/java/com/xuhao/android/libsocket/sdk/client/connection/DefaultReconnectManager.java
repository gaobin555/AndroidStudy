package com.xuhao.android.libsocket.sdk.client.connection;

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

import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.libsocket.impl.exceptions.PurifyException;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;

import java.util.Iterator;

/**
 * Created by xuhao on 2017/6/5.
 */

public class DefaultReconnectManager extends AbsReconnectionManager {
    /**
     * 默认重连时间(后面会以指数次增加)
     */
    private static final long DEFAULT = 5 * 1000;
    /**
     * 最大连接失败次数,不包括断开异常
     */
    private static final int MAX_CONNECTION_FAILED_TIMES = 12;
    /**
     * 延时连接时间
     */
    private long mReconnectTimeDelay = DEFAULT;
    /**
     * 连接失败次数,不包括断开异常
     */
    private int mConnectionFailedTimes = 0;

    public static final int ALARM_RECONNET_REQUEST_CODE = 10002;
    public static final String ACTION_ALARM_RECONNECT = "android.intent.action.ALARM_RECONNECT";
    private AlarmManager mAlarmManager;
    private ReconnectAlarmReceiver mReconnectAlarmReceiver;
    private static DefaultReconnectManager mDefaultReconnectManager;

    /*private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mDetach) {
                SLog.i("ReconnectionManager already detached by framework.We decide gave up this reconnection mission!");
                return;
            }
            boolean isHolden = mConnectionManager.getOption().isConnectionHolden();

            if (!isHolden) {
                detach();
                return;
            }
            ConnectionInfo info = mConnectionManager.getConnectionInfo();
            SLog.i("Reconnect the server " + info.getIp() + ":" + info.getPort() + " ...");
            if (!mConnectionManager.isConnect()) {
                mConnectionManager.connect();
            }
        }
    };*/

    DefaultReconnectManager() {
        mAlarmManager = (AlarmManager) OkSocket.getContext().getSystemService(Context.ALARM_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ALARM_RECONNECT);
        mReconnectAlarmReceiver = new ReconnectAlarmReceiver();
        OkSocket.getContext().registerReceiver(mReconnectAlarmReceiver, intentFilter);
        Log.i("DefaultReconnectManager", "init ReconnectAlarmReceiver");
    }

    public synchronized static DefaultReconnectManager getInstance() {
        if (mDefaultReconnectManager == null) {
            mDefaultReconnectManager = new DefaultReconnectManager();
        }
        return mDefaultReconnectManager;
    }

    private void reconnect() {
        if (mDetach) {
            SLog.i("ReconnectionManager already detached by framework.We decide gave up this reconnection mission!");
            return;
        }
        boolean isHolden = mConnectionManager.getOption().isConnectionHolden();

        if (!isHolden) {
            detach();
            return;
        }
        ConnectionInfo info = mConnectionManager.getConnectionInfo();
        SLog.i("Reconnect the server " + info.getIp() + ":" + info.getPort() + " ...");
        if (!mConnectionManager.isConnect()) {
            mConnectionManager.connect();
        }
    }

    @Override
    public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
        if (isNeedReconnect(e)) {//break with exception
            reconnectDelay();
        } else {
            reset();
        }
    }

    @Override
    public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
        reset();
    }

    @Override
    public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
        if (e != null) {
            mConnectionFailedTimes++;
            if (mConnectionFailedTimes > MAX_CONNECTION_FAILED_TIMES) {
                reset();
                //连接失败达到阈值,需要切换备用线路.(依照现有DEFAULT值和指数增长逻辑,会在4分多钟时切换备用线路)
                ConnectionInfo originInfo = mConnectionManager.getConnectionInfo();
                ConnectionInfo backupInfo = originInfo.getBackupInfo();
                if (backupInfo != null) {
                    ConnectionInfo bbInfo = new ConnectionInfo(originInfo.getIp(), originInfo.getPort());
                    backupInfo.setBackupInfo(bbInfo);
                    if (!mConnectionManager.isConnect()) {
                        mConnectionManager.switchConnectionInfo(backupInfo);
                        SLog.i("Prepare switch to the backup line " + backupInfo.getIp() + ":" + backupInfo.getPort() + " ...");
                        mConnectionManager.connect();
                    }
                } else {
                    reconnectDelay();
                }
            } else {
                reconnectDelay();
            }
        }
    }

    /**
     * 是否需要重连
     *
     * @param e
     * @return
     */
    private boolean isNeedReconnect(Exception e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            if (e != null && !(e instanceof PurifyException)) {//break with exception
                Iterator<Class<? extends Exception>> it = mIgnoreDisconnectExceptionList.iterator();
                while (it.hasNext()) {
                    Class<? extends Exception> classException = it.next();
                    if (classException.isAssignableFrom(e.getClass())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private void reset() {
        //mHandler.removeCallbacksAndMessages(null);
        mReconnectTimeDelay = DEFAULT;
        mConnectionFailedTimes = 0;
    }

    private void reconnectDelay() {
        //mHandler.removeCallbacksAndMessages(null);
        //mHandler.sendEmptyMessageDelayed(0, mReconnectTimeDelay);
        createPulseAlarm(mReconnectTimeDelay);
        SLog.i("Reconnect after " + mReconnectTimeDelay + " mills ...");
        mReconnectTimeDelay = mReconnectTimeDelay * 2;//5+10+20+40 = 75 4次
        if (mReconnectTimeDelay >= DEFAULT * 10) {//DEFAULT * 10 = 50
            mReconnectTimeDelay = DEFAULT;
        }
    }

    @Override
    public void detach() {
        mDetach = true;
        //mHandler.removeCallbacksAndMessages(null);
        super.detach();
    }

    public void createPulseAlarm(long delayTime) {
        Intent intent = new Intent(ACTION_ALARM_RECONNECT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(OkSocket.getContext(), ALARM_RECONNET_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayTime, pendingIntent);
    }

    public class ReconnectAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DefaultReconnectManager", "receive ALARM_RECONNECT");
            reconnect();
        }
    }
}
