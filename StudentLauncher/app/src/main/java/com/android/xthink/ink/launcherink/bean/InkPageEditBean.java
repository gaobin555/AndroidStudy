package com.android.xthink.ink.launcherink.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 编辑页的数据Bean
 *
 * @author luoyongjie
 * @version 1.0, 4/18/2017
 */
public class InkPageEditBean extends InkPageInfoBean implements Parcelable {
    private int picId;           //item picture resource
    private boolean isShow;      //item is checked (show) 对应数据库的selected属性
    private boolean isProtected; //item must be shown
    private boolean isUseful = true;    //some pages must dismiss, because App maybe uninstall
    private boolean isSelectedStatusChanged = false;
    private String title;
    private boolean isEditable = true;  //item is displayed in edit page

    public InkPageEditBean() {
        super();
    }

    protected InkPageEditBean(Parcel in) {
        super(in);
        picId = in.readInt();
        isShow = in.readByte() != 0;
        isProtected = in.readByte() != 0;
        isUseful = in.readByte() != 0;
        isSelectedStatusChanged = in.readByte() != 0;
        title = in.readString();
        isEditable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(picId);
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeByte((byte) (isProtected ? 1 : 0));
        dest.writeByte((byte) (isUseful ? 1 : 0));
        dest.writeByte((byte) (isSelectedStatusChanged ? 1 : 0));
        dest.writeString(title);
        dest.writeByte((byte) (isEditable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InkPageEditBean> CREATOR = new Creator<InkPageEditBean>() {
        @Override
        public InkPageEditBean createFromParcel(Parcel in) {
            return new InkPageEditBean(in);
        }

        @Override
        public InkPageEditBean[] newArray(int size) {
            return new InkPageEditBean[size];
        }
    };

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    /**
     * item is checked (show) 对应数据库的selected属性
     *
     * @return 已经被选中返回true，反之返回false.
     */
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public boolean isUseful() {
        return isUseful;
    }

    public void setUseful(boolean useful) {
        isUseful = useful;
    }

    public boolean isSelectedStatusChanged() {
        return isSelectedStatusChanged;
    }

    public void setSelectedStatusChanged(boolean selectedStatusChanged) {
        isSelectedStatusChanged = selectedStatusChanged;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    @Override
    public String toString() {
        super.toString();
        return "InkPageEditBean{" +
                "picId=" + picId +
                ", isShow=" + isShow +
                ", isProtected=" + isProtected +
                ", isUseful=" + isUseful +
                ", isSelectedStatusChanged=" + isSelectedStatusChanged +
                ", title='" + title + '\'' +
                ", isEditable=" + isEditable +
                '}';
    }
}
