/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.settingmanager;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.xthink.ink.launcherink.BuildConfig;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.manager.InkDbHelper;

/**
 * 设置暴露给其他app使用数据
 *
 * @author liyuyan
 * @version 1.0, 2017/3/20
 */

public class SettingProvider extends ContentProvider {

    private Cursor cursor;
    private SQLiteDatabase db;
    private final static String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    //常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PURE_LOCK = 1;
    private static final int LOCK_NOTIFICATIONS = 2;
    private static final int LOCK_ENCRYPT = 3;
    private static final int SHAKE = 4;

    public static final Uri PURE_LOCK_URI = Uri.parse("content://" + PACKAGE_NAME + "/jv_ink_setting_data/1");
    public static final Uri LOCK_NOTIFICATIONS_URI = Uri.parse("content://" + PACKAGE_NAME + "/jv_ink_setting_data/2");
    public static final Uri LOCK_ENCRYPT_URI = Uri.parse("content://" + PACKAGE_NAME + "/jv_ink_setting_data/3");
    public static final Uri SHAKE_URI = Uri.parse("content://" + PACKAGE_NAME + "/jv_ink_setting_data/4");


    static {
        MATCHER.addURI(PACKAGE_NAME, "jv_ink_setting_data/1", PURE_LOCK);
        MATCHER.addURI(PACKAGE_NAME, "jv_ink_setting_data/2", LOCK_NOTIFICATIONS);
        MATCHER.addURI(PACKAGE_NAME, "jv_ink_setting_data/3", LOCK_ENCRYPT);
        MATCHER.addURI(PACKAGE_NAME, "jv_ink_setting_data/4", SHAKE);

    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        db = InkDbHelper.getInstance(getContext()).getReadableDatabase();
        //将uri过滤，取得返回值
        int code = MATCHER.match(uri);
        //根据不同的返回值执行不同的查询语句
        switch (code) {
            case 1:
                cursor = db.rawQuery("select * from jv_ink_setting_data", null);
                break;
            case 2:
                cursor = db.rawQuery("select * from jv_ink_setting_data", null);
                break;
            case 3:
                cursor = db.rawQuery("select * from jv_ink_setting_data", null);
                break;
            case 4:
                cursor = db.rawQuery("select * from jv_ink_setting_data", null);
                break;
        }
        //执行查询语句后将结果集返回
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    /**
     * @param uri
     * @param contentValues
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        db = InkDbHelper.getInstance(getContext()).getWritableDatabase();
        //返回更新的个数
        int count = db.update(InkConstants.JV_SETTING_DATA, contentValues, s, strings);
        //更新数据库
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}