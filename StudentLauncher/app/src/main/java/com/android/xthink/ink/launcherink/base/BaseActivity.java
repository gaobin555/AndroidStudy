package com.android.xthink.ink.launcherink.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentActivity;

import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.utils.Utils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity的基类
 * Created by liyuyan on 2016/12/22.
 */

public abstract class BaseActivity extends FragmentActivity {

    private List<IPresenter> mPresenterList = new ArrayList<>();
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keepFontSize(this);

        //设置布局内容
        setContentView(getLayoutId());
        //初始化控件
        initView();
        //初始化数据
        initData(savedInstanceState);
        //设置事件监听
        setListener();

        Utils.setStatusBarFullTransparent(this);
        Utils.setDarkStatusIcon(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 只允许竖屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    @CallSuper
    protected void onDestroy() {

        for (IPresenter presenter : mPresenterList) {
            presenter.onDestroyView();
        }
        MyLog.d("BaseActivity", this.getClass().getName() + "onDestroy");
        super.onDestroy();
    }


    /**
     * 子类设置布局的方法
     */
    public abstract int getLayoutId();

    /**
     * 子类view初始化的方法
     */
    protected abstract void initView();

    /**
     * 子类view初始化数据的方法
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 子类view事件监听的方法
     */
    protected abstract void setListener();

    /**
     * 添加presenter
     */
    @CallSuper
    public void addPresenter(IPresenter presenter) {
        mPresenterList.add(presenter);
    }

    @CallSuper
    protected void onLocalReceive(Intent intent) {

        for (IPresenter presenter : this.mPresenterList) {
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        keepFontSize(this);
        newConfig.setToDefaults();
        super.onConfigurationChanged(newConfig);
    }

    void keepFontSize(Context context) {
        if (context == null) {
            return;
        }
        //update resource
        Resources res = context.getResources();

        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
    }

}
