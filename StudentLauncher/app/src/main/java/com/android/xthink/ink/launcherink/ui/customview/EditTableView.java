/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.manager.InkAppManager;
import com.android.xthink.ink.launcherink.ui.dialog.provider.EditTableData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 编辑表格页面
 *
 * @author wanchi@coolpad.com
 * @version 1.0, 2017/4/13
 */
public class EditTableView extends LinearLayout {

    private LayoutInflater mInflater;

    private List<TextView> mCellViews;
    private List<EditTableData> mDataList;

    private Set<Integer> mSelectedList = new HashSet<>();

    private int mColumn;
    private int mRow;
    private boolean multiSelect;
    private Context mContext;

    public EditTableView(Context context) {
        this(context, null);
    }

    public EditTableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }


    /**
     * 设置所有的数据
     *
     * @param dataList 数量列表
     * @param row      行
     * @param column   宽
     */
    public void setTableData(List<EditTableData> dataList, int row, int column) {

        mDataList = new ArrayList<>();
        mDataList.addAll(dataList);
        mCellViews = new ArrayList<>();
        mRow = row;
        mColumn = column;

        int dataCount = row * column;
        if (dataList.size() < dataCount) {
            return;
        }

        // 生成表格View
        removeAllViews();
        for (int i = 0; i < row; i++) {
            ViewGroup horizontalContainer = (ViewGroup) mInflater.inflate(R.layout.item_edit_horizontal_container, this, false);
            for (int j = 0; j < column; j++) {
                int index = column * i + j;
                TextView cellView = getItem(horizontalContainer, index);
                mCellViews.add(index, cellView);
                horizontalContainer.addView(cellView);
            }
            this.addView(horizontalContainer);
        }
    }


    /**
     * 设置是否能多选
     */
    public void setMultiSelect(boolean b) {
        multiSelect = b;
    }

    public void setDefaultData(@Nullable List<EditTableData> defaultDataList) {
        if (mCellViews == null || mDataList == null || defaultDataList == null || defaultDataList.size() == 0) {
            return;
        }
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mColumn; j++) {
                int index = mColumn * i + j;
                if (defaultDataList.contains(mDataList.get(index))) {
                    mCellViews.get(index).setSelected(true);
                    mSelectedList.add(index);
                    setBlackDot(mCellViews.get(index));
                } else {
                    mCellViews.get(index).setSelected(false);
                    setWhiteDot(mCellViews.get(index));
                }
            }
        }
    }

    /**
     * 得到当前被选中的Item
     *
     * @return 当前被选中的Item
     */
    @NonNull
    public List<EditTableData> getSelectedItem() {
        List<EditTableData> selectedItems = new ArrayList<>();
        for (int index : mSelectedList) {
            selectedItems.add(mDataList.get(index));
        }
        return selectedItems;
    }

    private TextView getItem(ViewGroup horizontalContainer, int index) {
        final TextView cellView = (TextView) mInflater.inflate(R.layout.item_edit_cell, horizontalContainer, false);
        cellView.setText(mDataList.get(index).name);
        if (InkAppManager.isZh(mContext)) {
            cellView.setGravity(Gravity.CENTER);
        } else {
            cellView.setGravity(Gravity.CENTER | Gravity.LEFT);
            cellView.setCompoundDrawablePadding(10);
        }
        cellView.setTag(index);
        cellView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer index = (Integer) v.getTag();
                if (index == null) {
                    return;
                }
                if (!multiSelect) {
                    // 如果不允许多点选择就清除之前选择的内容
                    clearSelectedView();
                }
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    setBlackDot(cellView);
                    mSelectedList.add(index);
                } else {
                    setWhiteDot(cellView);
                    mSelectedList.remove(index);
                }
            }
        });

        return cellView;
    }

    private void setBlackDot(TextView cellView) {
        Drawable drawable = getResources().getDrawable(R.drawable.dot);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        cellView.setCompoundDrawables(drawable, null, null, null);
    }

    private void setWhiteDot(TextView cellView) {
        //隐藏Drawables
        Drawable drawable = getResources().getDrawable(R.drawable.dot_white);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        cellView.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 清除所有选中的view
     */
    public void clearSelectedView() {
        if (mCellViews == null) {
            return;
        }
        for (int index : mSelectedList) {
            mCellViews.get(index).setSelected(false);
            setWhiteDot(mCellViews.get(index));
        }
        mSelectedList.clear();
    }
}

