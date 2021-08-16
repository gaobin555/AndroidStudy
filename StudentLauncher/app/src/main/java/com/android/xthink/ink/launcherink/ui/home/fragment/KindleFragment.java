/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.ServiceAccessUtils;
import com.android.xthink.ink.launcherink.service.AccessibilityService;
import com.android.xthink.ink.launcherink.utils.FreezeUtils;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

/**
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/10/5
 */
public class KindleFragment extends NativeFragment implements View.OnClickListener {

    public static final String TAG = "KindleFragment";
    private View mHomeBtn;
    private View mShoppingCart;
    private View mFavorite;
    private BroadcastReceiver mReceiver;

    public static KindleFragment newInstance() {

        Bundle args = new Bundle();

        KindleFragment fragment = new KindleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        registerReceiver(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterReceiver();
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View rootView = inflater.inflate(R.layout.fragment_kindle, container, false);

        mHomeBtn = rootView.findViewById(R.id.fragment_kindle_launcher);
        mShoppingCart = rootView.findViewById(R.id.fragment_kindle_shopping_cart);
        mFavorite = rootView.findViewById(R.id.fragment_kindle_reading);

        return rootView;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        mHomeBtn.setOnClickListener(this);
        mShoppingCart.setOnClickListener(this);
        mFavorite.setOnClickListener(this);
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        // 辅助服务
        if (!ServiceAccessUtils.isAccessibilityEnable(mContext, AccessibilityService.class)) {
            ServiceAccessUtils.setAccessibilityEnable(mContext, AccessibilityService.class, true);
        }
    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
        if (isVisibleToUser) {
            // 辅助服务
            if (!ServiceAccessUtils.isAccessibilityEnable(mContext, AccessibilityService.class)) {
                ServiceAccessUtils.setAccessibilityEnable(mContext, AccessibilityService.class, true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 英文版 com.amazon.kindle/com.amazon.kcp.library.StandaloneLibraryActivity
        // 中文版 com.amazon.kindlefc/com.amazon.kcp.library.StandaloneLibraryActivity

        String packageName = InkConstants.PACKAGE_NAME_KINDLE;
        String homeName = InkConstants.ACTIVITY_KINDLE_HOME;
        String shoppingCartName = InkConstants.ACTIVITY_KINDLE_SHOPPING_CART;

        String readingName = InkConstants.ACTIVITY_KINDLE_READING_PAGE;
//        搜索 ： com.amazon.kcp.search.SearchActivity
//        商店 ： com.amazon.kcp.store.LegacyStoreActivity
//        优惠券 ： com.amazon.kindle.map.StandaloneMAPWebViewActivity
//        阅读页面 ：com.amazon.kcp.reader.StandAloneBookReaderActivity

        // 点击按钮，kindle冻结
        if (FreezeUtils.isAppFrozen(mContext, InkConstants.PACKAGE_NAME_KINDLE)) {
            FreezeUtils.handleAppFreeze(mContext, InkConstants.PACKAGE_NAME_KINDLE);
            return;
        }

        try {
            if (id == R.id.fragment_kindle_launcher) {
                ComponentName cmp = new ComponentName(packageName, homeName);
                openKindle(cmp, true);
            } else if (id == R.id.fragment_kindle_shopping_cart) {
                ComponentName cmp = new ComponentName(packageName, shoppingCartName);
                openKindle(cmp, false);
            } else if (id == R.id.fragment_kindle_reading) {
                //AccessibilityService.notifyActions(AccessibilityService.ACTION_OPEN_KINDLE_READING_PAGE);
                ComponentName cmp = new ComponentName(packageName, readingName);
                openKindle(cmp, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监测微信是否安装 并注册安装监听
     */
    private void registerReceiver(Context context) {
        mReceiver = new KindleReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AccessibilityService.ACTION_OPEN_KINDLE_READING_PAGE);
        mContext = context;
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    class KindleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(AccessibilityService.ACTION_OPEN_KINDLE_READING_PAGE)) {
                MyLog.i(TAG, "kindle: 收到打开主页的广播。");
                ComponentName cmp = new ComponentName(InkConstants.PACKAGE_NAME_KINDLE, InkConstants.ACTIVITY_KINDLE_HOME);
                openKindle(cmp, true);
            }
        }
    }

    private void openKindle(ComponentName cmp, boolean isHome) {
        if (mContext == null) {
            return;
        }

        if (isHome) {
            PackageManager packageManager = mContext.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(cmp.getPackageName());
            if (intent == null) {
                return;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setComponent(cmp);
            InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
            mContext.startActivity(intent);
        }

    }
}

