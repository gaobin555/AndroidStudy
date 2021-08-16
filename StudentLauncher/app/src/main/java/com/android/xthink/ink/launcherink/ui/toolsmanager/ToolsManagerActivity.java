package com.android.xthink.ink.launcherink.ui.toolsmanager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.base.BaseActivity;
import com.android.xthink.ink.launcherink.constants.LauncherConstants;
import com.android.xthink.ink.launcherink.service.SosCallService;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.utils.Tool;
import com.eink.swtcon.SwtconControl;

import java.lang.reflect.Method;

import static android.media.AudioManager.RINGER_MODE_SILENT;

public class ToolsManagerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ToolsManagerActivity";
    private static final String CLOCK_TYPE = "clock_type";

    private ImageView mBack;
    private TextView mTouch;
    private TextView mSaveMode;
    private TextView mClock;
    private TextView mSilent;
    private TextView mLock;
    private TextView mCleanUp;
    private TextView mTwoCode;
    private TextView mSos;

    private FlashlightController mFlashlightController;
    private boolean bTouchState = Tool.getTouchState();
    private boolean bSaveState = Tool.getSaveState();
    private boolean bSilentState = Tool.getSilentState();
    private boolean bClockType;

    private PowerManager mPowerManager;
    private AudioManager mAudioManager;
    private static int mVolume = 0;

    public static String[] sosNumbers;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, " mBroadcastReceiver...action=" + action);
            if (action.equals(LauncherConstants.ACTION_SOS_LIST)) {
                String responseContent = intent.getStringExtra(LauncherConstants.EXTRA_SOS_LIST);
                Log.d(TAG, "收到SOS号码：" + responseContent);
                initSOS(responseContent);
            } else if (action.equals(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)) {
                boolean saveMode = mPowerManager.isPowerSaveMode();
                if (saveMode) {
                    mSaveMode.setCompoundDrawablesWithIntrinsicBounds(null,
                            getResources().getDrawable(R.drawable.save_mode_on, null), null, null);
                } else {
                    mSaveMode.setCompoundDrawablesWithIntrinsicBounds(null,
                            getResources().getDrawable(R.drawable.save_mode, null), null, null);
                }
            }
        }
    };

    public static void start(Context context) {
        Intent tools_action = new Intent("xthink.intent.action.TOOLS_MANAGER");
        ComponentName comp = new ComponentName("com.android.xx.launcherink", "com.android.xthink.ink.launcherink.ui.toolsmanager.ToolsManagerActivity");
        tools_action.setComponent(comp);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
        context.startActivity(tools_action);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tools_manager;
    }

    @Override
    protected void initView() {
        mBack = findViewById(R.id.bt_back);
        mTouch = (TextView) findViewById(R.id.t_touch);
        mSaveMode = (TextView) findViewById(R.id.t_bettery);
        mClock = (TextView) findViewById(R.id.t_change_clock);
        mSilent = findViewById(R.id.t_silent_mode);
        mLock = findViewById(R.id.t_lock_setting);
        mCleanUp = findViewById(R.id.t_clean_up);
        mTwoCode = findViewById(R.id.t_qr_code);
        mSos = findViewById(R.id.t_sos);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mFlashlightController = new FlashlightController(this);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            bSilentState = true;
        }

        if (bSaveState) {
            mSaveMode.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.save_mode_on, null), null, null);
        } else {
            mSaveMode.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.save_mode, null), null, null);
        }

        if (bTouchState) {
            mTouch.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.touch_on, null), null, null);
        } else {
            mTouch.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.touch, null), null, null);
        }

        if (bSilentState) {
            mSilent.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.silent, null), null, null);
            mSilent.setText(R.string.silent_mode);
        } else {
            mSilent.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.alarm, null), null, null);
            mSilent.setText(R.string.nomal_mode);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sosString = prefs.getString("SOS_LIST", null);
        if (sosString != null) {
            initSOS(sosString);
        }
        // 时钟样式
        bClockType = prefs.getBoolean(CLOCK_TYPE, true);
        if (bClockType) {
            mClock.setText(R.string.zodiac_clock);
        } else {
            mClock.setText(R.string.normal_clock);
        }
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mTouch.setOnClickListener(this);
        mSaveMode.setOnClickListener(this);
        mClock.setOnClickListener(this);
        mSilent.setOnClickListener(this);
        mLock.setOnClickListener(this);
        mCleanUp.setOnClickListener(this);
        mTwoCode.setOnClickListener(this);
        mSos.setOnClickListener(this);

        IntentFilter filter = new IntentFilter(LauncherConstants.ACTION_SOS_LIST);
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 手电筒
            case R.id.t_touch:
                Log.d(TAG, "xxx click touch!");
                onClickTouch();
                break;
            // 省电模式
            case R.id.t_bettery:
                onClickPowerSaveMode();
                break;
            // 切换时钟
            case R.id.t_change_clock:
                onClickChangeClock();
                break;
            // 震动模式
            case R.id.t_silent_mode:
                onClickSilent();
                break;
            // 锁屏加密
            case R.id.t_lock_setting:
                Intent intent = new Intent();
                ComponentName cm = new ComponentName("com.android.settings",
                        "com.android.settings.password.ChooseLockGeneric");
                intent.setComponent(cm);
                InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
                startActivity(intent);
                break;
            // 手机加速
            case R.id.t_clean_up:
                onClickCleanUp();
                break;
            // SOS
            case R.id.t_qr_code:
                QrCodeActivity.start(this);
                break;

            case R.id.t_sos:
                onClickSOS();
                break;

            case R.id.bt_back:
                finish();
                break;
        }
    }

    private void onClickChangeClock() {
        if (bClockType) {
            mClock.setText(R.string.normal_clock);
        } else {
            mClock.setText(R.string.zodiac_clock);
        }
        bClockType = !bClockType;
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(this).edit();
        editor.putBoolean(CLOCK_TYPE, bClockType);
        editor.apply();
    }

    private void onClickTouch() {
        // add Toast for low battery level warning, by gaob@x-thinks.com 20190128 start +++
//        if (mFlashlightController.isLowBattery()) {
//            Toast.makeText(this, R.string.torch_low_battery_warning,
//                    Toast.LENGTH_LONG).show();
//            bTouchState = false;
//        } else {
        bTouchState = !bTouchState;
        if (bTouchState) {
            mTouch.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.touch_on, null), null, null);
        } else {
            mTouch.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.touch, null), null, null);
        }
        mFlashlightController.setFlashlight(bTouchState);

