package com.android.xthink.ink.launcherink.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by LuckyJayce on 2016/6/24.
 */
public class DisplayUtil {
    /**
     * 根据dip值转化成px值
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dipToPix(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param context （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context context
     * @return 屏幕宽度
     */
    public static int getScreenW(Context context) {
        DisplayMetrics dm;
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param Context Activity
     * @return 屏幕高度
     */
    public static int getScreenH(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static Bitmap drawableToBitmap(Context context, int id) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, id);
    }
}

