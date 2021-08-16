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

/**
 * 请描述功能
 *
 * @author liyuyan
 * @version 1.0, 2017/4/20
 */

public class ExitDialog extends BaseEditDialog {

    private Context mContext;
    private final View mOkBtn;
    private final View mCancelBtn;

    public ExitDialog(Context context) {
        super(context);

        mContext = context;

        mDialog.setContentView(R.layout.dialog_exit);

        mOkBtn = mDialog.findViewById(R.id.dialog_common_bottom_ok_btn);
        mCancelBtn = mDialog.findViewById(R.id.dialog_common_bottom_cancel_btn);

        addListener();
    }

    private void addListener() {
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mOnEditResult != null) {
                    mOnEditResult.onResult(true);
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mOnEditResult != null) {
                    mOnEditResult.onResult(false);
                }
            }
        });
    }


}