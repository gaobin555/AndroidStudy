package com.android.xthink.ink.launcherink.common.mvp.presenter;

import android.content.Intent;

/**
 * mvp中的P
 * Created by wanchi on 2017/2/22.
 */
public interface IPresenter {
    /**
     * 回调
     * @return true,已经处理.
     */
    boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 注册广播
     */
    String[] registerReceivers();

    /**
     * 接收到广播
     */
    void onBroadcastReceive(Intent intent);

    /**
     * UI销毁,释放view
     */
    void onDestroyView();
}
