/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 插件信息
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/5/10
 */
public class PluginInfoBean implements Parcelable {
    /**
     * 布局名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    private String layoutName = "";

    /**
     * 包名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    private String packageName = "";

    /**
     * 插件名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    private String name = "";

    /**
     * 插件id,同pageId
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    private int id = -1;

    public PluginInfoBean(String layoutName, String packageName, String name, int id) {
        this.layoutName = layoutName;
        this.packageName = packageName;
        this.name = name;
        this.id = id;
    }

    /**
     * 布局名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public String getLayoutName() {
        return layoutName;
    }

    /**
     * 布局名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    /**
     * 包名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 包名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * 插件名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public String getName() {
        return name;
    }

    /**
     * 插件名称
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 插件id,同pageId
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public int getId() {
        return id;
    }

    /**
     * 插件id,同pageId
     * wanchi@coolpad.com,1.0, 2017/5/10
     */
    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.layoutName);
        dest.writeString(this.packageName);
        dest.writeString(this.name);
        dest.writeInt(this.id);
    }

    protected PluginInfoBean(Parcel in) {
        this.layoutName = in.readString();
        this.packageName = in.readString();
        this.name = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<PluginInfoBean> CREATOR = new Parcelable.Creator<PluginInfoBean>() {
        @Override
        public PluginInfoBean createFromParcel(Parcel source) {
            return new PluginInfoBean(source);
        }

        @Override
        public PluginInfoBean[] newArray(int size) {
            return new PluginInfoBean[size];
        }
    };

    @Override
    public String toString() {
        return "PluginInfoBean{" +
                "layoutName='" + layoutName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}

