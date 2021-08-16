/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.business.presenter.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.android.xthink.ink.launcherink.common.mvp.presenter.AbsPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IDirectAppPresenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IDirectAppView;
import com.android.xthink.ink.launcherink.common.mvp.view.IView;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppList;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.ui.direct.DirectAppDbImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 直通app的业务
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/8
 */
public class DirectAppPresenter extends AbsPresenter implements IDirectAppPresenter {

    private static final String TAG = "DirectAppPresenter";

    private final static int EACH_PAGE_COUNT = 20;

    private IDirectAppView mView;
    private final DirectAppDbImpl mDirectAppDb;
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";

    public static int APP_COUNT = 0;
    public static int READ_APP_COUNT = 0;

    public DirectAppPresenter(@NonNull IView view) {
        super(view);
        mView = (IDirectAppView) view;
        mDirectAppDb = new DirectAppDbImpl(mContext);
    }

    @Override
    public void loadAllDirectApp(final int resultCount, final boolean sortByUseTimes, final boolean createTestData) {
        loadDirectAppLocal(resultCount, sortByUseTimes);
    }

    private void loadDirectAppLocal(final int resultCount, final boolean sortByUseTimes) {
        List<DirectAppBean> directAppBeanList = mDirectAppDb.queryDirectApp(resultCount, sortByUseTimes);
        MyLog.i(TAG, "loadDirectAppLocal, queryResult:" + directAppBeanList);
        if (mView == null) {
            return;
        }
        mView.showDirectAppList(directAppBeanList);
    }

    @Override
    public void loadAllApp(Context context) {
        List<DirectAppBean> directAppBeanList = getAllApps(context);
        if (mView == null) {
            return;
        }
        mView.showDirectAppList(directAppBeanList);
    }

    @Override
    public void loadTypeIcon(Context context) {
        getAllApps(context);
        List<DirectAppBean> directAppBeanList = getTypeIcons();
        if (mView == null) {
            return;
        }
        mView.showDirectAppList(directAppBeanList);
    }

    @Override
    public void loadFixedApp() {
        List<DirectAppBean> directAppBeanList = getFixedApps();
        if (mView == null) {
            return;
        }
        mView.showDirectAppList(directAppBeanList);
    }

    /**
     * 查询手机内所有应用
     * @author gaob@x-thinks.com
     * @param context
     * @return
     */
    private List<DirectAppBean> getAllApps(Context context) {
        //获取手机内所有应用
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> rinfo = context.getPackageManager().queryIntentActivities(i, 0);
        List<DirectAppBean> appInfos = new ArrayList<DirectAppBean>();
        // 根据条件来过滤
        APP_COUNT = 0;
        for (ResolveInfo app : rinfo) {
                DirectAppBean temp = getAppInfo(context, app);
                if ((temp.getAppPackage().equals("com.android.calendar")
                        || temp.getAppPackage().equals("com.android.music")
                        || temp.getAppPackage().equals("com.xthinks.xchat")
                        || temp.getAppPackage().equals("com.android.deskclock")
                        || temp.getAppPackage().equals("com.mediatek.camera")
                        || temp.getAppPackage().equals("com.android.gallery3d")
                        || temp.getAppPackage().equals("com.android.soundrecorder")
                        || temp.getAppPackage().equals("com.mediatek.filemanager")
                        || temp.getAppPackage().equals("com.android.settings")
                        || temp.getAppPackage().equals("com.android.stk")
                        || temp.getAppPackage().equals("com.android.contacts")
                        || temp.getAppPackage().equals("com.android.dialer")
                        || temp.getAppPackage().equals("com.android.email")
                        || temp.getAppPackage().equals("com.android.mms")
                        || temp.getAppPackage().equals("com.android.fmradio")
                        || temp.getAppPackage().equals("com.android.quicksearchbox")
                        || temp.getAppPackage().equals("com.android.browser")
                        || temp.getAppPackage().equals("com.thinkrace.watchservice")
                        || temp.getAppPackage().equals("com.baidu.input")
                        || temp.getAppPackage().equals("com.android.inputmethod.latin")
                        || temp.getAppPackage().equals("com.android.xx.launcherink"))) {
                    continue;
                }
            MyLog.d(TAG, "getAllApps " + temp.getAppPackage());
            appInfos.add(temp);
            APP_COUNT++;
        }
        return appInfos;
    }

