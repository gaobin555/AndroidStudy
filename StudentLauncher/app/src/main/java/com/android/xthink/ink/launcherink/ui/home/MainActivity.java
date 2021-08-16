package com.android.xthink.ink.launcherink.ui.home;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.xthink.ink.launcherink.GlobalDataCache;
import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.base.mvp.MainBaseActivity;
import com.android.xthink.ink.launcherink.bean.InkPageEditBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.eink.SwtconController;
import com.android.xthink.ink.launcherink.receiver.NotificationReceiver;
import com.android.xthink.ink.launcherink.receiver.ParentalControlReceiver;
import com.android.xthink.ink.launcherink.receiver.ScreenReceiver;
import com.android.xthink.ink.launcherink.common.utils.UiTestUtils;
import com.android.xthink.ink.launcherink.ui.home.adapter.InkHomeFragmentAdapter;
import com.android.xthink.ink.launcherink.ui.home.fragment.BasePagerFragment;
import com.android.xthink.ink.launcherink.ui.notificationManager.NotificationMonitor;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.manager.event.IPageIndicatorUpdate;
import com.android.xthink.ink.launcherink.manager.event.IStepChangedListener;
import com.android.xthink.ink.launcherink.receiver.InstallReceiver;
import com.android.xthink.ink.launcherink.ui.customview.InkNoScrollViewPager;
import com.android.xthink.ink.launcherink.ui.edit.EditEvent;
import com.android.xthink.ink.launcherink.ui.edit.indicator.HomeIndicator;
import com.android.xthink.ink.launcherink.ui.home.fragment.manager.PagerHelper;
import com.android.xthink.ink.launcherink.ui.home.multitask.MultitaskHandler;
import com.android.xthink.ink.launcherink.ui.settingmanager.CustomWallPaperReceiver;
import com.eink.swtcon.SwtconControl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MainBaseActivity implements IStepChangedListener, IPageIndicatorUpdate {

    private static final String TAG = "MainActivity";

    public PagerHelper mPagerHelper;
    public HomeIndicator mHomeIndicator;
    private InkNoScrollViewPager mViewPager;
    private View mRootView;
    private MultitaskHandler mMultitaskHandler;
    private static ScreenReceiver mScreenReceiver;

    // 避免按home键时，已经在Launcher时，多出来的刷新。
    private boolean mAvoidUpdateHome = false;
    // 避免按多任务时，多出来的一次刷新。
    private boolean mAvoidUpdateMultiTask = false;
    private boolean mHasFocus;
    //activity是否不可见
    private boolean mIsOnPause = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private InstallReceiver mInstallReceiver;
    private CustomWallPaperReceiver mCustomWallPaperReceiver;

    private SharedPreferences mSp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addScreenOnListener();
        addNotifcationListener(this);
        EventBus.getDefault().register(this);
        mSp = this.getSharedPreferences("control_prefs", Context.MODE_PRIVATE);//added by chenjia
    }

    @Override
    protected void onResume() {
        MyLog.i(TAG, "onResume" + " mAvoidUpdateHome = " + mAvoidUpdateHome);
        super.onResume();
        // 此处控制刷新
        if (!InkDeviceUtils.isLauncher) {
            if (!mAvoidUpdateHome) {
                InkDeviceUtils.launcherUpdate(false);
            } else {
                mAvoidUpdateHome = false;
                MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
            }
            InkDeviceUtils.isLauncher = true;
        } else {
            MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
        }
        mIsOnPause = false;

        int reportLossState = mSp.getInt(ParentalControlReceiver.REPORT_LOSS_STATE, 0);
        int reservePowerState = mSp.getInt(ParentalControlReceiver.RESERVE_POWER_STATE, 0);
        int classForbiddenState = mSp.getInt(ParentalControlReceiver.CLASS_FORBIDDEN_STATE, 0);
        if (reportLossState == 1 || reservePowerState == 1 || classForbiddenState == 1) {
            mPagerHelper.showHomePage();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // 保存fragment到fragmentManager中
        if (mPagerHelper != null) {
            mPagerHelper.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        //MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_DU2);
        super.onPause();
        MyLog.d(TAG, "onPause");
        SwtconController.setPixelOff();
        mIsOnPause = true;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        mHandler.removeCallbacksAndMessages(null);
        //结束清理工作
        if (mPagerHelper != null) {
            mPagerHelper.unRegisterAll();
        }
        //反注册锁屏广播
        if (mScreenReceiver != null) {
            mScreenReceiver.unRegisterScreenReceiver(this);
        }
        if (mMultitaskHandler != null) {
            mMultitaskHandler.stopBroadcast();
        }
        MyLog.d(TAG, "onDestroy");

        if (mInstallReceiver != null) {
            mInstallReceiver.unregister();
        }

        super.onDestroy();
    }


    @Override
    public int getLayoutId() {
        MyLog.d(TAG, "onCreate");
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mRootView = findViewById(R.id.activity_main);
        mHomeIndicator = (HomeIndicator) findViewById(R.id.home_indicator);
        mViewPager = (InkNoScrollViewPager) findViewById(R.id.main_view_pager);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        //数据初始化 need move to application
        mPagerHelper = new PagerHelper(this);
        mPagerHelper.init(mViewPager, savedInstanceState);
        mPagerHelper.setIndicatorUpdate(this);
        mHomeIndicator.refreshIndicator(mPagerHelper.getPageCount(), mPagerHelper.getHomeScreenPosition(), false);
        mHandler.sendEmptyMessageDelayed(0, 1000);
        mInstallReceiver = new InstallReceiver(mPagerHelper, this);

        //监听多任务
        startMultitasking();
        // 删除计步器
//        initStepDetect();
        mHomeIndicator.setCurIndex(mViewPager.getCurrentItem());
        Log.d(TAG, "initData: getCurItem = " + mViewPager.getCurrentItem());
        mHomeIndicator.refreshIndicator(mPagerHelper.getPageCount(), mPagerHelper.getHomeScreenPosition(), false);

        //多任务
        processPagerData(getIntent());
    }

    protected void reloadFragments() {
        MyLog.i("reload", "reloadFragments");
        if (mPagerHelper != null) {
            MyLog.i("reload", "reloadAllPager");
            mPagerHelper.reloadAllPager();
        }
    }

    @Override
    protected void setListener() {
        mPagerHelper.setOnPageSelectedListener(new PagerHelper.OnPageSelectedListener() {
            @Override
            public void onPageSelected(int position) {
                // InkToastManager.showToastShort(getApplicationContext(), mFragmentList.get(position).getClass().getSimpleName());
                int pageId = mPagerHelper.getIdByPosition(position);
                Log.d(TAG, "onPageSelected: pageId = " + pageId);
                mHomeIndicator.setSelectedPoint(position);//当前选中页
            }
        });

        mViewPager.setOnMScrollChangeListener(new InkNoScrollViewPager.OnMScrollChangeListener() {
            @Override
            public void onScrollChange(int prePosition, int curPosition) {
                MyLog.i(TAG, "onScrollChange--prePos = " + prePosition + ", curPosition = " + curPosition);
                SwtconController.setPixelOn();
                // 位置不变时不相应任何事件。
                if (prePosition == curPosition) {
                    MyLog.i(TAG, "onScrollChange: 页面相同，不处理。");
                    return;
                }

                // 七次后GC刷一次
                InkDeviceUtils.launcherUpdate(true);
                /**
                 * X-Thinks begin, add
                 * what(reason) 用于统计页面打点 */
                List<BasePagerFragment> fragmentList = ((InkHomeFragmentAdapter) (mViewPager.getAdapter())).getmFragmentList();

                if (prePosition >= 0 && prePosition < fragmentList.size()) {
                    BasePagerFragment preBasePagerFragment = fragmentList.get(prePosition);

                    preBasePagerFragment.onFragmentPause(MainActivity.this);
                }
                if (curPosition >= 0 && curPosition < fragmentList.size()) {
                    BasePagerFragment curBasePagerFragment = fragmentList.get(curPosition);
                    curBasePagerFragment.onFragmentResume(MainActivity.this);
                }
            }
        });
    }

    /**
     * refactor by liyuyan
     * 启动多任务处理
     */
    private void startMultitasking() {
        //添加多任务处理
        if (mMultitaskHandler == null) {
            mMultitaskHandler = new MultitaskHandler(this);
        }
        mMultitaskHandler.registerBroadcast();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        UiTestUtils.tag("Launcher 正常执行了onNewIntent的生命周期。");

        super.onNewIntent(intent);

        int pageId = intent.getIntExtra("pageId", -1);
        UiTestUtils.tag("从多任务得到的页面信息是：" + pageId);
        if (pageId > 0) {
            //id 有值,走多任务
            MyLog.i(TAG, "id 有值,走多任务,不走home");
            mAvoidUpdateMultiTask = true;
            processPagerData(intent);
        } else {
            //否则,走HOME判断
            if (Intent.ACTION_MAIN.equals(intent.getAction())) {
                // 按下home键的响应 add by wanchi

                mAvoidUpdateHome = mHasFocus &&
                        ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                                != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                Runnable processIntent = new Runnable() {
                    @Override
                    public void run() {
                        if (mPagerHelper != null) {
                            // 跳到home页面
                            mPagerHelper.showHomePage();
                        }
//                     关闭输入法
                        final View v = getWindow().peekDecorView();
                        if (v != null && v.getWindowToken() != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(
                                    INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                };

                mViewPager.post(processIntent);
                MyLog.i(TAG, "main home get intent " + mHasFocus + " mAvoidUpdateHome = " + mAvoidUpdateHome);
            }
        }
    }

    private void processPagerData(Intent intent) {
        int pageId = intent.getIntExtra("pageId", -1);
        if (pageId != -1) {
            MyLog.i(TAG, "processPagerData pageId  " + pageId);
            mPagerHelper.showPageById(pageId);
        }
    }

    /**
     * 增加屏幕的监听
     */
    private void addScreenOnListener() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenReceiver();
            mScreenReceiver.registerBackScreenReceiver(this, new ScreenReceiver.ScreenOnListener() {

                @Override
                public void screenOn() {
                    MyLog.i(TAG, "screenOn()");
                    mAvoidUpdateHome = false;
                }

                @Override
                public void screenOff() {
                    MyLog.i(TAG, "screenOff()");
                }
            });
        }
    }

    @Override
    public void onStepCountChanged(int stepCount) {
        //fix 当界面不可见的时候不去设置，可见的时候再显示步数
        GlobalDataCache.getInstance().setCurrentStepCount(stepCount);
        if (!mIsOnPause) {
            mPagerHelper.setTodayFragmentStep(stepCount);
        }
    }

    @Override
    public void onStepInitData(int stepCount) {
        mPagerHelper.setTodayFragmentStep(stepCount);
    }

    @Override
    public void onPageIndicatorUpdate(int homePagePosition, int pageCount) {
        mHomeIndicator.refreshIndicator(pageCount, homePagePosition, false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHasFocus = hasFocus;
        MyLog.i(TAG, "mHasFocus " + mHasFocus);
        if (mHasFocus) {
            setEinkLocked(0);
        }
    }

    //编辑页的改变的eventBus监听
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final EditEvent event) {
        MyLog.i(TAG, "onEventMainThread edit event");
        if(event != null) {
            mViewPager.post(new Runnable() {
                @Override
                public void run() {
                    updatePages(event.homeId, event.toShowBeanList);
                }
            });
        }
    }

    /**
     * 页面更新
     *
     * @param homeId         主页id
     * @param toShowBeanList 显示页面列表
     */
    private void updatePages(int homeId, ArrayList<InkPageEditBean> toShowBeanList) {
        if (homeId > 0) {
            mPagerHelper.setHomePage(homeId);
        }
        if (toShowBeanList != null) {
            MyLog.i(TAG, "EditActivity" + "\r\n" + toShowBeanList);
            mPagerHelper.updatePages(toShowBeanList);
        }
    }

    private static boolean mFotaDialogShow = false;
    private void showRedStoneOtaDialog(Context context) {
        StatusBarNotification[] currentNos = NotificationMonitor.getCurrentNotifications();
        if (currentNos != null && currentNos.length > 0) {
            // 启动红石OTA
            for (StatusBarNotification statusBarNotification : currentNos) {
                if (statusBarNotification.getPackageName().equals("com.redstone.ota.ui") && !mFotaDialogShow) {
                    mFotaDialogShow = true;
                    MyLog.d(TAG, "PackageName " + statusBarNotification.getPackageName() + " key = " + statusBarNotification.getKey());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(resolveTitle(statusBarNotification.getNotification()))
                            .setMessage(resolveText(statusBarNotification.getNotification())).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MyLog.d(TAG, "start com.redstone.ota.ui");
                                    // 再打开应用
                                    PendingIntent intent = statusBarNotification.getNotification().contentIntent;
                                    if (intent != null) {
                                        MyLog.d(TAG, "open intent = " + intent.getCreatorPackage());
                                        try {
                                            InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
                                            intent.send();
                                        } catch (PendingIntent.CanceledException e) {
                                            MainBaseActivity.setEinkModeData(SwtconControl.WF_MODE_GLD16);
                                            e.printStackTrace();
                                        }
                                    }
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    mFotaDialogShow = false;
                                }
                            });
                    builder.create().show();
                    cancelNotificationbyKey(context, statusBarNotification.getKey());// 删除通知
                }
            }
        }
    }

    private void cancelNotification(Context context) {
        StatusBarNotification[] currentNos = NotificationMonitor.getCurrentNotifications();
        if (currentNos != null && currentNos.length > 0) {
            for (StatusBarNotification statusBarNotification : currentNos) {
                if (!statusBarNotification.getPackageName().equals("com.xthinks.xchat")
                        && !statusBarNotification.getPackageName().equals("com.android.deskclock")
                        && !statusBarNotification.getPackageName().equals("com.android.calendar")) {
                    cancelNotificationbyKey(context, statusBarNotification.getKey());// 删除通知
                }
            }
        }
    }

    private void cancelNotificationbyKey(Context context, String key){
        MyLog.d(TAG, "cancelNotificationbyKey:" + key);
        Intent intent = new Intent();
        intent.setAction(NotificationMonitor.ACTION_NLS_CONTROL);
        intent.putExtra("command", key);
        context.sendBroadcast(intent);
    }

    private CharSequence resolveText(Notification notification) {
        CharSequence contentText = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        if (contentText == null) {
            contentText = notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }
        return contentText;
    }

    private CharSequence resolveTitle(Notification notification) {
        CharSequence titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        if (titleText == null) {
            titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE_BIG);
        }
        return titleText;
    }

    /**
     * 增加通知的监听
     */
    private void addNotifcationListener(Context context) {
        NotificationReceiver mNotificationReceiver = new NotificationReceiver();
            mNotificationReceiver.registerNotificationReceiver(this, new NotificationReceiver.NotificationOnListener() {
                @Override
                public void updateNotification() {
                    cancelNotification(context);
                }
            });
    }

    //back应该处理掉
    @Override
    public void onBackPressed() {
        //do nothing
    }
}
