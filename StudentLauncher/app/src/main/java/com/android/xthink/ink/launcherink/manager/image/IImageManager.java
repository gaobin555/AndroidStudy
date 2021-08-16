package com.android.xthink.ink.launcherink.manager.image;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by liyuyan on 2016/12/26.
 */

public interface IImageManager {
    void loadImage(String url, ImageView imageView, Context context);

    void loadImage(int res, ImageView imageView, Context context);

    void loadImage(int res, ImageView imageView, Context context, int width, int height);

    /**
     * 记载图片
     *
     * @param imgUrl     网络url
     * @param imageView  控件
     * @param context    上下文
     * @param loadingRes 加载过程中的过度img
     */
    void loadImage(String imgUrl, ImageView imageView, Context context, int loadingRes);
}
