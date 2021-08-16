package com.android.xthink.ink.launcherink.common.network.request;

import com.android.xthink.ink.launcherink.common.network.InkHttpManager;
import com.android.xthink.ink.launcherink.common.network.call.CacheCall;
import com.android.xthink.ink.launcherink.common.network.callback.AbsCallback;
import com.android.xthink.ink.launcherink.common.network.convert.Converter;
import com.android.xthink.ink.launcherink.common.network.model.HttpHeaders;
import com.android.xthink.ink.launcherink.common.network.model.HttpParams;

import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * 所有请求的基类，其中泛型 R 主要用于属性设置方法后，返回对应的子类型，以便于实现链式调用
 * Created by liuwenrong on 2018/1/22.
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected String url;
    protected String method;
    protected String baseUrl;
    protected Object tag;
    protected HttpParams params = new HttpParams();                 //添加的param
    protected HttpHeaders headers = new HttpHeaders();              //添加的header

    private AbsCallback mCallback;
    private Converter mConverter;
    private Request mRequest;
    private int retryCount;

    public BaseRequest(String url) {
        this.url = url;
        baseUrl = url;
        InkHttpManager go = InkHttpManager.getInstance();
        //默认添加 Accept-Language
//        String acceptLanguage = HttpHeaders.getAcceptLanguage();
//        if (!TextUtils.isEmpty(acceptLanguage)) headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
//        //默认添加 User-Agent
//        String userAgent = HttpHeaders.getUserAgent();
//        if (!TextUtils.isEmpty(userAgent)) headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        //添加公共请求参数
        if (go.getCommonParams() != null) params.put(go.getCommonParams());
        if (go.getCommonHeaders() != null) headers.put(go.getCommonHeaders());

        //超时重试次数
        retryCount = go.getRetryCount();
    }

    public int getRetryCount() {
        return retryCount;
    }

    @SuppressWarnings("unchecked")
    public R url(String url) {
        this.url = url;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllHeaders() {
        headers.clear();
        return (R) this;
    }

    /**
     * 这里注意body的格式。默认我也不知道是什么，反正不是json
     */
    @SuppressWarnings("unchecked")
    public R params(HttpParams params) {
        this.params.put(params);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(Map<String, String> params, boolean... isReplace) {
        this.params.put(params, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, int value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, float value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, double value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, long value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, char value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, boolean value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addUrlParams(String key, List<String> values) {
        params.putUrlParams(key, values);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeParam(String key) {
        params.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllParams() {
        params.clear();
        return (R) this;
    }

    /**
     * 非阻塞方法，异步请求，但是回调在子线程中执行
     */
    @SuppressWarnings("unchecked")
    public <T> void execute(AbsCallback<T> callback) {
        mCallback = callback;
        mConverter = callback;
        new CacheCall<T>(this).execute(callback);

    }

    /**
     * 根据不同的请求方式和参数，生成不同的RequestBody
     */
    public abstract RequestBody generateRequestBody();

    /**
     * 根据不同的请求方式，将RequestBody转换成Request对象
     */
    public abstract Request generateRequest(RequestBody requestBody);

    /**
     * 根据当前的请求参数，生成对应的 Call 任务
     */
    public okhttp3.Call generateCall(Request request) {
        mRequest = request;
        return InkHttpManager.getInstance().getOkHttpClient().newCall(request);

    }

    /**
     * 获取同步call对象
     */
    public okhttp3.Call getCall() {
        //构建请求体，返回call对象
        RequestBody requestBody = generateRequestBody();
        mRequest = generateRequest(requestBody);
        return generateCall(mRequest);
    }

    public Converter getConverter() {
        return mConverter;
    }

}
