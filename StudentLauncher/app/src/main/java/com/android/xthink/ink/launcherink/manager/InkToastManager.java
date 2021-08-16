package com.android.xthink.ink.launcherink.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xthink.ink.launcherink.R;

/**
 * 经过了统一处理的Toast，使用了
 * Created by liyuyan on 2016/12/23.
 */

public class InkToastManager {
    private static Toast mToast;

    public InkToastManager() {
    }

    /**
     * 非阻塞试显示Toast,防止出现连续点击Toast时的显示问题
     */
    public static void showToast(Context context, CharSequence text, int duration) {
//        Context mEpdContext = EpdUtils.getEpdContext(context);//获取B屏的Context
        Context mEpdContext = context;//获取B屏的Context
        View toastRoot = LayoutInflater.from(mEpdContext).inflate(R.layout.toast_white, null);
        TextView tv = (TextView) toastRoot.findViewById(R.id.toast_notice);
        tv.setText(text);
        if (mToast == null) {
            mToast = new Toast(mEpdContext);
        }
        mToast.setDuration(duration);
        mToast.setView(toastRoot);
        mToast.show();
    }

    /**
     * 设置短时间toast
     */
    public static void showToastLong(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    /**
     * 设置短时间toast
     * 设置里面有个bug,YY3-5209,因为设置这个页面是白底，所以可以用setUpdateModeHighQuality的方式
     */
    public static void showToastLong(Context context, int resId) {
        CharSequence text = context.getText(resId);
        showToastLong(context, text);
    }

    /**
     * 设置短时间toast
     */
    public static void showToastShort(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * 设置短时间toast
     * 设置里面有个bug,YY3-5209,因为设置这个页面是白底，所以可以用setUpdateModeHighQuality的方式
     */
    public static void showToastShort(Context context, int resId) {
        CharSequence text = context.getText(resId);
        showToastShort(context, text);
    }

}
