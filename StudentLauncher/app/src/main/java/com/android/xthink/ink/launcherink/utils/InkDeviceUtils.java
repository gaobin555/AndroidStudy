/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.utils;

import android.os.Handler;

import com.android.xthink.ink.launcherink.base.mvp.MainBaseActivity;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.eink.SwtconController;
import com.eink.swtcon.SwtconControl;

import static com.android.xthink.ink.launcherink.eink.SwtconController.isPixelmode;

/**
 * @author wanchi@x-thinks.com
 * @version 1.0, 2018/5/5
 */
public class InkDeviceUtils {
    private static final String TAG = "InkDeviceUtils";
    private static boolean isWaitupdate = false;
    public static boolean isLauncher = true;
    private static int mUpdate = 0;

    /**
     * 设置刷新模式 <p/>
     * 自适应刷新 <p/>
     */
    public static void setUpdateModeforActivity(int mode) {
        MyLog.i(TAG, "setUpdateModeforActivity : isWaitupdate = " + isWaitupdate + ", mode = " + mode);
        InkDeviceUtils.isLauncher = false;
        if (isWaitupdate) {
            waitForUpdate(mode);
        } else {
            Handler handler = new Handler();
            isWaitupdate = true;
//            MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
                    SwtconController.forceRefresh();
                    //MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                    isWaitupdate = false;
//                    MainBaseActivity.setEinkModeData(mode);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainBaseActivity.setEinkModeData(mode);
                        }
                    }, 200);
                }
            }, 500);
        }
    }

    private static void waitForUpdate(int mode) {
        if (isWaitupdate) {
            MyLog.i(TAG, "waitForUpdate");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    waitForUpdate(mode);
                }
            }, 200);
        } else {
            MainBaseActivity.setEinkModeData(mode);
        }
    }

    public static void launcherUpdate(boolean bSelf) {
        MyLog.d(TAG, "performFullUpdate(View rootView)");
        fullUpdate(bSelf);
    }

    private static void fullUpdate(boolean bSelf) {
        // 保证刷新只有这一个入口，这样可以监听所有的刷新
        MyLog.d(TAG, "fullUpdate: BLauncher 调用了一次fullupdate！" + " isWaitupdate = " + isWaitupdate);
        Handler handler = new Handler();

        if(bSelf) {
            MyLog.d(TAG, "xxx mUpdate = " + mUpdate);
            // set Eink mode update when need full refresh add by gaob@x-thinks@com 20190131 start+++
            if (mUpdate < 7) {
                if (mUpdate == 0) {
                    if (isPixelmode()) {
                        MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
                        isWaitupdate = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyLog.i(TAG, "fullUpdate run()");
                                MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                                isWaitupdate = false;
                            }
                        }, 600);
                    } else {
                        SwtconController.setEinkMode(SwtconControl.WF_MODE_GLD16);
                    }
                } else {
                    if (isPixelmode()) {
                        MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
                        isWaitupdate = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyLog.i(TAG, "fullUpdate run()");
                                MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                                isWaitupdate = false;
                            }
                        }, 500);
                    } else {
                        MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                    }
                }
                mUpdate++;
            } else {
                MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
                mUpdate = 0;
                isWaitupdate = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyLog.i(TAG, "fullUpdate run()");
                        MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                        isWaitupdate = false;
                    }
                }, 500);
                // end+++
            }
        } else {
            //MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_DU2);
            isWaitupdate = true;
            MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyLog.i(TAG, "fullUpdate run()");
                    mUpdate = 0;
                    MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
                    SwtconController.forceRefresh();
                    isWaitupdate = false;
                    //MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                        }
                    }, 200);
                }
            }, 400);
        }
    }
}

