/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.xthink.ink.launcherink.common.utils.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * 地区数据提供
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/15
 */
public class AreaDataProvider {

    private static final String DATABASE_NAME = "area.db";
    private static final int DATABASE_VERSION = 1;

    private static final int VALUE_LEVEL_PROVINCE = 2;
    private static final int VALUE_LEVEL_CITY = 3;

    private static final String TABLE_NAME = "area";

    private static final String COL_ID = "id";
    private static final String COL_LEVEL = "level";
    private static final String COL_AREA_NAME = "area_name";
    private static final String COL_PARENT_ID = "parent_id";
    private static final String COL_IS_CENTER = "is_province";
    private AreaMap mAreaMap;
    private final SQLiteAssetHelper mSqLiteAssetHelper;

    public AreaDataProvider(Context context) {
        mSqLiteAssetHelper = new SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * 得到省
     *
     * @return 省的列表
     */
    public List<AreaBean> provideProvince() {
        prepareAllData();
        return mAreaMap.keyList();
    }

    /**
     * 得到某个省的市
     *
     * @param province 省
     * @return 市列表
     */
    public List<AreaBean> provideCity(AreaBean province) {
        prepareAllData();
        return mAreaMap.get(province);
    }


    /**
     * 根据id去查数据库
     *
     * @param id 地区id
     * @return 地区对象，查不到返回null
     */
    @Nullable
    public AreaBean queryById(int id) {
        SQLiteDatabase db = mSqLiteAssetHelper.getWritableDatabase();
        String[] columns = {COL_ID, COL_LEVEL, COL_AREA_NAME, COL_PARENT_ID, COL_IS_CENTER};

        AreaBean area = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, columns, COL_ID + "=" + id, null, null, null, COL_LEVEL);

            while (cursor.moveToNext()) {
                area = new AreaBean();
                area.id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                area.level = cursor.getInt(cursor.getColumnIndex(COL_LEVEL));
                area.areaName = cursor.getString(cursor.getColumnIndex(COL_AREA_NAME));
                area.parentId = cursor.getInt(cursor.getColumnIndex(COL_PARENT_ID));
                area.isCenter = cursor.getInt(cursor.getColumnIndex(COL_IS_CENTER));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return area;
    }

    private void initData() {
        mAreaMap = new AreaMap();
        SQLiteDatabase db = mSqLiteAssetHelper.getWritableDatabase();
        String[] columns = {COL_ID, COL_LEVEL, COL_AREA_NAME, COL_PARENT_ID, COL_IS_CENTER};

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, columns, null, null, null, null, COL_LEVEL);

            while (cursor.moveToNext()) {
                AreaBean area = new AreaBean();
                area.id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                area.level = cursor.getInt(cursor.getColumnIndex(COL_LEVEL));
                area.areaName = cursor.getString(cursor.getColumnIndex(COL_AREA_NAME));
                area.parentId = cursor.getInt(cursor.getColumnIndex(COL_PARENT_ID));
                area.isCenter = cursor.getInt(cursor.getColumnIndex(COL_IS_CENTER));
                mAreaMap.addArea(area);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void prepareAllData() {
        if (mAreaMap == null) {
            initData();
        }
    }

    /**
     * 包装一个hashMap
     */
    private static class AreaMap {
        private LinkedHashMap<AreaBean, List<AreaBean>> mAreaMap = new LinkedHashMap<>();

        private void addArea(@NonNull AreaBean areaBean) {

            if (areaBean.level == VALUE_LEVEL_PROVINCE) {
                mAreaMap.put(areaBean, null);
            }

            if (areaBean.level == VALUE_LEVEL_CITY || areaBean.isCenter != 0) {
                AreaBean province;
                if (areaBean.level == VALUE_LEVEL_CITY) {
                    province = new AreaBean();
                    province.id = areaBean.parentId;
                } else {
                    province = areaBean;
                }

                List<AreaBean> cityList = mAreaMap.get(province);
                if (cityList == null) {
                    cityList = new ArrayList<>();
                    mAreaMap.put(province, cityList);
                }
                cityList.add(areaBean);
            }
        }

        private List<AreaBean> keyList() {
            Set<AreaBean> provinces = mAreaMap.keySet();
            return new ArrayList<>(provinces);
        }

        private List<AreaBean> get(AreaBean key) {
            return mAreaMap.get(key);
        }
    }

    public static class AreaBean {
        public int id;
        public int isCenter;
        public int level;
        public String areaName;
        public int parentId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AreaBean)) return false;

            AreaBean areaBean = (AreaBean) o;

            return id == areaBean.id;

        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return areaName;
        }
    }

}

