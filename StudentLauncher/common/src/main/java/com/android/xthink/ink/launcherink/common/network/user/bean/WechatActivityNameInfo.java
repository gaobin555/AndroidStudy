/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.network.user.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/7/22
 */
public class WechatActivityNameInfo implements Parcelable {

    /**
     * 朋友圈	string
     * wanchi@X-Thinks.com,1.0, 2017/7/22
     */
    public String moments = "";
    /**
     * 收藏	string
     * wanchi@X-Thinks.com,1.0, 2017/7/22
     */
    public String store = "";
    /**
     * 订阅号	stringa
     * wanchi@X-Thinks.com,1.0, 2017/7/22
     */
    public String subscribe = "";

    public WechatActivityNameInfo(String moments, String store, String subscribe) {
        this.moments = moments;
        this.store = store;
        this.subscribe = subscribe;
    }

    public String getMoments() {
        return moments;
    }

    public void setMoments(String moments) {
        this.moments = moments;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public String toString() {
        return "WechatActivityNameInfo{" +
                "moments='" + moments + '\'' +
                ", store='" + store + '\'' +
                ", subscribe='" + subscribe + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.moments);
        dest.writeString(this.store);
        dest.writeString(this.subscribe);
    }

    protected WechatActivityNameInfo(Parcel in) {
        this.moments = in.readString();
        this.store = in.readString();
        this.subscribe = in.readString();
    }

    public static final Parcelable.Creator<WechatActivityNameInfo> CREATOR = new Parcelable.Creator<WechatActivityNameInfo>() {
        @Override
        public WechatActivityNameInfo createFromParcel(Parcel source) {
            return new WechatActivityNameInfo(source);
        }

        @Override
        public WechatActivityNameInfo[] newArray(int size) {
            return new WechatActivityNameInfo[size];
        }
    };
}

