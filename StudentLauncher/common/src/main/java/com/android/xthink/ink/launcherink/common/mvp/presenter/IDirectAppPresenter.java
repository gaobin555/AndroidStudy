/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.mvp.presenter;

import android.content.Context;

/**
 * 直通app业务接口
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/11/8
 */
public interface IDirectAppPresenter extends IPresenter {
    /**
     * 加载直通app
     *
     * @param resultCount    需要的结果数量
     * @param sortByUseTimes 是否根据使用次数排序
     */
    void loadAllDirectApp(int resultCount, boolean sortByUseTimes, boolean createTestData);

    /*
    *  读取全部应用信息
    * */
    void loadAllApp(Context context);

    /*
     * 读取固定应用
     */
    void loadFixedApp();


    /*
     * 读取固定应用
     */
    void loadTypeIcon(Context context);

    /**
     * 更新所有app的new状态
     */
    void updateAllAppStatus();

    /**
     * 更多的内容中是否有新的内容
     *
     * @param begin 起始位置 例如传7，则是从8个开始计算
     * @param count 从起始开始计算的个数，例如传1，那么就检查 begin，begin+1的位置。例如传-1，就检查从begin到end的位置。
     * @return 如果有就返回true，否则返回false
     */
    boolean hasMoreNewApps(int begin, int count);

    /**
     * 修改直通app的new状态
     *
     * @param id    app的id
     * @param isNew 是否是new
     */
    void updateDirectAppStatus(int id, boolean isNew);

    /**
     * 更新使用次数
     *
     * @param id app id
     */
    void updateDirectAppUseTimes(int id);
}

