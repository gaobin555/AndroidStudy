/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog.provider;

import android.content.Context;
import android.util.SparseArray;

import com.android.xthink.ink.launcherink.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读喜好数据提供者
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/16
 */
public class OccupationDataProvider {

    private final SparseArray<EditTableData> mOccupationMap;

    public OccupationDataProvider(Context context) {
        mOccupationMap = new SparseArray<>();
        mOccupationMap.put(11, new EditTableData(11, context.getString(R.string.the_internet)));
        mOccupationMap.put(12, new EditTableData(12, context.getString(R.string.occupations_it)));
        mOccupationMap.put(13, new EditTableData(13, context.getString(R.string.occupations_estate)));
        mOccupationMap.put(14, new EditTableData(14, context.getString(R.string.occupations_finance)));
        mOccupationMap.put(15, new EditTableData(15, context.getString(R.string.occupations_education)));
        mOccupationMap.put(16, new EditTableData(16, context.getString(R.string.occupations_medical)));
        mOccupationMap.put(17, new EditTableData(17, context.getString(R.string.occupations_maker)));
        mOccupationMap.put(18, new EditTableData(18, context.getString(R.string.occupations_media)));
        mOccupationMap.put(19, new EditTableData(19, context.getString(R.string.occupations_government)));
    }

    public List<EditTableData> provide() {
        List<EditTableData> editTableList = new ArrayList<>();
        for (int i = 0; i < mOccupationMap.size(); i++) {
            int key = mOccupationMap.keyAt(i);
            // get the object by the key.
            editTableList.add(mOccupationMap.get(key));
        }
        return editTableList;
    }

    public EditTableData provideById(int id) {
        return mOccupationMap.get(id);
    }
}

