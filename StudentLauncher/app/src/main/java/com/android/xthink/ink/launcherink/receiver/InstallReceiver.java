/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.SparseBooleanArray;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.mvp.view.IDirectAppView;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.datasave.pager.OperatePagersDBImpl;
import com.android.xthink.ink.launcherink.ui.home.fragment.IPageCallback;
import com.android.xthink.ink.launcherink.ui.home.fragment.BasePagerFragment;

/**
 * 检测安装与卸载的广播接收器
 *
 * @author gaob@x-thinks.com
 * @version 1.0, 2019/1/30
 */
public class InstallReceiver extends BroadcastReceiver {

    private static final String TAG = "InstallReceiver";

    private IPageCallback mPageCallback;
    private IDirectAppView mDirectAppView = null;
    private Context mContext;

    private static SparseBooleanArray mPageEnableMap = new SparseBooleanArray();

    public InstallReceiver(IPageCallback pageCallback, Context context) {
        mPageCallback = pageCallback;
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);
    }

    public InstallReceiver(IDirectAppView directAppView, Context context) {
        mDirectAppView = directAppView;
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        context.registerReceiver(this, filter);
    }

    public void unregister() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
            MyLog.i(TAG, "应用升级");
            return;
        }

        // 监听应用安装卸载刷新applist，gaob@x-thinks.com 2019.1.12
        if(mDirectAppView != null) {
            if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())
                    || "android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                mDirectAppView.updataAppList();
                return;
            }
        }

        // 根据intent找到对应的page.
        String packageName = intent.getDataString();
        MyLog.d(TAG, "packageName = " + packageName);
        int pageId = findPageIdByPackage(packageName);
        if (pageId == InkConstants.PAGE_ID_NONE) {
            // 没有这个page，不处理
            return;
        }

        //接收安装广播
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
            MyLog.i(TAG, "安装了:" + packageName + "包名的程序" + System.currentTimeMillis());
            handleInstallPage(context, pageId);
        }
        //接收卸载广播
        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
            MyLog.i(TAG, "卸载了:" + packageName + "包名的程序" + System.currentTimeMillis());
            handleUninstallPage(pageId);
        }
    }

    /**
     * 从广播接收器中得到的包名来寻找id,包名包含前缀package:
     * 如果要监听其他的app的卸载和安装，在这里统一加入package和id的对应关系即可。
     *
     * @param packageName 包含前缀package:的包名
     * @return 该page的id，如果没有找到就返回-1
     */
    private int findPageIdByPackage(String packageName) {
        if (("package:" + InkConstants.PACKAGE_NAME_WECHAT).equals(packageName)) {
            return InkConstants.PAGE_ID_WECHAT;
        } else if (("package:" + InkConstants.PACKAGE_NAME_KINDLE).equals(packageName)) {
            return InkConstants.PAGE_ID_KINDLE;
        }
        return InkConstants.PAGE_ID_NONE;
    }

    private void handleInstallPage(Context context, int pageId) {
        if (null != mPageCallback) {
            //查询页面是否在收据库中
            OperatePagersDBImpl db = new OperatePagersDBImpl(context);
            boolean isExist = db.queryPageExist(pageId);
            if (isExist) {
                //更改数据库 加载页面
                db.updatePageSelectedById(pageId, true);
                mPageCallback.showPager(pageId);
            }
        }
        // 标记为可以被显示，因为已经安装了。
        setPageEnable(pageId, true);
    }

    private void handleUninstallPage(int pageId) {
        if (null != mPageCallback) {
            mPageCallback.hidePager(pageId);
        }
        setPageEnable(pageId, false);
    }

    /**
     * 由于某些原因，虽然设置里添加了要显示，但是，fragment还是需要被隐藏。例如wechat没有安装，todo没有添加日程。
     *
     * @param enable 是否需要显示
     */
    public static void setPageEnable(int pageId, boolean enable) {
        mPageEnableMap.put(pageId, enable);
        MyLog.d(TAG, BasePagerFragment.class.getName() + "mEnable" + enable);
    }

    public static boolean isPageEnable(int pageId) {
        return mPageEnableMap.get(pageId, true);
    }
}

