package com.android.xthink.ink.launcherink.manager.image;

/**
 * Created by liyuyan on 2016/12/26.
 */

public class ImageManagerFactory {
    public static IImageManager getImageManager() {
        return new GlideManagerImpl();
    }
}
