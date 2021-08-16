/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.android.xthink.ink.launcherink.common.R;


/**
 * 请描述功能
 *
 * @author liyuyan
 * @version 1.0, 2017/3/20
 */

public class SwitchView extends ImageButton {
    boolean mIsOn;

    public SwitchView(Context context) {
        super(context);
        initView(context);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchView);
        mIsOn = mTypedArray.getBoolean(R.styleable.SwitchView_isOpen, false);
        //获取资源后要及时回收
        mTypedArray.recycle();
        initView(context);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        setImageResource(mIsOn ? R.drawable.switch_on : R.drawable.switch_off);
        setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public void setSwitchViewIsOn(Boolean isOn) {
        setImageResource(isOn ? R.drawable.switch_on : R.drawable.switch_off);
        mIsOn = isOn;
    }

    public void setSwitchViewOff() {
        setImageResource(R.drawable.switch_off);
        mIsOn = false;
    }

    public boolean getSwitchViewStatus() {
        return mIsOn;
    }
}