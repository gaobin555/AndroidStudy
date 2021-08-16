package com.android.xthink.ink.launcherink.common.utils;

import android.util.Log;

/**
 * 用于打印log
 *
 * @author gaob@x-thinks.com
 * @version 1.0, 5/1/2019
 */
public class MyLog {

    public static final String TAG = "xxLauncher";
    private static final boolean bDebug = true;
    /**
     * only use when error happened
     *
     * @param tag
     * @param errorMessage
     */
    public static void e(String tag, String errorMessage) {
        Log.e(TAG, tag + ":"+ errorMessage);
    }

    /**
     * only use when error happened
     * @param tag
     * @param errorMessage
     * @param e
     */
    public static void e(String tag, String errorMessage,Throwable e) {
        Log.e(TAG, tag + ":"+ errorMessage, e);
    }


    /**
     * used to debug code
     *
     * @param tag
     * @param debugInfo
     */
    public static void d(String tag, String debugInfo) {
        if (bDebug) {
            Log.d(TAG, tag + ":"+ debugInfo);
        }
    }

    /**
     * Send an {@link Log#INFO} log message.
     *
     * @param tag  Used to identify the source of a log message.  It usually identifies
     *             the class or activity where the log call occurs.
     * @param info The message you would like logged.
     */
    public static void i(String tag, String info) {
        Log.i(TAG, tag + ":"+ info);
    }

}
