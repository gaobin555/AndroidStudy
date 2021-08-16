package com.android.xthink.ink.launcherink.common.network.call;


import com.android.xthink.ink.launcherink.common.network.InkHttpManager;
import com.android.xthink.ink.launcherink.common.network.callback.AbsCallback;
import com.android.xthink.ink.launcherink.common.network.callback.AbsCallbackWrapper;
import com.android.xthink.ink.launcherink.common.network.model.Response;
import com.android.xthink.ink.launcherink.common.network.request.BaseRequest;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 描    述：带缓存的请求
 * Created by liuwenrong on 2017/1/22.
 */
public class CacheCall<T> implements Call<T> {

    private volatile boolean canceled;
    private boolean executed;
    private BaseRequest baseRequest;
    private okhttp3.Call rawCall;
    private AbsCallback<T> mCallback;

    private int currentRetryCount;

    public CacheCall(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    @Override
    public void execute(AbsCallback<T> callback) {

        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        mCallback = callback;
        if (mCallback == null) mCallback = new AbsCallbackWrapper<>();

        //请求执行前UI线程调用
        mCallback.onBefore(baseRequest);
        //构建请求
        RequestBody requestBody = baseRequest.generateRequestBody();
        final Request request = baseRequest.generateRequest(requestBody);
        rawCall = baseRequest.generateCall(request);

        if (canceled) {
            rawCall.cancel();
        }
        currentRetryCount = 0;
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (e instanceof SocketTimeoutException && currentRetryCount < baseRequest.getRetryCount()) {
                    //超时重试处理
                    currentRetryCount++;
                    okhttp3.Call newCall = baseRequest.generateCall(call.request());
                    newCall.enqueue(this);
                } else {
                    mCallback.parseError(call, e);
                    //请求失败，一般为url地址错误，网络错误等,并且过滤用户主动取消的网络请求
                    if (!call.isCanceled()) {
                        sendFailResultCallback(false, call, null, e);
                    }
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                int responseCode = response.code();
                //304缓存数据

                //响应失败，一般为服务器内部错误，或者找不到页面等
                if (responseCode == 404 || responseCode >= 500) {
                    sendFailResultCallback(false, call, response, new Exception("服务器数据异常!"));
                    return;
                }

                try {
                    Response<T> parseResponse = parseResponse(response);
                    T data = parseResponse.body();
                    //网络请求成功，保存缓存数据
//                    handleCache(response.headers(), data);
                    //网络请求成功回调
                    sendSuccessResultCallback(false, data, call, response);
                } catch (Exception e) {
                    //一般为服务器响应成功，但是数据解析错误
                    sendFailResultCallback(false, call, response, e);
                }
            }
        });
    }


    /**
     * 失败回调，发送到主线程
     */
    @SuppressWarnings("unchecked")
    private void sendFailResultCallback(final boolean isFromCache, final okhttp3.Call call, final okhttp3.Response response, final Exception e) {

        InkHttpManager.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                if (mCallback == null) {
                    return;
                }
                mCallback.onError(call, response, e);
            }
        });

    }

    /**
     * 成功回调，发送到主线程
     */
    private void sendSuccessResultCallback(final boolean isFromCache, final T t, final okhttp3.Call call, final okhttp3.Response response) {

        InkHttpManager.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {

                mCallback.onSuccess(t, call, response);      //请求成功回调 （UI线程）
                mCallback.onAfter(t, null);                  //请求结束回调 （UI线程）
            }
        });
    }

    @Override
    public Response<T> execute() throws Exception {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        okhttp3.Call call = baseRequest.getCall();
        if (canceled) {
            call.cancel();
        }
        return parseResponse(call.execute());
    }

    private Response<T> parseResponse(okhttp3.Response rawResponse) throws Exception {
        //no inspection unchecked
        T body = (T) baseRequest.getConverter().convertSuccess(rawResponse);
        return Response.success(body, rawResponse);
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
        if (rawCall != null) {
            rawCall.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public Call<T> clone() {
        return new CacheCall<>(baseRequest);
    }

    @Override
    public BaseRequest getBaseRequest() {
        return baseRequest;
    }
}