/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;

/**
 * 支付条目自定义组合控件
 *
 * @author renxu@coolpad.com
 * @version 1.0, 2017/4/11
 */
public class PayItemView extends RelativeLayout {
    private Context mContext;
    /**
     * 图标
     */
    private ImageView mIvIcon;
    /**
     * 描述文字
     */
    private TextView mTvDes;
    /**
     * 被选择状态图片
     */
    private ImageView mIvSelect;
    private boolean mIsSelect = false;
    private String mDes;
    private boolean mUseFul = true;

    public PayItemView(Context context) {
        this(context, null);
    }

    public PayItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.pay_item_custom, this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PayItemView, defStyleAttr, 0);
        Drawable icon = a.getDrawable(R.styleable.PayItemView_iconSrc);
        mDes = a.getString(R.styleable.PayItemView_des);
        mIsSelect = a.getBoolean(R.styleable.PayItemView_isSelect, false);
        mIvIcon = (ImageView) findViewById(R.id.iv_pay_item_icon);
        mTvDes = (TextView) findViewById(R.id.tv_pay_item_des);
        mIvSelect = (ImageView) findViewById(R.id.iv_pay_item_select);
        if (icon != null) {
            mIvIcon.setImageDrawable(icon);
        }
        if (mDes != null && mDes.length() > 0) {
            mTvDes.setText(mDes);
        }
        setSelect(mIsSelect);
        a.recycle();
    }

    /**
     * 设置支付图标
     *
     * @param res res资源ID
     */
    public void setIcon(int res) {
        if (res <= 0) {
            return;
        }
        try {
            mIvIcon.setImageResource(res);
        } catch (Exception e) {
            Log.d("",e.toString());
        }

    }

    /**
     * 设置描述文字
     *
     * @param des 描述文字
     */
    public void setDes(String des) {
        if (des == null) {
            return;
        }
        mDes = des;
        mTvDes.setText(des);
    }

    /**
     * 设置选择状态
     *
     * @param isSelect 是否选中
     */
    public void setSelect(boolean isSelect) {
        if (!mUseFul) {
            return;
        }
        if (isSelect) {
            mIvSelect.setImageResource(R.drawable.checkbox_sel);
        } else {
            mIvSelect.setImageResource(R.drawable.checkboxs_nor);
        }
        mIsSelect = isSelect;

    }

    /**
     * 设置条目的余额可用状态
     *
     * @param isUseful 可用状态
     */
    public void setBalanceUsable(boolean isUseful) {
        if (isUseful) {
            this.setEnabled(true);
            mIvIcon.setImageResource(R.drawable.icon_balance_nor);
            mTvDes.setTextColor(getResources().getColor(R.color.black));
            mIvSelect.setImageResource(R.drawable.checkbox_sel);
            mIsSelect = true;
            mUseFul = true;
        } else {
            this.setEnabled(false);
            mIvIcon.setImageResource(R.drawable.icon_balance_disable);
            mTvDes.setTextColor(getResources().getColor(R.color.white_68));
            mIvSelect.setImageResource(R.drawable.checkboxs_disabled);
            mIsSelect = false;
            mUseFul = false;
        }


    }

    /**
     * 获得条目是否可用信息
     *
     * @return Boolean 是否可用
     */
    public boolean isUseFul() {
        return mUseFul;
    }


    /**
     * 获取选中状态
     *
     * @return 是否被选中
     */
    public boolean isSelect() {
        return mIsSelect;
    }

}
