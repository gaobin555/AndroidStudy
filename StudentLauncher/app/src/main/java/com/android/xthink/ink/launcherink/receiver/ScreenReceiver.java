package com.android.xthink.ink.launcherink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;

import com.android.xthink.ink.launcherink.base.mvp.MainBaseActivity;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.eink.SwtconController;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

import java.util.Objects;

public class ScreenReceiver extends BroadcastReceiver {
    public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String SHOW_LOCKED = "android.intent.action.SHOW_LOCKED";
    public static final String HIDE_LOCKED = "android.intent.action.HIDE_LOCKED";
    public static final String FORCE_REFRESH = "android.intent.action.FORCE_REFRESH";
    private String TAG = "ScreenActionReceiver";
    private ScreenOnListener mScreenOnListener;
    private static int saveMode = SwtconControl.WF_MODE_DU2;

    public ScreenReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case SCREEN_ON:
                MyLog.d(TAG, "cy--SCREEN_ON 广播...");
//                SharedPreferences mSp = context.getSharedPreferences("control_prefs", Context.MODE_PRIVATE);//added by chenjia;
//
//                int reportLossState = mSp.getInt(ParentalControlReceiver.REPORT_LOSS_STATE, 0);
//                int reservePowerState = mSp.getInt(ParentalControlReceiver.RESERVE_POWER_STATE, 0);
//                int classForbiddenState = mSp.getInt(ParentalControlReceiver.CLASS_FORBIDDEN_STATE, 0);
//                if (reportLossState == 1 || reservePowerState == 1 || classForbiddenState == 1) {
//                    Intent intentTemp = new Intent(Intent.ACTION_MAIN);
//                    intentTemp.addCategory(Intent.CATEGORY_HOME);
//                    context.startActivity(intentTemp);
//                }
                break;
            case SCREEN_OFF:
                MyLog.d(MyLog.TAG, TAG + " 39-------onReceive 背屏: SCREEN_OFF");
                break;
            case SHOW_LOCKED: {
                MyLog.d(TAG, "锁屏广播：" + intent.getAction());
                if (!InkDeviceUtils.isLauncher) {
                    saveMode = MainBaseActivity.getEinkModeData();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyLog.d(TAG, "锁屏刷新");
                        }
                    }, 500);
                }
                break;
            }
            case HIDE_LOCKED: {
                MyLog.d(TAG, "解锁屏广播：" + intent.getAction());
                if (!InkDeviceUtils.isLauncher) {
                    MainBaseActivity.setEinkLocked(SwtconControl.WF_MODE_GC16);
                    MainBaseActivity.setEinkModeData(saveMode);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainBaseActivity.setEinkLocked(0);
                        }
                    }, 900);
                }
                break;
            }
            case FORCE_REFRESH:
                SwtconController.forceRefresh();
                break;
        }
    }

    public void registerBackScreenReceiver(Context context, ScreenOnListener screenOnListener) {
        try {
            this.mScreenOnListener = screenOnListener;
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCREEN_ON);
            filter.addAction(SCREEN_OFF);
            filter.addAction(SHOW_LOCKED);
            filter.addAction(HIDE_LOCKED);
            filter.addAction(FORCE_REFRESH);
            MyLog.d(TAG, "cy--接收到背屏解锁广播...");
            context.registerReceiver(this, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterScreenReceiver(Context context) {
        try {
            context.unregisterReceiver(this);
            MyLog.d(TAG, "注销屏幕解锁、加锁广播接收者...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ScreenOnListener {
        public void screenOn();
        public void screenOff();
    }
}