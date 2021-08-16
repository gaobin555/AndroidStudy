/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.common.R;


/**
 * 设置栏自定义控件
 *
 * @author liyuyan
 * @version 1.0, 2017/3/20
 */

public class SettingView extends RelativeLayout {
    private TextView tv_setting_view_title;
    private TextView tv_setting_view_des;
    private SwitchView sv_setting_view;
    private LinearLayout ll_update;
    private RelativeLayout rl_setting_view;
    private boolean mIsOpen;
    private String title;
    private String des;

    private boolean isUpdate;

    private OnSettingViewClickListener mOnSettingViewClickListener;

    public SettingView(Context context) {
        super(context);
        initView(context);
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
        mIsOpen = mTypedArray.getBoolean(R.styleable.SettingView_open, false);
        title = mTypedArray.getString(R.styleable.SettingView_title);
        des = mTypedArray.getString(R.styleable.SettingView_des);
        isUpdate = mTypedArray.getBoolean(R.styleable.SettingView_isUpdate, false);
        //获取资源后要及时回收
        mTypedArray.recycle();
        initView(context);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.common_setting_view, this, true);
        tv_setting_view_title = (TextView) findViewById(R.id.tv_setting_view_title);
        tv_setting_view_des = (TextView) findViewById(R.id.tv_setting_view_des);
        sv_setting_view = (SwitchView) findViewById(R.id.sv_setting_view);
        ll_update = (LinearLayout) findViewById(R.id.ll_update);
        rl_setting_view = (RelativeLayout) findViewById(R.id.rl_setting_view);
        tv_setting_view_title.setText(title);
        tv_setting_view_des.setText(des);
        sv_setting_view.setSwitchViewIsOn(mIsOpen);
        if (!isUpdate) {
            sv_setting_view.setVisibility(View.VISIBLE);
            ll_update.setVisibility(View.GONE);
        } else {
            sv_setting_view.setVisibility(View.GONE);
            ll_update.setVisibility(View.VISIBLE);
        }

        rl_setting_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsOpen(!getSwitchViewStatus());
                mIsOpen = getSwitchViewStatus();
                setSelected(mIsOpen);
                mOnSettingViewClickListener.onClick(SettingView.this);
            }
        });
    }

    public void setIsOpen(boolean isOpen) {
        mIsOpen = isOpen;
        setSelected(isOpen);
        if (!mIsOpen) {
            sv_setting_view.setSwitchViewIsOn(false);
        } else {
            sv_setting_view.setSwitchViewIsOn(true);
        }
    }

    public boolean getSwitchViewStatus() {
        return sv_setting_view.getSwitchViewStatus();
    }

    public interface OnSettingViewClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onClick(View v);
    }

    public void setOnSettingViewClickListener(OnSettingViewClickListener onSettingViewClickListener) {
        mOnSettingViewClickListener = onSettingViewClickListener;
    }

    public void setDes(String des) {
        tv_setting_view_des.setText(des);
    }
}