/* *
   * Copyright (C) 2018  X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.home.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.ui.home.fragment.BasePagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 对原生的PagerAdapter作出修改。{@link android.support.v4.app.FragmentStatePagerAdapter}
 * 主要修改了destroyItem方法
 *
 * @author wanchi@X-Thinks.com
 * @version 1.0, 2017/4/27
 */
public abstract class CustomFragmentStatePagerAdapter extends PagerAdapter {

    private static final String TAG = "FragmentStatePagerAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private Fragment mCurrentPrimaryItem = null;

    public CustomFragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            // modify by wanchi 页面是1,2,3 修改成 1,4,3时，先调用destroy，mFragment变成1,3.
            // 然后由于只修改了index为1的页面，这里就只会get(1),所以就会返回3.所以3出现在了第二个页面，第三个页面也是3，所以就空白了。
            if (f != null && position == getItemPosition(f)) {
                if (f instanceof BasePagerFragment) {
                    MyLog.i(TAG, "getItemCache,position : " + position + "; id : " + ((BasePagerFragment) f).getPageId());
                }
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        if (DEBUG) MyLog.d(TAG, "Adding item #" + position + ": f=" + fragment);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) MyLog.e(TAG, "Removing item #" + position + ": f=" + object
                + " v=" + ((Fragment) object).getView());

        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }

        mSavedState.set(position, fragment.isAdded()
                ? mFragmentManager.saveFragmentInstanceState(fragment) : null);
        mFragments.set(position, null);

        // 如果这个位置的Item不存在了，就直接remove掉，不会再占用一个null空位置。
        int itemPosition = getItemPosition(object);
        if (itemPosition == POSITION_NONE) {
            mSavedState.remove(position);
            mFragments.remove(position);
        }

//        try { //更新页面会导致crash,如掌阅,com.zhangyue.iReader.eink.view.MarketLayout.onDetachedFromWindow 去unbindService时 IllegalArgumentException: Service not registered
        mCurTransaction.remove(fragment);
//        } catch (Exception e) {//这里try 没用
//            e.printStackTrace();
//        }
    }

    @Override
    public void notifyDataSetChanged() {
        updateFragmentList();
        super.notifyDataSetChanged();
    }

    private void updateFragmentList() {
        ArrayList<Fragment> newFragments = new ArrayList<>();
        for (Fragment fragment : mFragments) {
            if (fragment != null) {
                int realPosition = getItemPosition(fragment);
                if (realPosition < 0) {
                    continue;
                }
                while (newFragments.size() <= realPosition) {
                    newFragments.add(null);
                }
                newFragments.set(realPosition, fragment);
            }
        }
        mFragments = newFragments;
        mSavedState.clear();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            try { //更新页面会导致crash,如掌阅,com.zhangyue.iReader.eink.view.MarketLayout.onDetachedFromWindow 去unbindService时 IllegalArgumentException: Service not registered
                mCurTransaction.commitNowAllowingStateLoss();
                mCurTransaction = null;
            } catch (Exception e) {
                e.printStackTrace();
//                EventBus.getDefault().post(e); //发送到MainActivity 处理,重新加载页面 或者重新startActivity
//                MyApplication.getInstance().mCatchException.handleException(e);//重启应用
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    } else {
                        MyLog.d(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    /**
     * 清除fragment缓存 add by renxu
     *
     * @param fragmentList
     */
    public void clear(List<BasePagerFragment> fragmentList) {
        MyLog.d(TAG, "FragmentManager clear");
        if (fragmentList != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            for (Fragment f : fragmentList) {
                try {
                    ft.remove(f);
                } catch (Exception e) {
                    MyLog.e(TAG, e.toString());
                }
            }
            //onSaveInstanceState() 后执行 AllowingStateLoss
            ft.commitAllowingStateLoss();
            //立即执行
            mFragmentManager.executePendingTransactions();
            fragmentList.clear();
        }
        mSavedState.clear();
    }
}

