/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.network.direct.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 直通app信息
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/7
 */
public class DirectAppBean implements Parcelable {

    /**
     * app描述	string
     * wanchi@coolpad.com,1.0, 2017/11/7
     */
    private String appDesc = "";

    /**
     * app名称	string
     * wanchi@coolpad.com,1.0, 2017/11/7
     */
    private String appName = "";

    /**
     * app包名	string
     * wanchi@coolpad.com,1.0, 2017/11/7
     */
    private String appPackage = "";

    /**
     * icon url
     */
    private String appIconUrl = "";

    /**
    /**
     * app对应id	number	此为数据库对应该条数据的id
     * wanchi@coolpad.com,1.0, 2017/11/7
     */
    private int id = -1;

    /**
     * 排序号	number
     * wanchi@coolpad.com,1.0, 2017/11/7
     */
    private int orderNo = 0;

    /**
     * 该app使用的次数，刚查询出来的值是对的，其他时候可能会有偏差，慎用。
     */
    private int userCount = 0;

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppIconUrl() {
        return appIconUrl;
    }

    /**
     * 根据语言获取appName
     *
     * @param language 语言。英语是en，中文是zh。
     * @return 多语言name
     */
    public String getAppName(String language) {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return "DirectAppBean{" +
                "appName='" + appName + '\'' +
                ", appPackage='" + appPackage + '\'' +
                ", orderNo=" + orderNo +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectAppBean)) return false;

        DirectAppBean that = (DirectAppBean) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appDesc);
        dest.writeString(this.appName);
        dest.writeString(this.appPackage);
        dest.writeInt(this.id);
        dest.writeInt(this.orderNo);
    }

    public DirectAppBean() {
    }

    protected DirectAppBean(Parcel in) {
        this.appDesc = in.readString();
        this.appName = in.readString();
        this.appPackage = in.readString();
        this.id = in.readInt();
        this.orderNo = in.readInt();
    }

    public static final Creator<DirectAppBean> CREATOR = new Creator<DirectAppBean>() {
        @Override
        public DirectAppBean createFromParcel(Parcel source) {
            return new DirectAppBean(source);
        }

        @Override
        public DirectAppBean[] newArray(int size) {
            return new DirectAppBean[size];
        }
    };

    public static final Comparator<DirectAppBean> COMPARATOR = new Comparator<DirectAppBean>() {
        @Override
        public int compare(DirectAppBean l, DirectAppBean r) {
            return l.getOrderNo() - r.getOrderNo();
        }
    };

}

