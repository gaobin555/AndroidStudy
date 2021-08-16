package com.android.xthink.ink.launcherink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.android.xthink.ink.launcherink.common.utils.MyLog;

import static com.android.xthink.ink.launcherink.ui.home.fragment.MusicFragment.mediaPlayer;
import static com.android.xthink.ink.launcherink.ui.home.fragment.MusicFragment.musicPause;
import static com.android.xthink.ink.launcherink.ui.home.fragment.MusicFragment.musicStart;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaButtonReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // 获得Action
        String intentAction = intent.getAction();
        // 获得KeyEvent对象
        KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        //MyLog.i(TAG, "Action ---->" + intentAction + "  KeyEvent----->"+ keyEvent.toString());

        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            int keyCode = keyEvent.getKeyCode();
            int keyAction = keyEvent.getAction();
            MyLog.d(TAG, "keyCode = " + keyCode + ", keyCode = " + keyCode);
            if (KeyEvent.KEYCODE_MEDIA_PAUSE == keyCode && keyAction == KeyEvent.ACTION_DOWN) {
                MyLog.d(TAG, "KEYCODE_MEDIA_PAUSE");
                if(mediaPlayer != null) {
                    musicPause();
                }
            }

            if (KeyEvent.KEYCODE_MEDIA_PLAY == keyCode && keyAction == KeyEvent.ACTION_DOWN) {
                MyLog.d(TAG, "KEYCODE_MEDIA_PLAY");
                if(mediaPlayer != null) {
                    musicStart();
                }
            }
        }
    }
}
