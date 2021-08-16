/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.mvp.model.others;

import android.content.Context;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.common.BuildConfig;
import com.android.xthink.ink.launcherink.common.network.user.bean.WechatActivityNameInfo;
import com.android.xthink.ink.launcherink.common.utils.SharePreferenceHelper;

/**
 * 提供微信类名
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/7/22
 */
public class WechatNameProvider {

    private static final String KEY_HAS_INIT = "has_init_" + BuildConfig.VERSION_CODE;

    // 直接传入类名 by gaob  20190129
    public final static String SP_PACKAGE_NAME_MOMENTS = "sp_package_name_moments";
    public final static String SP_PACKAGE_NAME_SUBSCRIBE = "sp_package_name_subscribe";
    public final static String SP_PACKAGE_NAME_FAVORITE = "sp_package_name_favorite";
    public final static String SP_PACKAGE_NAME_QR_CODE = "sp_package_name_qr_code";

    public final static String SP_ACTION_NAME_MOMENTS = "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI";//"sp_package_name_moments";
    public final static String SP_ACTION_NAME_SUBSCRIBE = "com.tencent.mm.ui.conversation.BizConversationUI";//"sp_package_name_subscribe";
    public final static String SP_ACTION_NAME_FAVORITE = "com.tencent.mm.plugin.fav.ui.FavoriteIndexUI";//"sp_package_name_favorite";

    private final static String SP_ALL_PACKAGE_NAME = "sp_all_package_name";

    private static WechatNameProvider instance;
    private SharePreferenceHelper mSpHelper;

    private String mMountsName = "";
    private String mSubscribeName = "";
    private String mFavoriteName = "";

    private WechatNameProvider(Context context) {
        mSpHelper = SharePreferenceHelper.getInstance(context);
        initData();

    }

    public static WechatNameProvider getInstance(Context context) {
        if (instance == null) {
            instance = new WechatNameProvider(context);
        }
        return instance;
    }

    private void initData() {
        boolean hasInit = mSpHelper.getBooleanValue(KEY_HAS_INIT, false);
        if (!hasInit) {

            // 1080-1020:En_424b8e16。1020以下：SnsTimeLineUI

            // 朋友圈activity类名
            mSpHelper.setStringValue(SP_PACKAGE_NAME_MOMENTS + "1019", "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI");
            mSpHelper.setStringValue(SP_PACKAGE_NAME_MOMENTS + "1020", "com.tencent.mm.plugin.sns.ui.En_424b8e16");

            // 订阅号activity类名
            mSpHelper.setStringValue(SP_PACKAGE_NAME_SUBSCRIBE + "1019", "com.tencent.mm.ui.conversation.BizConversationUI");
            mSpHelper.setStringValue(SP_PACKAGE_NAME_SUBSCRIBE + "1020", "com.tencent.mm.ui.conversation.BizConversationUI");

            // 收藏activity类名
            mSpHelper.setStringValue(SP_PACKAGE_NAME_FAVORITE + "1019", "com.tencent.mm.plugin.favorite.ui.FavoriteIndexUI");
            mSpHelper.setStringValue(SP_PACKAGE_NAME_FAVORITE + "1020", "com.tencent.mm.plugin.favorite.ui.FavoriteIndexUI");

            mSpHelper.setBooleanValue(KEY_HAS_INIT, true);
        }
    }

    /**
     * 微信版本号变换，因为微信版本太多，全部都存会影响效率，所以只存一部分关键节点的版本。然后将其他版本号转换到这些关键节点的版本。
     *
     * @param code 待转换的版本号
     * @return 关键节点的版本号
     */
    private int convertCode(int code) {
        // 目前微信版本，1080是一个分界线，从1080开始，朋友圈的类名修改了。
        // 1080-1020:En_424b8e16。1020以下：SnsTimeLineUI；1080是当前最新版本
        if (code < 1020) {
            return 1019;
        } else {
            return 1020;
        }
    }

    public String getName(String key, int code) {
        String name = mSpHelper.getStringValue(key + code, "");
        if (TextUtils.isEmpty(name)) {
            // 智能转换code,因为很多版本类名相同，不必每个版本都存一次，这样会降低效率
            int convertCode = convertCode(code);
            name = mSpHelper.getStringValue(key + convertCode, "");
        }
        return name;
    }

    public void saveName(String key, String code, String name) {
        mSpHelper.setStringValue(key + code, name);
        saveFastAccessPackageName(key, name);
    }

    public void saveName(WechatActivityNameInfo wechatActivityNameInfo, String code) {
        mSpHelper.setStringValue(SP_PACKAGE_NAME_MOMENTS + code, wechatActivityNameInfo.getMoments());
        mSpHelper.setStringValue(SP_PACKAGE_NAME_SUBSCRIBE + code, wechatActivityNameInfo.getSubscribe());
        mSpHelper.setStringValue(SP_PACKAGE_NAME_FAVORITE + code, wechatActivityNameInfo.getStore());

        saveFastAccessPackageName(SP_PACKAGE_NAME_MOMENTS, wechatActivityNameInfo.getMoments());
        saveFastAccessPackageName(SP_PACKAGE_NAME_SUBSCRIBE, wechatActivityNameInfo.getSubscribe());
        saveFastAccessPackageName(SP_PACKAGE_NAME_FAVORITE, wechatActivityNameInfo.getStore());
    }

    /**
     * 建立一个快速访问通道，无关版本，无关文件读取。快速内存访问。
     */
    public void saveFastAccessPackageName(String key, String name) {
        if (SP_PACKAGE_NAME_FAVORITE.equals(key)) {
            mFavoriteName = name;
        } else if (SP_PACKAGE_NAME_MOMENTS.equals(key)) {
            mMountsName = name;
        } else if (SP_PACKAGE_NAME_SUBSCRIBE.equals(key)) {
            mSubscribeName = name;
        }
    }

    /**
     * 快速访问通道，无关版本，无关文件读取。快速内存访问。用于对性能非常高，而对准确性没那么高的场景。如辅助服务。
     *
     * @param key {@link #SP_PACKAGE_NAME_FAVORITE} etc.
     */
    public String fastAccessPackageName(String key) {
        if (SP_PACKAGE_NAME_FAVORITE.equals(key)) {
            return mFavoriteName;
        } else if (SP_PACKAGE_NAME_MOMENTS.equals(key)) {
            return mMountsName;
        } else if (SP_PACKAGE_NAME_SUBSCRIBE.equals(key)) {
            return mSubscribeName;
        }
        return "";
    }
}

