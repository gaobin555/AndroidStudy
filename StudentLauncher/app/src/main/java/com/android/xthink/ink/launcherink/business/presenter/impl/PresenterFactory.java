package com.android.xthink.ink.launcherink.business.presenter.impl;

import android.os.Build;
import android.support.annotation.Nullable;

//import com.android.jv.ink.launcherink.common.mvp.presenter.IBadgePresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IDirectAppPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IPresenterFactory;
//import com.android.jv.ink.launcherink.common.mvp.presenter.IReadInfoPresenter;
import com.android.xthink.ink.launcherink.common.mvp.presenter.IWeChatPresenter;
import com.android.xthink.ink.launcherink.common.mvp.view.IDirectAppView;
import com.android.xthink.ink.launcherink.common.mvp.view.IView;
import com.android.xthink.ink.launcherink.common.mvp.view.IWeChatView;
//import com.android.jv.ink.launcherink.ui.user.buycard.BuyCardPresenter;
//import com.android.jv.ink.launcherink.ui.user.buycard.IBuyCardPresenter;
//import com.android.jv.ink.launcherink.ui.user.buycard.IBuyCardView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * presenter的生产工厂
 * Created by wanchi on 2017/2/28.
 */

public class PresenterFactory implements IPresenterFactory {
    @Nullable
    @Override
    public IPresenter newPresenter(IView view, Class clazz) {

        final String className = clazz.getName();

        if (view instanceof IDirectAppView && className.equals(IDirectAppPresenter.class.getName())) {
            return new DirectAppPresenter(view);
        }

        return null;
    }


    /**
     * 根据类名获取业务名称
     *
     * @param className 类名
     * @return 业务名
     * @deprecated 这原本是用反射解决重复问题的方案，但是反射需要业务类的包名，这样就把包名限定死了。暂时没想到解决方案，就先暂停。
     */
    private String getBusinessName(String className) {
        String matchPattern = "^I\\w*View$";
        Pattern r = Pattern.compile(matchPattern);
        Matcher m = r.matcher(className);
        if (m.matches()) {
            // 去除第一个I,去除最后一个View
            int length = className.length();
            return className.substring(1, length - 4);
        }
        return "";
    }
}
