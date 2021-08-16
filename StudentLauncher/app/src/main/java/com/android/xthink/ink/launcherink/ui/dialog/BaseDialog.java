/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.xthink.ink.launcherink.R;

/**
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/6/5
 */
public class BaseDialog {
    protected Dialog mDialog;
    private Window mDialogWindow;

    public BaseDialog(Context context) {
        mDialog = new Dialog(context, R.style.DialogTheme);
        mDialogWindow = mDialog.getWindow();
        if (mDialogWindow != null) {
            WindowManager.LayoutParams lp = mDialogWindow.getAttributes();
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            mDialogWindow.setGravity(Gravity.BOTTOM);
            mDialogWindow.setAttributes(lp);
            mDialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void displayFullScreen() {
        WindowManager.LayoutParams lp = mDialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialogWindow.setAttributes(lp);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mDialog.setOnDismissListener(listener);
    }

    public void show() {
        if (!mDialog.isShowing()) {
            if (mDialogWindow != null) {
                final View contentView = mDialogWindow.getDecorView();
                setUpdateMode(contentView);
                mDialog.show();
            }
        }
    }

    protected void setUpdateMode(@NonNull View rootView) {
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}

