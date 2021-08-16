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
 * 桌面管理的向导
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/6/8
 */
public class DeskManagerGuideDialog extends BaseEditDialog {

    private Context mContext;

    public DeskManagerGuideDialog(Context context) {
        super(context);
        mContext = context;

        mDialog.setContentView(R.layout.dialog_desk_manager_guide);
        displayFullScreen();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        View finishBtn = mDialog.findViewById(R.id.dialog_desk_manager_finish_btn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }


}

