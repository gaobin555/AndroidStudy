package com.android.xthink.ink.launcherink.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IView;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.InkAppManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment的基类
 * Created by liyuyan on 2016/12/22.
 */

public abstract class BaseFragment extends Fragment implements IView {

    /**
     * 根View
     */
    protected View mRootView;

    private static final String TAG = "BaseFragment";

    private BroadcastReceiver mReceiver;
    private List<IPresenter> mPresenterList = new ArrayList<>();
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = initView(inflater, container);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();
    }

    /**
     * 子类view初始化的方法
     */
    protected abstract View initView(LayoutInflater inflater, @Nullable ViewGroup container);

    /**
     * 子类view初始化数据的方法
     */
    protected abstract void initData();

    /**
     * 子类view事件监听的方法
     */
    protected abstract void setListener();

    public void setJvPagerBeanData() {

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof Activity) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    BaseFragment.this.onReceive(intent);
                }
            };
            registerReceiver();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @CallSuper
    @Override
    public void onDetach() {
        unregisterReceiver();

        for (IPresenter presenter : mPresenterList) {
            presenter.onDestroyView();
        }
        mPresenterList.clear();
        //mContext.unregisterReceiver(mReceiver);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @CallSuper
    public void addPresenter(IPresenter presenter) {
        mPresenterList.add(presenter);
    }

    @CallSuper
    protected void onReceive(Intent intent) {
        for (IPresenter presenter : mPresenterList) {
            presenter.onBroadcastReceive(intent);
        }
    }

    @CallSuper
    protected String[] registerReceivers() {
        ArrayList<String> allActionList = new ArrayList<>();
        List<String> actionList;

        for (IPresenter presenter : mPresenterList) {
            actionList = Arrays.asList(presenter.registerReceivers());
            allActionList.addAll(actionList);
        }

        String[] actions = new String[allActionList.size()];
        actions = allActionList.toArray(actions);
        return actions;
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        String[] actions = registerReceivers();
        for (String action : actions) {
            MyLog.d(TAG, "xxxx action = " + action);
            intentFilter.addAction(action);
        }

        mContext.registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }


    protected boolean isZh() {
        return InkAppManager.isZh(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 用于App统计
     */
    public void onFragmentResume(Context context) {

    }

    public void onFragmentPause(Context context) {


    }

    /**
     * 解锁的回调
     */
    public void onScreenOn() {

    }

}
