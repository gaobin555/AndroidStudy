package com.android.xthink.ink.launcherink.common.network.callback;


import com.android.xthink.ink.launcherink.common.network.convert.Converter;
import com.android.xthink.ink.launcherink.common.network.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 *
 * 描   述：抽象的回调接口
 *     Created by liuwenrong on 2017/1/22.
 */
public abstract class AbsCallback<T> implements Converter<T> {

    /** 请求网络开始前，UI线程 */
    public void onBefore(BaseRequest request) {
    }

    /** 对返回数据进行操作的回调， UI线程 */
    public abstract void onSuccess(T t, Call call, Response response);

    /** 缓存成功的回调,UI线程 */
    public void onCacheSuccess(T t, Call call) {
    }

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    public void onError(Call call, Response response, Exception e) {
    }

    /** 缓存失败的回调,UI线程 */
    public void onCacheError(Call call, Exception e) {
    }

    /** 网络失败结束之前的回调 */
    public void parseError(Call call, Exception e) {
    }

    /** 请求网络结束后，UI线程 */
    public void onAfter(T t, Exception e) {
        if (e != null) e.printStackTrace();
    }

    /**
     * Post执行上传过程中的进度回调，get请求不回调，UI线程
     *
     * @param currentSize  当前上传的字节数
     * @param totalSize    总共需要上传的字节数
     * @param progress     当前上传的进度
     * @param networkSpeed 当前上传的速度 字节/秒
     */
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }

    /**
     * 执行下载过程中的进度回调，UI线程
     *
     * @param currentSize  当前下载的字节数
     * @param totalSize    总共需要下载的字节数
     * @param progress     当前下载的进度
     * @param networkSpeed 当前下载的速度 字节/秒
     */
    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }
}