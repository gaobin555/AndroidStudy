package com.android.xthink.ink.launcherink.manager.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by liyuyan on 2016/12/26.
 */

public class GlideManagerImpl implements IImageManager {
    @Override
    public void loadImage(String imgUrl, ImageView imageView, Context context) {
        Glide.with(context).load(imgUrl).into(imageView);
    }

    @Override
    public void loadImage(int res, ImageView imageView, Context context) {
        Glide.with(context).load(res).into(imageView);
    }

    @Override
    public void loadImage(int res, ImageView imageView, Context context, int width, int height) {
        Glide.with(context).load(res).override(width, height).into(imageView);
    }

    @Override
    public void loadImage(String imgUrl, ImageView imageView, Context context, int loadingRes) {
        Glide.with(context).load(imgUrl).placeholder(loadingRes).into(imageView);
    }
}
