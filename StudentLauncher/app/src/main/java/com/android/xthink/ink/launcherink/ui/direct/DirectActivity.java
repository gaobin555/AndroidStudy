/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.direct;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.base.TitleBaseActivity;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IDirectAppPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.Presenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IDirectAppView;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.receiver.InstallReceiver;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.ui.direct.adapter.DirectAppAdapter;
import com.eink.swtcon.SwtconControl;

import java.util.List;

/**
 * 应用界面
 *
 * @author gaobin@x-thinks.com
 * @version 1.0, 2019/8/2
 */
public class DirectActivity extends TitleBaseActivity implements View.OnClickListener, IDirectAppView {

    private static final String TAG = "DirectActivity";

    private final static int PAGE_COUNT = 100;

    private GridView mAppGv;
    private IDirectAppPresenter mDirectAppPresenter;
    private View mRootView;
//    private InstallReceiver mInstallReceiver;
    private static boolean mStartbyOwn = false;

    public static void start(Context context) {
        MyLog.d(TAG, "DirectActivity start");
        Intent starter = new Intent(context, DirectActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(starter);
        mStartbyOwn = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Presenter.bind(this, IDirectAppPresenter.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void addPresenter(IPresenter presenter) {
        super.addPresenter(presenter);
        if (presenter instanceof IDirectAppPresenter) {
            mDirectAppPresenter = (IDirectAppPresenter) presenter;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        InkDeviceUtils.isLauncher = true;
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_GLD16);
        MyLog.d(TAG, "onResume ");
    }

    @Override
    protected void onPause() {
        // 退出Launcher暂时设置为DU2模式
        mStartbyOwn = false;
//        InkDeviceUtils.isLauncher = false;
        super.onPause();
        MyLog.d(TAG, "onPause");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_direct_app;
    }

    @Override
    protected void initView() {
        mRootView = findViewById(R.id.activity_direct_app_root);
        mAppGv = (GridView) findViewById(R.id.activity_direct_app_gv);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // load all app  2019.1.9 by gaob@x-think.com start +++
        if ("user".equals(Build.TYPE)) {
            mDirectAppPresenter.loadAllDirectApp(PAGE_COUNT, false, false);
        } else {
            mDirectAppPresenter.loadAllApp(this);
        }
        //mInstallReceiver = new InstallReceiver(this, this);
        // end +++
    }

    @Override
    protected void setListener() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mDirectAppPresenter.updateAllAppStatus();
        MyLog.i(TAG, "onBackPressed: ");
        super.onBackPressed();
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.direct_discover);
    }

    @Override
    public void showDirectAppList(List<DirectAppBean> directAppBeanList) {
        DirectAppAdapter mDirectAppAdapter = new DirectAppAdapter(this, mDirectAppPresenter, directAppBeanList, DirectAppAdapter.SKIN_WHITE_BG);
        mAppGv.setAdapter(mDirectAppAdapter);
    }

    @Override
    public void updataAppList() {
        mDirectAppPresenter.loadAllApp(this);
        MyLog.d(TAG, "updataAppList");
    }
}

