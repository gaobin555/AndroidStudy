package com.thinkrace.watchservice.orderlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.thinkrace.watchservice.KApplication;


/**
 * @author mare
 * @PS :同一次commit进行的操作如果含有clear()操作，则先执行clear()再执行其他，与代码前后顺序没有关系。
 * @Description: config of preference
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/8/25
 * @time 16:23
 */
public class PreferControler {
    public static final String KEY_PREFS = "prefer_controlller";

    public static final String KEY_IMEI = "config_imei";

    public static final String KEY_DEVICE_BIND = "config_device_bond";

    //快传配置
    private static final String KEY_PREFS_FLYRTC = "flyrtc";
    private static final String KEY_EXTRA_CALL_MIN_VIDEO_KBPS = "CALL_MIN_VIDEO_KBPS";
    private static final String KEY_EXTRA_CALL_MAX_VIDEO_KBPS = "CALL_Max_VIDEO_KBPS";
    private static final String KEY_EXTRA_CALL_MAX_FRAME_RATE = "CALL_MAX_FRAME_RATE";
    private static final String KEY_EXTRA_CALL_AUDIO_SAMPLE_RATE = "CALL_AUDIO_SAMPLE_RATE";
    private static final String KEY_EXTRA_CALL_BACK_CAMERA_RESOLUTION = "CALL_BACK_CAMERA_RESOLUTION";
    private static final String KEY_EXTRA_CALL_FRONT_CAMERA_RESOLUTION = "FRONT_CAMERA_RESOLUTIOIN";
    private static final String KEY_EXTRA_CALL_FIX_SAMPLE_RATE = "CALL_FIX_SAMPLE_RATE";
    //环信账户相关数据
    public static final String KEY_EXTRA_EASEMOB_USRID = "userId";
    public static final String KEY_EXTRA_EASEMOB_PWD = "pwd";
    public static final String KEY_EXTRA_EASEMOB_TOKEN = "token";
    public static final String KEY_EXTRA_EASEMOB_TOKEN_EXPIRE = "token_expire";//token的默认有效期

    public static final String KEY_PREFS_NET_PARSER = "net_parser";
    public static final String KEY_EXTRA_DOMAIN_STATE = "isDomainParsed";

    private PreferControler() {
    }

    private static class SingletonHolder {
        private static final PreferControler INSTANCE = new PreferControler();
    }

    public static PreferControler instance() {
        return SingletonHolder.INSTANCE;
    }

    private SharedPreferences getSharedPreferences(String key) {
        SharedPreferences sharedPreferences = KApplication.sContext.getSharedPreferences(key,
                Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    private String getString(String keyPreference, String keyValue, String defaultValue) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        return settings.getString(keyValue, defaultValue);
    }

    private int getInt(String keyPreference, String keyValue, int defaultValue) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        return settings.getInt(keyValue, defaultValue);
    }

