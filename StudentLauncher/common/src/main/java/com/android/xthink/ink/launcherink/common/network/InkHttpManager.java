package com.android.xthink.ink.launcherink.common.network;


import android.os.Handler;
import android.os.Looper;
import com.android.xthink.ink.launcherink.common.network.model.HttpHeaders;
import com.android.xthink.ink.launcherink.common.network.model.HttpParams;
import com.android.xthink.ink.launcherink.common.network.request.GetRequest;
import com.android.xthink.ink.launcherink.common.network.request.PostRequest;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 这个类用来辅助网络请求Http的
 * Created by liuwenrong on 2017/1/20.
 */

public class InkHttpManager implements InkIHttpManager {

    public static final int DEFAULT_MILLISECONDS = 20000;       //默认的超时时间
    public static final int REFRESH_TIME = 100;                       //回调刷新时间（单位ms）
    private HttpParams mCommonParams;                           //全局公共请求参数
    private HttpHeaders mCommonHeaders;                         //全局公共请求头
    private int mRetryCount = 3;                                //全局超时重试次数

    /**
     * 采用单例模式
     */
    private static InkHttpManager httpManager;
    private static OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpClientBuilder;           //ok请求的客户端
    private Handler mHandler;                                   //用于在主线程执行的调度器

    /**
     * 单例模式,私有的构造方法
     */
    private InkHttpManager(){
        okHttpClient = new OkHttpClient();
        okHttpClientBuilder = okHttpClient.newBuilder();

        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        mHandler = new Handler(Looper.getMainLooper());

    }

    public static InkHttpManager getInstance(){
        if(httpManager == null){

            synchronized(InkHttpManager.class){
                if(httpManager == null){
                    httpManager = new InkHttpManager();
                }
            }

        }
        return httpManager;
    }


    /** get请求 */
    public GetRequest get(String url) {
        return new GetRequest(url);
    }

    /** post请求 */
    public PostRequest post(String url) {
        return new PostRequest(url);
    }

    /** get请求 */
    public GetRequest download(String url) {
        return new GetRequest(url);
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) okHttpClient = okHttpClientBuilder.build();
        return okHttpClient;
    }

    public Handler getDelivery() {
        return mHandler;
    }

    /** 超时重试次数 */
    public int getRetryCount() {
        return mRetryCount;
    }

    /** 获取全局公共请求参数 */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /** 添加全局公共请求参数 */
    public InkHttpManager addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /** 获取全局公共请求头 */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /** 添加全局公共请求参数 */
    public InkHttpManager addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /** 根据Tag取消请求 */
    public void cancelTag(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /** 取消所有请求请求 */
    public void cancelAll() {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

}
