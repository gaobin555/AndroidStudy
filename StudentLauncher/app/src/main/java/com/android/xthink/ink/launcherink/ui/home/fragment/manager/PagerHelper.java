/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.fragment.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.xthink.ink.launcherink.bean.InkPageInfoBean;
import com.android.xthink.ink.launcherink.bean.PluginInfoBean;
import com.android.xthink.ink.launcherink.common.constants.InkConstants;
import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.manager.datasave.pager.OperatePagersDBImpl;
import com.android.xthink.ink.launcherink.manager.datasave.plugin.PluginDBImpl;
import com.android.xthink.ink.launcherink.manager.event.IPageIndicatorUpdate;
import com.android.xthink.ink.launcherink.receiver.InstallReceiver;
import com.android.xthink.ink.launcherink.ui.customview.InkNoScrollViewPager;
import com.android.xthink.ink.launcherink.ui.home.MainActivity;
import com.android.xthink.ink.launcherink.ui.home.adapter.InkHomeFragmentAdapter;
import com.android.xthink.ink.launcherink.ui.home.fragment.BasePagerFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.DiscoveryFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.IPageCallback;
import com.android.xthink.ink.launcherink.ui.home.fragment.ReadsFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.StcardFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.TodayFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.ToolsFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.WeChatFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.KindleFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.PluginFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.MusicFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.CurriculumFragment;
import com.android.xthink.ink.launcherink.ui.home.fragment.TodoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 请描述功能
 *
 * @author liyuyan
 * @version 1.0, 2017/3/25
 */

public class PagerHelper implements IPageCallback {

    private static final String TAG = "PagerHelper";
    private static final String EXTRA_KEY_FRAGMENT_SHOWING_INDEX = "extra_key_fragment_showing_index";
    private Map<Integer, BasePagerFragment> mAllFragmentMap; // 全部的fragment，包含未显示出来的。所以得到的fragment可能为null
    private List<BasePagerFragment> mShowingFragmentList;
    private static List<Integer> mListIds = new ArrayList<>();
    private int mCurrentHomePageId;
    private InkNoScrollViewPager mViewPager;

    private MainActivity mActivity;
    private InkHomeFragmentAdapter mHomeFragmentAdapter;

    private TodoFragment mJvTodoFragment;
    private TodayFragment mTodayFragment;
    private DiscoveryFragment mUserFragment;
    private ToolsFragment mToolsFragment;
    private ReadsFragment mReadsFragment;
    private MusicFragment mMusicFragment;
    private CurriculumFragment mCurriculumFragment;
    private StcardFragment mStcardFragment;

    private OnPageSelectedListener mOnPageSelectedListener;
    private IPageIndicatorUpdate indicatorUpdate;

    private PluginDBImpl mPluginDb;
    private int mDefaultHomePage;//默认主屏id
    private OperatePagersDBImpl mPagersDB;

    public PagerHelper(MainActivity activity) {
        mActivity = activity;
        mAllFragmentMap = new HashMap<>();
    }

    /**
     * 传入一个ViewPager来初始化页面
     */
    public void init(InkNoScrollViewPager viewPager, Bundle savedInstanceState) {
        this.mViewPager = viewPager;
        initViewPager(savedInstanceState);
    }

    /**
     * 重新加载所有pager
     */
    public void reloadAllPager() {
        ArrayList<InkPageInfoBean> pagesInfo = queryAllPageList();
        updatePages(pagesInfo);
    }

