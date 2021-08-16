/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog.provider;

/**
 * 职业
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/13
 */
public class EditTableData {
    public int id;
    public String name;

    public EditTableData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditTableData)) return false;

        EditTableData that = (EditTableData) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}

