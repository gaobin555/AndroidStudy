/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.settingmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.base.BaseActivity;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.SharePreferenceHelper;
import com.android.xthink.ink.launcherink.manager.InkToastManager;
import com.android.xthink.ink.launcherink.common.view.SettingView;
import com.android.xthink.ink.launcherink.manager.InkLocalCacheManager;
import com.android.xthink.ink.launcherink.ui.dialog.KidsModeHintDialog;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.utils.SettingsDBUtils;
import com.bumptech.glide.Glide;
import com.eink.swtcon.SwtconControl;


/**
 * 设置页面
 *
 * @author liyuyan
 * @version 1.0, 2019/2/17
 */

public class SettingManagerActivity extends BaseActivity implements SettingView.OnSettingViewClickListener {
    /// zengxx
    public static final String DOUBLE_TAP_TO_WAKE = "DOUBLE_TAP_TO_WAKE";

    public static final String mPageName = "SettingManagerActivity";
    private static final String TAG = "SettingManagerActivity";
    public static final int KIDS_MODE = 1;
    public static final int READING_MODE = 1;
    private final static String SP_IS_FIRST = "sp_is_fist_show_kids_hint";
    private final static String SP_NOT_SHOW_AGAIN = "sp_not_show_again_kids_mode";

    private SettingView mSvPureLock;
    private SettingView mSvLockNotification;
    private SettingView mSvLockEncrypt;
    private SettingView mSvShake;
    private SettingView mTapToWake;//when double tap E-Ink screen, devices will be wake
    private SettingView mSettingUpdate;
    private SettingView mPaperwallPushSv;
    private SettingView mKidsMode;
    private ImageView iv_back;
    private View mRootView;
    private TextView mTitleTv;
    private KidsModeHintDialog mKidsModeHintDialog;
    private SettingView mClockLocker;

