package com.thinkrace.watchservice.orderlibrary.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.thinkrace.watchservice.function.location.LocationUploadManager;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.parser.MsgSender;
import com.xuhao.android.common.constant.SPConstant;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.thinkrace.watchservice.receiver.GuardAlarmReceiver.ACTION_SET_GUARD_ALARM_END;
import static com.thinkrace.watchservice.receiver.GuardAlarmReceiver.ACTION_SET_GUARD_ALARM_START;


/**
 * Created by chenjia
 */

public class GuardAlarmUtils {
    public static final String LOG_TAG = "GuardAlarmUtils";

    public static final int ALARM_AM_START_DAY_1_REQUEST_CODE = 1001;
    public static final int ALARM_PM_START_DAY_1_REQUEST_CODE = 1002;
    public static final int ALARM_AM_END_DAY_1_REQUEST_CODE = 1003;
    public static final int ALARM_PM_END_DAY_1_REQUEST_CODE = 1004;

    public static final int ALARM_AM_START_DAY_2_REQUEST_CODE = 2001;
    public static final int ALARM_PM_START_DAY_2_REQUEST_CODE = 2002;
    public static final int ALARM_AM_END_DAY_2_REQUEST_CODE = 2003;
    public static final int ALARM_PM_END_DAY_2_REQUEST_CODE = 2004;

    public static final int ALARM_AM_START_DAY_3_REQUEST_CODE = 3001;
    public static final int ALARM_PM_START_DAY_3_REQUEST_CODE = 3002;
    public static final int ALARM_AM_END_DAY_3_REQUEST_CODE = 3003;
    public static final int ALARM_PM_END_DAY_3_REQUEST_CODE = 3004;

    public static final int ALARM_AM_START_DAY_4_REQUEST_CODE = 4001;
    public static final int ALARM_PM_START_DAY_4_REQUEST_CODE = 4002;
    public static final int ALARM_AM_END_DAY_4_REQUEST_CODE = 4003;
    public static final int ALARM_PM_END_DAY_4_REQUEST_CODE = 4004;

    public static final int ALARM_AM_START_DAY_5_REQUEST_CODE = 5001;
    public static final int ALARM_PM_START_DAY_5_REQUEST_CODE = 5002;
    public static final int ALARM_AM_END_DAY_5_REQUEST_CODE = 5003;
    public static final int ALARM_PM_END_DAY_5_REQUEST_CODE = 5004;

    public static final int ALARM_AM_START_DAY_6_REQUEST_CODE = 6001;
    public static final int ALARM_PM_START_DAY_6_REQUEST_CODE = 6002;
    public static final int ALARM_AM_END_DAY_6_REQUEST_CODE = 6003;
    public static final int ALARM_PM_END_DAY_6_REQUEST_CODE = 6004;

    public static final int ALARM_AM_START_DAY_7_REQUEST_CODE = 7001;
    public static final int ALARM_PM_START_DAY_7_REQUEST_CODE = 7002;
    public static final int ALARM_AM_END_DAY_7_REQUEST_CODE = 7003;
    public static final int ALARM_PM_END_DAY_7_REQUEST_CODE = 7004;

    private static GuardAlarmUtils instance;
    private Context mContext;
    private AlarmManager mAlarmManager;

    private String[] mAmTimes;
    private String[] mPmTimes;

    public synchronized static GuardAlarmUtils getInstance() {
        if (instance == null) {
            instance = new GuardAlarmUtils();
        }
        return instance;
    }

    public void setGuardAlarmByDays(Context context, AlarmManager alarmManager, String repeat, String[] guardAmTimes, String[] guardPmTimes) {
        Log.i(LOG_TAG, "repeat = " + repeat + ", amTimes = " + Arrays.deepToString(guardAmTimes) + ", pmTimes = " + Arrays.deepToString(guardPmTimes));
        if (TextUtils.isEmpty(repeat) || guardAmTimes == null || guardPmTimes == null) {
            return;
        }

        mContext = context;
        mAlarmManager = alarmManager;

        mAmTimes = guardAmTimes;
        mPmTimes = guardPmTimes;

        for (char c : repeat.toCharArray()) {
            parseRepeatDays(Integer.parseInt(c + ""));
        }
    }

    public  void parseRepeatDays(int day) {
        Log.i(LOG_TAG, "parseRepeatDays = " + day);
        int[] requestCodes = getRequestCodesByDay(day);
        createGuardAlarm(requestCodes[0], requestCodes[1], requestCodes[2], requestCodes[3], day);
    }