    private List<DirectAppBean> getFixedApps() {
        ArrayList<DirectAppBean> appList = new ArrayList<DirectAppBean>();

        DirectAppBean clockBean = new DirectAppBean();
        clockBean.setAppDesc("时钟");
        clockBean.setAppName("时钟");
        clockBean.setAppPackage("com.android.deskclock");
        clockBean.setOrderNo(2);
        clockBean.setId(2);
        appList.add(clockBean);

        DirectAppBean CalendarBean = new DirectAppBean();
        CalendarBean.setAppDesc("日历");
        CalendarBean.setAppName("日历");
        CalendarBean.setAppPackage("com.android.calendar");
        CalendarBean.setOrderNo(3);
        CalendarBean.setId(3);
        appList.add(CalendarBean);

        DirectAppBean CameraBean = new DirectAppBean();
        CameraBean.setAppDesc("相机");
        CameraBean.setAppName("相机");
        CameraBean.setAppPackage("com.mediatek.camera");
        CameraBean.setOrderNo(6);
        CameraBean.setId(6);
        appList.add(CameraBean);

        DirectAppBean GalleryBean = new DirectAppBean();
        GalleryBean.setAppDesc("图库");
        GalleryBean.setAppName("图库");
        GalleryBean.setAppPackage("com.android.gallery3d");
        GalleryBean.setOrderNo(7);
        GalleryBean.setId(7);
        appList.add(GalleryBean);

        DirectAppBean MusicBean = new DirectAppBean();
        MusicBean.setAppDesc("音乐");
        MusicBean.setAppName("音乐");
        MusicBean.setAppPackage("com.android.music");
        MusicBean.setOrderNo(8);
        MusicBean.setId(8);
        appList.add(MusicBean);

        DirectAppBean SRBean = new DirectAppBean();
        SRBean.setAppDesc("录音机");
        SRBean.setAppName("录音机");
        SRBean.setAppPackage("com.android.soundrecorder");
        SRBean.setOrderNo(9);
        SRBean.setId(9);
        appList.add(SRBean);

        DirectAppBean FileBean = new DirectAppBean();
        FileBean.setAppDesc("文件管理");
        FileBean.setAppName("文件管理");
        FileBean.setAppPackage("com.mediatek.filemanager");
        FileBean.setOrderNo(10);
        FileBean.setId(10);
        appList.add(FileBean);

        DirectAppBean SettingsBean = new DirectAppBean();
        SettingsBean.setAppDesc("设置");
        SettingsBean.setAppName("设置");
        SettingsBean.setAppPackage("com.android.settings");
        SettingsBean.setOrderNo(11);
        SettingsBean.setId(11);
        appList.add(SettingsBean);

        return appList;
    }


    private List<DirectAppBean> getTypeIcons() {
        ArrayList<DirectAppBean> appList = new ArrayList<DirectAppBean>();

        DirectAppBean studyBean = new DirectAppBean();
        studyBean.setAppDesc("学习工具");
        studyBean.setAppName("学习工具");
        studyBean.setAppPackage("icon_study_tools");
        studyBean.setOrderNo(1);
        studyBean.setId(1);
        appList.add(studyBean);

        DirectAppBean CalendarBean = new DirectAppBean();
        CalendarBean.setAppDesc("生活便利");
        CalendarBean.setAppName("生活便利");
        CalendarBean.setAppPackage("icon_convenient_life");
        CalendarBean.setOrderNo(2);
        CalendarBean.setId(2);
        appList.add(CalendarBean);

        DirectAppBean CameraBean = new DirectAppBean();
        CameraBean.setAppDesc("音乐故事");
        CameraBean.setAppName("音乐故事");
        CameraBean.setAppPackage("icon_music_story");
        CameraBean.setOrderNo(3);
        CameraBean.setId(3);
        appList.add(CameraBean);

        DirectAppBean GalleryBean = new DirectAppBean();
        GalleryBean.setAppDesc("益智应用");
        GalleryBean.setAppName("益智应用");
        GalleryBean.setAppPackage("icon_puzzle_app");
        GalleryBean.setOrderNo(4);
        GalleryBean.setId(4);
        appList.add(GalleryBean);

        return appList;
    }

    private DirectAppBean getAppInfo(Context context, ResolveInfo app){
        PackageManager packageManager = context.getPackageManager();
        DirectAppBean appInfo = new DirectAppBean();
        String appName = app.activityInfo.loadLabel(packageManager).toString();
        appInfo.setAppName(appName);
        appInfo.setAppDesc(app.activityInfo.name);
        appInfo.setAppPackage(app.activityInfo.packageName);//应用包名
        return appInfo;
    }

    @Override
    public void updateAllAppStatus() {
        mDirectAppDb.updateDirectAppStatus(false);
    }

    @Override
    public boolean hasMoreNewApps(int begin, int count) {
        return mDirectAppDb.checkHasNewApps(begin, count);
    }

    @Override
    public void updateDirectAppStatus(int id, boolean isNew) {
        mDirectAppDb.updateDirectAppStatus(id, isNew);
    }

    @Override
    public void updateDirectAppUseTimes(int id) {
        mDirectAppDb.increaseDirectAppUseTimes(id);
    }
}