    private void initViewPager(Bundle savedInstanceState) {
        mShowingFragmentList = initPagesFragment(savedInstanceState);

        mHomeFragmentAdapter = new InkHomeFragmentAdapter(mActivity.getSupportFragmentManager());
        mHomeFragmentAdapter.setFragmentList(mShowingFragmentList);
        mViewPager.setAdapter(mHomeFragmentAdapter);

        mViewPager.setOffscreenPageLimit(20);

        //设置默认显示的页面,根据保存的id来决定，如果没有保存过id那么就显示默认的主页
        int toShowId = -1;
        if (savedInstanceState != null) {
            toShowId = savedInstanceState.getInt(EXTRA_KEY_FRAGMENT_SHOWING_INDEX, -1);
        }

        if (toShowId == -1) {
            toShowId = getHomeScreenId();
            Log.d(TAG, "126--initViewPager: toShowId = " + toShowId);
        }
        showPageById(toShowId);

        mViewPager.addOnPageChangeListener(new InkNoScrollViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mOnPageSelectedListener != null) {
                    mOnPageSelectedListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<BasePagerFragment> initPagesFragment(Bundle savedInstanceState) {

        ArrayList<InkPageInfoBean> pagesInfo = queryAllPageList();

        List<BasePagerFragment> showingFragmentList = new ArrayList<>();

        MyLog.i(TAG, "initViewPager: " + pagesInfo);
        int count = pagesInfo.size();
        InkPageInfoBean selectedPage;
        BasePagerFragment fragment;

        for (int i = 0; i < count; i++) {
            selectedPage = pagesInfo.get(i);

            if (selectedPage.isHomePage()) {
                mCurrentHomePageId = selectedPage.getItemId();
            }
            if (selectedPage.isDefaultHomePage()) {
                mDefaultHomePage = selectedPage.getItemId();
            }
            fragment = getOrCreateFragment(selectedPage.getItemId(), selectedPage.getIndex(), savedInstanceState);
            if (null != fragment && !showingFragmentList.contains(fragment) && fragment.isPagerEnable()) {
                showingFragmentList.add(fragment);
                mListIds.add(fragment.getPageId());
                MyLog.d("multitask A", " mListIds.add(fragment.getCurrentPageId()" + fragment.getPageId());
            }
        }
        return showingFragmentList;
    }

    /**
     * 得到fragment，如果已经创建过就直接返回，否则创建一个。
     *
     * @param pageId id
     * @param index  item's index
     * @return 相应的Fragment，可能返回null。
     */
    @Nullable
    private BasePagerFragment getOrCreateFragment(int pageId, int index) {
        return getOrCreateFragment(pageId, index, null);
    }

    /**
     * 得到fragment，如果已经创建过就直接返回，否则创建一个。
     *
     * @param pageId             页面的id，固定值
     * @param index              页面的索引值，用来排序的
     * @param savedInstanceState 如果有需要恢复的fragment，可以传递这个参数，不需要就传null
     * @return 相应的Fragment，可能返回null。
     */
    @Nullable
    private BasePagerFragment getOrCreateFragment(int pageId, int index, @Nullable Bundle savedInstanceState) {

        BasePagerFragment fragment = null;

        // 如果之前有保存过fragment，就恢复出来
        if (savedInstanceState != null) {
            // TAG + pageId 参考 saveInstanceState()方法
            Fragment savedFragment = mActivity.getSupportFragmentManager().getFragment(savedInstanceState, TAG + pageId);
            if (savedFragment instanceof BasePagerFragment) {
                fragment = (BasePagerFragment) savedFragment;
            }
        }

        if ((pageId & InkConstants.PAGE_ID_PLUGIN_TAG) != InkConstants.PAGE_ID_PLUGIN_TAG) {
            //标志位不为插件
            MyLog.d(TAG, "add page" + pageId);
            switch (pageId) {
                case InkConstants.PAGE_ID_TODAY:
                    if (fragment instanceof TodayFragment) {
                        mTodayFragment = (TodayFragment) fragment;
                    }
                    if (mTodayFragment == null) {
                        mTodayFragment = TodayFragment.newInstance();
                        mTodayFragment.setPageId(pageId);
                        mTodayFragment.setIndex(index);
                    }
                    fragment = mTodayFragment;
                    break;

                case InkConstants.PAGE_ID_TODO:
                    if (fragment instanceof TodoFragment) {
                        mJvTodoFragment = (TodoFragment) fragment;
                    }
                    if (mJvTodoFragment == null) {
                        mJvTodoFragment = TodoFragment.newInstance();
                        mJvTodoFragment.setPageId(pageId);
                        mJvTodoFragment.setIndex(index);
                    }
                    fragment = mJvTodoFragment;
                    break;

                case InkConstants.PAGE_ID_WECHAT:
                    boolean isWeChatInstalled = CommonUtils.isPkgInstalled(mActivity, InkConstants.PACKAGE_NAME_WECHAT);
                    if (!isWeChatInstalled) {
                        fragment = null;
                        break;
                    }
                    if (fragment != null) {
                        fragment.setPageCallback(this);
                    }
                    if (fragment == null) {
                        fragment = WeChatFragment.newInstance();
                        fragment.setPageCallback(this);
                        fragment.setPageId(pageId);
                        fragment.setIndex(index);
                    }
                    break;

                case InkConstants.PAGE_ID_KINDLE:
                    boolean isKindleInstalled = CommonUtils.isPkgInstalled(mActivity, InkConstants.PACKAGE_NAME_KINDLE);
                    if (!isKindleInstalled) {
                        fragment = null;
                        break;
                    }
                    if (fragment != null) {
                        fragment.setPageCallback(this);
                    }
                    if (fragment == null) {
                        fragment = KindleFragment.newInstance();
                        fragment.setPageCallback(this);
                        fragment.setPageId(pageId);
                        fragment.setIndex(index);
                    }
                    break;

                case InkConstants.PAGE_ID_MUSIC:
                    if (fragment instanceof MusicFragment) {
                        mMusicFragment = (MusicFragment) fragment;
                    }
                    if (mMusicFragment == null) {
                        mMusicFragment = MusicFragment.newInstance();
                        mMusicFragment.setPageId(pageId);
                        mMusicFragment.setIndex(index);
                    }
                    fragment = mMusicFragment;
                    break;

                case InkConstants.PAGE_ID_CURRICULUM:
                    if (fragment instanceof CurriculumFragment) {
                        mCurriculumFragment = (CurriculumFragment) fragment;
                    }
                    if (mCurriculumFragment == null) {
                        mCurriculumFragment = CurriculumFragment.newInstance();
                        mCurriculumFragment.setPageId(pageId);
                        mCurriculumFragment.setIndex(index);
                    }
                    fragment = mCurriculumFragment;
                    break;

                case InkConstants.PAGE_ID_STCARD:
                    if (fragment instanceof StcardFragment) {
                        mStcardFragment = (StcardFragment) fragment;
                    }
                    if (mStcardFragment == null) {
                        mStcardFragment = StcardFragment.newInstance();
                        mStcardFragment.setPageId(pageId);
                        mStcardFragment.setIndex(index);
                    }
                    fragment = mStcardFragment;
                    break;

                case InkConstants.PAGE_ID_TOOLS:
                    if (fragment instanceof ToolsFragment) {
                        mToolsFragment = (ToolsFragment) fragment;
                    }
                    if (mToolsFragment == null) {
                        mToolsFragment = ToolsFragment.newInstance();
                        mToolsFragment.setPageId(pageId);
                        mToolsFragment.setIndex(index);
                    }
                    fragment = mToolsFragment;
                    break;

                case InkConstants.PAGE_ID_READS:
                    if (fragment instanceof ReadsFragment) {
                        mReadsFragment = (ReadsFragment) fragment;
                    }
                    if (mReadsFragment == null) {
                        mReadsFragment = ReadsFragment.newInstance();
                        mReadsFragment.setPageId(pageId);
                        mReadsFragment.setIndex(index);
                    }
                    fragment = mReadsFragment;
                    break;

                case InkConstants.PAGE_ID_USER:
                    if (fragment instanceof DiscoveryFragment) {
                        mUserFragment = (DiscoveryFragment) fragment;
                    }
                    if (mUserFragment == null) {
                        mUserFragment = DiscoveryFragment.newInstance();
                        mUserFragment.setPageId(pageId);
                        mUserFragment.setIndex(index);
                    }
                    fragment = mUserFragment;
                    break;

                default:
                    fragment = null;
                    break;
            }
        } else {
            //插件,判断手机又没以后安装包，有安装包才加载
            MyLog.d(TAG, "add plugin page" + pageId);
            if (fragment instanceof PluginFragment) {
                return fragment;
            }
            if (fragment == null) {
                fragment = mAllFragmentMap.get(pageId);
                if (fragment != null) {
                    return fragment;
                }
                PluginInfoBean pluginInfoBean = queryPlugin(pageId);
                if (pluginInfoBean != null) {
                    boolean isPackageInstalled = CommonUtils.isPkgInstalled(mActivity, pluginInfoBean.getPackageName());
                    if (!isPackageInstalled) {
                        fragment = null;
                    } else {
                        fragment = PluginFragment.newInstance(pluginInfoBean);
                        fragment.setPageId(pageId);
                        fragment.setIndex(index);
                    }
                }
            }
        }

        if (!mAllFragmentMap.containsValue(fragment)) {
            assert fragment != null;
            mAllFragmentMap.put(pageId, fragment);
        }
        return fragment;
    }

    private ArrayList<InkPageInfoBean> queryAllPageList() {
        if (mPagersDB == null) {
            mPagersDB = new OperatePagersDBImpl(mActivity);
        }
        return mPagersDB.querySelectedPagesInfo();
    }

    /**
     * 设置页面滑动的回调监听
     *
     * @param onPageSelectedListener 页面滑动
     */
    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        mOnPageSelectedListener = onPageSelectedListener;
    }

    public void setTodayFragmentStep(int stepCount) {
        if (null != mTodayFragment && mTodayFragment.isVisible()) {
            //mTodayFragment.refreshStepCount(stepCount);
        }
    }

    /**
     * 设置新的home
     *
     * @param pageId home的pageId
     */
    public void setHomePage(int pageId) {
        mCurrentHomePageId = pageId;
    }

    /**
     * 滑动到首页
     */
    public void showHomePage() {
        int id = getHomeScreenId();
        MyLog.i(TAG, "main show home " + id);
        showPageById(id);
    }

    /**
     * 滑动到指定的页面
     *
     * @param id 指定页面的id
     */
    public void showPageById(int id) {
        int position = getItemPosition(id);
        if (position < 0) {
            position = getItemPosition(mDefaultHomePage);
        }
//        mActivity.mHomeIndicator.setSelectedPoint(position);
        int prePosition = mViewPager.getCurrentItem();
        MyLog.d("main home", prePosition + "__" + position);
        if (prePosition != position) {
            mViewPager.setCurrentItem(position, false);
//            //不响应onResume状态
//            mActivity.setFullUpdateLater(true);
        }

        mViewPager.callONScrollChange(prePosition, position);
        MyLog.d("fullUpdate", "position in mus" + position);

    }

    public int getIdByPosition(int position) {
        BasePagerFragment fragment = mShowingFragmentList.get(position);
        return fragment.getPageId();
    }

    /**
     * get index from itemId
     *
     * @param itemId itemId
     * @return index
     */
    private int getItemPosition(int itemId) {
        for (BasePagerFragment page : mShowingFragmentList) {
            if (itemId == page.getPageId()) {
                return mShowingFragmentList.indexOf(page);
            }
        }
        //未被添加
        return -1;
    }

    private int getHomeScreenId() {
        return mCurrentHomePageId;
    }

    /**
     * 获取主屏位置
     *
     * @return 主屏位置索引, 异常时返回默认主屏位置
     */
    public int getHomeScreenPosition() {
        int homePosition = getItemPosition(mCurrentHomePageId);
        MyLog.d(TAG, homePosition + "homePosition_mCurrentHomePageId " + mCurrentHomePageId);
        if (homePosition >= 0) {
            return homePosition;
        }
        return getItemPosition(mDefaultHomePage);

    }

    /**
     * 删除所有页面
     */
    public void unRegisterAll() {
        // TODO: 2017/5/25 清除viewpager 缓存
        try { //切换语言会导致crash,卡住,如掌阅,com.zhangyue.iReader.eink.view.MarketLayout.onDetachedFromWindow 去unbindService时 IllegalArgumentException: Service not registered
            /** to resolve YY3-5580 bug, change by luoyongjie20170826*/
            mHomeFragmentAdapter.clear(mShowingFragmentList);
            mAllFragmentMap.clear();
            mListIds.clear();
            MyLog.d("multitask A", " mListIds.clear() unRegisterAll");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public int getPageCount() {
        return mShowingFragmentList.size();
    }

    /**
     * 根据{@link InkPageInfoBean}列表来更新fragment。
     *
     * @param toShowBeanList 要显示的page列表
     */
    public void updatePages(List<? extends InkPageInfoBean> toShowBeanList) {
        mShowingFragmentList.clear();
        mListIds.clear();
        MyLog.d("multitask A", " mListIds.clear() updatePages");
        List<Integer> showPagesId = new ArrayList<>();
        for (InkPageInfoBean bean : toShowBeanList) {
            int id = bean.getItemId();
            showPagesId.add(id);
            BasePagerFragment fragment = getOrCreateFragment(id, bean.getIndex());
            if (fragment != null && !mShowingFragmentList.contains(fragment) && fragment.isPagerEnable()) {
                mShowingFragmentList.add(fragment);
                mListIds.add(id);
                MyLog.d("multitask A", "  mListIds.add(id)" + id + " page = " + bean.toString());
            }
        }

        if (null != indicatorUpdate) {
            MyLog.d(TAG, "position" + getHomeScreenPosition() + "_" + getPageCount());
            indicatorUpdate.onPageIndicatorUpdate(getHomeScreenPosition(), getPageCount());
        }
        mHomeFragmentAdapter.update(mShowingFragmentList);
    }

    private PluginInfoBean queryPlugin(int id) {
        if (mPluginDb == null) {
            mPluginDb = new PluginDBImpl(mActivity);
        }
        return mPluginDb.queryPlugin(id);
    }

    public void setIndicatorUpdate(IPageIndicatorUpdate indicatorUpdate) {
        this.indicatorUpdate = indicatorUpdate;
    }

    /**
     * activity被意外销毁时，保存一下fragment的状态
     *
     * @param outState onSaveInstanceState回调传递的state
     */
    public void saveInstanceState(Bundle outState) {

        if (mViewPager == null || mHomeFragmentAdapter == null) {
            return;
        }

        for (Integer pageId : mAllFragmentMap.keySet()) {
            BasePagerFragment fragment = mAllFragmentMap.get(pageId);
            if (fragment != null && fragment.isAdded()) {
                mActivity.getSupportFragmentManager().putFragment(outState, TAG + fragment.getPageId(), fragment);
            }
        }

        int position = mViewPager.getCurrentItem();
        outState.putInt(EXTRA_KEY_FRAGMENT_SHOWING_INDEX, mHomeFragmentAdapter.getItem(position).getPageId());
    }

    @Override
    public void hidePager(int pageId) {
        //隐藏某个fragment 只适用TODO wechat
        BasePagerFragment page = mAllFragmentMap.get(pageId);
        if (page == null) {
            return;
        }
        if (mShowingFragmentList != null && mShowingFragmentList.contains(page)) {
            mShowingFragmentList.remove(page);
            mListIds.remove(Integer.valueOf(pageId));
            MyLog.d("multitask A", "   mListIds.remove(Integer.valueOf(pageId))" + pageId);
            // 当移除的page刚好是home时，把pageHome设置成defaultHome，并更新数据库
            if (mCurrentHomePageId == pageId) {
                mCurrentHomePageId = mDefaultHomePage;
                if (mPagersDB == null) {
                    mPagersDB = new OperatePagersDBImpl(mActivity);
                }
                // 更新数据库
                mPagersDB.updateHomePage(mCurrentHomePageId, pageId);
            }

            // 更新桌面下方的指示器。
            if (null != indicatorUpdate) {
                indicatorUpdate.onPageIndicatorUpdate(getHomeScreenPosition(), getPageCount());
            }

            // 根据正在显示的fragment来更新ViewPager。
            mHomeFragmentAdapter.update(mShowingFragmentList);
        }

        if (mShowingFragmentList != null) {
            MyLog.d(TAG, "hidePage" + mShowingFragmentList.toString());
        }
    }

    /**
     * 展示页面,没有就创造一个展示
     *
     * @param itemId
     */
    @Override
    public void showPager(int itemId) {
        MyLog.d(TAG, "showPage");
        BasePagerFragment page = mAllFragmentMap.get(itemId);
        if (page == null) {
            OperatePagersDBImpl db = new OperatePagersDBImpl(mActivity.getApplicationContext());
            int index = db.queryPageIndex(itemId);
            if (index < 0) {
                return;
            }
            page = getOrCreateFragment(itemId, index);
        }
        if (page == null || mShowingFragmentList.contains(page)) {
            return;
        }
        mShowingFragmentList.add(page);
        Collections.sort(mShowingFragmentList);
        mListIds.clear();
        MyLog.d("multitask A", " mListIds.clear() showPage");
        for (int i = 0; i < mShowingFragmentList.size(); i++) {
            if (mListIds.contains(mShowingFragmentList.get(i).getPageId())) {
                continue;
            }
            mListIds.add(mShowingFragmentList.get(i).getPageId());
            MyLog.d("multitask A", " mListIds.add(mShowingFragmentList.get(i).getCurrentPageId()" + mShowingFragmentList.get(i).getPageId());
        }

        MyLog.d(TAG, "showPage" + mShowingFragmentList.toString());
        if (null != indicatorUpdate) {
            indicatorUpdate.onPageIndicatorUpdate(getHomeScreenPosition(), getPageCount());
        }
        mHomeFragmentAdapter.update(mShowingFragmentList);
    }

    public interface OnPageSelectedListener {
        void onPageSelected(int position);
    }

    public static List<Integer> getAllIds() {
        if (mListIds != null) {
            return new ArrayList<>(new LinkedHashSet<>(mListIds));
        }
        return null;
    }
}