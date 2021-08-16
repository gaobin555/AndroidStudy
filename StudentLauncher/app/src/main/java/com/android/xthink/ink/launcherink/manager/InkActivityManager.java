package com.android.xthink.ink.launcherink.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

//import com.android.jv.ink.launcherink.ui.user.OptionFeedBackActivity;
//import com.android.jv.ink.launcherink.ui.user.WebViewActivity;
//
//import static com.android.jv.ink.launcherink.ui.user.WebViewActivity.INTENT_FLAG_STR;
//import static com.android.jv.ink.launcherink.ui.user.WebViewActivity.TYPE_TITLE;
//import static com.android.jv.ink.launcherink.ui.user.WebViewActivity.TYPE_URL;

/**
 * Created by liyuyan on 2016/12/23.
 */

public class InkActivityManager {

    /**
     * The {@code fragment} is added to the imageContainer view with mItemId {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static void startActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    public static void startActivityAndFinish(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startActivity(Activity activity, Class<?> cls, Bundle extras) {
        Intent intent = new Intent(activity, cls);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }

    public static void startActivityClearTask(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }


    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode, Bundle extras) {
        Intent intent = new Intent(activity, cls);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到web界面
     *
     * @param activity
     */
//    public static void jumpToWebActivity(Activity activity, String url, String title) {
//        Intent intent = getBaseIntent(activity, WebViewActivity.class);
//        intent.putExtra(INTENT_FLAG_STR + TYPE_URL, url).putExtra(INTENT_FLAG_STR + TYPE_TITLE, title);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        activity.startActivity(intent);
//        activity.overridePendingTransition(0, 0);
//    }

    /**
     * activity启动来源
     */
    public static final String INTENT_FLAG_VERIFY_SOURCE = "INTENT_FLAG_VERIFY_SOURCE";

    /**
     * 获取baseIntent
     *
     * @param activity 启动activity
     * @param clazz    将要打开的activity
     * @return intent
     */
    protected static Intent getBaseIntent(Activity activity, Class clazz) {
        return new Intent(activity, clazz).putExtra(INTENT_FLAG_VERIFY_SOURCE, true);
    }

    /**
     * 跳转到个人用户意见反馈界面
     *
     * @param activity
     */
    public static void jumpToOptionFeedBackActivity(Activity activity) {
//        Intent intent = getBaseIntent(activity, OptionFeedBackActivity.class);
//        activity.startActivity(intent);
//        activity.overridePendingTransition(0, 0);
    }
}
