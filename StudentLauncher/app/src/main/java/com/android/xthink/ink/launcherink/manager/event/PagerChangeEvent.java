package com.android.xthink.ink.launcherink.manager.event;

/**
 * Created by liyuyan on 2016/12/28.
 */

public class PagerChangeEvent {

    public static final int ACTION_ADD = 1;
    public static final int ACTION_DELETE = 2;
    public static final int ACTION_THREE = 3;
    int mAction = 1;
    int mPosition = -1;
    String mFragmentName;

    public PagerChangeEvent(String fragmentName, int position, int action) {
        mFragmentName = fragmentName;
        mPosition = position;
        mAction = action;
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getFragmentName() {
        return mFragmentName;
    }

    public void setFragmentName(String fragmentName) {
        mFragmentName = fragmentName;
    }
}
