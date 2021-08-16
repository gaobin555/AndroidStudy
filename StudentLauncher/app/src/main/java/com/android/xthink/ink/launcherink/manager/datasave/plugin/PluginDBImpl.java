/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.manager.datasave.plugin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.android.xthink.ink.launcherink.bean.PluginInfoBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.InkDbHelper;
import com.android.xthink.ink.launcherink.manager.datasave.pager.PagerConstants;

/**
 * 插件数据库
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/5/10
 */
public class PluginDBImpl {

    private Context mContext;

    public PluginDBImpl(Context context) {
        mContext = context;
    }

    /**
     * 根据插件的ID来查询插件
     *
     * @param pluginId 插件的ID
     * @return 插件对象
     */
    @Nullable
    public PluginInfoBean queryPlugin(int pluginId) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + InkConstants.JV_PLUGIN_DATA +
                    " where " + PagerConstants.PLUGIN_COLUMN_PLUGIN_ID + " = " + pluginId, null);

            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }
            // 只取第一个
            cursor.moveToNext();
            int indexOfLayout = cursor.getColumnIndex(PagerConstants.PLUGIN_COLUMN_LAYOUT_NAME);
            int indexOfPackage = cursor.getColumnIndex(PagerConstants.PLUGIN_COLUMN_PACKAGE_NAME);
            int indexOfName = cursor.getColumnIndex(PagerConstants.PLUGIN_COLUMN_PLUGIN_NAME);
            int indexOfId = cursor.getColumnIndex(PagerConstants.PLUGIN_COLUMN_PLUGIN_ID);

            String nameOfLayout = cursor.getString(indexOfLayout);
            String nameOfPackage = cursor.getString(indexOfPackage);
            String nameOfName = cursor.getString(indexOfName);
            int id = cursor.getInt(indexOfId);

            return new PluginInfoBean(nameOfLayout, nameOfPackage, nameOfName, id);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * 更新插件数据库
     * @param bean
     */
    private void updatePlugin(PluginInfoBean bean) {
        SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
        try {
            if (null != bean && 0 < bean.getId()) {
                db.execSQL("update "
                        + InkConstants.JV_PLUGIN_DATA + " set "
                        + PagerConstants.PLUGIN_COLUMN_PACKAGE_NAME
                        + "='" + bean.getPackageName() + "', "
                        + PagerConstants.PLUGIN_COLUMN_LAYOUT_NAME
                        + "='" + bean.getLayoutName() + "', "
                        + PagerConstants.PLUGIN_COLUMN_PLUGIN_NAME
                        + "='" + bean.getName()
                        + "' where " + PagerConstants.PLUGIN_COLUMN_PLUGIN_ID
                        + "=" + String.valueOf(bean.getId()));
            }
            db.close();
            MyLog.e("updatePlugin succeed:",  bean.getId()+"");
        } catch (Exception e) {
            MyLog.e("updatePlugin error:", e.toString());
        }

    }

    /**
     * 向插件数据库中插入数据
     *
     * @param bean 数据bean
     */
    public void insertPlugin(PluginInfoBean bean) {
        try {
            SQLiteDatabase db = InkDbHelper.getInstance(mContext).getWritableDatabase();
            String insertColumn = "insert into " + InkConstants.JV_PLUGIN_DATA
                    + "(" + PagerConstants.PLUGIN_COLUMN_LAYOUT_NAME + ", "
                    + PagerConstants.PLUGIN_COLUMN_PACKAGE_NAME + ", "
                    + PagerConstants.PLUGIN_COLUMN_PLUGIN_NAME + ", "
                    + PagerConstants.PLUGIN_COLUMN_PLUGIN_ID + ") ";

            db.execSQL(insertColumn + "Values(" + "'"
                    + bean.getLayoutName() + "','"
                    + bean.getPackageName() + "','"
                    + bean.getName() + "',"
                    + bean.getId()
                    + ")");
            db.close();
        } catch (Exception e) {
            MyLog.e("PluginDBImpl error :", e.toString());
        }

    }

    /**
     * 添加一个插件数据,数据库中有就更新原有的
     * @param bean
     */
    public void addPluginData(PluginInfoBean bean) {
        if (queryPlugin(bean.getId()) == null) {
            insertPlugin(bean);
        } else {
            updatePlugin(bean);
        }
    }

}

