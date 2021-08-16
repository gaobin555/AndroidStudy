package com.thinkrace.watchservice.model;


import android.text.TextUtils;

import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author mare
 * @Description:TODO
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/9/4
 * @time 16:24
 */
public class FlagFormat {

    public static int boolean2Int(boolean flag) {
        return flag ? 1 : 0;
    }

    public static boolean int2Boolean(int flag) {
        return flag == 1;
    }

    /**
     * 将百度地图时间转成我们想要的
     *
     * @param baiduTime 2017-09-04 21:15:02
     * @return [040917, 211502]
     */
    public static String[] baiduTime2Server(String baiduTime) {
        if (TextUtils.isEmpty(baiduTime)) return null;
        LogUtils.i("baiduTime2Server " + baiduTime);
        String[] result = new String[2];
        String[] srcs = baiduTime.split(" ");
        StringBuffer sb = new StringBuffer();
        String[] ymd = srcs[0].split("-");
        String[] hms = srcs[1].split(":");
        sb.append(ymd[2]).append(ymd[1]).append(ymd[0].substring(2));
        result[0] = sb.toString();
        sb.setLength(0);
        sb.append(hms[0]).append(hms[1]).append(hms[2]);
        result[1] = sb.toString();
        return result;
    }

    public static String[] utcLong2Server(long utcTime) {
        String time = getUTCTime("yyMMdd HHmmss");
        String[] result = time.split(" ");
        LogUtils.d("utcLong2Server " + Arrays.toString(result));
        return result;
    }

    /**
     * 获取UTC时间
     *
     * @return
     */
    public static String getUTCTime(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone("gmt"));
        return format.format(new Date());
    }

}
