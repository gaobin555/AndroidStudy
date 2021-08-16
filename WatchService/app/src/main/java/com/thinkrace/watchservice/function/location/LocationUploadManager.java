package com.thinkrace.watchservice.function.location;

import android.text.TextUtils;

import com.thinkrace.watchservice.KApplication;
import com.thinkrace.watchservice.function.alarm.AlarmTimer;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.SPUtils;
import com.xuhao.android.common.constant.SPConstant;


/**
 * @author mare
 * @Description:TODO 固定位置上传 (接收的间隔)
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/9/20
 * @time 17:23
 */
public class LocationUploadManager {
    public final static long EMERGENCY_MODE = 2 * 60 * 1000;   //2 mins
    public final static long NORMAL_MODE = 60 * 60 * 1000;     //1 hour
    public final static long POWER_SAVER_MODE = 60 * 60 * 1000;//1 hour

    public final static String EMERGENCY_TYPE = "3";
    public final static String NORMAL_TYPE = "1";
    public final static String POWER_SAVER_TYPE = "2";

    private LocationUploadManager() {
    }

    private static class SingletonHolder {
        private static final LocationUploadManager INSTANCE = new LocationUploadManager();
    }

    public static LocationUploadManager instance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 开启定时定位
     *
     * @param type
     */
    public void parseInteraval(String type) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        long interal = NORMAL_MODE;//单位是秒
        if (TextUtils.equals(type, NORMAL_TYPE)) {
            interal = NORMAL_MODE;
        } else if (TextUtils.equals(type, POWER_SAVER_TYPE)) {
            interal = POWER_SAVER_MODE;
        } else if (TextUtils.equals(type, EMERGENCY_TYPE)) {
            interal = EMERGENCY_MODE;
        }

        if (interal == getCurrentLocationMode()) {
            LogUtils.e("当前保存的location定时时间间隔与下发的时间间隔一致，不予处理");
            return;
        }

        LogUtils.e("更新location 定时时间间隔 : " + interal);
        SPUtils.getInstance().put(SPConstant.CURRENT_INTERVAL, interal,true);
        AMapLocationManager.instance().start();//开启第一次定位
        AlarmTimer.startConfirmedFrequencyUpload(KApplication.sContext);
    }

    public long getCurrentLocationMode() {
        long interal = SPUtils.getInstance().getLong(SPConstant.CURRENT_INTERVAL);
        return interal;
    }
}
