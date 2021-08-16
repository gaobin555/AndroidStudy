package com.android.xthink.ink.launcherink.manager.event;

/**
 * 请描述功能
 *
 * @author luoyongjie
 * @version 1.0, 4/24/2017
 */
public interface IStepChangedListener {
    void onStepCountChanged(int stepCount);
    void onStepInitData(int stepCount);
}
