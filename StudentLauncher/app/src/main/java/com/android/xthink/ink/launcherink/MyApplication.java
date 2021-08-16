package com.android.xthink.ink.launcherink;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode;

import com.android.xthink.ink.launcherink.business.presenter.impl.PresenterFactory;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.mvp.presenter.Presenter;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.init.InitAppHelper;
import com.android.xthink.ink.launcherink.manager.InkDbHelper;
import com.android.xthink.ink.launcherink.manager.InkLocalCacheManager;
import com.android.xthink.ink.launcherink.manager.layoutmanager.LoadingAndRetryManager;
import com.android.xthink.ink.launcherink.utils.CrashUtils;
import com.android.xthink.ink.launcherink.utils.Utils;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyuyan on 2018/12/22.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        InitAppHelper.getInstance();

        // 初始化LitePal数据库
        LitePal.initialize(this);

        // 初始化数据库，数据库由代码编写创建的。所以必须提前生成，否则调用的时候，可能会没有数据。
        InkDbHelper.init(this);

        StrictMode4Debug();//严格模式,debug 时可以打开测试一些很难发现的问题

        sInstance = this;

        //初始化缓存类
        initCache();

        // presenter工厂初始化
        Presenter.init(new PresenterFactory());
        //设置不同state下的layout
        LoadingAndRetryManager.BASE_RETRY_LAYOUT_ID = R.layout.base_retry;
        LoadingAndRetryManager.BASE_LOADING_LAYOUT_ID = R.layout.base_loading;
        LoadingAndRetryManager.BASE_EMPTY_LAYOUT_ID = R.layout.base_empty;
        Utils.init(this);
        CrashUtils.init(InkConstants.CRASH_LOG_PATH);
    }

    /**
     * 严格模式,debug 时可以打开测试一些很难发现的问题
     */
    private void StrictMode4Debug() {
        if (false) {
//      if (BuildConfig.DEBUG) {
            // 针对线程的相关策略
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());

            // 针对VM的相关策略
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

    //    检测用户体验开关状态的接口查找 EXPERIENCE_FILE_NAME 这个文件是否存在
    String EXPERIENCE_FILE_NAME = "/data/junk-server/UserExperiencePlan";

    public boolean isExperienceOn() {
        File file = new File(EXPERIENCE_FILE_NAME);
        boolean isExperienceOn = file.exists();
        MyLog.i(TAG, "isExperienceOn: " + isExperienceOn);
        return file.exists();
    }


    private void initCache() {
        InkLocalCacheManager.getInstance().init(getApplicationContext());
    }


    List<Activity> list = new ArrayList<Activity>();

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
