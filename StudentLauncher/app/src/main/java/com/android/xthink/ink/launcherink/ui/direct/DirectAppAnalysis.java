package com.android.xthink.ink.launcherink.ui.direct;

import android.content.Context;

import com.android.xthink.ink.launcherink.common.utils.MyLog;
//import com.coolyota.analysis.CYAnalysis;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 直通统计
 *
 * @author gaobin
 * @version 1.0, 2019/4/16
 */

public class DirectAppAnalysis {

    private static final String TAG = "DirectAppAnalysis";

    /**
     * 统计直通app打开的情况
     *
     * @param context     上下文
     * @param success     是否打开成功
     * @param packageName 包名
     */
    public static void recordOpenApp(Context context, boolean success, String packageName) {
        recordOpenApp(context, success, packageName, 1);
    }

    /**
     * 统计直通app打开的情况，可以添加统计次数的参数。
     *
     * @param context     上下文
     * @param success     是否打开成功
     * @param packageName 包名
     * @param times       打开次数
     */
    public static void recordOpenApp(Context context, boolean success, String packageName, int times) {
        String eventId = success ? "open_direct_app_success" : "open_direct_app_fail";
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("packageName", packageName);
            jsonObj.put("times", times);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyLog.i(TAG, "eventId:" + eventId + ", recordOpenApp: " + jsonObj.toString());
//        CYAnalysis.onEvent(context, eventId, "", jsonObj.toString());
    }

}
