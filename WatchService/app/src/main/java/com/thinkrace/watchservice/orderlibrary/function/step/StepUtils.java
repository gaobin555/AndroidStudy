package com.thinkrace.watchservice.orderlibrary.function.step;
/*
 *  @项目名：  WatchService
 *  @包名：    com.thinkrace.watchservice.function.step
 *  @文件名:   StepUtils
 *  @创建者:   win10
 *  @创建时间:  2018/4/23 15:40
 *  @描述：    TODO
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StepUtils {

    public static final String AUTHORITY = "com.launcher.db.provider.StepcountProvider";
    public static final String TABLE_NAME = "step_count_detail";
    public static final String COLUMNS_TIME = "get_time";
    public static final String COLUMNS_STEP = "step_count";
    public static final String COLUMNS_DISTANCE = "distance";
    public static final String COLUMNS_CAL = "kcal";
    public static final Uri STEP_CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/"+ TABLE_NAME);
    private static ContentResolver mCr;

    public static int getStepCount(Context context) {
        mCr = context.getContentResolver();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd####HH:mm:ss", Locale.getDefault());
        int todayStep = 0;
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        long time = calendar.getTimeInMillis(); //当天零点 00:00:00
        String selection = COLUMNS_TIME + " >= " + time + " and " + COLUMNS_TIME + " < " + currentTime;
        Cursor cursor = mCr.query(STEP_CONTENT_URI,null,
                selection,null,null);
        if(cursor == null) {
            return todayStep;
        }
        while (cursor.moveToNext()){
            long dbTime = cursor.getLong(cursor.getColumnIndex(COLUMNS_TIME));
            int step = cursor.getInt(cursor.getColumnIndex(COLUMNS_STEP));
            double distance = cursor.getDouble(cursor.getColumnIndex(COLUMNS_DISTANCE));
            double cal = cursor.getDouble(cursor.getColumnIndex(COLUMNS_CAL));

            Log.e("step","time:"+ simpleDateFormat.format(dbTime)+"--step:"+step
                    +"--distance:"+distance+"--cal:"+cal);

            todayStep += step;
        }
        Log.e("step","todayStep:" + todayStep);
        cursor.close();
        return todayStep;
    }

}
