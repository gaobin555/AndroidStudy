/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.init;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.InkAppManager;
import com.android.xthink.ink.launcherink.init.bean.InitFlavorBean;

import java.util.HashMap;

/**
 * 初始化app，读取配置文件，决定渠道。blauncher_cfg.json
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/8/31
 */
public class InitAppHelper {

    private static final String TAG = "InitAppHelper";

    private static InitAppHelper instance;

    private HashMap<String, String> mPackageNameMap;

    private HashMap<String, String> mPageNameMap;

    private HashMap<String, String> mPageTitleMap;

    private HashMap<String, Integer> mPagePicMap;

    private HashMap<String, Integer> mPagePicMapEn;

    private InitFlavorBean mInitFlavorBean;


    private InitAppHelper() {
        initFlavorFromJson(); // 从json文件读取配置
        initPackageName(); // 初始化所有的包名
        initPageName(); // 初始化页面名称
        initPageTitle(); // 初始化页面标题名称
        initPagePic();
        initPagePicEn();
    }

    public static InitAppHelper getInstance() {
        if (instance == null) {
            instance = new InitAppHelper();
        }
        return instance;
    }

    public InitFlavorBean getInitFlavorBean() {
        return mInitFlavorBean;
    }

    /**
     * 根据配置文件判断是否需要显示向导。
     *
     * @return true 需要，false不需要。
     */
//    public boolean needShowGuideByCfg() {
//        return mInitFlavorBean.isShowGuide();
//    }

    /**
     * 根据id得到包名。
     *
     * @param id 插件id
     * @return 包名，只有插件才会有包名。
     */
    @Nullable
    public String getPackageNameById(int id) {
        return mPackageNameMap.get(String.valueOf(id));
    }

    /**
     * 根据id得到页面名称。(中文版)
     *
     * @param id 插件id
     * @return 页面名称
     */
    public String getCnPageNameById(int id) {
        return mPageNameMap.get(String.valueOf(id));
    }

    /**
     * 根据id得到title(中文版)
     *
     * @param id 插件id
     * @return title
     */
    public String getCnPageTitleById(int id) {
        return mPageTitleMap.get(String.valueOf(id));
    }

    public int getPagePicById(Context context,int id) {
        Integer picId;
        if(InkAppManager.isZh(context)) {
            picId = mPagePicMap.get(String.valueOf(id));
        } else {
            picId = mPagePicMapEn.get(String.valueOf(id));
        }
        return picId;
    }

    /**
     * 根据id得到国际化的插件title
     *
     * @param context 上下文
     * @param id      id
     * @return 国际化的title
     */
    public String getPageTitleById(Context context, int id) {
        switch (id) {
            case InkConstants.PAGE_ID_WECHAT:
                return context.getString(R.string.wechat);
            case InkConstants.PAGE_ID_TODAY:
                return context.getString(R.string.today);
            case InkConstants.PAGE_ID_TODO:
                return context.getString(R.string.todo);
            case InkConstants.PAGE_ID_KINDLE:
                return context.getString(R.string.kindle);
            case InkConstants.PAGE_ID_USER:
                return context.getString(R.string.direct_discover);
            case InkConstants.PAGE_ID_MUSIC:
                return context.getString(R.string.music);
            case InkConstants.PAGE_ID_CURRICULUM:
                return context.getString(R.string.curriculum);
            case InkConstants.PAGE_ID_STCARD:
                return context.getString(R.string.stcard);
            default:
                return "";
        }

    }

    /**
     * 判断页面是否可用，例如微信卸载以后不可用。插件没安装也不可用。
     *
     * @param id id
     * @return 可用返回true，反之返回false
     */
    public boolean isPageUseful(Context context, int id) {
        String packageName = mPackageNameMap.get(String.valueOf(id));
        if (TextUtils.isEmpty(packageName)) {
            // 没有注册过包名，说明可能不是三方插件。就是可用的。
            return true;
        }
        return CommonUtils.isPkgInstalled(context, packageName);
    }

    private void initFlavorFromJson() {
        mInitFlavorBean = InitFlavorBean.createDefaultBean();
    }

    private void initPackageName() {
        // 只需要添加插件
        mPackageNameMap = new HashMap<>();
        mPackageNameMap.put(String.valueOf(InkConstants.PAGE_ID_WECHAT), InkConstants.PACKAGE_NAME_WECHAT);
        mPackageNameMap.put(String.valueOf(InkConstants.PAGE_ID_KINDLE), InkConstants.PACKAGE_NAME_KINDLE);
    }

    private void initPageName() {
        mPageNameMap = new HashMap<>();
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_WECHAT), InkConstants.PAGE_NAME_WECHAT);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_TODAY), InkConstants.PAGE_NAME_TODAY);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_USER), InkConstants.PAGE_NAME_USER);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_TOOLS), InkConstants.PAGE_NAME_TOOLS);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_TODO), InkConstants.PAGE_NAME_TODO);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_CURRICULUM), InkConstants.PAGE_NAME_CURRICULUM);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_STCARD), InkConstants.PAGE_NAME_STCARD);
        mPageNameMap.put(String.valueOf(InkConstants.PAGE_ID_READS), InkConstants.PAGE_NAME_READS);
    }

    private void initPageTitle() {
        mPageTitleMap = new HashMap<>();
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_WECHAT), InkConstants.PAGE_TITLE_WECHAT);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_TODAY), InkConstants.PAGE_TITLE_TODAY);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_USER), InkConstants.PAGE_TITLE_USER);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_TOOLS), InkConstants.PAGE_TITLE_TOOLS);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_TODO), InkConstants.PAGE_TITLE_TODO);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_CURRICULUM), InkConstants.PAGE_TITLE_CURRICULUM);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_STCARD), InkConstants.PAGE_TITLE_STCARD);
        mPageTitleMap.put(String.valueOf(InkConstants.PAGE_ID_READS), InkConstants.PAGE_TITLE_READS);
    }

    private void initPagePic() {
        mPagePicMap = new HashMap<>();
        mPagePicMap.put(String.valueOf(InkConstants.PAGE_ID_WECHAT), R.drawable.edit_wechat);
        mPagePicMap.put(String.valueOf(InkConstants.PAGE_ID_KINDLE), R.drawable.edit_kindle);
        mPagePicMap.put(String.valueOf(InkConstants.PAGE_ID_MUSIC), R.drawable.edit_music);
        mPagePicMap.put(String.valueOf(InkConstants.PAGE_ID_USER), R.drawable.edit_discovery);
        mPagePicMap.put(String.valueOf(InkConstants.PAGE_ID_TODAY), R.drawable.edit_today);
    }

    private void initPagePicEn() {
        mPagePicMapEn = new HashMap<>();
        mPagePicMapEn.put(String.valueOf(InkConstants.PAGE_ID_WECHAT), R.drawable.edit_wechat_en);
        mPagePicMapEn.put(String.valueOf(InkConstants.PAGE_ID_KINDLE), R.drawable.edit_kindle);
        mPagePicMapEn.put(String.valueOf(InkConstants.PAGE_ID_MUSIC), R.drawable.edit_music_en);
        mPagePicMapEn.put(String.valueOf(InkConstants.PAGE_ID_USER), R.drawable.edit_discovery_en);
        mPagePicMapEn.put(String.valueOf(InkConstants.PAGE_ID_TODAY), R.drawable.edit_today_en);
    }
}