    @Override
    public int getLayoutId() {
        return R.layout.activity_desktopmanager;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingManagerActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        mRootView = findViewById(R.id.activity_desktopmanager);
        mSvPureLock = (SettingView) findViewById(R.id.sv_pure_lock);
        mSvLockNotification = (SettingView) findViewById(R.id.sv_lock_notification);
        mSvLockEncrypt = (SettingView) findViewById(R.id.sv_lock_encrypt);
        mSvShake = (SettingView) findViewById(R.id.sv_shake);
        mTapToWake = (SettingView) findViewById(R.id.sv_tap_to_wake);
        mSettingUpdate = (SettingView) findViewById(R.id.setting_update);
        mTitleTv = (TextView) findViewById(R.id.tv_setting_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        mPaperwallPushSv = (SettingView) findViewById(R.id.sv_wallpaper_push);
        mKidsMode = (SettingView) findViewById(R.id.sv_kids_mode);
        mClockLocker = (SettingView) findViewById(R.id.sv_lock_clock);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSvPureLock.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_PURE_LOCK));
        mSvLockNotification.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_LOCK_NOTIFICATION));
        mSvLockEncrypt.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_LOCK_ENCRYPT));
        mSvShake.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_SV_SHAKE));
        mPaperwallPushSv.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_WALLPAPER_PUSH));
        mKidsMode.setIsOpen(isNowKidsMode());
        mTitleTv.setText(R.string.user_setting);
        mClockLocker.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_CLOCK_DISPLAY));

        if (isTapToWakeAvailable(this.getResources())) {
            mTapToWake.setIsOpen(isTapToWakeOpen());//init status
            mTapToWake.setOnSettingViewClickListener(this);
        } else {
            mTapToWake.setVisibility(View.GONE);
        }

        mSettingUpdate.setDes("V " + CommonUtils.getAppVersionName(this));
    }

    @Override
    protected void setListener() {
        mSvLockNotification.setOnSettingViewClickListener(this);
        mSvPureLock.setOnSettingViewClickListener(this);
        mSvShake.setOnSettingViewClickListener(this);
        mSvLockEncrypt.setOnSettingViewClickListener(this);
        mSettingUpdate.setOnSettingViewClickListener(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mPaperwallPushSv.setOnSettingViewClickListener(this);
        mKidsMode.setOnSettingViewClickListener(this);
        mClockLocker.setOnSettingViewClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sv_pure_lock:
                /**
                 * X-Thinks begin, add
                 * what(reason) 统计纯净锁屏,锁屏通知,锁屏加密,微信摇一摇开关
                 */
                boolean purelockValue = SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_PURE_LOCK);
                String pureLockEventId = "pureLock";
                //pure_lock写入系统Settings.Secure
                setLockerMode(InkConstants.SYSTEM_PURE_LOCK, !purelockValue);
                updateLockSettingUi();
                MyLog.i(TAG, String.valueOf(!purelockValue));
                break;
            case R.id.sv_lock_notification:
                boolean locknotificationValue = SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_LOCK_NOTIFICATION);
                String lockNotificationEventId = "lockNotification";
                SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_LOCK_NOTIFICATION, !locknotificationValue);
                MyLog.i(TAG, String.valueOf(!locknotificationValue));
                break;
            case R.id.sv_lock_encrypt:
                boolean lockencryptValue = SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_LOCK_ENCRYPT);
                String lockEncryptEventId = "lockEncrypt";
                SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_LOCK_ENCRYPT, !lockencryptValue);
                MyLog.i(TAG, String.valueOf(!lockencryptValue));
                break;
            case R.id.sv_shake:
                boolean shakeValue = SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_SV_SHAKE);
                SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_SV_SHAKE, !shakeValue);
                MyLog.i(TAG, String.valueOf(!shakeValue));
                if (!shakeValue) {
                    InkToastManager.showToastLong(SettingManagerActivity.this, getString(R.string.prep_picture));
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Glide.get(SettingManagerActivity.this).clearDiskCache();
                            InkLocalCacheManager.getInstance().saveShakeUri("");
                        }
                    }.start();
                }
                break;

            case R.id.sv_tap_to_wake:
                updateTapToWakeValue();
                break;

            case R.id.sv_wallpaper_push: // 壁纸推送
                boolean wallpaperPushEnable = SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_WALLPAPER_PUSH);
                setLockerMode(InkConstants.SYSTEM_WALLPAPER_PUSH, !wallpaperPushEnable);
                updateLockSettingUi();
                break;

            case R.id.sv_kids_mode: // 儿童模式
                final SharePreferenceHelper mSpHelper = SharePreferenceHelper.getInstance(this);
                if (isReadingMode()) {
                    mKidsMode.setIsOpen(false);
                    SystemProperties.set("sys.kids.mode", "0");
                    InkToastManager.showToastLong(SettingManagerActivity.this, getString(R.string.kis_cannot_open));
                } else {
                    if (isNowKidsMode()) {
                        SystemProperties.set("sys.kids.mode", "0");
                    } else {
                        if (isNeedShowKidsModeHint()) {
                            mKidsModeHintDialog = new KidsModeHintDialog(this);
                            mKidsModeHintDialog.setOnEditResult(new KidsModeHintDialog.OnEditResult() {
                                @Override
                                public void onResult(@NonNull Object... results) {
                                    if (results.length == 1) {
                                        Boolean ok = (Boolean) results[0];
                                        if (ok != null) {
                                            if (ok) {
                                                boolean notAskAgain = mKidsModeHintDialog.notAskAgain();
                                                mSpHelper.setBooleanValue(SP_NOT_SHOW_AGAIN, notAskAgain);
                                                mKidsModeHintDialog.dismiss();
                                            }
                                        }
                                    }
                                }
                            });
                            mKidsModeHintDialog.show();
                        }
                        SystemProperties.set("sys.kids.mode", "1");
                        mSpHelper.setBooleanValue(SP_IS_FIRST, false);
                    }
                }
                break;

            case R.id.sv_lock_clock:
                boolean clockLocker = SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_CLOCK_DISPLAY);
                //lock_notification写入系统Settings.Secure
                SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_CLOCK_DISPLAY, !clockLocker);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLockSettingUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * when double tap E-Ink screen, devices will be wake
     *
     * @return true if this function is open,else false
     */
    private boolean isTapToWakeOpen() {
        int value = Settings.Secure.getInt(getContentResolver(), DOUBLE_TAP_TO_WAKE, 0);
        return value != 0;
    }

    private static boolean isTapToWakeAvailable(Resources res) {
        return res.getBoolean(com.android.internal.R.bool.config_supportDoubleTapWake);
    }

    /**
     * update settings value. set 1 if double tap to wake E-Ink function is enable
     */
    private void updateTapToWakeValue() {
        boolean value = mTapToWake.getSwitchViewStatus();
        Settings.Secure.putInt(getContentResolver(), DOUBLE_TAP_TO_WAKE, value ? 1 : 0);
        MyLog.i(TAG, String.valueOf(!value));
    }

    private void updateLockSettingUi() {
        mSvPureLock.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_PURE_LOCK));
        mPaperwallPushSv.setIsOpen(SettingsDBUtils.getSystemSettingValue(this, InkConstants.SYSTEM_WALLPAPER_PUSH));
    }

    private class ShakeBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mSvShake.setIsOpen(SettingsDBUtils.getSystemSettingValue(SettingManagerActivity.this, InkConstants.SYSTEM_SV_SHAKE));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyLog.i(TAG, "onBackPressed: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i(TAG, "onDestroy");
    }

    /**
     * 1、Today锁屏、锁屏推送和自定义壁纸三者之间互斥。
     * 锁屏通知功能的启用与否不受前三者功能影响。
     * <p>
     * 2、Today锁屏默认关闭；锁屏推送默认开启，将其关闭时锁屏将不接受推送内容；当前两个都关闭时，
     * 锁屏显示用户最近自定义的壁纸（如果没有，则显示系统预制的那张壁纸）。
     *
     * @param mode   锁屏模式
     * @param enable 开启或者关闭
     */
    private void setLockerMode(String mode, boolean enable) {
        boolean wallpaperPushEnable = false;
        boolean todayLockEnable = false;
        boolean wallpaperPush = false;

        // 逻辑处理
        if (enable) {
            // 打开某一项的时候，其他项关闭
            switch (mode) {
                case InkConstants.SYSTEM_CUSTOM_WALLPAPER:  // 自定义壁纸
                    wallpaperPushEnable = true;
                    break;
                case InkConstants.SYSTEM_PURE_LOCK:  // today锁屏
                    todayLockEnable = true;
                    break;
                case InkConstants.SYSTEM_WALLPAPER_PUSH:  // 壁纸推送
                    wallpaperPush = true;
                    break;
            }
        } else {
            // 关闭某一项的时候，特殊处理
            switch (mode) {
                case InkConstants.SYSTEM_CUSTOM_WALLPAPER:  // 自定义壁纸
                    // do nothing目前没有自定义壁纸开关
                    break;
                case InkConstants.SYSTEM_PURE_LOCK:  // today锁屏
                    // 关闭today锁屏时，打开自定义壁纸
                    wallpaperPushEnable = true;
                    break;
                case InkConstants.SYSTEM_WALLPAPER_PUSH:  // 壁纸推送
                    // 关闭壁纸推送时，打开自定义壁纸
                    wallpaperPushEnable = true;
                    break;
            }
        }

        // 更新数据库
        SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_CUSTOM_WALLPAPER, wallpaperPushEnable);
        SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_PURE_LOCK, todayLockEnable);
        SettingsDBUtils.putSystemSettingValue(this, InkConstants.SYSTEM_WALLPAPER_PUSH, wallpaperPush);

        MyLog.i(TAG, "wallpaperPushEnable:" + String.valueOf(wallpaperPushEnable) + " todayLockEnable:" + todayLockEnable + " wallpaperPush:" + wallpaperPush);
    }

    //判断是否在儿童模式
    public boolean isNowKidsMode() {
        return SystemProperties.getInt("sys.kids.mode", 0) == KIDS_MODE ? true : false;
    }

    //判断是否在背屏单开模式
    public boolean isReadingMode() {
        return SystemProperties.getInt("sys.pure.reading.mode", 0) == READING_MODE ? true : false;
    }

    //判断是否需要显示
    public boolean isNeedShowKidsModeHint() {
        return isFirstShow(this) || !notShowAgain(this);
    }

    /**
     * 判断入网是否被允许
     *
     * @return true 允许，false不允许
     */
    public static boolean isFirstShow(Context context) {
        SharePreferenceHelper mSpHelper = SharePreferenceHelper.getInstance(context);
        return mSpHelper.getBooleanValue(SP_IS_FIRST, true);
    }

    /**
     * 判断是否还需要询问
     */
    private static boolean notShowAgain(Context context) {
        SharePreferenceHelper mSpHelper = SharePreferenceHelper.getInstance(context);
        return mSpHelper.getBooleanValue(SP_NOT_SHOW_AGAIN, false);
    }
}