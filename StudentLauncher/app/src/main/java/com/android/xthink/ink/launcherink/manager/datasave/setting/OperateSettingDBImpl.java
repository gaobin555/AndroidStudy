/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.manager.datasave.setting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.manager.InkDbHelper;

/**
 * 设置数据库实现类
 *
 * @author liyuyan
 * @version 1.0, 2017/3/21
 */

public class OperateSettingDBImpl {
    private Context mContext;

    public OperateSettingDBImpl(Context context) {
        mContext = context;
    }

    public void saveSetting(boolean pure_lock, boolean lock_notification, boolean lock_encrypt, boolean shake) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InkConstants.PURE_LOCK, pure_lock);
        values.put(InkConstants.LOCK_NOTIFICATION, lock_notification);
        values.put(InkConstants.LOCK_ENCRYPT, lock_encrypt);
        values.put(InkConstants.SHAKE, shake);
        db.insert(InkConstants.JV_SETTING_DATA
                , null, values);

    }


    public void updateSetting(Context context, String key, String value, Uri uri) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        if (value.equals("false")) {
            value = "0";
        }
        if (value.equals("true")) {
            value = "1";
        }
        values.put(key, value);
        context.getContentResolver().update(uri, values, null, null);
    }


    public Boolean querySetting(String key) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + key + " from " + InkConstants.JV_SETTING_DATA, null);
        int a = 0;
        //游标移到第一条记录准备获取数据
        if (cursor.moveToFirst()) {
            // 获取数据中的LONG类型数据
            a = cursor.getInt(0);
            cursor.close();
        }
        return !(a == 0);

    }

    public int querySettingCount() {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + InkConstants.JV_SETTING_DATA, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}