/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.base.mvp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentActivity;

import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.eink.SwtconController;
import com.android.xthink.ink.launcherink.init.InitAppHelper;
import com.android.xthink.ink.launcherink.utils.Utils;
import com.eink.swtcon.SwtconControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.xthink.ink.launcherink.eink.SwtconController.ACTION_EINK_MODE_LOCKED;
import static com.android.xthink.ink.launcherink.eink.SwtconController.ACTION_EINK_MODE_SETTINGS;

/**
 * MainBaseActivity 同意Activity结构和风格
 *
 * @author liyuyan
 * @version 1.0, 2018/7/10
 */

public abstract class MainBaseActivity extends FragmentActivity {
    private List<IPresenter> mPresenterList = new ArrayList<>();
    private BroadcastReceiver mReceiver;
    private static Context mContext;
    private ChangeContentObserver mChangeContentObserver = null;
    private static final String TAG = "MainBaseActivity";

    private String[] permissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =this;
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                MainBaseActivity.this.onLocalReceive(intent);
            }
        };
        this.registerReceiver();

        //设置布局内容
        setContentView(getLayoutId());

        InitAppHelper initAppHelper = InitAppHelper.getInstance();

        requestPermissions(this, permissions, 1);

        initViewsAndEvents(savedInstanceState);
        reloadFragments();

        Utils.setStatusBarFullTransparent(this);
        Utils.setDarkStatusIcon(this, true);

        mChangeContentObserver = new ChangeContentObserver();
        getContentResolver().registerContentObserver(Settings.Global.getUriFor(ACTION_EINK_MODE_SETTINGS), true, mChangeContentObserver);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor(ACTION_EINK_MODE_LOCKED), true, mChangeContentObserver);
    }

    //判断应用是否已经授权权限
    public static boolean requestPermisson(Activity activity, String permission, int requestCode) {
        int hasPermission = activity.checkSelfPermission(permission);
        //没有授权
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            //进行授权提示
            activity.requestPermissions(new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public void initViewsAndEvents(Bundle savedInstanceState) {
        //初始化控件
        initView();
        //初始化数据
        initData(savedInstanceState);
        //设置事件监听
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        unregisterReceiver();
        MyLog.d(TAG, "onDestroy");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GC16);
            }
        }, 100);
        for (IPresenter presenter : mPresenterList) {
            presenter.onDestroyView();
        }
        MyLog.d(TAG, this.getClass().getName() + " onDestroy");
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
     * 子类view初始化数据的方法
     */
    protected abstract void reloadFragments();

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

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        String[] actions = registerReceivers();
        for (String action : actions) {
            intentFilter.addAction(action);
        }

        registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mReceiver);
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

    /*
     * 监听值变化
     */
    class ChangeContentObserver extends ContentObserver {
        public ChangeContentObserver (Handler handler) {
            super(handler);
        }
        public ChangeContentObserver () {
            // TODO Auto-generated constructor stub
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int mode = getEinkModeData();
            int locked = getEinkLocked();
            MyLog.d(TAG, "xxx ChangeContentObserver" + ", mode = " + mode + ", locked = " + locked);
            if(locked != 0 && (mode == 1 || mode == 2 || mode == 5 || mode == 6)) {
                mode = locked;
            }

            if (mode == 1 || mode == 2 || mode == 5 || mode == 6) {
                SwtconController.setEinkMode(mode);
            } else {
                MyLog.e(TAG, "wrong eink mode!");
                SwtconController.setEinkMode(SwtconControl.WF_MODE_DU2);
            }
        }
    }

    //Setting 数据库中写值
    public static void setEinkModeData(int mode) {
        if (mode == SwtconController.getCurMode()) {
            return;
        }
        MyLog.d(TAG, "setEinkModeData mode = " + mode);

        try {
            Settings.Global.putInt(mContext.getContentResolver(), ACTION_EINK_MODE_SETTINGS, mode);
        } catch (Exception e) {
            MyLog.i(TAG, "setEinkModeData(" + mode + ")> " + e.getMessage());
        }
    }

    //Setting 数据库中读取值
    public static int getEinkModeData() {
        int mode = SwtconControl.WF_MODE_DU2;
        try {
            mode = Settings.Global.getInt(mContext.getContentResolver(), ACTION_EINK_MODE_SETTINGS, SwtconControl.WF_MODE_DU2);
        } catch (Exception e) {
            MyLog.i(TAG, "getEinkModeData()> " + e.getMessage());
        }
        MyLog.d(TAG, "getEinkModeData mode = " + mode);
        return mode;
    }

    //Setting 数据库中写值
    public static void setEinkLocked(int locked) {
        try {
            Settings.Global.putInt(mContext.getContentResolver(), ACTION_EINK_MODE_LOCKED, locked);
        } catch (Exception e) {
            MyLog.i(TAG, "setEinkLocked(" + locked + ")> " + e.getMessage());
        }
    }

    public static int getEinkLocked() {
        try {
            return Settings.Global.getInt(mContext.getContentResolver(), ACTION_EINK_MODE_LOCKED, 0);
        } catch (Exception e) {
            MyLog.i(TAG, "getEinkLocked()> " + e.getMessage());
        }
        return 0;
    }
}