/* *
   * Copyright (C) 2018  X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.xthink.ink.launcherink.base.BaseFragment;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.receiver.InstallReceiver;
import com.android.xthink.ink.launcherink.ui.home.fragment.manager.PagerHelper;

/**
 * 主页各fragment 父类
 *
 * @author renxu@X-Thinks.com
 * @version 1.0, 2017/4/6
 */
public abstract class BasePagerFragment extends BaseFragment implements Comparable<BasePagerFragment> {

    private static final String TAG = "BasePagerFragment";

    private static final String EXTRA_KEY_PAGE_ID = "extra_key_page_id";
    private static final String EXTRA_KEY_PAGE_INDEX = "EXTRA_KEY_PAGE_INDEX";

    private boolean hasInit = false;

    private boolean hasAttach = false;
    private boolean hasCreated = false;
    private int mPageId;
    private int mIndex;//页面在数据库中定义的先后顺序

    /**
     * 由于某些原因，虽然设置里添加了要显示，但是，fragment还是需要被隐藏。例如wechat没有安装，todo没有添加日程。
     */
    private boolean mNeedToSwitch;
    private boolean mNeedToLazyInit;

    protected IPageCallback mPageCallback;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_KEY_PAGE_ID, mPageId);
        outState.putInt(EXTRA_KEY_PAGE_INDEX, mIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(TAG, "onCreateView: " + getClass().getSimpleName());
        hasCreated = true;
        if (savedInstanceState != null) {
            mPageId = savedInstanceState.getInt(EXTRA_KEY_PAGE_ID);
            mIndex = savedInstanceState.getInt(EXTRA_KEY_PAGE_INDEX);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callLifeCycleIfNeeded();
    }

    @CallSuper
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hasAttach = true;
        MyLog.i(TAG, "onAttach: " + getClass().getSimpleName());
        callLifeCycleIfNeeded();
    }

    @CallSuper
    @Override
    public void onDetach() {
        super.onDetach();
        hasAttach = false;
        MyLog.i(TAG, "onDetach: " + getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasInit = false;
        hasCreated = false;
        MyLog.i(TAG, "onDestroyView: " + getClass().getName());
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (!hasInit) {
                // 调用这里的时候，可能会还没有attach，所以加上这个判断，以免getContext为null。
                if (hasAttach) {
                    lazyInit();
                    hasInit = true;
                } else {
                    mNeedToLazyInit = true;
                }
            } else {
                // 调用这里的时候，可能会还没有attach，所以加上这个判断，以免getContext为null。
                if (hasAttach) {
                    switchPage(true);
                } else {
                    mNeedToSwitch = true;
                }
            }
        } else {
            if (hasInit && hasAttach) {
                switchPage(false);
            }
        }
    }

    /**
     * 滑动到该页面,第一次滑动到此页面时，不会回调此方法，其他时候会。
     *
     * @param isVisibleToUser true:滑到这一页。false:滑走
     */
    public void switchPage(boolean isVisibleToUser) {
        MyLog.i(TAG, "life switchPage: " + this.getClass().getSimpleName());
    }

    /**
     * 懒加载
     */
    public void lazyInit() {
        MyLog.i(TAG, "life lazyInit: " + this.getClass().getSimpleName());
    }

    private void callLifeCycleIfNeeded() {
        if (mNeedToLazyInit && hasCreated) {
            mNeedToLazyInit = false;
            MyLog.i("fragmentlife", "life lazyInit  callLifeCycleIfNeeded");
            lazyInit();
            hasInit = true;
        }
        if (mNeedToSwitch && hasCreated) {
            mNeedToSwitch = false;
            switchPage(true);
        }
    }

    @Override
    public int compareTo(@NonNull BasePagerFragment pagerFragment) {
        //根据id排序
        Integer a = this.getIndex();
        return a.compareTo(pagerFragment.getIndex());
    }

    public int getPageId() {
        return mPageId;
    }

    public void setPageId(int itemId) {
        mPageId = itemId;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    /**
     * 由于某些原因，虽然设置里添加了要显示，但是，fragment还是需要被隐藏。例如wechat没有安装，todo没有添加日程。
     *
     * @return 是否需要显示
     */
    public boolean isPagerEnable() {
        int pageId = getPageId();
        return InstallReceiver.isPageEnable(pageId);
    }

    /**
     * 设置一些现实或者隐藏的回调。
     *
     * @param mPageCallback 回调
     */
    public void setPageCallback(PagerHelper mPageCallback) {
        this.mPageCallback = mPageCallback;
    }
}
