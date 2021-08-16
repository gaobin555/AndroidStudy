package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.business.presenter.impl.DirectAppPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IDirectAppPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.Presenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IDirectAppView;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.receiver.InstallReceiver;
import com.android.xthink.ink.launcherink.ui.direct.adapter.DirectAppAdapter;

import java.util.List;

/**
 * 发现页面
 * Created by wanchi on 2017/3/14.
 */
public class ReadsFragment extends NativeFragment implements View.OnClickListener, IDirectAppView {
    private static final String TAG = "ReadsFragment";
    private final static int PAGE_APP_COUNT = 11;

    private GridView mDirectAppGv;
    private LinearLayout mNoApp;

    private IDirectAppPresenter mDirectAppPresenter;
    private DirectAppAdapter mDirectAppAdapter;
    private InstallReceiver mInstallReceiver;

    public static ReadsFragment newInstance() {
        MyLog.d(TAG, "newInstance" + "ReadsFragment");
        Bundle args = new Bundle();
        ReadsFragment fragment = new ReadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View rootView = inflater.inflate(R.layout.fragment_reads, container, false);
        mDirectAppGv = rootView.findViewById(R.id.fragment_discovery_direct_app_container);
        mNoApp = rootView.findViewById(R.id.no_app);
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        mInstallReceiver = new InstallReceiver(this, mContext);
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        MyLog.d(TAG, "lazyInit " + "ReadsFragment");
        //mDirectAppPresenter.loadAllDirectApp(PAGE_APP_COUNT, true, false);
        //mDirectAppPresenter.loadAllApp(mContext);
    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
    }

    @Override
    protected void setListener() {
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onDestroy() {
        if (mInstallReceiver != null) {
            mInstallReceiver.unregister();
        }
        super.onDestroy();
        MyLog.d(TAG, "onDestroy" + "ReadsFragment");
    }

    @Override
    public void onScreenOn() {
        super.onScreenOn();
    }


    public static final String mPageName = "ReadsFragment";

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
        DirectAppAdapter mDirectAppAdapter = new DirectAppAdapter(mContext, mDirectAppPresenter, directAppBeanList, DirectAppAdapter.SKIN_WHITE_BG);
        mDirectAppGv.setAdapter(mDirectAppAdapter);

        if (DirectAppPresenter.READ_APP_COUNT == 0) {
            mNoApp.setVisibility(View.VISIBLE);
            mDirectAppGv.setVisibility(View.GONE);
        } else {
            mDirectAppGv.setVisibility(View.VISIBLE);
            mNoApp.setVisibility(View.GONE);
        }
    }

    @Override
    public void updataAppList() {
        //mDirectAppPresenter.loadAllApp(mContext);
        MyLog.d(TAG, "updataAppList");
    }
}
