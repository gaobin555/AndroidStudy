/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.mvp.model.others;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.common.constants.InkWeChatConstants;
import com.android.xthink.ink.launcherink.common.network.user.bean.WechatActivityNameInfo;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.UiTestUtils;

import java.util.List;

import static com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider.SP_ACTION_NAME_FAVORITE;
import static com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider.SP_ACTION_NAME_MOMENTS;
import static com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider.SP_ACTION_NAME_SUBSCRIBE;
import static com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider.SP_PACKAGE_NAME_FAVORITE;
import static com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider.SP_PACKAGE_NAME_MOMENTS;
import static com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider.SP_PACKAGE_NAME_SUBSCRIBE;


/**
 * 微信的model
 *
 * @author wanchi@x-thinks.com
 * @version 1.0, 2018/6/14
 */
public class WechatModel {

    private static final String TAG = "WechatModel";

    private Context mContext;
    private String mImei;
    private final WechatNameProvider mProvider;

    public WechatModel(Context context) {
        mContext = context;
        mImei = CommonUtils.getIMEI(context);
        mProvider = WechatNameProvider.getInstance(context);
    }

    /**
     * 打开朋友圈
     */
    public boolean openWeChatMoments() {
        // 得到之前打开过的包名
        return openAuto(SP_PACKAGE_NAME_MOMENTS);
    }

    public final static String WEIXIN_SNS_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.plugin.sns.SnsTimeLineUI";//微信朋友圈
    /**
     * 朋友圈
     * @param context
     * @param id
     */
    public static void shareToTimeLine(Context context,int id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.withAppendedPath(
                ContactsContract.Data.CONTENT_URI, String.valueOf(id)), WEIXIN_SNS_MIMETYPE);
        setUpdateModeforWechat(context, 1);
        context.startActivity(intent);
    }

    /**
     * 打开订阅号
     */
    public boolean openWeChatSubScribe() {
        return openAuto(SP_PACKAGE_NAME_SUBSCRIBE);
    }

    /**
     * 打开微信
     */
    public boolean openWechatLauncher() {
        return openOtherActivitySafely(InkWeChatConstants.WECHAT_LAUNCHER_PACKAGE_NAME, true);
    }

    /**
     * 打开微信收藏
     */
    public boolean openWeChatFavorite() {
        return openAuto(SP_PACKAGE_NAME_FAVORITE);
    }

    /**
     * 打开微信二维码
     */
    public boolean openQrCode() {
        return openAuto(WechatNameProvider.SP_PACKAGE_NAME_QR_CODE);
    }

    private boolean openAuto(final String key) {
        // 得到之前打开过的包名
        int code = CommonUtils.getWechatVersion(mContext);
        UiTestUtils.tag("openAuto: 开始打开" + key);
        UiTestUtils.tag("openAuto: 版本号是：" + code);

        String packageNameCache;//mProvider.getName(key, code);
        if (key.equals(SP_PACKAGE_NAME_MOMENTS)) {
            packageNameCache = SP_ACTION_NAME_MOMENTS;
        } else if (key.equals(SP_PACKAGE_NAME_SUBSCRIBE)) {
            packageNameCache = SP_ACTION_NAME_SUBSCRIBE;
        } else if (key.equals(SP_PACKAGE_NAME_FAVORITE)) {
            packageNameCache = SP_ACTION_NAME_FAVORITE;
        } else {
            packageNameCache = mProvider.getName(key, code);
        }

        MyLog.d(TAG, "openAuto packageNameCache = " + packageNameCache);

        // 如果本地包名存在，直接打开这个包名
        if (!TextUtils.isEmpty(packageNameCache)) {
            UiTestUtils.tag("openAuto: 得到包名：" + packageNameCache);
            if (openOtherActivitySafely(packageNameCache)) {
                // 打开成功
                MyLog.d(TAG,"openAuto: 打开成功");
                mProvider.saveFastAccessPackageName(key, packageNameCache);
                return true;
            } else {
                MyLog.d(TAG,"openAuto: 打开失败");
            }
        }

        return false;
    }

    private void openOtherActivity(String packageName, boolean isLaunch) throws Exception {
        Intent intent = new Intent();

        if (isLaunch) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        ComponentName cmp = new ComponentName(InkWeChatConstants.WECHAT_PACKAGE_NAME, packageName);
        intent.setComponent(cmp);
        setUpdateModeforWechat(mContext, 1);
        mContext.startActivity(intent);
    }

    private boolean openOtherActivitySafely(String packageName) {
        return openOtherActivitySafely(packageName, false);
    }

    /**
     * 打开指定的Activity
     *
     * @param packageNames 所有版本的包名
     * @return 正确的包名
     */
    private String openOtherActivitySafely(List<WechatActivityNameInfo> packageNames, String key) {
        for (WechatActivityNameInfo packageNameInfo : packageNames) {
            String packageName = "";
            if (SP_PACKAGE_NAME_MOMENTS.equals(key)) {
                packageName = packageNameInfo.getMoments();
            }
            if (SP_PACKAGE_NAME_SUBSCRIBE.equals(key)) {
                packageName = packageNameInfo.getSubscribe();
            }
            if (SP_PACKAGE_NAME_FAVORITE.equals(key)) {
                packageName = packageNameInfo.getStore();
            }

            try {
                openOtherActivity(packageName, false);
            } catch (Exception e) {
                continue;
            }
            return packageName;
        }
        return null;
    }

    private boolean openOtherActivitySafely(String packageName, boolean isLaunch) {
        try {
            openOtherActivity(packageName, isLaunch);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    //private static int WF_MODE_DU2 = 1;
    //Setting 数据库中写值
    private static void setEinkModeData(Context context, int mode) {
        if (mode == getEinkModeData(context)) {
            return;
        }
        MyLog.d(TAG, "setEinkModeData mode = " + mode);

        try {
            Settings.Global.putInt(context.getContentResolver(), "xthink.settings.EINK_MODE_SETTINGS", mode);
        } catch (Exception e) {
            MyLog.i(TAG, "setEinkModeData(" + mode + ")> " + e.getMessage());
        }
    }

    //Setting 数据库中读取值
    private static int getEinkModeData(Context context) {
        int mode = 1;
        try {
            mode = Settings.Global.getInt(context.getContentResolver(), "xthink.settings.EINK_MODE_SETTINGS", 1);
        } catch (Exception e) {
            if (e != null) {
                MyLog.i(TAG, "getEinkModeData()> " + e.getMessage());
            }
        }
        MyLog.d(TAG, "getEinkModeData mode = " + mode);
        return mode;
    }

    private static void setUpdateModeforWechat(final Context context, final int mode) {
        MyLog.i(TAG, "setUpdateModeforWechat : mode = " + mode);
        Handler handler = new Handler();
        setEinkModeData(context,2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setEinkModeData(context, mode);
            }
        }, 700);
    }
}

