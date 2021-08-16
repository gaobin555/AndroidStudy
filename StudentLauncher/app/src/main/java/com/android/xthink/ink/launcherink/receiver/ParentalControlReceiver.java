package com.android.xthink.ink.launcherink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.android.xthink.ink.launcherink.R;

import static com.android.xthink.ink.launcherink.common.constants.InkConstants.PAGE_ID_TODAY;


public class ParentalControlReceiver extends BroadcastReceiver {
    private final String TAG = "ParentalControlReceiver";

    public static final String ACTION_REPORT_LOSS = "com.android.hotpeper.REPORT_LOSS";
    public static final String ACTION_RESERVE_POWER = "com.android.hotpeper.RESERVE_POWER";
    public static final String ACTION_CLASS_FORBIDDEN = "com.android.hotpeper.ACTION_CLASS_FORBIDDEN";

    public static final String REPORT_LOSS_STATE = "report_loss_state";
    public static final String RESERVE_POWER_STATE = "reserve_power_state";
    public static final String CLASS_FORBIDDEN_STATE = "class_forbidden_state";

    private Context mContext;
    private static int mVolume = 0;
    private boolean isPauseMusic = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        SharedPreferences mSp = context.getSharedPreferences("control_prefs", Context.MODE_PRIVATE);//added by chenjia
        SharedPreferences.Editor mEditor = mSp.edit();
        String intentAction = intent.getAction();
        Log.i(TAG, "intentAction = " + intentAction);

        if (ACTION_REPORT_LOSS.equals(intentAction)) {
            int state = intent.getIntExtra("isReportLoss", 0);
            Log.i(TAG, "REPORT_LOSS mode = " + state);
            int oldReportLossState = mSp.getInt(ParentalControlReceiver.REPORT_LOSS_STATE, 0);
            mEditor.putInt(REPORT_LOSS_STATE, state).apply();
            if (state == 1 && oldReportLossState != 1) {//进入手机挂失模式
                startLauncher();
            } else if (oldReportLossState == 1 && state == 0) {//退出手机挂失模式
                startLauncher();
            }
            startLauncher();
        } else if (ACTION_RESERVE_POWER.equals(intentAction)) {
            int state = intent.getIntExtra("isReservePower", 0);
            Log.i(TAG, "RESERVE_POWER mode = " + state);
            int oldReservePowerState = mSp.getInt(ParentalControlReceiver.RESERVE_POWER_STATE, 0);
            mEditor.putInt(RESERVE_POWER_STATE, state).apply();
            if (state == 1 && oldReservePowerState != 1) {//进入预留电量模式
                startLauncher();
            } else if (oldReservePowerState == 1 && state == 0) {//退出预留电量模式
                startLauncher();
            }
        } else if (ACTION_CLASS_FORBIDDEN.equals(intentAction)) {
            int state = intent.getIntExtra(CLASS_FORBIDDEN_STATE, 0);
            Log.i(TAG, "CLASS_FORBIDDEN mode = " + state);
            int oldClassForbiddenState = mSp.getInt(ParentalControlReceiver.CLASS_FORBIDDEN_STATE, 0);
            mEditor.putInt(CLASS_FORBIDDEN_STATE, state).apply();
            if (state == 1 && oldClassForbiddenState != 1) {// 进入禁用模式
                startLauncher();
                setToSilentMode(true);
            } else if (oldClassForbiddenState == 1 && state == 0) {//退出禁用模式
                startLauncher();
                setToSilentMode(false);
            }
        }
    }

    public void startLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.putExtra("pageId", PAGE_ID_TODAY);
        mContext.startActivity(intent);
    }

    private void setToSilentMode(boolean bSilentState) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (bSilentState) {
            mVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            if(audioManager.isMusicActive()) {
                audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                isPauseMusic = true;
            }
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, mVolume == 0 ? audioManager.getStreamVolume(AudioManager.STREAM_RING) : mVolume, 0);
            if (isPauseMusic) {
                audioManager.abandonAudioFocus(null);
                isPauseMusic = false;
            }
        }
    }
}