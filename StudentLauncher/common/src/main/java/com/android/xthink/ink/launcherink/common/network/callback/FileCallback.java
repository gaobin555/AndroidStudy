package com.android.xthink.ink.launcherink.common.network.callback;


import com.android.xthink.ink.launcherink.common.network.convert.FileConvert;

import java.io.File;

import okhttp3.Response;

/**
 * 描    述：文件的回调下载进度监听
 * Created by liuwenrong on 2017/1/23.
 */
public abstract class FileCallback extends AbsCallback<File> {

    private FileConvert convert;    //文件转换类

    public FileCallback() {
        this(null);
    }

    public FileCallback(String destFileName) {
        this(null, destFileName);
    }

    public FileCallback(String destFileDir, String destFileName) {
        convert = new FileConvert(destFileDir, destFileName);
        convert.setCallback(this);
    }

    @Override
    public File convertSuccess(Response response) throws Exception {
        File file = convert.convertSuccess(response);
        response.close();
        return file;
    }
}