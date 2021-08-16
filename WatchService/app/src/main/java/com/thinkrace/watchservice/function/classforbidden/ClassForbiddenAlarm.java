package com.thinkrace.watchservice.function.classforbidden;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;

import java.util.Arrays;
import java.util.Date;

import static com.thinkrace.watchservice.receiver.ClassForbiddenAlarmReceiver.ACTION_SET_CLASSFORBIDDEN_ALARM_START;
import static com.thinkrace.watchservice.receiver.ClassForbiddenAlarmReceiver.ACTION_SET_CLASSFORBIDDEN_ALARM_STOP;

public class ClassForbiddenAlarm {
    private static final String LOG_TAG = "ClassForbiddenAlarm";

    private static final String ClassForbidenStart = "com.thinkrace.intent.ClassForbidenStart";
    private static final String ClassForbidenStop = "com.thinkrace.intent.ClassForbidenStop";

    private static final int ALARM_AM_START_REQUEST_CODE_START = 1100;
    private static final int MAX_FORBIDDENS = 15;

    public enum Type {
        ClassForbidenStart,
        ClassForbidenStop
    }

    public ClassForbiddenAlarm.Type type;

    public String action;

    private long firstStartTime = 0;

    public ClassForbiddenAlarm(Type type) {
        this.type = type;
        String action;
        switch (type) {
            case ClassForbidenStart:
                action = ClassForbidenStart;
                break;
            case ClassForbidenStop:
                action = ClassForbidenStop;
                break;
            default:
                action = null;
                break;
        }
        this.action = action;
    }

    public void setFirstStartTime (Long startTime) {
        this.firstStartTime = startTime;
    }

