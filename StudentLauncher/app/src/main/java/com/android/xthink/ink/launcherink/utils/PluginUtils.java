/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.utils;

import android.content.Context;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.bean.InkPageFullInfoBean;
import com.android.xthink.ink.launcherink.bean.PluginInfoBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.manager.datasave.pager.OperatePagersDBImpl;
import com.android.xthink.ink.launcherink.manager.datasave.plugin.PluginDBImpl;

/**
 * 插件添加工具类
 *
 * @author renxu@coolpad.com
 * @version 1.0, 2017/6/7
 */
public class PluginUtils {


    /**
     * 添加插件到数据库,删除一条数据时set PageBean中isSelected,isEditable为false,插件不再显示
     * 更新数据直接添加新数据,id需保持一致
     *
     * @param context  数据库context
     * @param PageBean 页面信息
     * @param bean     插件信息
     */
    public static void addPluginInDB(Context context, InkPageFullInfoBean PageBean, PluginInfoBean bean) {
        boolean isPluginInstalled = CommonUtils.isPkgInstalled(context, bean.getPackageName());
        if (!isPluginInstalled) {
            return;
        }
//        添加页面数据,插件数据库信息
        OperatePagersDBImpl pagersDB = new OperatePagersDBImpl(context);
        PluginDBImpl pluginDB = new PluginDBImpl(context);
        pagersDB.addPageData(PageBean);
        pluginDB.addPluginData(bean);

    }

//    public static int getPluginIdInByName(Context context, String name) {
//        if (name.contains(context.getString(R.string.music))) {
//            return InkConstants.PAGE_ID_MUSIC;
//        } else {
//            return -1;
//        }
//    }

    /**
     * @param context
     * @return
     */
    public static String getPermissionFromID(Context context) {
        return context.getString(R.string.permission_mozhi);
    }

//    public static String getNameFromID(Context context, int id) {
//        String name = "";
//        switch (id) {
//            case InkConstants.PAGE_ID_WECHAT:
//                return context.getString(R.string.wechat_title);
//            case InkConstants.PAGE_ID_TODAY:
//                return context.getString(R.string.today);
//            case InkConstants.PAGE_ID_TODO:
//                return context.getString(R.string.todo);
//            case InkConstants.PAGE_ID_MUSIC:
//                return context.getString(R.string.music);
//        }
//        return name;
//    }

    public static String getSPAccessID(int id) {
        String name = "sp_is_access";
        return name + id;
    }

    public static String getSPNotShowAgainID(int id) {
        String name = "sp_not_show_again";
        return name + id;
    }
}