//        }
        // end +++
    }

    private void onClickPowerSaveMode() {
        boolean allowed =false;
        try {
            Class<?> clazz = Class.forName("android.os.PowerManager");
            Method m = clazz.getMethod("setPowerSaveMode", new Class[]{boolean.class});
            allowed = (boolean)m.invoke(mPowerManager, !bSaveState);
        } catch (Exception  e) {
            e.printStackTrace();
        }

        if (allowed) {
            Log.d(TAG, "xxx onClickPowerSaveMode allowed bSaveState = " + bSaveState);
            bSaveState = !bSaveState;
        } else {
            bSaveState = false;
        }

//        if (bSaveState) {
//            mSaveMode.setCompoundDrawablesWithIntrinsicBounds(null,
//                    getResources().getDrawable(R.drawable.save_mode_on, null), null, null);
//        } else {
//            mSaveMode.setCompoundDrawablesWithIntrinsicBounds(null,
//                    getResources().getDrawable(R.drawable.save_mode, null), null, null);
//        }
    }

    private void onClickCleanUp() {
        Intent clean_action = new Intent("com.xthink.system.action.CLEAR_APPS");
        clean_action.putExtra("CleanImmediately", true);
        sendBroadcast(clean_action);
        Toast.makeText(this, R.string.clean_up_text, Toast.LENGTH_SHORT).show();
    }

    private void onClickSilent() {
        bSilentState = !bSilentState;

        if (bSilentState) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            mSilent.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.silent, null), null, null);
            mSilent.setText(R.string.silent_mode);
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else {
            mSilent.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.alarm, null), null, null);
            mSilent.setText(R.string.nomal_mode);
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                    mVolume == 0 ? mAudioManager.getStreamVolume(AudioManager.STREAM_RING) : mVolume, 0);
        }
    }

    private void initSOS(String responseContent) {
        sosNumbers = responseContent.split(",");
    }

    private void onClickSOS() {
        if (sosNumbers != null && sosNumbers.length > 0) {
            Intent sosService = new Intent(this, SosCallService.class);
            startForegroundService(sosService);
        } else {
            Toast.makeText(this, "请通过掌上智典添加SOS号码", Toast.LENGTH_SHORT).show();
        }
    }

    private void callPhone(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Tool.setbSaveState(bSaveState);
        Tool.setbSilentState(bSilentState);
        Tool.setbTouchState(bTouchState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
