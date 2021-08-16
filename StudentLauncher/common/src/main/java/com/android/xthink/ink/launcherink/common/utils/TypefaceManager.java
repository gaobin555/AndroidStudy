package com.android.xthink.ink.launcherink.common.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by liyuyan on 2017/2/24.
 */

public class TypefaceManager {
    public static Typeface getPFDinTextCondPro(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/PFDinTextCondPro-Regular.ttf");
    }

    public static Typeface getPFDinTextPro(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/PFDinTextPro-Regular.otf");
    }

    public static Typeface getRobotoLight(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }
}
