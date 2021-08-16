package com.android.xthink.ink.launcherink.common.mvp.view;

import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;

/**
 * mvp 中的v
 * Created by wanchi on 2017/2/22.
 */

public interface IView {
    /**
     * 添加 presenter
     */
    void addPresenter(IPresenter presenter);

    /**
     * 结束当前view
     */
    void finish();
}
