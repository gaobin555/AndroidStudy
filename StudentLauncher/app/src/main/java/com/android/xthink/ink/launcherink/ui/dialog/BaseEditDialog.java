/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;


/**
 * 基础对话框,内部封装了一个dialog
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/11
 */
public class BaseEditDialog extends BaseDialog{

    protected final static int SP_TEXT_SIZE_DEFAULT = 18;

    protected OnEditResult mOnEditResult;

    public BaseEditDialog(Context context) {
        super(context);
    }


    /**
     * 设置编辑结果监听
     *
     * @param onEditResult 编辑结果监听
     */
    public void setOnEditResult(OnEditResult onEditResult) {
        mOnEditResult = onEditResult;
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
}

