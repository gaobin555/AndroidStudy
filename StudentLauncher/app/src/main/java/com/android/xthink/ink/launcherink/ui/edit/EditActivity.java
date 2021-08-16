/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.base.BaseActivity;
import com.android.xthink.ink.launcherink.bean.InkPageEditBean;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.event.IPageEditChangeListener;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.init.InitAppHelper;
import com.android.xthink.ink.launcherink.manager.datasave.pager.OperatePagersDBImpl;
import com.android.xthink.ink.launcherink.ui.edit.indicator.HomeIndicator;
import com.eink.swtcon.SwtconControl;
//import com.coolyota.analysis.CYAnalysis;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * Activity of desktop manager
 *
 * @author renxu
 * @version 1.0, 2017/3/20
 */

public class EditActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, IPageEditChangeListener {

    public static final String mPageName = "EditActivity";
    public static int mSelectedPageAmount;//选中的页面总数
    private static final String TAG = "EditActivity";
    private View mBtBack;
    private TextView mTvTittle;
    private ViewPager mVpGallery;
    private ViewGroup.LayoutParams mLayoutParams;
    private HomeIndicator mHomeIndicator;
    private OperatePagersDBImpl mPagersManagerDB;
    private ArrayList<InkPageEditBean> mAllPages;
    private ArrayList<InkPageEditBean> mShowPages;
    private EditAdapter mEditAdapter;
    private int mCurrentHomePagePosition;
    private int mSourceHomePagePosition;
    private View mRootView;
    private View mTop;

    public static void start(Context context) {
        Intent starter = new Intent(context, EditActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_GLD16);
        context.startActivity(starter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_edit_back: {
                //返回按钮的处理
                finish();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        sendAndSaveMessage();
        super.onPause();
    }

    /**
     * 保存并向显示页发送改变后的数据
     */
    private boolean sendAndSaveMessage() {

        ArrayList<InkPageEditBean> toShowPageList = new ArrayList<>();
        boolean isPagesChanged = false;

        /**
         * 选中状态是否有改变，
         */
        for (InkPageEditBean page : mAllPages) {
            MyLog.d(TAG, "pageInfo = " + page.toString());
            if (page.isShow()) {
                toShowPageList.add(page);
            }

            if (!isPagesChanged && page.isSelectedStatusChanged()) {
                isPagesChanged = true;
            }
        }

        if (isPagesChanged) {
            // K1: 不需要修改主页
            OperatePagersDBImpl pagesDB = new OperatePagersDBImpl(EditActivity.this);
            pagesDB.updateChangedEditPage(mAllPages);

            //更改为eventBus方式 或广播方式
            EditEvent event = new EditEvent(mAllPages.get(mCurrentHomePagePosition).getItemId(), toShowPageList);
            EventBus.getDefault().postSticky(event);
            MyLog.i(TAG, "homeId = " + event.homeId + " showPages = " + event.toShowBeanList.toString());
        }
        return isPagesChanged;

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jv_launcher_edit;
    }

    @Override
    protected void initView() {
        mRootView = findViewById(R.id.activity_edit_root);
        mBtBack = findViewById(R.id.bt_edit_back);
        mTop = findViewById(R.id.view_top);
        mTvTittle = (TextView) findViewById(R.id.tv_edit_item_title);
        mVpGallery = (ViewPager) findViewById(R.id.view_pager_edit);
        mHomeIndicator = (HomeIndicator) findViewById(R.id.home_indicator);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSelectedPageAmount = 0;
        initViewPager();

        mPagersManagerDB = new OperatePagersDBImpl(this);

        new Runnable() {
            @Override
            public void run() {
                mAllPages = mPagersManagerDB.queryAllPages();
                mShowPages = new ArrayList<>();
                int size = mAllPages.size();
                InkPageEditBean homePage = null;
                for (int i = 0; i < size; i++) {
                    InkPageEditBean page = mAllPages.get(i);
                    if (page.isShow()) {
                        mSelectedPageAmount++;
                    }
                    //set useful
                    boolean pageUseful = InitAppHelper.getInstance().isPageUseful(EditActivity.this, page.getItemId());
                    page.setUseful(pageUseful);
                    if (page.isEditable() && pageUseful) {
                        mShowPages.add(page);
                    }
                    if (page.isHomePage()) {
                        homePage = page;
                    }
                }
                //获得可编辑页面中home的position
                if (homePage != null) {
                    mSourceHomePagePosition = mAllPages.indexOf(homePage);
                }

                mVpGallery.post(new Runnable() {
                    @Override
                    public void run() {

                        mEditAdapter = new EditAdapter(mShowPages, EditActivity.this);
                        mEditAdapter.setHomePageChangeListener(EditActivity.this);
                        mVpGallery.setAdapter(mEditAdapter);
                        mHomeIndicator.setPagesCount(mShowPages.size());
                        mHomeIndicator.refresh(mSourceHomePagePosition);
                        if (mShowPages != null && mShowPages.size() > 0) {
                            int pageId = mShowPages.get(0).getItemId();
                            String title = InitAppHelper.getInstance().getPageTitleById(EditActivity.this, pageId);
                            mTvTittle.setText(title);
                        }
                        mCurrentHomePagePosition = mSourceHomePagePosition;
                    }
                });

            }
        }.run();
    }

    /**
     * 设置viewpager 多页面显示
     */
    private void initViewPager() {
        int pagerWidth = getResources().getDisplayMetrics().widthPixels * 248 / 360;
        mLayoutParams = mVpGallery.getLayoutParams();
        if (mLayoutParams == null) {
            mLayoutParams = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            mLayoutParams.width = pagerWidth;
        }
        mVpGallery.setLayoutParams(mLayoutParams);
        mVpGallery.setPageMargin(getResources().getDimensionPixelSize(R.dimen.edit_page_margin));
        mVpGallery.setOffscreenPageLimit(3);

        ViewGroup view = (ViewGroup) mVpGallery.getParent();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mVpGallery.dispatchTouchEvent(motionEvent);
            }
        });
        if (view != null) {
            view.setClipChildren(false);
        }
    }


    @Override
    protected void setListener() {
        mTop.setOnClickListener(this);
        mBtBack.setOnClickListener(this);
        mVpGallery.setOnPageChangeListener(this);
    }

    /**
     * 返回键传递编辑结果
     */
    @Override
    public void onBackPressed() {
        sendAndSaveMessage();
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int pageId = mShowPages.get(position).getItemId();
        String title = InitAppHelper.getInstance().getPageTitleById(this, pageId);
        mTvTittle.setText(title);
        mHomeIndicator.setSelectedPoint(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onHomePageChanged(int changedPosition) {
        mCurrentHomePagePosition = changedPosition;
        mHomeIndicator.refresh(changedPosition);
    }

}
