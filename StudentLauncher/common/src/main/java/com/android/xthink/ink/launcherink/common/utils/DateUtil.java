package com.android.xthink.ink.launcherink.common.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.android.xthink.ink.launcherink.common.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liyuyan on 2017/3/16.
 */


public class DateUtil {

    /*获取日期*/
    public static String getDay(String date) {
        String h;
        String[] day = date.split("-");
        h = day[2];
        return h;
    }

    /*获取月份*/
    public static String getMonth(String date) {
        String m;
        String[] day = date.split("-");
        m = day[1];
        return m;
    }

    /*获取年份*/
    public static String getYear(String date) {
        String y;
        String[] day = date.split("-");
        y = day[0];
        return y;
    }

    /*获取当前系统时间*/
    public static String getSysDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        return sdf.format(date);
    }

    /*格式化日期时间*/
    public static String formatDatetime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        return sdf.format(date);
    }

    /*获得ReleaseTime*/
    public static String formatReleaseTime(long releaseTime) {
        java.sql.Date date = new java.sql.Date(releaseTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    /**
     * 自定义格式的时间转换
     */
    public static String formatDatetime(long time, String format) {
        return formatDatetime(new Date(time), format);
    }

    public static boolean isToday(long when) {
        return DateUtils.isToday(when);
    }

    /**
     * 自定义格式的时间转换
     */
    public static String formatDatetime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    public static String formatDatetime(String date) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date d = fmt.parse(date);
        return d.toString();
    }

    public static String formatDatetime(String date, int forid) {
        if (date == null || "".equals(date.trim())) {
            return "";
        } else {
            String str = "";
            str = date.substring(0, date.indexOf("."));
            String[] array = str.split(" ");
            String[] dates = array[0].split("-");
            switch (forid) {
                case 0:  //yyyy-MM-dd HH:mm:ss
                    str = date.substring(0, date.indexOf("."));
                    break;
                case 1:  //yyyy-MM-dd
                    str = date.substring(0, date.indexOf("."));
                    str = str.substring(0, str.indexOf(" "));
                    break;
                case 2:  //yyyy年MM月dd日 HH:mm:ss
                    str = dates[0] + "年" + dates[1] + "月" + dates[2] + "日 " + array[1];
                    break;
                case 3:  //yyyy年MM月dd日 HH:mm
                    str = dates[0] + "年" + dates[1] + "月" + dates[2] + "日 " + array[1].substring(0, array[1].lastIndexOf(":"));
                    break;
                case 4:  //yyyy年MM月dd日 HH:mm:ss
                    str = dates[0] + "年" + dates[1] + "月" + dates[2] + "日 ";
                    break;
                default:
                    break;
            }
            return str;
        }
    }

    /*获取当前时间的毫秒*/
    public String getSysTimeMillise() {
        long i = System.currentTimeMillis();
        return String.valueOf(i);
    }

    /*获取星期几*/
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case Calendar.SUNDAY:
                return "周日";
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
            default:
                return "";
        }
    }

    public static String formatCommentTime(String str) {

        Date date = parse(str, "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);

        return sdf.format(date);
    }

    public static Date parse(String str, String pattern, Locale locale) {
        if (str == null || pattern == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern, locale).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static boolean isMinuteTick() {
        SimpleDateFormat formatter = new SimpleDateFormat("ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str.equals("00");
    }

    public static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    /**
     * @return if am
     */
    private static boolean isAM() {
        Calendar c = Calendar.getInstance();
        int amOrPm = c.get(Calendar.AM_PM);
        if (amOrPm == Calendar.AM) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到区分小时制的时间
     * @param when time
     * @param context 上下文
     * @return get the format of time
     */
    public static String getDateWith24Mode(long when, Context context) {

        Date date = new Date(when);
        if (isToday(when)) {

            if (get24HourMode(context)) {
                return formatDatetime(date, "HH:mm");
            } else if (isAM()) {
                return context.getResources().getString(R.string.calendar_am) + formatDatetime(date, "hh:mm");
            } else {
                return context.getResources().getString(R.string.calendar_pm) + formatDatetime(date, "hh:mm");
            }

        } else {
            return formatDatetime(date, "MM-dd");
        }
    }

    /**
     * 根据年月得到时间 InMillis
     *
     * @param year  年份
     * @param month 1表示1月份
     * @return 时间 inMillis
     */
    public static long getDateByYearAndMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 10);
        calendar.get(Calendar.MONTH);
        return calendar.getTimeInMillis();
    }

    /**
     * 根据时间获得年份
     *
     * @param time 时间
     * @return 年份
     */
    public static int getYearByTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 根据时间获得月份
     *
     * @param time 时间
     * @return 月份，1表示1月份
     */
    public static int getMonthByTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH) + 1;
    }

}