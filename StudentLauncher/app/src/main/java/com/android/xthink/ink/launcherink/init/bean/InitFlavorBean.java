/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.init.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import java.util.ArrayList;

/**
 * 渠道信息
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/8/31
 */
public class InitFlavorBean implements Parcelable {
//    private boolean showGuide;
    private ArrayList<InitPageBean> pageList;

    /**
     * 是否需要显示向导
     */
//    public boolean isShowGuide() {
//        return showGuide;
//    }

//    public void setShowGuide(boolean showGuide) {
//        this.showGuide = showGuide;
//    }

    /**
     * 得到页面产生器的列表
     */
    public ArrayList<InitPageBean> getPageList() {
        return pageList;
    }

    public void setPageList(ArrayList<InitPageBean> pageList) {
        this.pageList = pageList;
    }

    @Override
    public String toString() {
        return "InitFlavorBean{" +
//                "showGuide=" + showGuide +
                ", pageList=" + pageList +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeByte(this.showGuide ? (byte) 1 : (byte) 0);
        dest.writeList(this.pageList);
    }

    public InitFlavorBean() {
    }

    protected InitFlavorBean(Parcel in) {
//        this.showGuide = in.readByte() != 0;
        this.pageList = new ArrayList<InitPageBean>();
        in.readList(this.pageList, InitPageBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<InitFlavorBean> CREATOR = new Parcelable.Creator<InitFlavorBean>() {
        @Override
        public InitFlavorBean createFromParcel(Parcel source) {
            return new InitFlavorBean(source);
        }

        @Override
        public InitFlavorBean[] newArray(int size) {
            return new InitFlavorBean[size];
        }
    };

    public static InitFlavorBean createDefaultBean() {
        InitFlavorBean initFlavorBean = new InitFlavorBean();
//        initFlavorBean.setShowGuide(true);

        ArrayList<InitPageBean> initPageBeanList = new ArrayList<>();
        // data按照顺序分别是：位置，是否选中，是否收保护(是否强制选中)，是否是默认home，是否是home，是否是可以编辑
        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_TODAY, "128+8*1, 1, 1, 1, 1, 0"));//今天
//        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_WECHAT, "128+8*3, 1, 0, 0, 0, 1"));//微信
//        initPageBeanList.add(new InitPageBean(true, InkConstants.PAGE_ID_KINDLE, "128+8*3, 1, 0, 0, 0, 1"));//Kindle
//        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_MUSIC, "128+8*4, 1, 1, 0, 0, 1"));//Music
        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_CURRICULUM, "128+8*2, 1, 1, 0, 0, 1"));//课程表
        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_STCARD, "128+8*3, 1, 1, 0, 0, 1"));//学生证
        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_TOOLS, "128+8*4, 1, 1, 0, 0, 1"));//下载应用
        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_USER, "128+8*5, 1, 1, 0, 0, 1"));//内置应用
        initPageBeanList.add(new InitPageBean(false, InkConstants.PAGE_ID_TODO, "128+8*6, 1, 1, 0, 0, 1"));//日程


        initFlavorBean.setPageList(initPageBeanList);
        return initFlavorBean;
    }
}

