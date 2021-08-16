package com.android.xthink.ink.launcherink.common.mvp.presenter;

import android.support.annotation.Nullable;

import com.android.xthink.ink.launcherink.common.mvp.view.IView;


/**
 * Presenter工厂,用于实例化相对的Presenter
 * Created by wanchi on 2017/2/28.
 */
public interface IPresenterFactory {
    /**
     * 返回Presenter实现类的实例
     *
     * @param view  view的实例
     * @param clazz presenter的业务接口类
     * @return presenter的实例
     */
    @Nullable
    IPresenter newPresenter(IView view, Class clazz);
}