    public int[] getRequestCodesByDay(int day) {
        int alarm_am_start_request_code = ALARM_AM_START_DAY_1_REQUEST_CODE;
        int alarm_pm_start_request_code = ALARM_PM_START_DAY_1_REQUEST_CODE;
        int alarm_am_end_request_code = ALARM_AM_END_DAY_1_REQUEST_CODE;
        int alarm_pm_end_request_code = ALARM_PM_END_DAY_1_REQUEST_CODE;
        int[] requestCodes = {alarm_am_start_request_code, alarm_pm_start_request_code, alarm_am_end_request_code, alarm_pm_end_request_code};

        switch (day) {
            case 1:
                break;
            case 2:
                alarm_am_start_request_code = ALARM_AM_START_DAY_2_REQUEST_CODE;
                alarm_pm_start_request_code = ALARM_PM_START_DAY_2_REQUEST_CODE;
                alarm_am_end_request_code = ALARM_AM_END_DAY_2_REQUEST_CODE;
                alarm_pm_end_request_code = ALARM_PM_END_DAY_2_REQUEST_CODE;
                break;
            case 3:
                alarm_am_start_request_code = ALARM_AM_START_DAY_3_REQUEST_CODE;
                alarm_pm_start_request_code = ALARM_PM_START_DAY_3_REQUEST_CODE;
                alarm_am_end_request_code = ALARM_AM_END_DAY_3_REQUEST_CODE;
                alarm_pm_end_request_code = ALARM_PM_END_DAY_3_REQUEST_CODE;
                break;
            case 4:
                alarm_am_start_request_code = ALARM_AM_START_DAY_4_REQUEST_CODE;
                alarm_pm_start_request_code = ALARM_PM_START_DAY_4_REQUEST_CODE;
                alarm_am_end_request_code = ALARM_AM_END_DAY_4_REQUEST_CODE;
                alarm_pm_end_request_code = ALARM_PM_END_DAY_4_REQUEST_CODE;
                break;
            case 5:
                alarm_am_start_request_code = ALARM_AM_START_DAY_5_REQUEST_CODE;
                alarm_pm_start_request_code = ALARM_PM_START_DAY_5_REQUEST_CODE;
                alarm_am_end_request_code = ALARM_AM_END_DAY_5_REQUEST_CODE;
                alarm_pm_end_request_code = ALARM_PM_END_DAY_5_REQUEST_CODE;
                break;
            case 6:
                alarm_am_start_request_code = ALARM_AM_START_DAY_6_REQUEST_CODE;
                alarm_pm_start_request_code = ALARM_PM_START_DAY_6_REQUEST_CODE;
                alarm_am_end_request_code = ALARM_AM_END_DAY_6_REQUEST_CODE;
                alarm_pm_end_request_code = ALARM_PM_END_DAY_6_REQUEST_CODE;
                break;
            case 7:
                alarm_am_start_request_code = ALARM_AM_START_DAY_7_REQUEST_CODE;
                alarm_pm_start_request_code = ALARM_PM_START_DAY_7_REQUEST_CODE;
                alarm_am_end_request_code = ALARM_AM_END_DAY_7_REQUEST_CODE;
                alarm_pm_end_request_code = ALARM_PM_END_DAY_7_REQUEST_CODE;
                break;
        }

        for (int i = 0; i < requestCodes.length; i++) {
            switch (i){
                case 0:
                    requestCodes[i] = alarm_am_start_request_code;
                    continue;
                case 1:
                    requestCodes[i] = alarm_pm_start_request_code;
                    continue;
                case 2:
                    requestCodes[i] = alarm_am_end_request_code;
                    continue;
                case 3:
                    requestCodes[i] = alarm_pm_end_request_code;
                    break;
            }
        }

        return requestCodes;
    }

