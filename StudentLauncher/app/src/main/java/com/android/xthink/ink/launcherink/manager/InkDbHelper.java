package com.android.xthink.ink.launcherink.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.bean.InkPageEditBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.constants.InkDirectConstants;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppList;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.SharePreferenceHelper;
import com.android.xthink.ink.launcherink.init.InitAppHelper;
import com.android.xthink.ink.launcherink.init.bean.InitFlavorBean;
import com.android.xthink.ink.launcherink.init.bean.InitPageBean;
import com.android.xthink.ink.launcherink.manager.datasave.pager.OperatePagersDBImpl;
import com.android.xthink.ink.launcherink.manager.datasave.pager.PagerConstants;
import com.android.xthink.ink.launcherink.ui.direct.DirectAppDbImpl;
import com.android.xthink.ink.launcherink.ui.direct.DirectCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyuyan on 2016/12/26.
 */

public class InkDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "InkDbHelper";
    private static InkDbHelper helper;

    private static String NEW_APP_INSTALLER = "com.android.hotpper.REPLACE_ICON";
    private static String PACKAGENAME = "packagename";
    private static String ICON_URL = "apkiconurl";

    /**
     * 数据库名称常量
     */
    private static final String DATABASE_NAME = "jv_ink_launcher.db";
    /**
     * 数据库版本常量
     */
    private static final int DATABASE_VERSION = 14; // T6修改背屏集成app。

    private static Context mContext;
    private OperatePagersDBImpl mPagersManagerDB;

    private SharePreferenceHelper mDirectSpHelper;
    private boolean isOta;
    private DirectAppDbImpl mDirectAppDb;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(NEW_APP_INSTALLER)) {
                String packagename = intent.getStringExtra(PACKAGENAME);
                String iconurl = intent.getStringExtra(ICON_URL);
                MyLog.d(TAG, "packagename = " + packagename + "\n iconurl = " + iconurl);
                DirectAppList direct = new DirectAppList();
                ArrayList<DirectAppBean> appList = new ArrayList<DirectAppBean>();
                direct.setList(appList);
                DirectAppBean newAppBean = new DirectAppBean();
                newAppBean.setAppDesc(getAppName(packagename));
                newAppBean.setAppName(getAppName(packagename));
                newAppBean.setAppPackage(packagename);
                newAppBean.setOrderNo(0);
                newAppBean.setAppIconUrl(iconurl);
                direct.getList().add(newAppBean);
                mDirectAppDb.updateDirectApp(direct, true);
            }
        }
    };

    private String getAppName(String packageName) {
        String appName = "";
        PackageManager mPm = mContext.getPackageManager();
        try {
            appName=mPm.getApplicationLabel(mPm.getApplicationInfo(packageName,PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }

    /**
     * 初始化数据库，保证调用onCreate方法来插入数据。
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        InkDbHelper inkDBHelper = getInstance(context);
        SQLiteDatabase readableDatabase = inkDBHelper.getReadableDatabase();
        readableDatabase.close();
    }

    public static InkDbHelper getInstance(Context context) {
        mContext = context;
        if (helper == null) {
            helper = new InkDbHelper(mContext);
        }
        return helper;
    }

    private InkDbHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        mDirectSpHelper = SharePreferenceHelper.getInstance(mContext, InkDirectConstants.SP_DIRECT_NAME);
        mDirectAppDb = new DirectAppDbImpl(mContext);
        MyLog.i(TAG, "InkDbHelper: " + "new instance");

        IntentFilter filter = new IntentFilter(NEW_APP_INSTALLER);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        MyLog.i(TAG, "datebase   onCreate");
        // 新增pager数据库
        final String CREATE_JV_PAGER_DATA = "create table if not exists " + InkConstants.JV_PAGER_DATA + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PagerConstants.ITEM_IS_HOME_PAGE + " bit,"
                + PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE + " bit,"
                + PagerConstants.ITEM_NAME + " text,"
                + PagerConstants.ITEM_ID + " int UNIQUE,"
                + PagerConstants.ITEM_TITLE + " text,"
                + PagerConstants.ITEM_IS_SELECTED + " bit,"
                + PagerConstants.ITEM_INDEX + " int,"
                + PagerConstants.ITEM_IS_PROTECTED + " bit,"
                + PagerConstants.ITEM_IS_EDITABLE + " bit"
                + ");";
        db.execSQL(CREATE_JV_PAGER_DATA);

        // 新增plugin数据库
        final String CREATE_JV_PLUGIN_DATA = "create table if not exists " + InkConstants.JV_PLUGIN_DATA + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PagerConstants.PLUGIN_COLUMN_LAYOUT_NAME + " text,"
                + PagerConstants.PLUGIN_COLUMN_PACKAGE_NAME + " text UNIQUE,"
                + PagerConstants.PLUGIN_COLUMN_PLUGIN_NAME + " text,"
                + PagerConstants.PLUGIN_COLUMN_PLUGIN_ID + " int UNIQUE"
                + ");";
        MyLog.i(TAG, "onCreate: " + CREATE_JV_PLUGIN_DATA);
        db.execSQL(CREATE_JV_PLUGIN_DATA);

        // 新增直通app数据库
        final String CREATE_DIRECT_APP_DATA = "create table if not exists " + InkConstants.JV_DIRECT_APP_DATA + " ("
                + InkDirectConstants.ITEM_DIRECT_APP_ID + " INTEGER PRIMARY KEY,"
                + InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES + " int,"
                + InkDirectConstants.ITEM_DIRECT_APP_DEFAULT_INDEX + " int,"
                + InkDirectConstants.ITEM_DIRECT_APP_NEW + " int,"
                + InkDirectConstants.ITEM_DIRECT_DATA + " text"
                + ");";
        MyLog.i(TAG, "直通app onCreate: " + CREATE_DIRECT_APP_DATA);
        db.execSQL(CREATE_DIRECT_APP_DATA);

        initPagerData(db);
        initPluginData(db);

        // 如果不是ota升级上来的，那么重新创建数据库的时候会生成默认数据
        if (!isOta) {
            MyLog.i(TAG, "ota");
            mDirectAppDb.generateDefaultDirectApps(db);
        }
    }

    /**
     * 添加初始化数据
     *
     * @param db
     */
    private void initPluginData(SQLiteDatabase db) {
        String insertColumn = "insert into " + InkConstants.JV_PLUGIN_DATA
                + "(" + PagerConstants.PLUGIN_COLUMN_LAYOUT_NAME + ", "
                + PagerConstants.PLUGIN_COLUMN_PACKAGE_NAME + ", "
                + PagerConstants.PLUGIN_COLUMN_PLUGIN_NAME + ", "
                + PagerConstants.PLUGIN_COLUMN_PLUGIN_ID + ") ";

//        db.execSQL(insertColumn + "Values(" + "'"
//                + InkConstants.PLUGIN_BR_LAYOUT_NAME + "','"
//                + InkConstants.PLUGIN_BR_PACKAGE_NAME + "','"
//                + InkConstants.PLUGIN_BR_PLUGIN_NAME + "',"
//                + InkConstants.PAGE_ID_BR
//                + ")");
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_QQ_PACKAGE_NAME)) {
//            //QQ阅读
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_QQ_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_QQ_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_QQ_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_QQ_READER
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_BR_PACKAGE_NAME)) {
//            //掌阅阅读
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_IR_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_IR_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_IR_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_IR_READER
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_THS_PACKAGE_NAME)) {
//            //同花顺
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_THS_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_THS_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_THS_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_THS_READER
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_JIEMIAN_PACKAGE_NAME)) {
//            //界面
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_JIEMIAN_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_JIEMIAN_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_JIEMIAN_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_JIEMIAN_NEWS
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_SYZK_PACKAGE_NAME)) {
//            //商业周刊
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_SYZK_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_SYZK_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_SYZK_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_SYZK_NEWS
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_MG_PACKAGE_NAME)) {
//            //咪咕
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_MG_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_MG_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_MG_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_MG_READER
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_JD_PACKAGE_NAME)) {
//            //京东阅读
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_JD_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_JD_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_JD_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_JD_READER
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_BYTEST_PACKAGE_NAME)) {
//            //宝力优特测试
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_BYTEST_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_BYTEST_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_BYTEST_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_BYTEST_NEWS
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_MOAI_PACKAGE_NAME)) {
//            //moai
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_MOAI_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_MOAI_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_MOAI_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_MOAI
//                    + ")");
//        }
//
//        if (CommonUtils.isPkgInstalled(mContext, InkConstants.PLUGIN_MUSIC_PACKAGE_NAME)) {
//            //music
//            db.execSQL(insertColumn + "Values(" + "'"
//                    + InkConstants.PLUGIN_MUSIC_LAYOUT_NAME + "','"
//                    + InkConstants.PLUGIN_MUSIC_PACKAGE_NAME + "','"
//                    + InkConstants.PLUGIN_MUSIC_PLUGIN_NAME + "',"
//                    + InkConstants.PAGE_ID_MUSIC
//                    + ")");
//        }
    }

    /**
     * 设置数据库数据
     * 页面顺序是“同花顺、商业周刊、界面新闻、墨知推荐、微信、今天、日程、QQ阅读、掌阅、咪咕、我的”
     *
     * @param db db
     */
    private void initPagerData(SQLiteDatabase db) {

        InitAppHelper initAppHelper = InitAppHelper.getInstance();
        InitFlavorBean initFlavorBean = initAppHelper.getInitFlavorBean();
        List<InitPageBean> pageList = initFlavorBean.getPageList();

        String insertColumn = "insert into " + InkConstants.JV_PAGER_DATA
                + "(" + PagerConstants.ITEM_ID + ", "
                + PagerConstants.ITEM_TITLE + ", "
                + PagerConstants.ITEM_NAME + ", "
                + PagerConstants.ITEM_INDEX + ", "
                + PagerConstants.ITEM_IS_SELECTED + ", "
                + PagerConstants.ITEM_IS_PROTECTED + ", "
                + PagerConstants.ITEM_IS_DEFAULT_HOME_PAGE + ", "
                + PagerConstants.ITEM_IS_HOME_PAGE + ", "
                + PagerConstants.ITEM_IS_EDITABLE + ") ";

        // 遍历所有page
        for (InitPageBean page : pageList) {

            int id = page.getId(); // page id

            // 如果是插件，并且没有安装，就直接忽略
            if (page.isPlugin()) {
                String packageName = initAppHelper.getPackageNameById(id); // 插件包名
                if (TextUtils.isEmpty(packageName) || !CommonUtils.isPkgInstalled(mContext, packageName)) {
                    continue;
                }
            }

            String pageName = initAppHelper.getCnPageNameById(id);
            String titleName = initAppHelper.getCnPageTitleById(id);

            // 开始执行写数据库
            db.execSQL(insertColumn + "Values(" + id + ", '" + titleName + "', '"
                    + pageName
                    + "'," + page.getDataCreator() + ")");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        MyLog.i(TAG, "onUpgrade: 开始升级数据库");
        // 取出页面数据
        mPagersManagerDB = new OperatePagersDBImpl(mContext);
        List<InkPageEditBean> allPageList;
        try {
            allPageList = mPagersManagerDB.queryAllPages(db);
        } catch (Exception e) {
            MyLog.i(TAG, "onUpgrade: 查询page旧数据库失败，跳过升级");
            allPageList = new ArrayList<>();
        }

        // 取出直通数据
        List<DirectAppBean> allDirectAppList;
        try {
            if (mDirectAppDb == null) {
                mDirectAppDb = new DirectAppDbImpl(mContext);
            }
            allDirectAppList = mDirectAppDb.queryDirectApp(db, -1, true, false);
        } catch (Exception e) {
            MyLog.i(TAG, "onUpgrade: 查询直通旧数据库失败，跳过升级");
            allDirectAppList = new ArrayList<>();
        }

        // 删除旧的数据库
        db.execSQL("DROP TABLE IF EXISTS " + InkConstants.JV_PAGER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + InkConstants.JV_PLUGIN_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + InkConstants.JV_DIRECT_APP_DATA);
        // 重新创建新的数据库
        isOta = true;
        onCreate(db);
        isOta = false;

        // 恢复旧的数据
        // 处理直通app
        DirectCache directCache = DirectCache.getInstance(mContext);
        directCache.saveRequestTime(0);
        boolean hasOldDirectApp = false;
        for (DirectAppBean bean : allDirectAppList) {
            if (bean == null) {
                continue;
            }
            MyLog.i(TAG, "onUpgrade direct app: " + bean.getAppName() + ",time:" + bean.getUserCount());
            if (bean.getUserCount() > 0) {
                hasOldDirectApp = true;
                mDirectSpHelper.setIntValue(String.valueOf(bean.getId()), bean.getUserCount());
            }
        }
        mDirectSpHelper.setBooleanValue(InkDirectConstants.SP_KEY_HAS_UPGRADE_DIRECT_APP, hasOldDirectApp);

        // 处理页面数据
        InkPageEditBean homePage = null;
        if (isPageFull(allPageList)) {
            // 页面数据如果超过了个数了，ota升级上来的页面将不会被勾选。
            MyLog.i(TAG, "onUpgrade: 页面选中个数超出");
            mPagersManagerDB.updateAllPageSelected(db, false);
        }
        for (InkPageEditBean page : allPageList) {
            mPagersManagerDB.updatePageSelectedById(db, page.getItemId(), page.isShow());
            if (page.isHomePage()) {
                homePage = page;
            }
        }
        if (homePage != null) {
            mPagersManagerDB.updateHomePage(db, homePage.getItemId());
        }
        MyLog.i(TAG, "onUpgrade: 升级数据库结束");

    }

    // 页面最大数是固定的，不可以超过这个数字
    private boolean isPageFull(List<InkPageEditBean> allPageList) {
        int selectedCount = 0;
        for (InkPageEditBean bean : allPageList) {
            if (bean == null) {
                continue;
            }
            if (bean.isShow()) {
                selectedCount++;
            }
        }
        return selectedCount >= InkConstants.MAX_AMOUNT_OF_PAGE;
    }
}
