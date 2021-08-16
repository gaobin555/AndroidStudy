/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog.provider;

import android.content.Context;
import android.support.annotation.NonNull;
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
public class HobbyDataProvider {

    private final SparseArray<EditTableData> mHobbyMap;

    public HobbyDataProvider(Context context) {
        mHobbyMap = new SparseArray<>();
        mHobbyMap.put(1, new EditTableData(1, context.getString(R.string.reading_hobby_reading)));
        mHobbyMap.put(2, new EditTableData(2, context.getString(R.string.reading_hobby_art)));
        mHobbyMap.put(3, new EditTableData(3, context.getString(R.string.reading_hobby_positive_energy)));
        mHobbyMap.put(4, new EditTableData(4, context.getString(R.string.reading_hobby_talk_culture)));
        mHobbyMap.put(5, new EditTableData(5, context.getString(R.string.the_internet)));
        mHobbyMap.put(6, new EditTableData(6, context.getString(R.string.reading_hobby_social_sciences)));
        mHobbyMap.put(7, new EditTableData(7, context.getString(R.string.reading_hobby_army)));
        mHobbyMap.put(8, new EditTableData(8, context.getString(R.string.reading_hobby_game)));
        mHobbyMap.put(9, new EditTableData(9, context.getString(R.string.reading_hobby_reading_heart)));
        mHobbyMap.put(10, new EditTableData(10, context.getString(R.string.reading_hobby_science)));
        mHobbyMap.put(11, new EditTableData(11, context.getString(R.string.reading_hobby_learn)));
        mHobbyMap.put(12, new EditTableData(12, context.getString(R.string.reading_hobby_profession)));
        mHobbyMap.put(13, new EditTableData(13, context.getString(R.string.reading_hobby_reading_history)));
        mHobbyMap.put(14, new EditTableData(14, context.getString(R.string.reading_hobby_learn_finance)));
        mHobbyMap.put(15, new EditTableData(15, context.getString(R.string.reading_hobby_love_live)));
        mHobbyMap.put(16, new EditTableData(16, context.getString(R.string.reading_hobby_character_records)));
        mHobbyMap.put(17, new EditTableData(17, context.getString(R.string.reading_hobby_chose_magazine)));
        mHobbyMap.put(18, new EditTableData(18, context.getString(R.string.reading_hobby_foreign)));
    }

    public List<EditTableData> provide() {
        List<EditTableData> editTableList = new ArrayList<>();
        for (int i = 0; i < mHobbyMap.size(); i++) {
            int key = mHobbyMap.keyAt(i);
            // get the object by the key.
            editTableList.add(mHobbyMap.get(key));
        }
        return editTableList;
    }

    /**
     * 根据id得到阅读喜好
     *
     * @param ids id 列表
     * @return 阅读喜好，不为null
     */
    @NonNull
    public List<EditTableData> provideById(List<Long> ids) {
        List<EditTableData> editTableList = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                if (id == null) {
                    continue;
                }
                long idLong = id;
                editTableList.add(mHobbyMap.get((int) idLong));
            }
        }
        return editTableList;
    }
}

