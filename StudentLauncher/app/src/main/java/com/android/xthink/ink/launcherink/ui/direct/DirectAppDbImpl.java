/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.direct;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.constants.InkDirectConstants;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppList;
import com.android.xthink.ink.launcherink.common.utils.DbUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.InkDbHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.android.xthink.ink.launcherink.common.constants.InkDirectConstants.ITEM_CONST_IS_NEW;

/**
 * 直通app数据库操作类
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/8
 */
public class DirectAppDbImpl {

    private static final String TAG = "DirectAppDbImpl";

    private final Context mContext;

    public DirectAppDbImpl(Context context) {
        mContext = context;
    }

    /**
     * 增量更新数据库
     *
     * @param directAppList 服务器请求的数据
     * @param isFirstUpdate 是否第一次更新数据库，如果是，那么所有的new标记都不会有
     * @return 是否成功更新数据库
     */
    public boolean updateDirectApp(DirectAppList directAppList, boolean isFirstUpdate) {

        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        boolean success = updateDirectApp(db, directAppList, isFirstUpdate);
        close(null, db);
        return success;
    }

    /**
     * 增量更新数据库
     *
     * @param db            指定一个数据库
     * @param directAppList 服务器请求的数据
     * @param isFirstUpdate 是否第一次更新数据库，如果是，那么所有的new标记都不会有
     * @return 是否成功更新数据库
     */
    private boolean updateDirectApp(SQLiteDatabase db, DirectAppList directAppList, boolean isFirstUpdate) {

        if (directAppList == null || directAppList.getList() == null || directAppList.getList().size() == 0) {
            MyLog.i(TAG, "updateDirectApp: empty data,fail");
            return false;
        }

        boolean success = true;

        try {
            List<DirectAppBean> directAppBeanList = directAppList.getList();
            MyLog.i(TAG, "updateDirectApp: 开始更新数据库，一共" + directAppBeanList.size() + "条数据");
            for (DirectAppBean bean : directAppBeanList) {
                boolean isRowExist = DbUtils.isRowExist(db, InkConstants.JV_DIRECT_APP_DATA, InkDirectConstants.ITEM_DIRECT_APP_ID, bean.getId());
                MyLog.d(TAG, "isRowExist = " + isRowExist + " bean = " + bean.toString());
                if (isRowExist) {
                        // 没有被删除，则更新这条数据。
                        updateDirectApp(db, bean);
                } else {
                        // 添加这条数据。
                        addDirectApp(db, bean, isFirstUpdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * 修改直通app的new状态
     *
     * @param id    app的id
     * @param isNew 是否是new
     */
    public void updateDirectAppStatus(int id, boolean isNew) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(InkDirectConstants.ITEM_DIRECT_APP_NEW,
                isNew ? ITEM_CONST_IS_NEW : InkDirectConstants.ITEM_CONST_IS_NOT_NEW);
        String where = InkDirectConstants.ITEM_DIRECT_APP_ID + "=" + id;
        db.update(InkConstants.JV_DIRECT_APP_DATA, contentValues, where, null);
        close(null, db);
    }

    /**
     * 修改直通app的new状态
     *
     * @param isNew 是否是new
     */
    public void updateDirectAppStatus(boolean isNew) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(InkDirectConstants.ITEM_DIRECT_APP_NEW,
                isNew ? ITEM_CONST_IS_NEW : InkDirectConstants.ITEM_CONST_IS_NOT_NEW);
        db.update(InkConstants.JV_DIRECT_APP_DATA, contentValues, null, null);
        close(null, db);
    }

    /**
     * 增加某个app的使用次数
     *
     * @param id app的id
     */
    public void increaseDirectAppUseTimes(int id) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();

        //update table1 set clicks=clicks+1 where newsid=1233
        String updateSql = "update " + InkConstants.JV_DIRECT_APP_DATA + " set " +
                InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES + "=" + InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES + "+1"
                + " where " + InkDirectConstants.ITEM_DIRECT_APP_ID + "=" + id;
        db.execSQL(updateSql);

        close(null, db);
    }

    /**
     * 修改某个app的使用次数
     *
     * @param id        app的id
     * @param userCount 使用次数
     */
    public void updateDirectAppUseTimes(int id, int userCount) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        updateDirectAppUseTimes(db, id, userCount);
        close(null, db);
    }

    /**
     * 修改某个app的使用次数
     *
     * @param id        app的id
     * @param userCount 使用次数
     */
    public void updateDirectAppUseTimes(SQLiteDatabase db, int id, int userCount) {
        //update table1 set clicks=userCount where newsid=1233
        String updateSql = "update " + InkConstants.JV_DIRECT_APP_DATA + " set " +
                InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES + "=" + userCount
                + " where " + InkDirectConstants.ITEM_DIRECT_APP_ID + "=" + id;
        db.execSQL(updateSql);
    }

    /**
     * 查询直通app
     *
     * @param count       要查的数量，传-1查所有
     * @param byUserTimes 按照使用频率排序
     * @return 查询结果
     */
    public List<DirectAppBean> queryDirectApp(int count, boolean byUserTimes) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        List<DirectAppBean> result = queryDirectApp(db, count, byUserTimes);
        close(null, db);
        return result;
    }


