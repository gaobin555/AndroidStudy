/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.init.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.xthink.ink.launcherink.manager.datasave.pager.PagerConstants;

/**
 * 初始化单独一页的数据
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/8/31
 */
public class InitPageBean implements Parcelable {
    private boolean isPlugin;
    private int id;

    /**
     * 数据库产生的生成器
     * e.g: "128+8*3, 1, 0, 0, 0, 1"
     * 按照顺序分别是：位置，是否选中，是否收保护(是否强制选中)，是否是默认home，是否是home，是否是可以编辑
     * {@link PagerConstants#ITEM_INDEX}
     * {@link PagerConstants#ITEM_IS_SELECTED}
     * {@link PagerConstants#ITEM_IS_PROTECTED}
     * {@link PagerConstants#ITEM_IS_DEFAULT_HOME_PAGE}
     * {@link PagerConstants#ITEM_IS_HOME_PAGE}
     * {@link PagerConstants#ITEM_IS_EDITABLE}
     */
    private String dataCreator;

    public InitPageBean(boolean isPlugin, int id, String dataCreator) {
        this.isPlugin = isPlugin;
        this.id = id;
        this.dataCreator = dataCreator;
    }

    /**
     * 是否为插件
     */
    public boolean isPlugin() {
        return isPlugin;
    }

    public void setPlugin(boolean plugin) {
        isPlugin = plugin;
    }

    /**
     * 数据库产生的生成器
     * e.g: "128+8*3, 1, 0, 0, 0, 1"
     * 按照顺序分别是：位置，是否选中，是否收保护(是否强制选中)，是否是默认home，是否是home，是否是可以编辑
     * {@link PagerConstants#ITEM_INDEX}
     * {@link PagerConstants#ITEM_IS_SELECTED}
     * {@link PagerConstants#ITEM_IS_PROTECTED}
     * {@link PagerConstants#ITEM_IS_DEFAULT_HOME_PAGE}
     * {@link PagerConstants#ITEM_IS_HOME_PAGE}
     * {@link PagerConstants#ITEM_IS_EDITABLE}
     */
    public String getDataCreator() {
        return dataCreator;
    }

    /**
     * 数据库产生的生成器
     * e.g: "128+8*3, 1, 0, 0, 0, 1"
     * 按照顺序分别是：位置，是否选中，是否收保护(是否强制选中)，是否是默认home，是否是home，是否是可以编辑
     * {@link PagerConstants#ITEM_INDEX}
     * {@link PagerConstants#ITEM_IS_SELECTED}
     * {@link PagerConstants#ITEM_IS_PROTECTED}
     * {@link PagerConstants#ITEM_IS_DEFAULT_HOME_PAGE}
     * {@link PagerConstants#ITEM_IS_HOME_PAGE}
     * {@link PagerConstants#ITEM_IS_EDITABLE}
     */
    public void setDataCreator(String dataCreator) {
        this.dataCreator = dataCreator;
    }

    /**
     * 页面的ID
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "InitPageBean{" +
                "isPlugin=" + isPlugin +
                ", id=" + id +
                ", dataCreator='" + dataCreator + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isPlugin ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeString(this.dataCreator);
    }

    public InitPageBean() {
    }

    protected InitPageBean(Parcel in) {
        this.isPlugin = in.readByte() != 0;
        this.id = in.readInt();
        this.dataCreator = in.readString();
    }

    public static final Parcelable.Creator<InitPageBean> CREATOR = new Parcelable.Creator<InitPageBean>() {
        @Override
        public InitPageBean createFromParcel(Parcel source) {
            return new InitPageBean(source);
        }

        @Override
        public InitPageBean[] newArray(int size) {
            return new InitPageBean[size];
        }
    };
}

