package com.android.xthink.ink.launcherink.ui.home.adapter;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.ui.home.fragment.BasePagerFragment;

import java.util.List;

/**
 * 主页适配器
 * Created by gaob@x-thinks on 2018/12/22.
 */

public class InkHomeFragmentAdapter extends CustomFragmentStatePagerAdapter {

    private static final String TAG = "InkHomeFragmentAdapter";

    private List<BasePagerFragment> fragmentList;

    public InkHomeFragmentAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public BasePagerFragment getItem(int position) {
        MyLog.d("InkHomeFragmentAdapter", "getItem");
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @Override
    public int getItemPosition(Object object) {
        if (object instanceof BasePagerFragment) {
            int id = ((BasePagerFragment) object).getPageId();
            for (BasePagerFragment fragment : fragmentList) {
                if (fragment.getPageId() == id) {
                    return fragmentList.indexOf(fragment);
                }
            }
        }
        return POSITION_NONE;
    }

    public List<BasePagerFragment> getmFragmentList() {
        return fragmentList;
    }

    /**
     * 通过此方法来更新ViewPager。
     *
     * @param fragmentList 新的列表
     */
    public void update(List<BasePagerFragment> fragmentList) {
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


    public void setFragmentList(List<BasePagerFragment> fragmentList) {
        this.fragmentList = fragmentList;
    }
}
