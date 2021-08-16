package com.android.xthink.ink.launcherink.common.constants;

/**
 * 微信字符串
 * Created by wanchi on 2017/3/7.
 */
public class InkWeChatConstants {
    public final static String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    public final static String WECHAT_LAUNCHER_PACKAGE_NAME = "com.tencent.mm.ui.LauncherUI";

    /**
     * 朋友圈
     */
    public final static String[] WECHAT_FRIEND_PACKAGE_NAME_ARRAY =
            new String[]{"com.tencent.mm.plugin.sns.ui.En_424b8e16",
                    "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI"
            };

    /**
     * 订阅号
     */
    public final static String WECHAT_SUBSCRIBE_PACKAGE_NAME = "com.tencent.mm.ui.conversation.BizConversationUI";

    /**
     * 收藏
     */
    public final static String WECHAT_FAVORITE_PACKAGE_NAME = "com.tencent.mm.plugin.favorite.ui.FavoriteIndexUI";

    /**
     * 二维码
     */
    public final static String WECHAT_SELFQRCODE_PACKAGE_NAME = "com.tencent.mm.plugin.setting.ui.setting.SelfQRCodeUI";

//    订阅号：cmp=com.tencent.mm/.ui.conversation.BizConversationUI（adb可直接启动，但消息列表中有订阅号消息才会有内容）
//    二维码：cmp=com.tencent.mm/.plugin.setting.ui.setting.SelfQRCodeUI （adb可直接启动）
//    公众号：com.tencent.mm/.plugin.brandservice.ui.BrandServiceIndexUI（adb可直接启动）

    /**
     * 通知标题
     */
    public static final String EXTRA_KEY_TITLE = "android.title";
    /**
     * 通知内容
     */
    public static final String EXTRA_KEY_CONTENT = "android.text";
    /**
     * 消息人的头像
     */
    public static final String EXTRA_KEY_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_KEY_ICON = "android.icon";


    public static final String[] KEY_NEW_MSG_SIMPLE = new String[]{
            "你收到了一条消息", // 简体中文
            "你收到了一則訊息", // 繁体中文(台) WeChat：你收到了一則訊息。
            "你收到了一條訊息", // 繁体中文(港) WeChat：你收到了一條訊息。
            "You've received a message", // 英文 WeChat: You've received a message.
            "Вы получили сообщение" // 俄语 WeChat: Вы получили сообщение.
    };

    public static final String[] KEY_LOGGING_OUT_DIALOG = new String[]{
            "正在退出",// 简体中文
            "正在退出",// 繁体中文
            "正在退出",// 英文
            "正在退出",// 俄语
    };

}
