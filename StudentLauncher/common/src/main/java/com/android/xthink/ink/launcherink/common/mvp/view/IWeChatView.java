package com.android.xthink.ink.launcherink.common.mvp.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * 微信界面实现接口
 * Created by wanchi on 2017/3/1.
 */

public interface IWeChatView extends IView {
    /**
     * 收到消息
     *
     * @param title       标题
     * @param content     内容
     * @param drawable    图片
     * @param time        时间
     * @param isSimpleMsg 是否是精简消息，消息详情被关闭了
     */
    void onReceiveNotification(String title, String content, @Nullable Drawable drawable, String time, boolean isSimpleMsg);

    /**
     * 移除所有的通知
     */
    void removeAllNotification();

    /**
     * 打开页面失败
     */
    void showOpenFailed(boolean isLaunch);
}
