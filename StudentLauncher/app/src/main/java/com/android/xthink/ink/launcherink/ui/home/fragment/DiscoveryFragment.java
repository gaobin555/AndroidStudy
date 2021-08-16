package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IDirectAppPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.Presenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IDirectAppView;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.ui.direct.adapter.DirectAppAdapter;
import com.android.xthink.ink.launcherink.ui.toolsmanager.ToolsManagerActivity;

import java.util.List;

/**
 * 发现页面
 * Created by wanchi on 2017/3/14.
 */
public class DiscoveryFragment extends NativeFragment implements View.OnClickListener, IDirectAppView {
    private static final String TAG = "DiscoveryFragment";
    private final static int PAGE_APP_COUNT = 11;

    private TextView mDeskManagerBtn;

    private GridView mDirectAppGv;

    private IDirectAppPresenter mDirectAppPresenter;
    private DirectAppAdapter mDirectAppAdapter;

    public static DiscoveryFragment newInstance() {
        MyLog.d(TAG, "newInstance" + "DiscoveryFragment");
        Bundle args = new Bundle();
        DiscoveryFragment fragment = new DiscoveryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(TAG, "onCreate" + "DiscoveryFragment");
    }

    @Override
    public void onAttach(Activity activity) {
        Presenter.bind(this, IDirectAppPresenter.class);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void addPresenter(IPresenter presenter) {
        super.addPresenter(presenter);
        if (presenter instanceof IDirectAppPresenter) {
            mDirectAppPresenter = (IDirectAppPresenter) presenter;
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View rootView = inflater.inflate(R.layout.fragment_discovery_layout, container, false);
//        mSettingBtn = (TextView) rootView.findViewById(R.id.tv_user_setting_manager);
        mDeskManagerBtn = (TextView) rootView.findViewById(R.id.tv_user_desktop_manager);
        mDirectAppGv = (GridView) rootView.findViewById(R.id.fragment_discovery_direct_app_container);

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        MyLog.d(TAG, "lazyInit " + "DiscoveryFragment");
        //mDirectAppPresenter.loadAllDirectApp(PAGE_APP_COUNT, true, false);
        mDirectAppPresenter.loadFixedApp();
    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
    }

    @Override
    protected void setListener() {
        mDeskManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolsManagerActivity.start(mContext);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.d(TAG, "onDestroy" + "DiscoveryFragment");
    }

    @Override
    public void onScreenOn() {
        super.onScreenOn();
    }


    public static final String mPageName = "DiscoveryFragment";

    /**
     * 用于App统计
     */
    public void onFragmentResume(Context context) {
        MyLog.d(TAG, "cy--=fragment onResume=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
    }

    public void onFragmentPause(Context context) {
        MyLog.d(TAG, "cy--=fragment onPause=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
    }

    @Override
    public void showDirectAppList(List<DirectAppBean> directAppBeanList) {

        if (mDirectAppAdapter == null) {
            MyLog.i(TAG, "showDirectAppList: mDirectAppAdapter is null,now create it");
            mDirectAppAdapter = new DirectAppAdapter(mContext, mDirectAppPresenter, directAppBeanList, DirectAppAdapter.SKIN_BLACK_BG);
            mDirectAppGv.setAdapter(mDirectAppAdapter);
        } else {
            MyLog.i(TAG, "showDirectAppList: mDirectAppAdapter is not null,now update it");
            mDirectAppAdapter.updateApps(directAppBeanList);
        }
    }

    @Override
    public void updataAppList() {
        mDirectAppPresenter.loadAllApp(mContext);
    }
}
