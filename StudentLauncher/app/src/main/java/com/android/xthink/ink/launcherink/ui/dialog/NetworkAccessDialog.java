/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.utils.PluginUtils;

/**
 * 入网权限的对话框
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/6/5
 */
public class NetworkAccessDialog extends BaseEditDialog implements View.OnClickListener {

    private final View mNotAskBtn;
    private final View mNotAskCb;
    private TextView mTextView;

    public NetworkAccessDialog(Context context) {
        super(context);
        mDialog.setContentView(R.layout.dialog_network_access);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mNotAskBtn = mDialog.findViewById(R.id.dialog_not_ask_again_btn);
        mNotAskCb = mDialog.findViewById(R.id.dialog_not_ask_again_cb);
        View cancelBtn = mDialog.findViewById(R.id.dialog_network_cancel_btn);
        View okBtn = mDialog.findViewById(R.id.dialog_network_access_btn);
        mTextView = (TextView) mDialog.findViewById(R.id.textView);
        mNotAskBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);

        mNotAskCb.setSelected(true);
        String hint = PluginUtils.getPermissionFromID(context);
        mTextView.setText(hint);
    }

    public boolean notAskAgain() {
        return mNotAskCb.isSelected();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_not_ask_again_btn) {
            mNotAskCb.setSelected(!mNotAskCb.isSelected());
        } else if (id == R.id.dialog_network_cancel_btn) {
            if (mOnEditResult != null) {
                mOnEditResult.onResult(false);
            }
        } else if (id == R.id.dialog_network_access_btn) {
            if (mOnEditResult != null) {
                mOnEditResult.onResult(true);
            }
        }
    }
}

