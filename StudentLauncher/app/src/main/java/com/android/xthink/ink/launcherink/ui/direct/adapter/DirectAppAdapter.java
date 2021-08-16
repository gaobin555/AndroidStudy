/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.direct.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IDirectAppPresenter;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.view.MyImageView;
import com.android.xthink.ink.launcherink.manager.InkToastManager;
import com.android.xthink.ink.launcherink.manager.image.GlideManagerImpl;
import com.android.xthink.ink.launcherink.ui.direct.DirectActivity;
import com.android.xthink.ink.launcherink.ui.direct.DirectAppAnalysis;
import com.android.xthink.ink.launcherink.utils.FreezeUtils;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 直通app适配器
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/1
 */
public class DirectAppAdapter extends BaseAdapter {

    private static final String TAG = "DirectAppAdapter";

    /**
     * 白色背景皮肤，更多页面
     */
    public static final int SKIN_WHITE_BG = 0;
    /**
     * 黑色背景皮肤，发现页面
     */
    public static final int SKIN_BLACK_BG = 1;

    private static final int SKIN_END_WITH_MORE = SKIN_BLACK_BG;
    private static final int SKIN_ALL = SKIN_WHITE_BG;

    private List<DirectAppBean> mDirectAppBeanList;
    private final LayoutInflater mInflater;
    private Context mContext;
    private int mSkin = SKIN_WHITE_BG; // 结尾是more图标
    private final GlideManagerImpl mGlideManager;
    private final IDirectAppPresenter mPresenter;
    private boolean hasMoreNewApp;
    PackageManager mPm;

    /**
     * @param context           上下文
     * @param directAppBeanList app列表
     * @param skin              皮肤{@link #SKIN_BLACK_BG}{@link #SKIN_WHITE_BG} 发现页面是黑色背景皮肤，更多页面里面是白色背景皮肤。
     */
    public DirectAppAdapter(Context context, IDirectAppPresenter presenter, List<DirectAppBean> directAppBeanList, int skin) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDirectAppBeanList = new ArrayList<>();
        mDirectAppBeanList.addAll(directAppBeanList);
        mSkin = skin;
        mGlideManager = new GlideManagerImpl();
        mPresenter = presenter;
        hasMoreNewApp = presenter.hasMoreNewApps(7, -1);
        mPm = mContext.getPackageManager();

        initSkin(skin);
    }

    public void updateApps(List<DirectAppBean> directAppBeanList) {
        mDirectAppBeanList.clear();
        mDirectAppBeanList.addAll(directAppBeanList);
        hasMoreNewApp = mPresenter.hasMoreNewApps(7, -1);
        MyLog.i(TAG, "updateApps: hasMoreNewApp" + hasMoreNewApp);
        initSkin(mSkin);
        notifyDataSetChanged();
    }

    private void initSkin(int skin) {
        if (skin == SKIN_END_WITH_MORE) {
            DirectAppBean directAppBean = new DirectAppBean();
            mDirectAppBeanList.add(directAppBean);
        }
    }

    @Override
    public int getCount() {
        return mDirectAppBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDirectAppBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            if (mSkin == SKIN_BLACK_BG) {
                convertView = mInflater.inflate(R.layout.item_direct_app_white_bg, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.item_direct_app_white_bg, parent, false);
            }
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_direct_app_name_tv);
            viewHolder.icon = (MyImageView) convertView.findViewById(R.id.item_direct_app_icon_iv);
//            viewHolder.newFlag = convertView.findViewById(R.id.item_direct_app_new_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final DirectAppBean directAppBean = (DirectAppBean) getItem(position);


        String language = Locale.getDefault().getLanguage();
        String name = directAppBean.getAppName(language);
        final String packageName = directAppBean.getAppPackage();