    private long getLong(String keyPreference, String keyValue, long defaultValue) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        return settings.getLong(keyValue, defaultValue);
    }

    private boolean getBoolean(String keyPreference, String keyValue, boolean defaultValue) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        return settings.getBoolean(keyValue, defaultValue);
    }

    private boolean putInt(String keyPreference, String key, int value) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    private boolean putLong(String keyPreference, String key, long value) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    private boolean putBoolean(String keyPreference, String key, boolean value) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private boolean putString(String keyPreference, String key, String value) {
        SharedPreferences settings = getSharedPreferences(keyPreference);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public boolean setImei(String imei) {
        return putString(KEY_PREFS, KEY_IMEI, imei);
    }

    public String getImei() {
        return getString(KEY_PREFS, KEY_IMEI, "");
    }

    public void clearBond() {
        getSharedPreferences(KEY_PREFS).edit().clear().commit();
    }

    public boolean getDeviceBondState() {
        return getBoolean(KEY_PREFS, KEY_DEVICE_BIND, false);
    }

    public boolean setEaseToken(String token) {
        return putString(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_TOKEN, token);
    }

    public String getEaseToken() {
        return getString(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_TOKEN, "");
    }

    public boolean setEaseTokenExpire(long tokenExpire) {
        return putLong(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_TOKEN_EXPIRE, tokenExpire);
    }

    public long isEaseTokenExpire() {
        return getLong(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_TOKEN_EXPIRE, System.currentTimeMillis());
    }

    public boolean setEaseId(String userid) {
        return putString(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_USRID, userid);
    }

    public String getEaseId() {
        return getString(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_USRID, "");
    }

    public boolean setEasePwd(String pwd) {
        return putString(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_PWD, pwd);
    }

    public String getEasePwd() {
        return getString(KEY_PREFS_FLYRTC, KEY_EXTRA_EASEMOB_PWD, "");
    }

    public void clearEase() {
        getSharedPreferences(KEY_PREFS_FLYRTC).edit().clear().commit();
    }


    /**
     * Min Video kbps
     * if no value was set, return -1
     *
     * @return
     */
    public int getCallMinVideoKbps() {
        return getInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MIN_VIDEO_KBPS, -1);
    }

    public void setCallMinVideoKbps(int minBitRate) {
        putInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MIN_VIDEO_KBPS, minBitRate);
    }

    /**
     * Max Video kbps
     * if no value was set, return -1
     *
     * @return
     */
    public int getCallMaxVideoKbps() {
        return getInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MAX_VIDEO_KBPS, -1);
    }

    public void setCallMaxVideoKbps(int maxBitRate) {
        putInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MAX_VIDEO_KBPS, maxBitRate);
    }

    /**
     * audio sample rate
     * if no value was set, return -1
     *
     * @return
     */
    public int getCallAudioSampleRate() {
        return getInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MAX_VIDEO_KBPS, -1);
    }

    public void setCallAudioSampleRate(int audioSampleRate) {
        putInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MAX_VIDEO_KBPS, audioSampleRate);
    }

    /**
     * Max frame rate
     * if no value was set, return -1
     *
     * @return
     */
    public int getCallMaxFrameRate() {
        return getInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MAX_FRAME_RATE, -1);
    }

    public void setCallMaxFrameRate(int maxFrameRate) {
        putInt(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_MAX_FRAME_RATE, maxFrameRate);
    }

    /**
     * back camera resolution
     * format: 320x240
     * if no value was set, return ""
     */
    public String getCallBackCameraResolution() {
        return getString(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_BACK_CAMERA_RESOLUTION, "");
    }

    public void setCallBackCameraResolution(String resolution) {
        putString(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_BACK_CAMERA_RESOLUTION, resolution);
    }

    /**
     * front camera resolution
     * format: 320x240
     * if no value was set, return ""
     */
    public String getCallFrontCameraResolution() {
        return getString(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_FRONT_CAMERA_RESOLUTION, "");
    }

    public void setCallFrontCameraResolution(String resolution) {
        putString(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_FRONT_CAMERA_RESOLUTION, resolution);
    }

    /**
     * fixed video sample rate
     * if no value was set, return false
     *
     * @return
     */
    public boolean isCallFixedVideoResolution() {
        return getBoolean(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_FIX_SAMPLE_RATE, false);
    }

    public void setCallFixedVideoResolution(boolean enable) {
        putBoolean(KEY_PREFS_FLYRTC, KEY_EXTRA_CALL_FIX_SAMPLE_RATE, enable);
    }

    /**
     * TODO 存储域名解析状态
     *
     * @param isParsed
     * @return
     */
    public boolean setDomainParseState(boolean isParsed) {
        return putBoolean(KEY_PREFS_NET_PARSER, KEY_EXTRA_DOMAIN_STATE, isParsed);
    }

    /**
     * TODO 域名是否被解析过了(开机第一次是false 之后解析了就为true)
     *
     * @return
     */
    public boolean isDomainParsed() {
        return getBoolean(KEY_PREFS_NET_PARSER, KEY_EXTRA_DOMAIN_STATE, false);
    }

}

