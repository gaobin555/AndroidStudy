/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog;

import android.content.Context;
import android.view.View;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.ui.customview.EditTableView;
import com.android.xthink.ink.launcherink.ui.dialog.provider.EditTableData;
import com.android.xthink.ink.launcherink.ui.dialog.provider.HobbyDataProvider;

import java.util.List;

/**
 * 阅读喜好编辑
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/13
 */
public class HobbyEditDialog extends BaseEditDialog {

    private static final int ROW = 6;
    private static final int COLUMN = 3;
    private final HobbyDataProvider mProvider;
    private final EditTableView mEditTableView;
    private final View mOkBtn;
    private final View mCancelBtn;

    public HobbyEditDialog(Context context, HobbyDataProvider provider) {
        super(context);
        mDialog.setContentView(R.layout.dialog_edit_hobby);
        mEditTableView = (EditTableView) mDialog.findViewById(R.id.dialog_edit_hobby_container);

        mOkBtn = mDialog.findViewById(R.id.dialog_common_bottom_ok_btn);
        mCancelBtn = mDialog.findViewById(R.id.dialog_common_bottom_cancel_btn);

        mProvider = provider;

        List<EditTableData> yotaOccupationList = provider.provide();
        mEditTableView.setTableData(yotaOccupationList, ROW, COLUMN);
        mEditTableView.setMultiSelect(true);

        addListener();
    }

    /**
     * 设置默认的阅读喜好
     *
     * @param ids 喜好的id
     */
    public void setDefaultData(List<Long> ids) {
        List<EditTableData> defaultData = mProvider.provideById(ids);
        mEditTableView.setDefaultData(defaultData);
    }

    private void addListener() {
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEditResult != null) {
                    List<EditTableData> results = mEditTableView.getSelectedItem();
                    Object[] editList = new EditTableData[results.size()];
                    editList = results.toArray(editList);
                    mOnEditResult.onResult(editList);
                }
                mDialog.dismiss();
                mEditTableView.clearSelectedView();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTableView.clearSelectedView();
                mDialog.dismiss();
            }
        });
    }


}

