package com.android.xthink.ink.launcherink.common.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.xthink.ink.launcherink.common.mvp.view.IView;


/**
 * 业务处理基类
 * Created by wanchi on 2017/2/22.
 */
public abstract class AbsPresenter implements IPresenter {

    protected FragmentActivity mActivity;
    protected Context mContext;

    public AbsPresenter(@NonNull IView view) {
        if (view instanceof FragmentActivity) {
            initWithActivity((FragmentActivity) view);
        } else if (view instanceof Fragment) {
            initWithFragment((Fragment) view);
        } else {
            throw new RuntimeException("view must be instance of FragmentActivity or Fragment.");
        }
    }

    private void initWithActivity(FragmentActivity activity) {
        mActivity = activity;
        mContext = mActivity;
    }

    private void initWithFragment(Fragment fragment) {
        mActivity = fragment.getActivity();
        mContext = fragment.getContext();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public String[] registerReceivers() {
        return new String[0];
    }

    @Override
    public void onBroadcastReceive(Intent intent) {

    }

    @Override
    @CallSuper
    public void onDestroyView() {
        mActivity = null;
        mContext = null;
    }
}
