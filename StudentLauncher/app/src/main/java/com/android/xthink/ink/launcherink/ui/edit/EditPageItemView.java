/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */
package com.android.xthink.ink.launcherink.ui.edit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.bean.InkPageEditBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.UiTestUtils;
import com.android.xthink.ink.launcherink.init.InitAppHelper;
import com.android.xthink.ink.launcherink.manager.InkToastManager;
import com.android.xthink.ink.launcherink.manager.event.IPageEditChangeListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
//import com.coolyota.analysis.CYAnalysis;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * one page of the viewPager .
 *
 * @author renxu
 * @version 1.0, 2017/3/20
 */

public class EditPageItemView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "JvEditPageItemView";

    private static final int BACKGROUND_HOME = 0;
    private static final int BACKGROUND_SELECTED = 1;
    private static final int BACKGROUND_UNSELECTED = 2;
    private InkPageEditBean mScreen; //页的数据
    private ImageView mIvPic;   //背景
    private TextView mTvSelect; //选择是否开启此页
    private TextView mTvSetHomeScreen; //设置为主屏
    private IPageEditChangeListener pageEditListener;
    private int position;
    private boolean SourceIsSelectedStatus;
    private Context  mContext;


    public EditPageItemView(Context context, InkPageEditBean screen) {
        super(context);
        mContext = context;
        this.mScreen = screen;
        initView();
        initListener();
        initData();
    }

    private void initListener() {
        mTvSelect.setOnClickListener(this);
        mTvSetHomeScreen.setOnClickListener(this);
    }

    private void initData() {
        int pageId = mScreen.getItemId();
        int picId = InitAppHelper.getInstance().getPagePicById(mContext, pageId);
        setPic(picId);
        setSelectable(!mScreen.isProtected());//页面是否可以被取消和开启
        selectPage(mScreen.isShow() || mScreen.isProtected()); // 被保护的page一定是选中的。
        SourceIsSelectedStatus = mScreen.isShow();

        /**
         * 页面是否可用,weChat kindle 是否安装
         *
         * 不可用时设为主页不可点击
         */
        if (!mScreen.isUseful()) {
            mTvSetHomeScreen.setText(R.string.set_home_screen);
            mTvSetHomeScreen.setTextColor(getResources().getColor(R.color.text_unselected_color));
            mTvSetHomeScreen.setClickable(false);
        }

    }

    /**
     * 页面选中状态ui更改
     *
     * @param isSelected 页面选中状态
     */
    private void selectPage(boolean isSelected) {
        if (isSelected) {
            setTextViewRightDrawable(R.drawable.edit_checkbox_on, mTvSelect);
            setPagerBackground(BACKGROUND_SELECTED);
        } else {
            setPagerBackground(BACKGROUND_UNSELECTED);
            setTextViewRightDrawable(R.drawable.edit_checkbox_off, mTvSelect);
        }

        boolean b = isSelected && mScreen.isUseful();
        mTvSetHomeScreen.setTextColor(b ?Color.WHITE:getResources().getColor(R.color.text_unselected_color));
        mTvSetHomeScreen.setEnabled(b);
    }

    public void setHomePage(boolean isHomePage) {
//        if (isHomePage) {
//            setPagerBackground(BACKGROUND_HOME);
//            mTvSetHomeScreen.setText(R.string.home_screen);
//            setTextViewLeftDrawable(R.drawable.home_screen_icon, mTvSetHomeScreen);
//            mTvSetHomeScreen.setClickable(false);
//        } else {
//            if (mScreen.isShow()) {
//                setPagerBackground(BACKGROUND_SELECTED);
//            } else {
//                setPagerBackground(BACKGROUND_UNSELECTED);
//            }
//            mTvSetHomeScreen.setText(R.string.set_home_screen);
//            mTvSetHomeScreen.setCompoundDrawables(null, null, null, null);
//            mTvSetHomeScreen.setClickable(true);
//        }

        mScreen.setHomePage(false);
    }

    /**
     * 设置选择按钮是否可点击,可见
     *
     * @param selectable 按钮状态
     */
    private void setSelectable(boolean selectable) {
        if (selectable) {
            mTvSelect.setVisibility(VISIBLE);
        } else {
            mTvSelect.setVisibility(INVISIBLE);
        }

    }

    /**
     * 设置主图片,背景圆角
     *
     * @param picId 图片资源Id
     */
    private void setPic(int picId) {
        MyLog.i(TAG, "setPic:" + mScreen.getTitle());
        Glide.with(this.getContext())
                .load(picId)
                .bitmapTransform(new GlideRoundTransform(getContext(), 2))
                .diskCacheStrategy(DiskCacheStrategy.NONE) //禁用磁盘缓存,避免更换图片没改名字,还显示之前的图片
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .skipMemoryCache(true) //跳过内存缓存
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        MyLog.i(TAG, "onResourceReady:" + mScreen.getTitle());
                        if (mIvPic == null) {
                            return false;
                        }
                        if (mIvPic.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            mIvPic.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        ViewGroup.LayoutParams params = mIvPic.getLayoutParams();
                        int vw = mIvPic.getWidth() - mIvPic.getPaddingLeft() - mIvPic.getPaddingRight();
                        float scale = (float) vw / (float) resource.getIntrinsicWidth();
                        int vh = Math.round(resource.getIntrinsicHeight() * scale);
                        params.height = vh + mIvPic.getPaddingTop() + mIvPic.getPaddingBottom();
                        mIvPic.setLayoutParams(params);
                        return false;
                    }
                })
                .into(mIvPic);
    }


    /**
     * 设置此页面背景色
     *
     * @param colorState 颜色状态值
     */
    private void setPagerBackground(int colorState) {
        switch (colorState) {
            case (BACKGROUND_SELECTED): {
                setBackgroundResource(R.drawable.edit_view_pager_background_on);
                break;
            }
            case (BACKGROUND_UNSELECTED): {
                setBackgroundResource(R.drawable.edit_view_pager_background_off);
                break;
            }
            case (BACKGROUND_HOME): {
                setBackgroundResource(R.drawable.edit_view_pager_background_home);
                break;
            }
            default: {
                break;
            }
        }

    }

    /**
     * 选择按钮是否正常显示
     *
     * @param show 是否可见
     */
    public void showSelectButton(boolean show) {
        if (mScreen.isProtected() || !mScreen.isUseful()) {
            return;
        }

        setSelectable(show);
    }


    /**
     * 初始化控件
     */
    private void initView() {
        View.inflate(getContext(), R.layout.edit_view_pager_item_view, this);
        mIvPic = (ImageView) findViewById(R.id.iv_edit_item_pic);
        mTvSelect = (TextView) findViewById(R.id.iv_edit_item_choose);
        mTvSetHomeScreen = (TextView) findViewById(R.id.tv_edit_set_home);
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_item_choose: {
                //是否选中状态的改变 不可用页面设置主屏不可用
                boolean isShow = !mScreen.isShow();
                if (isShow) {
                    UiTestUtils.tag(mScreen.getName() + "被选中了");
                } else {
                    UiTestUtils.tag(mScreen.getName() + "被取消选中");
                }
                if (isShow) {
                    //改变为显示，判断数目 超过就提示 return
                    int maxAmount = InkConstants.MAX_AMOUNT_OF_PAGE;
                    if (EditActivity.mSelectedPageAmount >= maxAmount) {
                        UiTestUtils.tag(mScreen.getName() + "被选中了，超过了12屏，无法选择。");
                        InkToastManager.showToastShort(this.getContext(), getContext().getString(R.string.Apps_can_not_be_added, maxAmount));
                        return;
                    }
                }

                /**X-Thinks end*/
                //更改选中状态
                selectPage(isShow);

                //不可用时点击选中提示安装
                if (isShow&&!mScreen.isUseful()){
                    InkToastManager.showToastShort(getContext(),mContext.getString(R.string.install_in_color_screen));
                }

                if (SourceIsSelectedStatus == isShow) {
                    mScreen.setSelectedStatusChanged(false);
                } else {
                    mScreen.setSelectedStatusChanged(true);
                }
                mScreen.setShow(isShow);
                //页面，成功后改变页面总数
                EditActivity.mSelectedPageAmount += isShow ? (1) : (-1);
                break;
            }

            case R.id.tv_edit_set_home: {
                //取消原主屏 设置此主屏
                setHomePage(true);
                callHomePageListener(position);
                UiTestUtils.tag("桌面管理点击了设置home");
                /**
                 * X-Thinks begin, add
                 * what(reason) 统计页面编辑 设置首页 */
                if (mContext != null) {
                    JSONObject jsonObj = new JSONObject();

                    try {
                        jsonObj.put("page_id", mScreen.getItemId());
                        jsonObj.put("page_name", mScreen.getName());
                        UiTestUtils.tag(mScreen.getName() + "被设置为home");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String setHomeEventId = "setHome";//设置主屏的事件

                }

                break;
            }
            default: {
                break;
            }
        }
    }

    private void callHomePageListener(int position) {
        if (null != pageEditListener) {
            pageEditListener.onHomePageChanged(position);
        }
    }

    public void setHomeChangeListener(IPageEditChangeListener homeChangeListener) {
        this.pageEditListener = homeChangeListener;
    }

    private void setTextViewRightDrawable(int drawableId, TextView view) {
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        view.setCompoundDrawables(null, null, drawable, null);//画在右边
    }

    private void setTextViewLeftDrawable(int drawableId, TextView view) {
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        view.setCompoundDrawables(drawable, null, null, null);//画在右边
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
