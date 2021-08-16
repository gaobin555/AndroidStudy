package com.android.xthink.ink.launcherink;

/**
 * 请描述功能
 *
 * @author luoyongjie
 * @version 1.0, 4/15/2017
 */
public class GlobalDataCache {
    public static GlobalDataCache mGlobalDataCache;
    private int defaultHomeScreenIndex;
    private int currentStepCount;
    private String stepDate;
    private boolean isNowDownload = false;
    private int fullUpdateAnimationType = 0;//add by luoyongjie,设置全局刷新的动画模式,20170609
    //    private int currentHomeScreenIndex;
    private boolean isShowNetworkLicense = true;

    private GlobalDataCache() {

    }

    public static GlobalDataCache getInstance() {
        if (null == mGlobalDataCache) {
            mGlobalDataCache = new GlobalDataCache();
        }

        return mGlobalDataCache;
    }

    public int getDefaultHomeScreenIndex() {
        return defaultHomeScreenIndex;
    }

    public void setDefaultHomeScreenIndex(int defaultHomeScreenIndex) {
        this.defaultHomeScreenIndex = defaultHomeScreenIndex;
    }

    public String getStepDate() {
        return stepDate;
    }

    public void setStepDate(String stepDate) {
        this.stepDate = stepDate;
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }

    public void setCurrentStepCount(int currentStepCount) {
        this.currentStepCount = currentStepCount;
    }

    /**
     * 是否正在更新下载操作
     *
     * @return
     */
    public boolean isNowDownload() {
        return isNowDownload;
    }

    public void setNowDownload(boolean nowDownload) {
        isNowDownload = nowDownload;
    }

    public int getFullUpdateAnimationType() {
        return fullUpdateAnimationType;
    }

    public void setFullUpdateAnimationType(int fullUpdateAnimationType) {
        this.fullUpdateAnimationType = fullUpdateAnimationType;
    }

    public boolean isShowNetworkLicense() {
        return isShowNetworkLicense;
    }

    public void setShowNetworkLicense(boolean showNetworkLicense) {
        isShowNetworkLicense = showNetworkLicense;
    }
}
