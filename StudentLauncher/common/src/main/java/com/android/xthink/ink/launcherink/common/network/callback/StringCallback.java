package com.android.xthink.ink.launcherink.common.network.callback;


import com.android.xthink.ink.launcherink.common.network.convert.StringConvert;

import okhttp3.Response;

/**
 * 描    述：返回字符串类型的数据
 *
 */
public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String convertSuccess(Response response) throws Exception {
        String s = StringConvert.create().convertSuccess(response);
        response.close();
        return s;
    }
}