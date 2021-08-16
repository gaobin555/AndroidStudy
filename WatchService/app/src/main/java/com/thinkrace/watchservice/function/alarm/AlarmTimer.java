package com.thinkrace.watchservice.function.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.thinkrace.watchservice.KApplication;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.SPUtils;
import com.xuhao.android.common.constant.SPConstant;

import java.util.Calendar;


/**
 * @author mare
 * @Description:TODO
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/9/13
 * @time 20:20
 */
public class AlarmTimer {

    private static final long DEFAULT_CONFIRMED__UPLOAD_FREQUENCY = 10 * 60 * 1000;

    //===============begin============
    public static void setLocationAlarmStart(Context context) {
        AlarmEntity entity = new AlarmEntity(AlarmEntity.Type.LocateStart);
        cancelAlarmTimer(context, entity);
        entity.setRepeate(true);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        entity.setFirstStartTime(calendar.getTimeInMillis());
        entity.setInterval(1000 * 3);
        setAlarmTimer(context, entity);
    }

    public static void setLocationAlarmStop(Context context) {
        AlarmEntity stopAlarm = new AlarmEntity(AlarmEntity.Type.LocateStop);
        cancelAlarmTimer(context, stopAlarm);
        stopAlarm.setRepeate(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        stopAlarm.setFirstStartTime(calendar.getTimeInMillis() + 1000 * 60 * 3);
        setAlarmTimer(context, stopAlarm);
    }
    //==================end===========

    /**
     * 开启固定频率上传
     *
     * @param context
     */
    public static void startConfirmedFrequencyUpload(Context context) {
        AlarmTimer.cancelAlarmTimer(KApplication.sContext, new AlarmEntity(AlarmEntity.Type.CONFIRMED_FREQUENCY_UPLOAD));//取消之前的定时器
        AlarmEntity alarm = new AlarmEntity(AlarmEntity.Type.CONFIRMED_FREQUENCY_UPLOAD);
        alarm.setRepeate(true);
        long interval = SPUtils.getInstance().getLong(SPConstant.CURRENT_INTERVAL, DEFAULT_CONFIRMED__UPLOAD_FREQUENCY);
        LogUtils.i("固定频率上传间隔 " + interval);
        alarm.setInterval(interval);
        setAlarmTimer(context, alarm);
    }

    private static void setAlarmTimer(Context ctx, AlarmEntity entity) {
        String action = entity.getAction();
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        int alarmManagerType = entity.getAlarmManagerType();
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        long firstStartTime = entity.getFirstStartTime();
        firstStartTime = firstStartTime <= 0 ? System.currentTimeMillis() : firstStartTime;
        boolean isRepeat = entity.isRepeate();
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if(alarm == null) {
            LogUtils.d("alarm is null");
            return;
        }
        LogUtils.d(entity.toString());
        long interval = entity.getInterval();
        if (isRepeat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, sender);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(alarmManagerType, System.currentTimeMillis() + interval, sender);
            } else {
                alarm.setRepeating(alarmManagerType, System.currentTimeMillis(), interval, sender);
            }
        } else {
            alarm.set(alarmManagerType, firstStartTime + interval, sender);
        }
        LogUtils.d("闹钟设置完成 " + entity.getType().toString());
    }

    public static void cancelAlarmTimer(Context ctx, AlarmEntity entity) {
        Intent myIntent = new Intent();
        myIntent.setAction(entity.getAction());
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        assert alarm != null;
        alarm.cancel(sender);
        LogUtils.d("取消闹钟设置完成 " + entity.getType().toString());
    }

}