    public void createGuardAlarm(int alarm_am_start_request_code, int alarm_pm_start_request_code, int alarm_am_end_request_code, int alarm_pm_end_request_code, int day) {
        Intent intent = new Intent();
        intent.putExtra("isCancelGuardAlarm", false);
        Calendar nowCalendar  = Calendar.getInstance();
        nowCalendar .setTime(new Date());
        int nowDay    = nowCalendar.get(Calendar.DAY_OF_WEEK);
        int nowHour   = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);
        Log.i(LOG_TAG, "nowDay = " + nowDay + ", nowHour = " + nowHour + ", nowMinute = " + nowMinute);
        if (day == 7) {
            day = Calendar.SUNDAY;
        } else if (day >=1 && day < 7) {
            day++;
        }
        Calendar calendar_am_start = Calendar.getInstance();
        calendar_am_start.set(Calendar.DAY_OF_WEEK, day);
        intent.setAction(ACTION_SET_GUARD_ALARM_START);
        String[] alarmTime0 = mAmTimes[0].split(":");
        calendar_am_start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime0[0]));
        calendar_am_start.set(Calendar.MINUTE, Integer.parseInt(alarmTime0[1]));
        calendar_am_start.set(Calendar.SECOND, 0);
        if (day < nowDay || (day == nowDay && Integer.parseInt(alarmTime0[0]) < nowHour) || (day == nowDay && Integer.parseInt(alarmTime0[0]) == nowHour && Integer.parseInt(alarmTime0[1]) < nowMinute)) {
            calendar_am_start.add(Calendar.SECOND, 7 * 24 * 60 * 60);
        }
        PendingIntent pendingIntent_am_start = PendingIntent.getBroadcast(mContext, alarm_am_start_request_code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar_am_start.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent_am_start);

        Calendar calendar_pm_start = Calendar.getInstance();
        calendar_pm_start.set(Calendar.DAY_OF_WEEK, day);
        String[] alarmTime1 = mPmTimes[0].split(":");
        calendar_pm_start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime1[0]));
        calendar_pm_start.set(Calendar.MINUTE, Integer.parseInt(alarmTime1[1]));
        calendar_pm_start.set(Calendar.SECOND, 0);
        if (day < nowDay || (day == nowDay && Integer.parseInt(alarmTime1[0]) < nowHour) || (day == nowDay && Integer.parseInt(alarmTime1[0]) == nowHour && Integer.parseInt(alarmTime1[1]) < nowMinute)) {
            calendar_pm_start.add(Calendar.SECOND, 7 * 24 * 60 * 60);
        }
        PendingIntent pendingIntent_pm_start = PendingIntent.getBroadcast(mContext, alarm_pm_start_request_code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar_pm_start.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent_pm_start);

        Calendar calendar_am_end = Calendar.getInstance();
        calendar_am_end.set(Calendar.DAY_OF_WEEK, day);
        intent.setAction(ACTION_SET_GUARD_ALARM_END);
        String[] alarmTime2 = mAmTimes[1].split(":");
        calendar_am_end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime2[0]));
        calendar_am_end.set(Calendar.MINUTE, Integer.parseInt(alarmTime2[1]));
        calendar_am_end.set(Calendar.SECOND, 0);
        if (day < nowDay || (day == nowDay && Integer.parseInt(alarmTime2[0]) < nowHour) || (day == nowDay && Integer.parseInt(alarmTime2[0]) == nowHour && Integer.parseInt(alarmTime2[1]) < nowMinute)) {
            calendar_am_end.add(Calendar.SECOND, 7 * 24 * 60 * 60);
        }
        PendingIntent pendingIntent_am_end = PendingIntent.getBroadcast(mContext, alarm_am_end_request_code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar_am_end.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent_am_end);

        Calendar calendar_pm_end = Calendar.getInstance();
        calendar_pm_end.set(Calendar.DAY_OF_WEEK, day);
        String[] alarmTime3 = mPmTimes[1].split(":");
        calendar_pm_end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime3[0]));
        calendar_pm_end.set(Calendar.MINUTE, Integer.parseInt(alarmTime3[1]));
        calendar_pm_end.set(Calendar.SECOND, 0);
        if (day < nowDay || (day == nowDay && Integer.parseInt(alarmTime3[0]) < nowHour) || (day == nowDay && Integer.parseInt(alarmTime3[0]) == nowHour && Integer.parseInt(alarmTime3[1]) < nowMinute)) {
            calendar_pm_end.add(Calendar.SECOND, 7 * 24 * 60 * 60);
        }
        PendingIntent pendingIntent_pm_end = PendingIntent.getBroadcast(mContext, alarm_pm_end_request_code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar_pm_end.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent_pm_end);
    }

    public void cancelGuardAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.putExtra("isCancelGuardAlarm", true);

        for (int day = 1; day <= 7; day++) {
            int[] requestCodes = getRequestCodesByDay(day);
            intent.setAction(ACTION_SET_GUARD_ALARM_START);
            PendingIntent pendingIntent_am_start = PendingIntent.getService(context, requestCodes[0], intent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent_am_start);
            PendingIntent pendingIntent_pm_start = PendingIntent.getService(context, requestCodes[1], intent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent_pm_start);

            intent.setAction(ACTION_SET_GUARD_ALARM_END);
            PendingIntent pendingIntent_am_end = PendingIntent.getService(context, requestCodes[2], intent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent_am_end);
            PendingIntent pendingIntent_pm_end = PendingIntent.getService(context, requestCodes[3], intent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent_pm_end);
        }

        LocationUploadManager.instance().parseInteraval(LocationUploadManager.NORMAL_TYPE);
    }

    public boolean isInGuardTime(Context context) {
        boolean result = false;
        SharedPreferences sp = context.getSharedPreferences(SPConstant.PARENTAL_CONTROL, Context.MODE_PRIVATE);
        String repeat_day = sp.getString("guard_repeat_day", "");
        String am_time = sp.getString("guard_am_time", "");
        String[] amTimes = am_time.split("-");
        String pm_time = sp.getString("guard_pm_time", "");
        String[] pmTimes = pm_time.split("-");

        String[] amStartAlarmTime = amTimes[0].split(":");
        String[] amEndAlarmTime = amTimes[1].split(":");

        String[] pmStartAlarmTime = pmTimes[0].split(":");
        String[] pmEndAlarmTime = pmTimes[1].split(":");

        for (char c : repeat_day.toCharArray()) {
            result = result || isCurrentInTimeScope(Integer.parseInt(c+""), Integer.parseInt(amStartAlarmTime[0]), Integer.parseInt(amStartAlarmTime[1]), Integer.parseInt(amEndAlarmTime[0]), Integer.parseInt(amEndAlarmTime[1]))
                            || isCurrentInTimeScope(Integer.parseInt(c+""), Integer.parseInt(pmStartAlarmTime[0]), Integer.parseInt(pmStartAlarmTime[1]), Integer.parseInt(pmEndAlarmTime[0]), Integer.parseInt(pmEndAlarmTime[1]));
        }
        return result;
    }

    public boolean isCurrentInTimeScope(int day, int beginHour, int beginMin, int endHour, int endMin) {
        boolean result = false;
        if (day == 7) {
            day = Calendar.SUNDAY;
        } else if (day >=1 && day < 7) {
            day++;
        }

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(new Date());
        //设置开始时间
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.DAY_OF_WEEK, day);
        beginTime.set(Calendar.HOUR_OF_DAY, beginHour);
        beginTime.set(Calendar.MINUTE, beginMin);
        beginTime.set(Calendar.SECOND, 0);
        //设置结束时间
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.DAY_OF_WEEK, day);
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endHour);
        endTime.set(Calendar.SECOND, 0);
        //处于开始时间之后，和结束时间之前的判断
        result = nowDate.after(beginTime) && nowDate.before(endTime);

        Log.i(LOG_TAG, "Week of "+ day + " " + beginHour + ":" + beginMin + "-" + endHour + ":" + endMin + " isInGuardTime = " + result);
        return result;
    }

    public final static String SCHOOL_GUARD_TYPE = "1";
    public final static String HOME_GUARD_TYPE = "2";
    public final static String CUSTOM_GUARD_TYPE = "3";

    public final static String ENTER_GUARD_ARER = "1";
    public final static String EXIT_GUARD_ARER = "2";

    public void reportGuard(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPConstant.PARENTAL_CONTROL, Context.MODE_PRIVATE);//added by chenjia
        boolean guardState = sp.getBoolean("guard_state", false);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //获取当前连接wifi mac
        String macCurrent = wifiInfo.getBSSID();
        String macGuard = sp.getString("guard_wifi_mac", "");
        Log.i(LOG_TAG, "guardState = " + guardState + ", macCurrent = " + macCurrent + ", macGuard = " + macGuard);
        if (!guardState) {
            return;
        }
        if (!isInGuardTime(context)) {
            LocationUploadManager.instance().parseInteraval(LocationUploadManager.NORMAL_TYPE);
            return;
        } else {
            LocationUploadManager.instance().parseInteraval(LocationUploadManager.EMERGENCY_TYPE);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//yyyyMMddHHmmss
        Date date = new Date(System.currentTimeMillis());
        String currentTime = simpleDateFormat.format(date);
        if (TextUtils.equals(macCurrent, macGuard)) {
            String enter_cmd = currentTime + "," + HOME_GUARD_TYPE + "," + ENTER_GUARD_ARER + "," + macGuard + "," + "";
            //MsgSender.sendTxtMsg(MsgType.IWAP86, enter_cmd);
        } else {
            String exit_cmd = currentTime + "," + HOME_GUARD_TYPE + "," + EXIT_GUARD_ARER + "," + macGuard + "," + "";
            //MsgSender.sendTxtMsg(MsgType.IWAP86, exit_cmd);
        }
    }
}
