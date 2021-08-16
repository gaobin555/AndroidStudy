/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */
package com.android.xthink.ink.launcherink.common.constants;


import android.os.Environment;

import com.android.xthink.ink.launcherink.common.BuildConfig;

/**
 * 全局常量
 *
 * @author xthink
 * @version 1.0, 2019/1/6
 */

public class InkConstants {
    public static final String TAG_UI_TEST = "ui_automator";
    public static final int MAX_AMOUNT_OF_PAGE = 12;//BLauncher最大可显示页面数目
    public static long EPD_UNLOCK_TIME = 0;//解锁的时间戳记录在内存中,如果记录在文件中会导致墨知屏时长太长,跟产品讨论宁愿少记,也不能多记
    public static boolean DEBUG = BuildConfig.DEBUG;
    public static final String PLUGIN = "plugin";

    public static String CRASH_LOG_PATH = Environment.getExternalStorageDirectory() + "/BLCrash/";

    public static final int TODO_TOTALITY_COUNT = 7;//TodoPage 最多显示泡泡数
    public static final int TODO_FRONT_COUNT = 1;//TodoPage 最多显示过期事件数
    public static final int TODO_REFRESH_HOUR = 22;//刷新显示次日事件的时间小时数
    public static final int TODO_REFRESH_MINUTE = 0;//刷新显示次日事件的时间分钟数
    public static final int PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    public static final int PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    //____插件 ID 通过id判断是否为插件128
    public static final int PAGE_ID_PLUGIN_TAG = 128;//插件id标志位
    //页面id
    public static final int PAGE_ID_NONE = -1;//不存在的page
    public static final int PAGE_ID_WECHAT = 1;//wecat屏itemId
    public static final int PAGE_ID_TODAY = 2;//首屏itemId
    public static final int PAGE_ID_TODO = 3;//TODO屏itemId
    public static final int PAGE_ID_USER = 10;//用户屏itemId
    public static final int PAGE_ID_KINDLE = 13;//kindle
    public static final int PAGE_ID_MUSIC = 4;//音乐
    public static final int PAGE_ID_CURRICULUM = 5;//课程表
    public static final int PAGE_ID_STCARD = 6;//学生证
    public static final int PAGE_ID_TOOLS = 7;//工具页
    public static final int PAGE_ID_READS = 8;//阅读天地



    //____插件 NAME 目前无实际作用
    public static final String PACKAGE_NAME_WECHAT = "com.tencent.mm";//微信包名
    public static final String PACKAGE_NAME_KINDLE = "com.amazon.kindle";
    public static final int PAGE_INDEX_BEGIN = 1;//数据库中所定义初始页面的索引

    public static final String ACTIVITY_KINDLE_READING_PAGE = "com.amazon.kcp.reader.StandAloneBookReaderActivity";//"com.amazon.kcp.reader.StandAloneBookReaderActivity";
    public static final String ACTIVITY_KINDLE_HOME = "com.amazon.kcp.oob.MainActivity";
    public static final String ACTIVITY_KINDLE_SHOPPING_CART = "com.amazon.kcp.store.RubyStoreActivity";//"com.amazon.kcp.store.StoreActivity";
    public static final String ACTIVITY_KINDLE_SEARCH = "com.amazon.kcp.search.SearchActivity";

    //downLoad
    public static final int UP_TO_DATE_CODE = 64101;
    public static final String downLoadFilePath = Environment.getExternalStorageDirectory() + "/BLauncherDownloads" + "/v";

    public static final String JV_PAGER_DATA = "jv_ink_pager_data";
    public static final String JV_SETTING_DATA = "jv_ink_setting_data";
    public static final String JV_PLUGIN_DATA = "jv_ink_plugin_data"; // 插件的表名
    public static final String JV_DIRECT_APP_DATA = "jv_ink_direct_app_data";// 直通app的表名

    public static final String PAGE_NAME_WECHAT = "wechat";
    public static final String PAGE_NAME_TODAY = "today";
    public static final String PAGE_NAME_USER = "apps";
    public static final String PAGE_NAME_TOOLS = "tools";
    public static final String PAGE_NAME_TODO = "calendar";
    public static final String PAGE_NAME_CURRICULUM = "curriculum";
    public static final String PAGE_NAME_STCARD = "stcard";
    public static final String PAGE_NAME_READS = "reads";

    public static boolean isLogin = false;


    // 音乐
//    public static final String PLUGIN_MUSIC_PACKAGE_NAME = "com.xthinks.music";
//    public static final String PLUGIN_MUSIC_LAYOUT_NAME = "music_player_bs_main_layout";
//    public static final String PLUGIN_MUSIC_PLUGIN_NAME = "Music";

    public static final String WECHAT_QRCODE_PATH = Environment.getExternalStorageDirectory() + "/Download" + "/logo.png";

    //setting常量
    public static final String PURE_LOCK = "pure_lock";
    public static final String LOCK_NOTIFICATION = "lock_notification";
    public static final String LOCK_ENCRYPT = "lock_encrypt";
    public static final String SHAKE = "shake";

    public static final String CHINA_MOBiLE_CODE = "01";
    public static final String OPEN_CODE = "06";

    // title
    public static final String PAGE_TITLE_WECHAT = "微信";
    public static final String PAGE_TITLE_TODAY = "今天";
    public static final String PAGE_TITLE_TODO = "日程";
    public static final String PAGE_TITLE_USER = "应用";
    public static final String PAGE_TITLE_TOOLS = "工具";
    public static final String PAGE_TITLE_MUSIC = "Music";
    public static final String PAGE_TITLE_CURRICULUM = "课程表";
    public static final String PAGE_TITLE_STCARD = "学生证";
    public static final String PAGE_TITLE_READS = "阅读天地";

    //Settings.db fields
    public static final String SYSTEM_PURE_LOCK = "system_pure_lock"; // 纯净锁屏
    public static final String SYSTEM_LOCK_NOTIFICATION = "system_lock_notification"; // 锁屏通知
    public static final String SYSTEM_LOCK_ENCRYPT = "system_lock_encrypt"; // 锁屏加密
    public static final String SYSTEM_SV_SHAKE = "system_sv_shake"; // 摇一摇
    public static final String SYSTEM_CUSTOM_WALLPAPER = "system_custom_wallpaper"; // 自定义壁纸
    public static final String SYSTEM_WALLPAPER_PUSH = "system_wallpaper_push"; // 壁纸推送
    public static final String SYSTEM_CLOCK_DISPLAY = "system_clock_display"; // 时钟锁屏开关

    /**
     * 系统设备号
     */
    public static final String SYSTEM_MODEL = "ro.product.model";

    /**
     * 直通app上次访问时间
     */
    public static final String SP_DIRECT_APP_LAST_TIME = "SP_DIRECT_APP_LAST_TIME";
    public static final String SP_DIRECT_APP_LAST_VERSION = "SP_DIRECT_APP_LAST_VERSION";

    /**
     * 自定义壁纸的广播
     */
    public static final String ACTION_CUSTOM_WALLPAPER_BROADCAST = "com.journeyui.gallery3d.wallpaper.image";
}
