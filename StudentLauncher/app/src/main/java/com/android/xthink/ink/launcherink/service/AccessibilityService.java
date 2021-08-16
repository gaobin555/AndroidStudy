/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.constants.InkWeChatConstants;
import com.android.xthink.ink.launcherink.common.mvp.model.others.WechatNameProvider;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.UiTestUtils;

import java.util.List;

import static java.lang.System.currentTimeMillis;


/**
 * 监听其他app举动的一个服务
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/5/15
 */
public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    private static final String TAG = "WechatAccessibilityServ";

    private static final String ACTION_NONE = "action_none";
    public static final String ACTION_OPEN_MOMENTS = "action_open_moments";
    public static final String ACTION_OPEN_SUBSCRIPTIONS = "action_open_subscriptions";
    public static final String ACTION_OPEN_FAVORITES = "action_open_favorites";
    public static final String ACTION_OPEN_QRCODE = "action_open_qrcode";
    public static final String ACTION_GENERATE_QRCODE = "action_generate_qrcode";

    public static final String ACTION_OPEN_KINDLE_READING_PAGE = "action_open_kindle_reading_page";

    private static final int VALUE_TIME_OUT = 1000 * 10; // action的超时时间。

    private static String sAction = ACTION_NONE;
    private static boolean sClearMsgAction = false;
    private static long sActionTime; // action有超时相应，如果一个操作，ACTION_TIME_OUT的时间还没有打开，则取消这次操作。

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        MyLog.i(TAG, "onServiceConnected");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MyLog.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        MyLog.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        MyLog.i(TAG, "onInterrupt");
    }

    /**
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            CharSequence packageName = event.getPackageName();
            UiTestUtils.tag("onAccessibilityEvent packageName = " + packageName);
            int eventType = event.getEventType();
            switch (eventType) {

                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    UiTestUtils.tag("TYPE_WINDOW_CONTENT_CHANGED sAction = " + sAction);
                    if (InkWeChatConstants.WECHAT_PACKAGE_NAME.equals(packageName)) {

                        // 这里监听内容变化，会检测的比较频繁。而且第一次打开微信也会调这里
                        // 用户清空微信消息的通知，没有超时
                        if (sClearMsgAction) {
                            if (isShowingViewPager()) {
                                MyLog.i(TAG, "onAccessibilityEvent: showing wechat,clear msg");
                                sClearMsgAction = false;
                            }
                        }

                        // 这里回调次数太多，导致卡顿，所以需要增加一个时间间隔，保证一定时间内只能调用一次。
                        if (!sAction.equals(ACTION_NONE) && !timeout()) {
                            long currentTime = System.currentTimeMillis();
                            if (isShowingViewPager()) {
                                UiTestUtils.tag("辅助服务检测到微信主页打开，并且包含二级页面命令：" + sAction);
                                sendBroadcast(sAction);
                                MyLog.i(TAG, "broadcast: send:" + sAction);
                                sAction = ACTION_NONE;
                                long end = System.currentTimeMillis();
                                MyLog.i(TAG, "isShowingViewPager: waste time:" + (end - currentTime));
                            }
                        }
                    }
                    if (InkConstants.PACKAGE_NAME_KINDLE.equals(packageName)) {
                        MyLog.i(TAG, "onAccessibilityEvent1: " + packageName + "");
                    }

                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    UiTestUtils.tag("TYPE_WINDOW_STATE_CHANGED sAction = " + sAction);
                    if (InkWeChatConstants.WECHAT_PACKAGE_NAME.equals(packageName)) {

                        // 这个回调调用很快，但是第一次打开微信不会走
//                        if (isShowingViewPager() || isShowingSecondPage(event)) {
                            if (!sAction.equals(ACTION_NONE) && !timeout()) {
                                UiTestUtils.tag("辅助服务检测到微信主页打开，并且包含二级页面命令：" + sAction);
                                sendBroadcast(sAction);
                                MyLog.i(TAG, "broadcast: send:" + sAction);
                            }
                            sAction = ACTION_NONE;
                            MyLog.i(TAG, "TYPE_WINDOW_STATE_CHANGED: openWechat!");
//                        }
                    }

                    // 打开kindle阅读页面的检测
                    if (ACTION_OPEN_KINDLE_READING_PAGE.equals(sAction)) {
                        MyLog.i(TAG, "kindle: 开始打开阅读页");
                        if (InkConstants.PACKAGE_NAME_KINDLE.equals(packageName)) {
                            // 打开了kindle页面，结束。
                            MyLog.i(TAG, "kindle: 打开成功，结束");
                            sAction = "";
                        } else if (this.getPackageName().equals(packageName)) {
                            // 没有打开成功
                            MyLog.i(TAG, "kindle: 打开阅读页失败。");
                            long currentTime = currentTimeMillis();
                            if (currentTime - sActionTime > 3 * 1000) {
                                // 超时了，结束。
                                MyLog.i(TAG, "kindle: 打开阅读页失败以后，因为超时而不打开主页");
                                sAction = "";
                            } else {
                                // 闪了一下，Launcher发生了一些变化，这时候打开主页。
                                MyLog.i(TAG, "kindle: 打开阅读页失败以后，打开主页");
                                sendBroadcast(ACTION_OPEN_KINDLE_READING_PAGE);
                                sAction = "";
                            }
                        }
                    }

                    break;
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    private boolean timeout() {
        long currentTime = currentTimeMillis();
        boolean timeout = currentTime - sActionTime > VALUE_TIME_OUT;
        MyLog.i(TAG, "用时：" + (currentTime - sActionTime));
        MyLog.i(TAG, "timeout: " + timeout);
        return timeout;
    }

    private void sendBroadcast(String action) {
        UiTestUtils.tag("微信辅助服务发送打开二级页面的广播");
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    /**
     * 通知作出的动作
     */
    public static void notifyActions(String action) {
        UiTestUtils.tag("微信辅助服务收到二级页面命令：" + action);
        sAction = action;
        sActionTime = currentTimeMillis();
    }

    public static void notifyClearMsgAction() {
        sClearMsgAction = true;
    }

    private boolean isShowingViewPager() {
        MyLog.i(TAG, "isShowingViewPager: is judging!");
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            int count = rootNode.getChildCount();
            for (int i = 0; i < count; i++) {
                AccessibilityNodeInfo info = rootNode.getChild(i);
                if (info.getClassName().toString().contains("ViewPager") &&
                        InkWeChatConstants.WECHAT_PACKAGE_NAME.equals(info.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否正在显示二级页面.朋友圈，订阅号，收藏
     */
    private boolean isShowingSecondPage(AccessibilityEvent event) {
        String className = event.getClassName().toString();

        WechatNameProvider provider = WechatNameProvider.getInstance(this);

        String mountsName = provider.fastAccessPackageName(WechatNameProvider.SP_PACKAGE_NAME_MOMENTS);
        String subscribe = provider.fastAccessPackageName(WechatNameProvider.SP_PACKAGE_NAME_SUBSCRIBE);
        String favorite = provider.fastAccessPackageName(WechatNameProvider.SP_PACKAGE_NAME_FAVORITE);
        return className.equals(mountsName) || className.equals(subscribe) || className.equals(favorite);
    }

    /**
     * private boolean isShowingQRCode() {
     * AccessibilityNodeInfo rootNode = getRootInActiveWindow();
     * if (rootNode != null) {
     * AccessibilityNodeInfo child = rootNode.getChild(0).getChild(3).getChild(2);
     * //二维码页判断条件
     * if (child.getClassName().toString().equals("android.widget.ImageView") && child.getPackageName().toString().equals("com.tencent.mm")) {
     * return true;
     * }
     * }
     * return false;
     * }
     */


    private boolean isLoggingOut(AccessibilityEvent event) {
        if (event != null) {
            List<CharSequence> textList = event.getText();
            for (CharSequence text : textList) {
                if (CommonUtils.containsKeys(text.toString(), InkWeChatConstants.KEY_LOGGING_OUT_DIALOG)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isShowingLoginView(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        return className.contains("Login") || className.contains("login");
    }

    /**
     * 通过ID获取控件，并进行模拟点击
     *
     * @param clickId
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void inputClick(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

}

