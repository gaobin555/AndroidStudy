/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.network.callback;

import android.support.annotation.Nullable;

/**
 * 基本的网络请求回调
 *
 * @author 万驰
 * @version 1.0, 2017/3/29
 */

public interface BaseRequestCallback<T> {
    /**
     * 调用用户体系以后成功的回调
     *
     * @param result 成功则返回响应的对象,失败则对象为空
     */
    void onSuccess(T result);

    /**
     * 调用用户体系以后失败的回调
     *
     * @param errorCode 错误代码
     * @param errorInfo 错误信息
     */
    void onFailed(int errorCode, @Nullable String errorInfo);
}