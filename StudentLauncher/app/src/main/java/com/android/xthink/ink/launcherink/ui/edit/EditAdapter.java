/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.edit;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.xthink.ink.launcherink.bean.InkPageEditBean;
import com.android.xthink.ink.launcherink.manager.event.IPageEditChangeListener;

import java.util.ArrayList;

/**
 * Adapter of the viewpager in EditActivity
 *
 * @author renxu
 * @version 1.0, 2017/3/16
 */
public class EditAdapter extends PagerAdapter implements IPageEditChangeListener {

    private static final String TAG = "PagerAdapter";
    private ArrayList<InkPageEditBean> mPages;
    private ArrayList<EditPageItemView> mViewList;
    private int currentHomePagePosition;
    private int defaultHomePagePosition = 0;
    private IPageEditChangeListener homePageChangeListener;

    /**
     * @param pages the information of pages
     */
    public EditAdapter(ArrayList<InkPageEditBean> pages, Context context) {

        if(null == pages || 0 >= pages.size()) {
            return;
        }

        mViewList = new ArrayList<EditPageItemView>();
        mPages = pages;

        int size = mPages.size();
        Log.d(TAG, "size = " + size);

        for (int i = 0; i < size; i++) {
            InkPageEditBean page = mPages.get(i);

//            if(page.isDefaultHomePage()) {
//                defaultHomePagePosition = i;
//            }
//
//            if(page.isHomePage()) {
//                currentHomePagePosition = i;
//            }

            EditPageItemView convertView = new EditPageItemView(context, page);
            convertView.setHomeChangeListener(this);
            convertView.setPosition(i);
            mViewList.add(convertView);
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {

        container.removeView(mViewList.get(position));

    }

    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mPages.get(position).getName();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        EditPageItemView pageItemVIew = mViewList.get(position);
        if(null != pageItemVIew) {
            container.addView(pageItemVIew);
        }

        return pageItemVIew;
    }

    @Override
    public void onHomePageChanged(int changeHomePagePosition) {

        if(-1 == changeHomePagePosition) {
            mViewList.get(defaultHomePagePosition).setHomePage(true);
            currentHomePagePosition = defaultHomePagePosition;
        } else {
            mViewList.get(currentHomePagePosition).setHomePage(false);
            currentHomePagePosition = changeHomePagePosition;
        }

        if(null != homePageChangeListener) {
            homePageChangeListener.onHomePageChanged(currentHomePagePosition);
        }
    }


    public void setHomePageChangeListener(IPageEditChangeListener homePageChangeListener) {
        this.homePageChangeListener = homePageChangeListener;
    }

}