//        boolean isNew = directAppBean.isNew();
//        if (isNew) {
//            viewHolder.newFlag.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.newFlag.setVisibility(View.GONE);
//        }

        //加载图片名字，区分一二级页面
        if (mSkin == SKIN_BLACK_BG) {
            // 一级页面
            if (position == getCount() - 1) {
                // 其他
                viewHolder.icon.setImageResource(R.mipmap.app_store);
                viewHolder.name.setText(R.string.app_store);
                viewHolder.icon.setVisibility(View.GONE);
                viewHolder.name.setVisibility(View.GONE);

            } else {
                // load app icon from package 2016.01.09 modify by gaob@x-thinks.com start +++
                // mGlideManager.loadImage(directAppBean.getIcon1(), viewHolder.icon, mContext, R.drawable.icon_direct_app_default);
                String appIconUrl = directAppBean.getAppIconUrl();
                if (appIconUrl == null || appIconUrl.equals("")) {
                    viewHolder.icon.setImageDrawable(getAppIcon(packageName));
                } else {
                    viewHolder.icon.setImageURL(appIconUrl, getAppIcon(packageName));
                }
                // end +++
                String appName = getAppName(packageName);
                if (appName == null || appName.equals(""))
                    viewHolder.name.setText(name);
                else
                    viewHolder.name.setText(appName);
            }

        } else {
            // 二级页面
            // load app icon from package 2016.01.09 modify by gaob@x-thinks.com start +++
            // mGlideManager.loadImage(directAppBean.getIcon2(), viewHolder.icon, mContext, R.drawable.icon_direct_app_default);
            viewHolder.icon.setImageDrawable(getAppIcon(packageName));
//            MyLog.i(TAG, "packageName = " + packageName);
            // end +++
            viewHolder.name.setText(name);
        }


        // 点击事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = directAppBean.getId();

                if (mSkin == SKIN_END_WITH_MORE && position == getCount() - 1) {
                    // 点击其他
//                    if (!"user".equals(Build.TYPE)) {
//                        DirectActivity.start(mContext);
//                    }
                } else {
                    // 点击应用
                    String noAppTip = mContext.getString(R.string.direct_no_app);
                    MyLog.d(TAG, "click packageName = " + packageName);
                    if ("icon_study_tools,icon_convenient_life,icon_music_story,icon_puzzle_app".contains(packageName)) {
                        InkToastManager.showToastShort(mContext, "此功能正在开发中......");
                        return;
                    }
                    try {
                        PackageManager packageManager = mContext.getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                        if (intent == null) {
                            //没有得到打开app的intent。
                            if (FreezeUtils.isAppFrozen(mContext, packageName)) {
                                FreezeUtils.handleAppFreeze(mContext, packageName);
                            } else {
                                InkToastManager.showToastShort(mContext, noAppTip);
                                DirectAppAnalysis.recordOpenApp(mContext, false, packageName);
                            }
                            return;
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        MyLog.d(TAG, "startActivity:" + intent);
                        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        InkToastManager.showToastShort(mContext, noAppTip);
                        DirectAppAnalysis.recordOpenApp(mContext, false, packageName);
                        return;
                    }
                    mPresenter.updateDirectAppUseTimes(id);
                    DirectAppAnalysis.recordOpenApp(mContext, true, packageName);
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        MyImageView icon;
//        View newFlag;
    }

    /**
     * get app Icon from package or local
     * author gaob@x-thinks.com
     * @param pakeagename
     * @return
     */
    private Drawable getAppIcon(String pakeagename){
        if (pakeagename.equals("icon_study_tools")) {
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_study_tools);
        } else if (pakeagename.equals("icon_convenient_life")) {
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_convenient_life);
        } else if (pakeagename.equals("icon_music_story")) {
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_music_story);
        } else if (pakeagename.equals("icon_puzzle_app")) {
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_puzzle_app);
        } else if (pakeagename.equals("com.android.dialer")) {
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_phone);
        } else if (pakeagename.equals("com.android.settings")&&isSystemAPP(pakeagename, mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_settings);
        } else if(pakeagename.equals("com.android.deskclock")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_clock);
        } else if (pakeagename.contains("camera")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_camera);
        } else if (pakeagename.contains("music")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_music);
        } else if(pakeagename.equals("com.android.gallery3d")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_gallery);
        } else if(pakeagename.contains("com.android.calendar")){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_calendar);
        } else if(pakeagename.equals("com.android.soundrecorder")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_soundrecord);
        } else if(pakeagename.equals("com.android.fmradio")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_fmradio);
        } else if(pakeagename.equals("com.android.mms")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_message);
        } else if(pakeagename.equals("com.amazon.kindle")){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_kindle);
        } else if(pakeagename.equals("com.android.calculator2")){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_file_calculator);
        } else if(pakeagename.equals("com.tencent.mm")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_wechat);
        } else if(pakeagename.equals("com.android.email")){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_email);
        } else if(pakeagename.equals("com.mediatek.filemanager")&&isSystemAPP(pakeagename,mContext)){
            return ContextCompat.getDrawable(mContext,R.mipmap.icon_filemanage);
        } else {
            try {
                MyLog.d(TAG, "loadIcon:" + pakeagename);
                ApplicationInfo info = mPm.getApplicationInfo(pakeagename, 0);
                return info.loadIcon(mPm);
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return ContextCompat.getDrawable(mContext, R.drawable.icon_direct_app_default);
    }

    /**
     * get application name from package name
     * @param packageName
     * @return
     */
    private String getAppName(String packageName) {
        String appName = "";
        try {
            appName=mPm.getApplicationLabel(mPm.getApplicationInfo(packageName,PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }

    /**
     * 判断是否是系统应用
     *
     * @param packageInfo
     * @return
     */
    public static Boolean isSystemAPP(PackageInfo packageInfo) {
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
            return false;
        } else { // 系统应用
            return true;
        }
    }

    public static Boolean isSystemAPP(String packagename,Context mContext){
        try {
            PackageInfo pkgInfo = mContext.getPackageManager()
                    .getPackageInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);
            if ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                return false;
            } else { // 系统应用
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}

