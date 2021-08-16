package com.thinkrace.watchservice;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.kct.sdk.KCSdk;
import com.kct.sdk.listen.KCEventListen;
import com.kct.sdk.util.KCLog;
import com.thinkrace.watchservice.function.location.AMapLocationManager;
import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.thinkrace.watchservice.orderlibrary.call.CallFragmentActivity;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.UIAction;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;
import com.xuhao.android.libsocket.sdk.OkSocket;

import java.util.Timer;
import java.util.TimerTask;

import static com.kct.sdk.KCBase.CALL_ALERT;
import static com.kct.sdk.KCBase.CALL_ANETWORK;
import static com.kct.sdk.KCBase.CALL_ANSWER;
import static com.kct.sdk.KCBase.CALL_AUDIO_MODE;
import static com.kct.sdk.KCBase.CALL_HANDUP;
import static com.kct.sdk.KCBase.CALL_INCOME;
import static com.kct.sdk.KCBase.CALL_OUTFAIL;
import static com.kct.sdk.KCBase.CALL_VNETWORK;
import static com.kct.sdk.KCBase.ICE_RTPP;
import static com.kct.sdk.KCBase.TCP_CONNECT;
import static com.kct.sdk.KCBase.TCP_DISCONNECT;

/**
 * @author mare
 * @Description:
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/8/24
 * @time 14:32
 */
public class KApplication extends Application{
    private static KApplication instance;
    public static Context sContext;
    // TCP连接状态
    public static boolean bConnect = false;
    // 是否重连标记
    public static boolean bReCon = false;

    public static KApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        instance = this;
        Utils.init(this);
        initLocationSDK();
        OkSocket.initialize(this,true);
//        OkSocket.setIpAndPort("119.23.161.169",10500); //调试ip
//        OkSocket.setIpAndPort("192.168.1.84",60000); //调试ip
    }

    public void initLocationSDK(){
        GlobalSettings.instance();
        AMapLocationManager.instance().initLocationSDK();//主线程初始化地图
        GlobalSettings.instance().saveImei(Utils.getContext());
        GlobalSettings.instance().saveImsi(Utils.getContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
