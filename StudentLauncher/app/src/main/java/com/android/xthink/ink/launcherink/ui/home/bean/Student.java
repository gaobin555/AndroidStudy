package com.android.xthink.ink.launcherink.ui.home.bean;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.xthink.ink.launcherink.R;

public class Student {
    public String s_birthday;
    public String s_name;
    public String s_school;
    public String s_class;
    public String s_addr;
    public String url_photo;
    public Bitmap bitmap_photo;

    public Student(Context context) {
        s_birthday = null;
        s_name = context.getString(R.string.user_null);
        s_school = context.getString(R.string.user_null);
        s_class = context.getString(R.string.user_null);
        s_addr = context.getString(R.string.user_null);
        url_photo = null;
        bitmap_photo = null;
    }
}
