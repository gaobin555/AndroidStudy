/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.view.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日期选择器
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/12
 */
public class DateEditDialog extends BaseEditDialog implements View.OnClickListener {

    private Context mContext;
    private final static int BEGIN_YEAR = 1940;
    private final static String DEFAULT_SELECT_YEAR = "1990";
    private final WheelView mYearWheelView;
    private final WheelView mMonthWheelView;
    private final List<Object> mYears;
    private final List<Object> mMonths;

    public DateEditDialog(Context context) {
        super(context);
        mContext = context;
        mDialog.setContentView(R.layout.dialog_edit_date);

        mYearWheelView = (WheelView) mDialog.findViewById(R.id.dialog_edit_date_year);
        mMonthWheelView = (WheelView) mDialog.findViewById(R.id.dialog_edit_date_month);

        View okBtn = mDialog.findViewById(R.id.dialog_common_bottom_ok_btn);
        View cancelBtn = mDialog.findViewById(R.id.dialog_common_bottom_cancel_btn);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mYears = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        for (int i = BEGIN_YEAR; i <= thisYear; i++) {
            mYears.add(String.valueOf(i));
        }
        initWheelView(mYearWheelView, mYears, DEFAULT_SELECT_YEAR);

        mMonths = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            mMonths.add(String.valueOf(i + 1));
        }
        initWheelView(mMonthWheelView, mMonths, mMonths.get(0));
    }

    /**
     * 设置默认的年月
     *
     * @param year  年
     * @param month 月
     */
    public void setDefaultDate(String year, String month) {
        if (mYears.contains(year)) {
            mYearWheelView.setSelectedItem(year);
        }
        if (mMonths.contains(month)) {
            mMonthWheelView.setSelectedItem(month);
        }
    }

    private void initWheelView(WheelView wheelView, List<Object> dataList, Object selectData) {
        int colorSecond = mContext.getResources().getColor(R.color.second);
        int colorPrimary = mContext.getResources().getColor(R.color.primary);
        wheelView.setTextColor(colorSecond, colorPrimary);
        wheelView.setTextSize(SP_TEXT_SIZE_DEFAULT);

        WheelView.LineConfig lineConfig = new WheelView.LineConfig();
        lineConfig.setColor(Color.BLACK);
        wheelView.setLineConfig(lineConfig);

        wheelView.setItems(dataList, selectData);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_common_bottom_ok_btn) {
            // ok
            mDialog.dismiss();
            if (mOnEditResult != null) {
                Object year = mYearWheelView.getSelectedItem();
                Object month = mMonthWheelView.getSelectedItem();
                mOnEditResult.onResult(year, month);
            }
        } else if (id == R.id.dialog_common_bottom_cancel_btn) {
            // cancel
            mDialog.dismiss();
        }
    }

    @Override
    protected void setUpdateMode(@NonNull View rootView) {

    }
}

