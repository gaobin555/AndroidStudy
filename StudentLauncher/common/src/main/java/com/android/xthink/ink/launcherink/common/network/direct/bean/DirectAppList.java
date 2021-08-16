/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.network.direct.bean;

import java.util.ArrayList;

/**
 * 直通app的列表
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/11/8
 */
public class DirectAppList {

    /**
     * array<object>
     * wanchi@coolpad.com,1.0, 2017/11/8
     */
    private ArrayList<DirectAppBean> list;

    /**
     * 当前页数	number
     * wanchi@coolpad.com,1.0, 2017/11/8
     */
    private int pNo;

    /**
     * 每页记录条数	number
     * wanchi@coolpad.com,1.0, 2017/11/8
     */
    private int pSize;

    /**
     * 总记录数	number
     * wanchi@coolpad.com,1.0, 2017/11/8
     */
    private int totalCount;

    /**
     * 服务器当前时间 long
     */
    private long updateTime;

    public ArrayList<DirectAppBean> getList() {
        return list;
    }

    public void setList(ArrayList<DirectAppBean> list) {
        this.list = list;
    }

    public int getpNo() {
        return pNo;
    }

    public void setpNo(int pNo) {
        this.pNo = pNo;
    }

    public int getpSize() {
        return pSize;
    }

    public void setpSize(int pSize) {
        this.pSize = pSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectAppList)) return false;

        DirectAppList that = (DirectAppList) o;

        if (pNo != that.pNo) return false;
        if (pSize != that.pSize) return false;
        if (totalCount != that.totalCount) return false;
        if (updateTime != that.updateTime) return false;
        return list != null ? list.equals(that.list) : that.list == null;
    }

    @Override
    public int hashCode() {
        int result = list != null ? list.hashCode() : 0;
        result = 31 * result + pNo;
        result = 31 * result + pSize;
        result = 31 * result + totalCount;
        result = 31 * result + (int) (updateTime ^ (updateTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DirectAppList{" +
                "list=" + list +
                ", pNo=" + pNo +
                ", pSize=" + pSize +
                ", totalCount=" + totalCount +
                ", updateTime=" + updateTime +
                '}';
    }

    public void add(DirectAppList other) {
        if (other == null) {
            return;
        }

        ArrayList<DirectAppBean> list = other.getList();
        if (list == null || list.isEmpty()) {
            return;
        }
        //this.list.addAll(list);
        this.list.addAll(list);
    }

}

