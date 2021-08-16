package com.android.xthink.ink.launcherink.common.mvp.presenter;

import android.app.Activity;

/**
 * 微信页面的业务
 * Created by wanchi on 2017/3/1.
 */

public interface IWeChatPresenter extends IPresenter {

    /**
     * 打开指定的通知
     *
     * @param title 指定通知的标题
     */
    void openNotification(String title);

    /**
     * 打开微信首页
     */
    void openWechatLauncher();

    /**
     * 打开朋友圈
     */
    void openWeChatFriend();

    /**
     * 打开订阅号
     */
    void openWeChatSubScribe();

    /**
     * 打开收藏
     */
    void openWeChatFavorite();

    /**
     * 打开二维码
     */
    void openWeChatQrcode();

    /**
     * 如果处于未绑定状态，就重新绑定系统服务
     */
    void rebindIfNecessary(Activity mActivity);

    /**
     * 加载已有的微信消息
     */
    void loadWechatMessage();

    /**
     * 从服务器加载activity的名称。
     */
    void loadWechatActivityNameIfNeeded();

}
