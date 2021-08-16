package com.android.xthink.ink.launcherink.common.mvp.presenter;

import android.support.annotation.NonNull;

import com.android.xthink.ink.launcherink.common.mvp.view.IView;


/**
 * 用于绑定presenter
 * Created by wanchi on 2017/2/28.
 */
public class Presenter {
    // 负责生成Presenter的工厂类
    private static IPresenterFactory sPresenterFactory;

    private Presenter() {
        // do nothing
    }

    /**
     * 初始化此Presenter类
     */
    public static void init(@NonNull IPresenterFactory factory) {
        sPresenterFactory = factory;
    }

    /**
     * view与presenter进行绑定
     *
     * @param view 当前进行绑定的view
     */
    public static void bind(@NonNull IView view, Class... clazzs) {
        if (sPresenterFactory == null) {
            throw new RuntimeException("must invoke Presenter.init() to init IPresenterFactory first!");
        }

        for (Class clazz : clazzs) {
            IPresenter presenter = sPresenterFactory.newPresenter(view, clazz);
            if (presenter != null) {
                view.addPresenter(presenter);
            }
        }
    }
}
