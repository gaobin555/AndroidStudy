/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 与插件相对，原生的页面。wechat,me,today,and so on.
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/8/22
 */
public abstract class NativeFragment extends BasePagerFragment {

    private static final String TAG = "NativeFragment";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        keepFontSize(getContext());
        newConfig.setToDefaults();
        super.onConfigurationChanged(newConfig);
    }

    @CallSuper
    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        keepFontSize(getContext());
        return null;
    }

    void keepFontSize(Context context) {
        if (context == null) {
            return;
        }
        //update resource
        Resources res = context.getResources();

        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
    }
}

