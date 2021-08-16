package com.android.xthink.ink.launcherink.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Tool {
    private static final String TAG = Tool.class.getSimpleName();
    private static String IMEI = "999999999999999";
    private static String MODEL = "";

    private static boolean bTouchState = false;
    private static boolean bSaveState = false;
    private static boolean bSilentState = false;

    public static boolean getTouchState() {
        return bTouchState;
    }

    public static boolean getSaveState() {
        return bSaveState;
    }

    public static boolean getSilentState() {
        return  bSilentState;
    }

    public static void setbTouchState(boolean touchState) {
        bTouchState = touchState;
    }

    public static void setbSaveState(boolean saveState) {
        bSaveState = saveState;
    }

    public static void setbSilentState(boolean silentState) {
        bSilentState = silentState;
    }
    /**
     * 判定手机网络状态，返回boolean 类型
     */
    public static boolean hasNetwork(Context context) {
        Boolean connected = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo info = connManager.getActiveNetworkInfo();
            if (info != null) {
                connected = info.isConnected();
            }
        }
        return connected;
    }


    /**
     * 返回两个字符串中间的内容
     *
     * @param all
     * @param start
     * @param end
     *
     * @return
     */
    public static String getMiddleString(String all, String start, String end) {
        int beginIdx = all.indexOf(start) + start.length();
        int endIdx   = all.indexOf(end);
        return all.substring(beginIdx, endIdx);
    }

    public static String getIMEI(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm==null){
            Log.d(TAG," TelephonyManager is null!!! ");
            return "999999999999999";
        }
        Log.d(TAG, " TelephonyManager.getPhoneCount() = "+ tm.getPhoneCount());
        if (!TextUtils.isEmpty(tm.getImei())) {
            return tm.getImei(0);
        }

        return "999999999999999";
    }

    /**
     * 手机型号
     */

    public static String getMobileModel() {
        MODEL = android.os.Build.MODEL;
        return MODEL;
    }

    /**
     * TODO 将unicode的汉字码转换成utf-8格式的汉字
     *
     * @param unicode
     * @return
     */
    public static String unicodeNo0xuToString(String unicode) {
        if (TextUtils.isEmpty(unicode)) {
            return null;
        }
        StringBuffer string = new StringBuffer();
        int len = unicode.length();
        int arrayLen = len / 4;
        String hex;
        int data;
        try {
            for (int i = 0; i < arrayLen; i++) {
                hex = unicode.substring(i * 4, (i + 1) * 4);
                data = Integer.parseInt(hex, 16);
                string.append((char) data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string.toString();
    }

    /**
     * 将bitmap转换成byte,该过程时间较长,建议子线程运行,但是这里我为了setText，就放主线程了
     *
     * @param bitmap
     * @return
     */
    public static String imageToBase64(Bitmap bitmap) {
        //以防解析错误之后bitmap为null
        if (bitmap == null)
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //此步骤为将bitmap进行压缩，我选择了原格式png，第二个参数为压缩质量，我选择了原画质，也就是100，第三个参数传入outputstream去写入压缩后的数据
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将获取到的outputstream转换成byte数组
        byte[] bytes = outputStream.toByteArray();
        //android.util包下有Base64工具类，直接调用，格式选择Base64.DEFAULT即可
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        //打印数据，下面计算用
        Log.i("MyLog", "imageToBase64: " + str.length());
        return str;
    }

    /**
     * 将base64转成bitmap，该过程速度很快
     *
     * @param text
     * @return
     */
    public static Bitmap base64ToImage(String text) {
        //同样的，用base64.decode解析编码，格式跟上面一致
        byte[] bytes = Base64.decode(text, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}