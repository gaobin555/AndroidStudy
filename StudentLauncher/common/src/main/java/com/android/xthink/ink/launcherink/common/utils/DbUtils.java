/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库的一些工具类
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/10/16
 */
public class DbUtils {

    private static final String TAG = "DbUtils";

    /**
     * 判断数据库的某个字段是否存在
     *
     * @param db        指定数据库
     * @param tableName 表名
     * @param fieldName 字段名
     * @return true 存在，反之不存在。
     */
    public static boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isExist = false;
        Cursor res = null;
        try {
            res = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            res.moveToFirst();
            int value = res.getColumnIndex(fieldName);

            if (value != -1) {
                isExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) {
                res.close();
            }
        }
        return isExist;
    }

    /**
     * 插入数据，例如有字段a和b，值分别是1,'2'. 则keys传："a,b".values:"1,'2'"
     *
     * @param db         数据库
     * @param table      表名
     * @param keyArray   keys
     * @param valueArray values
     */
    public static void insert(SQLiteDatabase db, String table, Object[] keyArray, Object[] valueArray) {
        String keys = generateKeys(keyArray);
        String values = generateValues(valueArray);

        //insert into test(id,use_times,new,data) Values(1,0,1,'a');
        String insertSql = "insert into " + table + "(" + keys + ") Values(" + values + ");";
        MyLog.i(TAG, "insert: " + insertSql);
        db.execSQL(insertSql);
    }

    /**
     * 判断某条数据是否存在
     *
     * @param db        数据库
     * @param tableName 表名
     * @param key       key
     * @param value     value
     * @return 存在true，不存在false。
     */
    public static boolean isRowExist(SQLiteDatabase db, String tableName, String key, int value) {
        return isRowExist(db, tableName, key, String.valueOf(value));
    }

    /**
     * 判断某条数据是否存在
     *
     * @param db        数据库
     * @param tableName 表名
     * @param key       key
     * @param value     value
     * @return 存在true，不存在false。
     */
    public static boolean isRowExist(SQLiteDatabase db, String tableName, String key, String value) {
        String sql = "SELECT EXISTS(SELECT 1 FROM " + tableName + " WHERE " + key + "=" + value + " LIMIT 1);";
        Cursor cursor = null;
        int exist = -1;
        try {
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            exist = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exist == 1;
    }

    private static String generateKeys(Object[] keyArray) {
        String keys = "";
        for (Object key : keyArray) {
            keys += key + ",";
        }
        if (!android.text.TextUtils.isEmpty(keys)) {
            int size = keys.length();
            keys = keys.substring(0, size - 1);
        }
        return keys;
    }

    private static String generateValues(Object[] valueArray) {
        String values = "";
        for (Object value : valueArray) {
            values += "'" + value + "',";
        }
        if (!android.text.TextUtils.isEmpty(values)) {
            int size = values.length();
            values = values.substring(0, size - 1);
        }
        return values;
    }
}

