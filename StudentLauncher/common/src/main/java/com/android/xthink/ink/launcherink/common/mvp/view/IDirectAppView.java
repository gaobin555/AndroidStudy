/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.mvp.view;

import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;

import java.util.List;

/**
 * 直通app页面信息
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/11/8
 */
public interface IDirectAppView extends IBaseView {
    /**
     * 显示直通app列表
     *
     * @param directAppBeanList 直通列表
     */
    void showDirectAppList(List<DirectAppBean> directAppBeanList);

    // XTHINK:用于刷新APP列表
    void updataAppList();
}

