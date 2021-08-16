/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;

import com.android.xthink.ink.launcherink.common.constants.InkWeChatConstants;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 一些通用的工具类
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/3/23
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";

    /**
     * 去多余的零
     *
     * @param number
     * @return
     */
    public static String getPrettyNumber(double number) {
        String plainString = BigDecimal.valueOf(number)
                .stripTrailingZeros().toPlainString();
        if (plainString.equals("0.0")) {
            plainString = "0";
        }
        return plainString;
    }

    public static String getAppVersionName(Context context) {
        PackageInfo mPackageInfo = getPackageInfo(context);
        if (mPackageInfo != null) {
            return mPackageInfo.versionName;
        }
        return "";
    }

    /**
     * 获取设备imei号
     *
     * @param context
     * @return imei号
     */
    public static String getIMEI(Context context) {
        String imei = "123456";
//        TelephonyManager sTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (null != sTelephonyManager) {
//            imei = sTelephonyManager.getDeviceId();
//        }
//
//        if (StringUtils.isEmpty(imei)) {
//            imei = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
//        }

        return imei;
    }

    /**
     * 获取设备系统版本号
     *
     * @return imei号
     */
    public static String getSystemOSVersion() {
        return Build.ID;
    }

    /**
     * 获取设备运营商
     *
     * @return imei号
     */
    public static String getOperatorCode() {
        String buildId = getSystemOSVersion();
        if (!android.text.TextUtils.isEmpty(buildId) && buildId.length() >= 8) {
            MyLog.e(TAG, "getOperatorCode length error,buildId is:" + buildId);
            return getSystemOSVersion().substring(6, 8);
        }
        return "";
    }

    /**
     * 获取App包 信息版本号
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }


    /**
     * check the app is installed
     *
     * @param pkgName app's package name
     * @return is installed
     */
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        return packageInfo != null;
    }

    public static <T> List<T> jsonArrayToList(JSONArray array, Class<T> classT) {
        List<T> list = new ArrayList<>();
        if (array != null) {
            int len = array.length();
            for (int i = 0; i < len; i++) {
                try {
                    Object o = array.get(i);
                    if (o != null) {
                        list.add((T) o);
                    }
                } catch (JSONException | ClassCastException e) {
                    e.printStackTrace();
                }

            }
        }
        return list;
    }

    /**
     * 解析json字符串，捕获异常以后返回null
     *
     * @param json   待解析的json字符串
     * @param classT 数据类型的class
     * @return 解析后的对象
     */
    @Nullable
    public static <T> T fromJsonSafely(String json, Class<T> classT) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(json, classT);
        } catch (JsonParseException e) {
            //do nothing
        }
        return t;
    }

    /**
     * 解析json字符串，捕获异常以后返回null
     *
     * @param json    待解析的json字符串
     * @param typeOfT The specific genericized type of src. You can obtain this type by using the
     * @return 解析后的对象
     */
    @Nullable
    public static <T> T fromJsonSafely(String json, Type typeOfT) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(json, typeOfT);
        } catch (JsonParseException e) {
            // do nothing
        }
        return t;
    }


    public static boolean containsKeys(String target, String[] keys) {
        if (android.text.TextUtils.isEmpty(target)) {
            return false;
        }
        for (String key : keys) {
            if (target.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (android.text.TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length;
        }
    }

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    public static boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        return false;
    }


    /**
     * 返回微信版本Code，如果没有找到微信，返回-1
     *
     * @param context 上下文
     * @return VersionCode
     */
    public static int getWechatVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(InkWeChatConstants.WECHAT_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            return -1;
        }
        return pInfo.versionCode;
    }

    /**
     * 返回微信版本名称，如果没有找到微信，返回""
     *
     * @param context 上下文
     * @return 版本号
     */
    public static String getWechatVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(InkWeChatConstants.WECHAT_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            return "";
        }
        return pInfo.versionName;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

