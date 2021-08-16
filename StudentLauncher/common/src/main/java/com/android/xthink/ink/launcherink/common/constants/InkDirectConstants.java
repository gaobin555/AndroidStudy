/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.constants;

//import android.text.TextUtils;

//import com.coolyota.analysis.tools.SystemProperties;

//import static com.android.jv.ink.launcherink.common.constants.InkConstants.PERSIST_SYS_TEST_SERVER;

/**
 * 直通app
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/8
 */
public class InkDirectConstants {

    public static final String SP_DIRECT_NAME = "sp_direct_app";
    public static final String SP_KEY_HAS_UPGRADE_DIRECT_APP = "sp_has_upgrade_direct_app";
    public static final String SP_DIRECT_ANALYSIS = "sp_direct_analysis";
    public static final String SP_HAS_ANALYSIS_OLD_DATA = "sp_has_analysis_old_data";

    private static String sUrl = "http://test.msms.baoliyota.com/yota-msms";

    /**
     * yota服务器IP地址
     * wanchi@coolpad.com,1.0, 2017/3/20
     */
    public static String getDirectBaseUrl() {
//        if (TextUtils.isEmpty(sUrl)) {
//            String boolStr = SystemProperties.get(PERSIST_SYS_TEST_SERVER, "false");
//            boolean isDebug = Boolean.parseBoolean(boolStr);
//            if (isDebug) {
//                sUrl = "http://test.msms.baoliyota.com/yota-msms";
//            } else {
//                sUrl = "http://msms.baoliyota.com/yota-msms";
//            }
//        }
        return sUrl;
    }

    /**
     * 直通app的id，与服务器的app id一致。
     */
    public static final String ITEM_DIRECT_APP_ID = "item_direct_app_id";

    /**
     * 使用频率
     */
    public static final String ITEM_DIRECT_APP_USE_TIMES = "item_direct_app_use_times";

    /**
     * 使用频率
     */
    public static final String ITEM_DIRECT_APP_DEFAULT_INDEX = "item_direct_app_default_index";

    /**
     * 是否有更新
     */
    public static final String ITEM_DIRECT_APP_NEW = "item_direct_app_new";

    /**
     * 直通app的数据
     */
    public static final String ITEM_DIRECT_DATA = "item_direct_data";

    /**
     * 直通app状态正常，相对于被删除的状态。
     */
    public static final int ITEM_DIRECT_STATUS_NORMAL = 0;

    /**
     * 直通app被删除了
     */
    public static final int ITEM_DIRECT_STATUS_DELETED = 1;

    /**
     * 直通app是新的
     */
    public static final int ITEM_CONST_IS_NEW = 1;

    /**
     * 直通app不是新的
     */
    public static final int ITEM_CONST_IS_NOT_NEW = 0;

    /**
     * 直通app获取地址
     *
     * @return 地址
     */
    public static String getDirectListPath() {
        return getDirectBaseUrl() + "/app/info/getDirectList";
    }

    /**
     * 直通反馈地址
     *
     * @return 反馈地址
     */
    public static String getDirectFeedbackPath() {
        return getDirectBaseUrl() + "/rest/feedBack/add";
    }

    /**
     * 初始的直通数据
     */
    public static final String DEFAULT_JSON = "{\n" +
            "\t\"list\": [{\n" +
            "\t\t\t\"appDesc\": \"通讯录\",\n" +
            "\t\t\t\"appName\": \"通讯录\",\n" +
            "\t\t\t\"appPackage\": \"com.android.contacts\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 51,\n" +
            "\t\t\t\"info\": [{\n" +
            "\t\t\t\t\"appDesc\": \"通讯录\",\n" +
            "\t\t\t\t\"appName\": \"通讯录\",\n" +
            "\t\t\t\t\"language\": \"zh\"\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"keyWord\": \"通讯录\",\n" +
            "\t\t\t\"orderNo\": 1,\n" +
            "\t\t\t\"url\": \"123\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"浏览器\",\n" +
            "\t\t\t\"appName\": \"浏览器\",\n" +
            "\t\t\t\"appPackage\": \"com.android.browser\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 6,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 2,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"相机\",\n" +
            "\t\t\t\"appName\": \"相机\",\n" +
            "\t\t\t\"appPackage\": \"com.mediatek.camera\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 12,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 3,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"图库\",\n" +
            "\t\t\t\"appName\": \"图库\",\n" +
            "\t\t\t\"appPackage\": \"com.android.gallery3d\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 9,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 4,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"时钟\",\n" +
            "\t\t\t\"appName\": \"时钟\",\n" +
            "\t\t\t\"appPackage\": \"com.android.deskclock\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 15,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 5,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"日历\",\n" +
            "\t\t\t\"appName\": \"日历\",\n" +
            "\t\t\t\"appPackage\": \"com.android.calendar\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 21,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 6,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"录音机\",\n" +
            "\t\t\t\"appName\": \"录音机\",\n" +
            "\t\t\t\"appPackage\": \"com.android.soundrecorder\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 20,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 7,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"收音机\",\n" +
            "\t\t\t\"appName\": \"收音机\",\n" +
            "\t\t\t\"appPackage\": \"com.android.fmradio\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 17,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 8,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"文件管理\",\n" +
            "\t\t\t\"appName\": \"文件管理\",\n" +
            "\t\t\t\"appPackage\": \"com.mediatek.filemanager\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 19,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 9,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"Kindle\",\n" +
            "\t\t\t\"appName\": \"Kindle\",\n" +
            "\t\t\t\"appPackage\": \"com.amazon.kindle\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 18,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 10,\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"appDesc\": \"设置\",\n" +
            "\t\t\t\"appName\": \"设置\",\n" +
            "\t\t\t\"appPackage\": \"com.android.settings\",\n" +
            "\t\t\t\"compatibility\": 1,\n" +
            "\t\t\t\"id\": 14,\n" +
            "\t\t\t\"info\": [\n" +
            "\n" +
            "\t\t\t],\n" +
            "\t\t\t\"orderNo\": 11,\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"pNo\": 1,\n" +
            "\t\"pSize\": 11,\n" +
            "\t\"totalCount\": 21,\n" +
            "}";
}

