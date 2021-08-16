/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.edit.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.MyLog;

import java.util.ArrayList;

/**
 * 背屏指示器,显示主页和点的样式
 *
 * @author renxu
 * @version 1.0, 2017/3/22
 */
public class HomeIndicator extends LinearLayout {

    private ArrayList<ImageView> indicationPoint;
    private int mCurrentIndex = 0;
    private int mHomeIndex;
    private int mCircleWidth;
    private int mHomeWidth;//指示器home的宽度
    private int mMargin;//指示器圆圈的宽度
    private int pagesCount;//指示器点的间距

    public void setCurIndex(int curIndex) {
        mCurrentIndex = curIndex;
    }
    public HomeIndicator(Context context) {
        super(context, null);
    }

    public HomeIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHomeWidth = (int) getContext().getResources().getDimension(R.dimen.home_indicator_home_width);
        mCircleWidth =(int)  getContext().getResources().getDimension(R.dimen.home_indicator_circle_width);
        mMargin = (int) getContext().getResources().getDimension(R.dimen.home_indicator_margin_width);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        indicationPoint = new ArrayList<>();
    }


    /**
     * 根据数据刷新界面
     */
    public void refresh(int homeIndex) {
        mHomeIndex = homeIndex;
        setPoints(homeIndex);
        setSelectedPoint(mCurrentIndex);
    }

    /**
     * 根据 homeScreen 添加点
     *
     * @param homeIndex 主屏index
     */
    private void setPoints(int homeIndex) {
        this.removeAllViews();
        indicationPoint.clear();
        for (int i = 0; i < pagesCount; i++) {
            ImageView point = new ImageView(getContext());
            if (i == homeIndex) {
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(mHomeWidth, mHomeWidth);
                layout.setMargins(mMargin, 0, mMargin, 0);
                point.setLayoutParams(layout);
            } else {
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(mCircleWidth, mCircleWidth);
                layout.setMargins(mMargin, 0, mMargin, 0);
                point.setLayoutParams(layout);
            }
            this.addView(point);
            indicationPoint.add(point);
        }
    }

    /**
     * //根据currentItem 设置黑白
     *
     * @param position 当前屏位置
     */
    public void setSelectedPoint(int position) {
        mCurrentIndex = position;
        Log.i("HomeIndicator", "93--setSelectedPoint: mCur = " + mCurrentIndex + ", homeIndex = " + mHomeIndex);
        for (int i = 0; i < indicationPoint.size(); i++) {
            if (i == mHomeIndex) {
                if (i == mCurrentIndex) {
                    indicationPoint.get(i).setBackgroundResource(R.drawable.home_screen_indicator_light);
                } else {
                    indicationPoint.get(i).setBackgroundResource(R.drawable.home_screen_indicator_dark);
                }
            } else {
                if (i == mCurrentIndex) {
                    indicationPoint.get(i).setBackgroundResource(R.drawable.edit_circle_on_bg);
                } else {
                    indicationPoint.get(i).setBackgroundResource(R.drawable.edit_circle_off_bg);
                }
            }
        }

    }

    public void refreshIndicator(int pagesCount, int homeIndex, boolean isFirstTime) {
        if (isFirstTime) {
            mCurrentIndex = homeIndex;
        }
        setPagesCount(pagesCount);
        refresh(homeIndex);
        MyLog.d("homeIndicator",pagesCount+"pagesCount_"+homeIndex+"homeIndex"+mCurrentIndex+"mCurrentIndex");
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
    }
}
