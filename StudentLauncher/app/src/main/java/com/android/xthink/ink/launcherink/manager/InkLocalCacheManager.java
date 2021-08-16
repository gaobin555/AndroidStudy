package com.android.xthink.ink.launcherink.manager;

import android.content.Context;

import com.android.xthink.ink.launcherink.common.utils.SharePreferenceHelper;

/**
 * 缓存类，缓存相关写到此类统一管理
 * Created by liyuyan on 2016/12/22.
 */

public class InkLocalCacheManager {

    private static InkLocalCacheManager INSTANCE;
    private SharePreferenceHelper preferenceUtil;

    private final String IS_FIRST_UES = "isFirstUse";

    private InkLocalCacheManager() {
    }

    public void init(Context context) {
        this.preferenceUtil = SharePreferenceHelper.getInstance(context.getApplicationContext());
    }

    public static InkLocalCacheManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InkLocalCacheManager();
        }
        return INSTANCE;
    }

    /**
     * 用户是否第一次使用情况的缓存
     *
     * @param b
     */
    public void saveUseInfo(Boolean b) {
        this.preferenceUtil.setBooleanValue(IS_FIRST_UES, b);
    }

    public Boolean getUseInfo() {
        return this.preferenceUtil.getBooleanValue(IS_FIRST_UES, true);
    }

    /**
     * 清理或还原所有用户缓存
     */
    public void clearAllUserCache() {
        this.preferenceUtil.setBooleanValue(IS_FIRST_UES, true);
    }

    /**
     * BLauncher存储路径
     */
    public void saveDownAppFilePath(String appName, String path) {
        this.preferenceUtil.setStringValue(appName, path);
    }

    public String getDownAppFilePath(String appName) {
        return this.preferenceUtil.getStringValue(appName, "");
    }

    public void saveIsUpdateByData(String data) {
        this.preferenceUtil.setStringValue("isUpdate", data);
    }

    public String getIsUpdateByDate() {
        return this.preferenceUtil.getStringValue("isUpdate", "");
    }

    /**
     * 保存更新请求时间
     *
     * @param currentTime
     */
    public void saveUpdateRequest(long currentTime) {
        this.preferenceUtil.setLongValue("updateRequestTime", currentTime);
    }

    public long getLastUpdateRequest() {
        return this.preferenceUtil.getLongValue("updateRequestTime");
    }

    /**
     * 保存Gallery设置的图片uri
     *
     * @param imageUri
     */
    public void saveShakeUri(String imageUri) {
        this.preferenceUtil.setStringValue("imageUri", imageUri);
    }

    /**
     * 保存Gallery设置的图片uri
     *
     * @param
     */
    public String getShakeUri() {
        return this.preferenceUtil.getStringValue("imageUri");
    }
}
