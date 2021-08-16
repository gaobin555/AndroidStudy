package com.android.xthink.ink.launcherink.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 页面信息
 *
 * @author luoyongjie
 * @version 1.0, 4/18/2017
 */
public class InkPageInfoBean implements Parcelable {
    private int itemId;/*item id 服务器定义*/
    private int index;/*item's index*/
    private boolean isHomePage;/*item is HomePage*/
    private String name;/*item's mName*/
    private boolean isDefaultHomePage;

    public InkPageInfoBean() {
       super();
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isHomePage() {
        return isHomePage;
    }

    public void setHomePage(boolean homePage) {
        isHomePage = homePage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultHomePage() {
        return isDefaultHomePage;
    }

    public void setDefaultHomePage(boolean defaultHomePage) {
        isDefaultHomePage = defaultHomePage;
    }

    @Override
    public String toString() {
        return "InkPageInfoBean{" +
                "itemId=" + itemId +
                ", index=" + index +
                ", isHomePage=" + isHomePage +
                ", name='" + name + '\'' +
                ", isDefaultHomePage=" + isDefaultHomePage +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemId);
        dest.writeInt(index);
        dest.writeByte((byte) (isHomePage ? 1 : 0));
        dest.writeString(name);
        dest.writeByte((byte) (isDefaultHomePage ? 1 : 0));
    }

    protected InkPageInfoBean(Parcel in) {
        itemId = in.readInt();
        index = in.readInt();
        isHomePage = in.readByte() != 0;
        name = in.readString();
        isDefaultHomePage = in.readByte() != 0;
    }

    public static final Creator<InkPageInfoBean> CREATOR = new Creator<InkPageInfoBean>() {
        @Override
        public InkPageInfoBean createFromParcel(Parcel in) {
            return new InkPageInfoBean(in);
        }

        @Override
        public InkPageInfoBean[] newArray(int size) {
            return new InkPageInfoBean[size];
        }
    };
}
