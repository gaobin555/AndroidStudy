package com.android.xthink.ink.launcherink.manager.net;

/**
 * Created by liyuyan on 2016/12/23.
 */

public interface JvIRequestCallBack {
    void onSuccess(String response);

    void onFailure(Throwable throwable);
}
