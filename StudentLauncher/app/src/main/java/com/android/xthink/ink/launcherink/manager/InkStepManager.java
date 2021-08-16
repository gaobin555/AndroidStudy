package com.android.xthink.ink.launcherink.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.android.xthink.ink.launcherink.GlobalDataCache;
import com.android.xthink.ink.launcherink.common.utils.DateUtil;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.SharePreferenceHelper;
import com.android.xthink.ink.launcherink.manager.event.IStepChangedListener;

/**
 * 请描述功能
 *
 * @author luoyongjie
 * @version 1.0, 4/24/2017
 */
public class InkStepManager implements SensorEventListener {

    private static final String TAG = "InkStepManager";
    private String mLastDate;
    private int mLastSensorStepCount = 0;
    private int mTodayStepCount = 0;
    private Context context;
    private IStepChangedListener mStepChangedListener;
    private SensorManager sensorManager;
    private BroadcastReceiver mStepReceiver;
    private static final String EPD_UNLOCK_ACTION = "android.intent.action.EPD_UNLOCK";

    public InkStepManager(Context context) {
        this.context = context;
    }

    public void startStepDetect() {
        initTodayData();
        beginCountStep();
        initBroadcastReceiver();
        //TODO sharedPreference保存的key会越来越多的问题，是否需要用于做统计
    }

    public void stopStepDetect() {
        if (null != sensorManager) {
            sensorManager.unregisterListener(this);
        }

        if (mStepReceiver != null) {
            context.unregisterReceiver(mStepReceiver);
        }

    }


    /**
     * 添加传感器监听
     */
    private void beginCountStep() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            return;
        }
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            MyLog.e(TAG, "Count sensor not available !");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isNewDay()) {
            MyLog.i(TAG, "与上一次的记步日期不一致，步数清零");
            initNewDayData();
        } else {
            int currentSensorStepCount = (int) event.values[0];
            if (currentSensorStepCount > mLastSensorStepCount && mLastSensorStepCount != 0) {
                mTodayStepCount += currentSensorStepCount - mLastSensorStepCount;
            }
            mLastSensorStepCount = currentSensorStepCount;
        }

        mLastDate = DateUtil.getTodayDate();
        notifyAndSaveStepChanged(mTodayStepCount);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void initBroadcastReceiver() {

        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);

        //监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED);

        //监听锁屏变化
        filter.addAction(EPD_UNLOCK_ACTION);

        mStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    MyLog.i(TAG, "锁屏保存步数");
                    saveStep();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    MyLog.i(TAG, " 关机保存步数");
                    saveStep();
                } else if (Intent.ACTION_DATE_CHANGED.equals(action)) {//日期变化步数重置为0
                    //TODO 支付宝计步改时间后会保留当天数据
                    MyLog.i(TAG, " 日期改变，记步清零");
//                    saveStep(DateUtil.getTodayDate(), mTodayStepCount);
                    if (isNewDay()) {
                        initNewDayData();
                    }
                } else if (EPD_UNLOCK_ACTION.equals(action)) {
                    //同步SystemUI步数
                    int systemUiStep = intent.getIntExtra("STEPCOUNT", 0);
                    if (systemUiStep > 0) {
                        mTodayStepCount = systemUiStep;
                        notifyAndSaveStepChanged(mTodayStepCount);
                        MyLog.i(TAG, "systemUiStep :" + systemUiStep);
                    }
                }
            }
        };
        context.registerReceiver(mStepReceiver, filter);
    }

    private void saveStep() {
        int stepCount = GlobalDataCache.getInstance().getCurrentStepCount();
        String date = GlobalDataCache.getInstance().getStepDate();
        SharePreferenceHelper.getInstance(context).setIntValue(date, stepCount);
    }

    /**
     * 监听晚上0点变化初始化数据
     */
    private boolean isNewDay() {
        return !mLastDate.equals(DateUtil.getTodayDate());
    }

    /**
     * 初始化当天的步数
     */
    private void initTodayData() {
        String currentDate = DateUtil.getTodayDate();
        int step = getTodayStep(currentDate);

        mLastDate = currentDate;
        if (step > 0) {
            mTodayStepCount = step;
        } else {
            mTodayStepCount = 0;
        }
        notifyAndSaveStepChanged(mTodayStepCount);
    }

    /**
     * 初始化当天的步数
     */
    private void initNewDayData() {
        mLastDate = DateUtil.getTodayDate();
        mTodayStepCount = 0;
        notifyAndSaveStepChanged(mTodayStepCount);
        saveStep();
    }

    public int getTodayStep(String todayDate) {
        mTodayStepCount = SharePreferenceHelper.getInstance(context).getIntValue(todayDate, 0);
        return mTodayStepCount;
    }

    public void setStepChangedListener(IStepChangedListener mStepChangedListener) {
        this.mStepChangedListener = mStepChangedListener;
    }

    public void saveTodayStep() {
        saveStep();
    }

    private void notifyAndSaveStepChanged(int count) {
        String date = DateUtil.getTodayDate();
        GlobalDataCache.getInstance().setCurrentStepCount(count);
        GlobalDataCache.getInstance().setStepDate(date);

        if (null != mStepChangedListener) {
            mStepChangedListener.onStepInitData(count);
        }
    }
}