    public Long getFirstStartTime() {
        return this.firstStartTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setClassForbiddenAlarm(Context context, AlarmManager alarmManager, ClassForbidden classForbidden, boolean isRepeate) {
        Intent intentStart = new Intent();
        Intent intentStop = new Intent();
        Calendar nowCalendar  = Calendar.getInstance();
        nowCalendar .setTime(new Date());
        int nowDay    = nowCalendar.get(Calendar.DAY_OF_WEEK);
        int nowHour   = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);
        int day = Integer.parseInt(classForbidden.repeat); // 重复的星期
        long interval = AlarmManager.INTERVAL_DAY * 7;
        boolean bEnableForbidden = false;
        int[] requestCode = getRequestCodeByDay(day);
        Log.i(LOG_TAG, "Day = " + day + ", requestCode = " + Arrays.toString(requestCode));
        Log.i(LOG_TAG, "nowDay = " + nowDay + ", nowHour = " + nowHour + ", nowMinute = " + nowMinute);
        String[] nowTime = new String[2];
        nowTime[0] = String.valueOf(nowHour);
        nowTime[1] = String.valueOf(nowMinute);
        Log.i(LOG_TAG, "nowTime---- nowHour = " + nowTime[0] + ", nowMinute = " + nowTime[1]);
        if (day == 7) {
            day = Calendar.SUNDAY;
        } else if (day >= 1 && day < 7) {
            day++;
        }

        intentStart.setAction(ACTION_SET_CLASSFORBIDDEN_ALARM_START);
        intentStop.setAction(ACTION_SET_CLASSFORBIDDEN_ALARM_STOP);

        int forbiddenSize = classForbidden.forbiddenTimes.size();
        if (forbiddenSize >= MAX_FORBIDDENS) {
            forbiddenSize = MAX_FORBIDDENS;
        }

        for (int i = 0; i < forbiddenSize; i++) {
            PendingIntent pendingIntent_start = PendingIntent.getBroadcast(context,
                    requestCode[i], intentStart, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingIntent_stop = PendingIntent.getBroadcast(context,
                    requestCode[i], intentStop, PendingIntent.FLAG_CANCEL_CURRENT);
            String[] start = classForbidden.forbiddenTimes.get(i).start;
            String[] stop = classForbidden.forbiddenTimes.get(i).stop;

            if (start != null && stop != null) {
                // start
                Calendar calendar_am_start = Calendar.getInstance();
                calendar_am_start.set(Calendar.DAY_OF_WEEK, day);
                calendar_am_start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
                calendar_am_start.set(Calendar.MINUTE, Integer.parseInt(start[1]));
                calendar_am_start.set(Calendar.SECOND, 0);
                if (day < nowDay || (day == nowDay && Integer.parseInt(start[0]) < nowHour)
                        || (day == nowDay && Integer.parseInt(start[0]) == nowHour && Integer.parseInt(start[1]) < nowMinute)) {
                    calendar_am_start.add(Calendar.SECOND, 7 * 24 * 60 * 60);
                }
                Log.i(LOG_TAG, "set Start Day = " + calendar_am_start.get(Calendar.DAY_OF_WEEK) + ", noHour = "
                        + calendar_am_start.get(Calendar.HOUR_OF_DAY) + ", Minute = " + calendar_am_start.get(Calendar.MINUTE));
                // 七天重复
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar_am_start.getTimeInMillis(),
                        interval, pendingIntent_start);

                // stop
                Calendar calendar_am_end = Calendar.getInstance();
                calendar_am_end.set(Calendar.DAY_OF_WEEK, day);
                calendar_am_end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stop[0]));
                calendar_am_end.set(Calendar.MINUTE, Integer.parseInt(stop[1]));
                calendar_am_end.set(Calendar.SECOND, 0);
                if (day < nowDay || (day == nowDay && Integer.parseInt(stop[0]) < nowHour)
                        || (day == nowDay && Integer.parseInt(stop[0]) == nowHour && Integer.parseInt(stop[1]) < nowMinute)) {
                    calendar_am_end.add(Calendar.SECOND, 7 * 24 * 60 * 60);
                }
                Log.i(LOG_TAG, "set amStop Day = " + calendar_am_end.get(Calendar.DAY_OF_WEEK) + ", noHour = "
                        + calendar_am_end.get(Calendar.HOUR_OF_DAY) + ", Minute = " + calendar_am_end.get(Calendar.MINUTE));
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar_am_end.getTimeInMillis(),
                        interval, pendingIntent_stop);

                // 判断是否需要开启禁用
                if (nowDay == day && OrderUtil.compareTime(nowTime, start) && OrderUtil.compareTime(stop, nowTime)) {
                    Log.i(LOG_TAG, "开启禁用：now = " + nowDay);
                    OrderUtil.enableClassForbidden(context,1); // 开启禁用
                    bEnableForbidden = true;
                }
            } else {
                alarmManager.cancel(pendingIntent_start);
                alarmManager.cancel(pendingIntent_stop);
            }
        }

        if (nowDay == day && !bEnableForbidden) {// 今天没有开启禁用则关闭禁用
            OrderUtil.enableClassForbidden(context,0); // 关闭禁用
        }
    }

    public static void cancelForbiddenAlarm(Context context, AlarmManager alarmManager) {
        Intent intent = new Intent();
        for (int i = 0; i < 7; i++) {
            int[] requestCode = getRequestCodeByDay(i);
            for (int value : requestCode) {
                // 开始
                intent.setAction(ACTION_SET_CLASSFORBIDDEN_ALARM_START);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, value, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent);
                // 结束
                intent.setAction(ACTION_SET_CLASSFORBIDDEN_ALARM_STOP);
                pendingIntent = PendingIntent.getBroadcast(context, value, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private static int[] getRequestCodeByDay(int day) {
        int[] requestCode = new int[MAX_FORBIDDENS];
        for (int i = 0; i < requestCode.length; i++) {
            requestCode[i] = ALARM_AM_START_REQUEST_CODE_START + (day - 1) * MAX_FORBIDDENS + i;
        }

        return requestCode;
    }
}
