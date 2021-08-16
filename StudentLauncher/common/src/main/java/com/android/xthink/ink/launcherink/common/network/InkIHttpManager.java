package com.android.xthink.ink.launcherink.common.network;

import com.android.xthink.ink.launcherink.common.network.request.BaseRequest;

/**
 * Created by liuwenrong on 2017/1/20.
 */

public interface InkIHttpManager {

    BaseRequest get(String url);
    BaseRequest post(String url);
    BaseRequest download(String url);

}
