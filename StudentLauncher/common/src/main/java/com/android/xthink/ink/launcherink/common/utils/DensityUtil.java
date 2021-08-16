package com.android.xthink.ink.launcherink.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by panxuqin on 2017/1/22.
 * Description:
 */

public class DensityUtil {

    public static final int DEFAULT_COMPRESS_VALUE = 1;

    public DensityUtil() {
    }

    public static int dipToPx(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int) (var1 * var2 + 0.5F);
    }

    public static int pxToDip(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int) (var1 / var2 + 0.5F);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
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
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 按比例缩放bitmap
     *
     * @param activity  activity
     * @param res       图片资源
     * @param reqWidth  需要的宽度
     * @param reqHeight 需要的高度
     * @return 压缩过后的bitmap
     */
    public static Bitmap decodeBitmapFromResource(Activity activity, int res, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), res, options);
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(activity.getResources(), res, options);
    }

    /**
     * 按比例缩放bitmap
     *
     * @param activity   activity
     * @param res        图片资源
     * @param sampleSize 缩放倍数，必须是2的次方
     * @return 压缩过后的bitmap
     */
    public static Bitmap decodeBitmapFromResource(Activity activity, int res, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(activity.getResources(), res, options);
    }

    // 计算合适的采样率(当然这里还可以自己定义计算规则)，reqWidth为期望的图片大小，单位是px
    private static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        while ((halfWidth / inSampleSize) >= reqWidth && (halfHeight / inSampleSize) >= reqHeight) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
