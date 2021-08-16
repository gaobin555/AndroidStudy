/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;


/**
 * 用户体系页面的基础activity.
 * <p>
 * 都使用了一个common_user_title_layout作为头部的布局
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/3/17
 */
public abstract class TitleBaseActivity extends BaseActivity implements View.OnClickListener {

    private View mBackBtn;
    private TextView mTitleTv;
    private View mFunctionView;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBackBtn = findViewById(R.id.common_user_title_back_btn);
        mTitleTv = (TextView) findViewById(R.id.common_user_title_tv);

        // back
        if (mBackBtn != null) {
            mBackBtn.setOnClickListener(this);
        }
        if (mTitleTv == null) {
            return;
        }

        // title
        String title = getTitleText();
        if (TextUtils.isEmpty(title)) {
            mTitleTv.setVisibility(View.INVISIBLE);
        } else {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(title);
        }

        // 右侧功能按钮
        if (!TextUtils.isEmpty(getFunctionText())) {
            TextView functionTv = (TextView) findViewById(R.id.common_user_title_right_function_tv);
            functionTv.setText(getFunctionText());
            mFunctionView = functionTv;
        } else if (getFunctionImageRes() != 0) {
            ImageView functionIv = (ImageView) findViewById(R.id.common_user_title_right_function_iv);
            functionIv.setImageResource(getFunctionImageRes());
            mFunctionView = functionIv;
        }
        View.OnClickListener onRightFunctionClickListener = getOnRightFunctionClickListener();
        if (onRightFunctionClickListener != null && mFunctionView != null) {
            mFunctionView.setOnClickListener(onRightFunctionClickListener);
        }
        if (mFunctionView != null) {
            mFunctionView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 得到标题内容，如果返回空，则隐藏标题。
     */
    protected abstract String getTitleText();

    /**
     * 如果右侧功能键是文字就复写此方法，返回文字
     *
     * @return 功能键名称
     */
    protected String getFunctionText() {
        return "";
    }

    /**
     * 如果功能键是一个图片，则复习此方法，返回图片资源id
     *
     * @return 功能的资源id
     */
    protected int getFunctionImageRes() {
        return 0;
    }

    /**
     * 设置标题上面右侧功能键的回调监听
     */
    protected View.OnClickListener getOnRightFunctionClickListener() {
        return null;
    }

    public void onTitleBackPressed() {
        finish();
    }

    @Override
    @CallSuper
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.common_user_title_back_btn) {
            onTitleBackPressed();
        }
    }
}

