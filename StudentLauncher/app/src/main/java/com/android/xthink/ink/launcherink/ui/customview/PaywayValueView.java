/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */
package com.android.xthink.ink.launcherink.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;


/**
 * Created by renxu on 2017/3/13.
 * Description: 自定义组合VIP价钱选择View
 */
public class PaywayValueView extends RelativeLayout {
    private Context mContext;
    private ImageView mIv;
    private TextView mTvValue;
    private TextView mTvExplain;
    private String mStrValue;
    private String mStrExplain;
    private ImageView mIvSelect;

    public PaywayValueView(Context context) {
        this(context, null);
    }

    public PaywayValueView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaywayValueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.payway_value_view, this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PaywayValueView, defStyleAttr, 0);
        mStrValue = a.getString(R.styleable.PaywayValueView_value);
        mStrExplain = a.getString(R.styleable.PaywayValueView_explain);
        a.recycle();
        initView();
        loadData();
    }

    private void initView() {
        mIv = (ImageView) findViewById(R.id.iv_payway_value);
        mIvSelect = (ImageView) findViewById(R.id.iv_pay_select);
        mTvValue = (TextView) findViewById(R.id.tv_payway_value_fir);
        mTvExplain = (TextView) findViewById(R.id.tv_payway_value_sec);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        setContent(mStrValue,mStrExplain);
    }

    /**
     * 设置按钮内容
     * @param price 金额
     * @param present 赠送额
     */
    public void setContent(String price,String present ){
        if (!TextUtils.isEmpty(price)) {
            mTvValue.setText(price);
        }else {
            mTvValue.setText("");
        }
        if (!TextUtils.isEmpty(present)) {
            mTvExplain.setText(present);
            mTvExplain.setVisibility(View.VISIBLE);
        } else {
            mTvExplain.setVisibility(View.GONE);
        }
    }

    /**
     * 获取VIP的价格
     * @return 获取VIP的价格
     */
    public String getText() {
        String[] split = mStrValue.split("[^0-9]");
        return getResources().getString(R.string.payment_amount) + split[0];
    }

    /**
     * 设置是否选中
     * @param isChecked  是否选中
     */
    public void setChecked(boolean isChecked) {
        if (isChecked) {
            mIv.setBackground(mContext.getResources().getDrawable(R.drawable.shape_cb_payway));
            mIvSelect.setVisibility(VISIBLE);
            mTvValue.setTextColor(Color.BLACK);
        } else {
            mIv.setBackground(mContext.getResources().getDrawable(R.drawable.shape_cb_payway_un));
            mTvValue.setTextColor(this.getContext().getResources().getColor(R.color.white_68));
            mIvSelect.setVisibility(GONE);
        }
    }
}