    /**
     * 查询直通app
     *
     * @param db          指定数据库
     * @param count       要查的数量，传-1查所有
     * @param byUserTimes 按照使用频率排序
     * @return 查询结果
     */
    public List<DirectAppBean> queryDirectApp(SQLiteDatabase db, int count, boolean byUserTimes) {
        return queryDirectApp(db, count, byUserTimes, true);
    }


    /**
     * 查询直通app
     *
     * @param db          指定数据库
     * @param count       要查的数量，传-1查所有
     * @param byUserTimes 按照使用频率排序
     * @param byIndex     是否按照index排序
     * @return 查询结果
     */
    public List<DirectAppBean> queryDirectApp(SQLiteDatabase db, int count, boolean byUserTimes, boolean byIndex) {
        String countString = "";
        if (count > 0) {
            //limit 0,2
            countString = " limit 0," + count;
        }

        // 加上排序，如果有时间排序就优先时间排序，如果没有时间就默认index排序。
        String orderString = " order by ";
        if (byUserTimes) {
            // 有时间，先时间，时间相同时，就按照index排序
            orderString = orderString + InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES + " desc";
            if (byIndex) {
                orderString = orderString + ", ";
            }
        }
        if (byIndex) {
            orderString = orderString + InkDirectConstants.ITEM_DIRECT_APP_DEFAULT_INDEX;
        }
        if (!byUserTimes && !byIndex) {
            orderString = "";
        }

        // select top X *  from table_name order by column_name desc
        String queryString = "select " + " * from " + InkConstants.JV_DIRECT_APP_DATA + orderString + countString;
        MyLog.i(TAG, "queryDirectApp sql: " + queryString);
        Cursor cursor = db.rawQuery(queryString, null);

        Gson gson = new Gson();
        List<DirectAppBean> directAppBeanList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String data = cursor.getString(cursor.getColumnIndex(InkDirectConstants.ITEM_DIRECT_DATA));
            int userCount = cursor.getInt(cursor.getColumnIndex(InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES));
            DirectAppBean directAppBean = gson.fromJson(data, DirectAppBean.class);
            directAppBean.setUserCount(userCount);
            directAppBeanList.add(directAppBean);
        }
        close(cursor, null);
        return directAppBeanList;
    }

    /**
     * 指定位置是否有new的app
     *
     * @param begin 起始位置 例如传7，则是从8个开始计算
     * @param count 从起始开始计算的个数，例如传1，那么就检查 begin，begin+1的位置。例如传-1，就检查从begin到end的位置。
     * @return 如果有就返回true，没有则返回false
     */
    public boolean checkHasNewApps(int begin, int count) {
        //select * from (select * from jv_ink_direct_app_data order by item_direct_app_use_times desc limit 7,-1)
        // where item_direct_app_new
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        String querySql = "select " + InkDirectConstants.ITEM_DIRECT_APP_ID +
                " from(" +
                " select " + "*" +
                " from " + InkConstants.JV_DIRECT_APP_DATA +
                " order by " + InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES + " desc " +
                " limit " + begin + "," + count + ")" +
                " where " + InkDirectConstants.ITEM_DIRECT_APP_NEW + "=" + ITEM_CONST_IS_NEW;

        MyLog.i(TAG, "checkHasNewApps: querySql:" + querySql);
        Cursor cursor = db.rawQuery(querySql, null);
        boolean hasNewApps = false;
        if (cursor != null && cursor.getCount() > 0) {
            hasNewApps = true;
        }
        close(cursor, db);
        return hasNewApps;
    }

    // 添加直通app
    private void addDirectApp(SQLiteDatabase db, DirectAppBean directAppBean, boolean isFirstUpdate) {
        int id = directAppBean.getId();
        int userTime = directAppBean.getUserCount();
        int isNew = isFirstUpdate ? InkDirectConstants.ITEM_CONST_IS_NOT_NEW : InkDirectConstants.ITEM_CONST_IS_NEW;
        int defaultIndex = directAppBean.getOrderNo();
        String data = beanToString(directAppBean);
        String[] keys = new String[]{
                InkDirectConstants.ITEM_DIRECT_APP_ID,
                InkDirectConstants.ITEM_DIRECT_APP_USE_TIMES,
                InkDirectConstants.ITEM_DIRECT_APP_DEFAULT_INDEX,
                InkDirectConstants.ITEM_DIRECT_APP_NEW,
                InkDirectConstants.ITEM_DIRECT_DATA
        };
        Object[] values = new Object[]{
                id, userTime, defaultIndex, isNew, data
        };
        DbUtils.insert(db, InkConstants.JV_DIRECT_APP_DATA, keys, values);
    }

    // 更新直通app信息
    private void updateDirectApp(SQLiteDatabase db, DirectAppBean directAppBean) {
        int id = directAppBean.getId();
        String data = beanToString(directAppBean);
        ContentValues contentValues = new ContentValues();
        contentValues.put(InkDirectConstants.ITEM_DIRECT_DATA, data);
        contentValues.put(InkDirectConstants.ITEM_DIRECT_APP_DEFAULT_INDEX, directAppBean.getOrderNo());

        String where = InkDirectConstants.ITEM_DIRECT_APP_ID + "=" + id;
        db.update(InkConstants.JV_DIRECT_APP_DATA, contentValues, where, null);
    }

    // 删除直通app
    private void deleteDirectApp(SQLiteDatabase db, DirectAppBean directAppBean) {

        int id = directAppBean.getId();
        String where = InkDirectConstants.ITEM_DIRECT_APP_ID + "=" + id;
        db.delete(InkConstants.JV_DIRECT_APP_DATA, where, null);
    }

    private String beanToString(DirectAppBean bean) {
        Gson gson = new Gson();
        return bean == null ? "" : gson.toJson(bean);
    }

    private void close(Cursor cursor, SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * 生成默认的直通数据，目前用于数据库初始数据是空的时候，用这个方法来生成默认数据。
     */
    public void generateDefaultDirectApps(SQLiteDatabase db) {
        DirectAppList direct = getDefaultDirectApps();
        updateDirectApp(db, direct, true);
    }

    private DirectAppList getDefaultDirectApps() {
        DirectAppList direct = new DirectAppList();
        ArrayList<DirectAppBean> appList = new ArrayList<DirectAppBean>();
        direct.setList(appList);

        return direct;
    }
}

