package com.android.xthink.ink.launcherink.common.network.request;


import android.util.Log;

import com.android.xthink.ink.launcherink.common.network.InkHttpUtils;
import com.android.xthink.ink.launcherink.common.network.model.HttpHeaders;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 描    述：Post请求的实现类，注意需要传入本类的泛型
 *
 */
public class PostRequest extends BaseBodyRequest<PostRequest> {

    public PostRequest(String url) {
        super(url);
        method = "POST";
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        try {
            headers.put(HttpHeaders.HEAD_KEY_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            Log.e("xx", "generateRequest: ", e);
        }
        Request.Builder requestBuilder = InkHttpUtils.appendHeaders(headers);
        return requestBuilder.post(requestBody).url(url).tag(tag).build();
    }
}