package com.android.xthink.ink.launcherink.manager.net;

import java.io.File;
import java.util.Map;

/**
 * Created by liyuyan on 2016/12/23.
 */

public interface JvIRequestManager {

     void doGet(String url, JvIRequestCallBack callBack);

     void doPost(String url, String requestJsonBody, JvIRequestCallBack callBack);

     void doUpload(String url, String requestJsonBody, Map<String, File> map, JvIRequestCallBack callBack);

     void doDownload(String url, String requestJsonBody, JvIRequestCallBack callBack);
}
