package com.android.xthink.ink.launcherink.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;

/**
 * 儿童模式Dialog
 *
 * @author liyuyan
 * @version 1.0, 2018/1/5
 */

public class KidsModeHintDialog implements View.OnClickListener {

    private View mNotAskBtn;
    private View mNotAskCb;
    private TextView mTextView;
    protected Dialog mDialog;
    private Window mDialogWindow;

    public KidsModeHintDialog(Context context) {
        mDialog = null;
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
        mDialog.setContentView(R.layout.dialog_kdis_mode_hint);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mNotAskBtn = mDialog.findViewById(R.id.dialog_not_ask_again_btn);
        mNotAskCb = mDialog.findViewById(R.id.dialog_not_ask_again_cb);
        View okBtn = mDialog.findViewById(R.id.dialog_network_access_btn);
        mTextView = (TextView) mDialog.findViewById(R.id.textView);
        mNotAskBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);

        mNotAskCb.setSelected(false);
    }

    public boolean notAskAgain() {
        return mNotAskCb.isSelected();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_not_ask_again_btn) {
            mNotAskCb.setSelected(!mNotAskCb.isSelected());
        } else if (id == R.id.dialog_network_access_btn) {
            if (mOnEditResult != null) {
                mOnEditResult.onResult(true);
            }
        }
    }

    protected OnEditResult mOnEditResult;

    /**
     * 设置编辑结果监听
     *
     * @param onEditResult 编辑结果监听
     */
    public void setOnEditResult(OnEditResult onEditResult) {
        mOnEditResult = onEditResult;
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

    /**
     * 编辑结果监听器
     */
    public interface OnEditResult {
        /**
         * 得到编辑的结果
         *
         * @param results 结果，不为null
         */
        void onResult(@NonNull Object... results);
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
