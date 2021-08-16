/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.view.mainviewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * [-∞ , -1)  :
 * 表示左边 的View 且已经看不到了
 * [-1 ,   0]  :
 * 表示左边的 View ,且可以看见
 * ( 0 ,   1]  :
 * 表示右边的VIew , 且可以看见了
 * ( 1 , -∞)  :
 * 表示右边的 View 且已经看不见了
 *
 * @author liyuyan
 * @version 1.0, 2017/3/24
 */

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position < -0.5) { // [-1,0]
            // Use the default slide transition when moving to the left page
            //view.setAlpha(1 + position);
            view.setAlpha(0);
            view.setTranslationX(0);
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1);
            view.setTranslationX(pageWidth * -position);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}