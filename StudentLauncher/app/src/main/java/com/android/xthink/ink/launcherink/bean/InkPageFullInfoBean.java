package com.android.xthink.ink.launcherink.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 数据库中页面表的所有信息
 *
 * @author renxu@coolpad.com
 * @version 1.0, 2017/6/7
 */
public class InkPageFullInfoBean extends InkPageInfoBean implements Parcelable {

    private String title;  //PagerConstants.ITEM_TITLE
    private boolean isSelected;      //PagerConstants.ITEM_IS_SELECTED
    private boolean isProtected; //PagerConstants.ITEM_IS_PROTECTED
    private boolean isEditable = true;  //PagerConstants.ITEM_IS_EDITABLE


    public InkPageFullInfoBean() {
        super();
    }


    public InkPageFullInfoBean(
            int itemId, String title, String name, int index,
            boolean isSelected, boolean isProtected, boolean isDefaultHomePage,
            boolean isHomePage, boolean isEditable) {
        setItemId(itemId);
        setIndex(index);
        setHomePage(isHomePage);
        setDefaultHomePage(isDefaultHomePage);
        setName(name);
        this.title = title;
        this.isSelected = isSelected;
        this.isProtected = isProtected;
        this.isEditable = isEditable;
    }


    protected InkPageFullInfoBean(Parcel in) {
        super(in);
        title = in.readString();
        isSelected = in.readByte() != 0;
        isProtected = in.readByte() != 0;
        isEditable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isProtected ? 1 : 0));
        dest.writeByte((byte) (isEditable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InkPageFullInfoBean> CREATOR = new Creator<InkPageFullInfoBean>() {
        @Override
        public InkPageFullInfoBean createFromParcel(Parcel in) {
            return new InkPageFullInfoBean(in);
        }

        @Override
        public InkPageFullInfoBean[] newArray(int size) {
            return new InkPageFullInfoBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    @Override
    public String toString() {
        return "InkPageFullInfoBean{" +
                super.toString() +
                "title='" + title + '\'' +
                ", isSelected=" + isSelected +
                ", isProtected=" + isProtected +
                ", isEditable=" + isEditable +
                '}';
    }

}
