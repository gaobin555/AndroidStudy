package com.android.xthink.ink.launcherink.manager.net;

import com.android.xthink.ink.launcherink.manager.InkAppManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liyuyan on 2016/12/23.
 */

public class JvOkHttpManagerImpl implements JvIRequestManager {

    private OkHttpClient mOkHttpClient;
    private JvWeakHandlerNew mHandler;

    public JvOkHttpManagerImpl() {
        mHandler = new JvWeakHandlerNew();
        File file = new File(InkAppManager.getSystemcachedir());
        Cache cache = new Cache(file, 10 * 1024 * 1024);
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cache(cache)
                // .addInterceptor(new GzipRequestInterceptor())
                .addInterceptor(new JvLoggingInterceptor())
                .build();
    }

    public static JvOkHttpManagerImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final JvOkHttpManagerImpl INSTANCE = new JvOkHttpManagerImpl();
    }

    @Override
    public void doGet(String url, JvIRequestCallBack callBack) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        addCallBack(callBack, request);
    }

    @Override
    public void doPost(String url, String requestJsonBody, JvIRequestCallBack callBack) {

    }

    @Override
    public void doUpload(String url, String requestJsonBody, Map<String, File> map, JvIRequestCallBack callBack) {

    }

    @Override
    public void doDownload(String url, String requestJsonBody, JvIRequestCallBack callBack) {

    }

    private void addCallBack(final JvIRequestCallBack requestCallback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestCallback.onFailure(e);
                    }
                });

            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String json = response.body().string();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.onSuccess(json);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestCallback.onFailure(new IOException(response.message() + ",url=" + call.request().url().toString()));
                        }
                    });
                }
            }
        });
    }
}
