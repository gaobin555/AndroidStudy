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
import com.android.xthink.ink.launcherink.ui.dialog.provider.AreaDataProvider;

import java.util.List;

import static com.android.xthink.ink.launcherink.ui.dialog.provider.AreaDataProvider.AreaBean;

/**
 * 地区编辑
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/12
 */
public class AreaEditDialog extends BaseEditDialog {

    private final View mOkBtn;
    private final View mCancelBtn;
    private Context mContext;
    private final WheelView mProvinceView;
    private final WheelView mCityView;
    private AreaDataProvider mDataProvider;

    public AreaEditDialog(Context context) {
        super(context);
        mContext = context;
        mDataProvider = new AreaDataProvider(context);

        mDialog.setContentView(R.layout.dialog_edit_area);
        mProvinceView = (WheelView) mDialog.findViewById(R.id.dialog_edit_date_province);
        mCityView = (WheelView) mDialog.findViewById(R.id.dialog_edit_date_city);

        mOkBtn = mDialog.findViewById(R.id.dialog_common_bottom_ok_btn);
        mCancelBtn = mDialog.findViewById(R.id.dialog_common_bottom_cancel_btn);

        initWheelView(mCityView, false);
        initWheelView(mProvinceView, true);

        initData();

        addListener();
    }

    /**
     * 设置默认地区
     *
     * @param provinceId id
     * @param cityId     id
     */
    public void setDefaultArea(int provinceId, int cityId) {
        AreaBean province = mDataProvider.queryById(provinceId);
        AreaBean city = mDataProvider.queryById(cityId);

        if (province == null || city == null || mCityView == null || mProvinceView == null) {
            return;
        }
        mProvinceView.setSelectedItem(province);
        mCityView.setSelectedItem(city);

    }

    @Override
    public void show() {
        super.show();
    }

    private void initWheelView(WheelView wheelView, boolean cycle) {
        int colorSecond = mContext.getResources().getColor(R.color.second);
        int colorPrimary = mContext.getResources().getColor(R.color.primary);
        wheelView.setTextColor(colorSecond, colorPrimary);
        wheelView.setTextSize(SP_TEXT_SIZE_DEFAULT);
        wheelView.setCycleDisable(!cycle);

        WheelView.LineConfig lineConfig = new WheelView.LineConfig();
        lineConfig.setColor(Color.BLACK);
        wheelView.setLineConfig(lineConfig);
    }


    private void addListener() {
        mProvinceView.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, Object item) {
                if (!(item instanceof AreaBean)) {
                    return;
                }
                List<?> cities = mDataProvider.provideCity((AreaBean) item);
                if (!cities.isEmpty()) {
                    mCityView.setItems(cities, 0);
                }

            }
        });

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mOnEditResult != null) {
                    Object province = mProvinceView.getSelectedItem();
                    Object city = mCityView.getSelectedItem();
                    mOnEditResult.onResult(province, city);
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    private void initData() {
        List<?> provinceList = mDataProvider.provideProvince();
        Object selectData = provinceList.get(0);
        mProvinceView.setItems(provinceList, selectData);

        List<?> cities = mDataProvider.provideCity((AreaBean) selectData);
        if (!cities.isEmpty()) {
            mCityView.setItems(cities, 0);
        }
    }

    @Override
    protected void setUpdateMode(@NonNull View rootView) {

    }
}

