/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.edit;

import com.android.xthink.ink.launcherink.bean.InkPageEditBean;

import java.util.ArrayList;

/**
 * 编辑页数据传输的eventBean
 *
 * @author renxu@coolpad.com
 * @version 1.0, 2017/6/15
 */
public class EditEvent {
    public EditEvent(int homeId,ArrayList<InkPageEditBean> toShowBeanList ) {
        this.toShowBeanList = toShowBeanList;
        this.homeId = homeId;
    }

    public int homeId;
    public ArrayList<InkPageEditBean> toShowBeanList;
}
