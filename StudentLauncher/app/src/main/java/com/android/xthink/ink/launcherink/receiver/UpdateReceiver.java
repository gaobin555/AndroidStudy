package com.android.xthink.ink.launcherink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.android.xthink.ink.launcherink.eink.SwtconController;
import com.android.xthink.ink.launcherink.service.AutoUpdateService;

public class UpdateReceiver extends BroadcastReceiver {
    private final String TAG = "UpdateReceiver";

    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String intentAction = intent.getAction();
        if (BOOT_COMPLETED.equals(intentAction)) {
            Log.i(TAG, "update BOOT_COMPLETED refresh");
            SwtconController.forceRefresh();
//            Intent autoupdate = new Intent(context, AutoUpdateService.class);
//            context.startService(autoupdate);
        }
    }
}