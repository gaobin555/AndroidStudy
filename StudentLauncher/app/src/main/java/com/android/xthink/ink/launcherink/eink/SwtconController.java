package com.android.xthink.ink.launcherink.eink;

import android.os.RemoteException;

import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.eink.swtcon.ISwtconControl;
import com.eink.swtcon.SwtconControl;

/**
 *  对Eink刷屏操作封装控制类
 * @author gaob@x-thinks.com
 * @version 1.0, 11/01/2019
 */
public final class SwtconController {
    private static String TAG = "SwtconController";
    private static String PIXEL_PROP = "eink.new.refresh";
    public static final String ACTION_EINK_MODE_SETTINGS = "xthink.settings.EINK_MODE_SETTINGS";
    // 0:can update, 1:locked
    public static final String ACTION_EINK_MODE_LOCKED = "xthink.settings.EINK_MODE_LOCKED";
    private static ISwtconControl mSwtcon = SwtconControl.getService();;
    private static int MODE_DU2 = SwtconControl.WF_MODE_DU2;
    private static int MODE_A2 = SwtconControl.WF_MODE_A2;
    private static int mDither = 1;
    private static int curMode = MODE_A2;
    private static boolean usePixel = true;

    public static void forceRefresh() {
        try {
            MyLog.i(TAG, "forceRefresh");
            if (mSwtcon == null) {
                //mSwtcon = SwtconControl.getService();
                return;
            }
            mSwtcon.forceRefresh();
        } catch (RemoteException e) {
            MyLog.e(TAG, "", e);
        }
    }

    public static void setEinkMode(int mode) {
        try {
            //new Exception("why print stack").printStackTrace();
            if (mSwtcon == null) {
                return;
            }
            if (mode == SwtconControl.WF_MODE_GC16) {
                mDither = 1;
            } else {
                mDither = 1;
            }
            if(mode == MODE_A2) {
                mode = MODE_DU2;
            }
            if (mode != curMode) {
                MyLog.i(TAG, "setEinkMode=" + mode + ", dither = " + mDither);
                mSwtcon.setWaveformMode(mode, mDither);
                curMode = mode;
            }
        } catch (RemoteException e) {
            MyLog.e(TAG, "", e);
        }
    }

    public static void setEinkMode(int mode, int dither){
        try {
            MyLog.i(TAG, "setEinkMode="+ mode + ", dither = " + dither);
            if (mSwtcon == null) {
                return;
            }
            if (mode != curMode) {
                mSwtcon.setWaveformMode(mode, dither);
                curMode = mode;
            }
        } catch (RemoteException e) {
            MyLog.e(TAG, "", e);
        }
    }

    public static int getCurMode() {
        return  curMode;
    }

    public static void setPixelOn() {
        if (usePixel && "0".equals(getPixelProp())) {
//            android.os.SystemProperties.set(PIXEL_PROP, "1");
        }
    }

    public static void setPixelOff() {
        if (usePixel && "1".equals(getPixelProp())) {
            android.os.SystemProperties.set(PIXEL_PROP, "0");
        }
    }

    private static String getPixelProp() {
        return android.os.SystemProperties.get(PIXEL_PROP, "0");
    }

    public static Boolean isPixelmode() {
        return "1".equals(getPixelProp());
    }
}
