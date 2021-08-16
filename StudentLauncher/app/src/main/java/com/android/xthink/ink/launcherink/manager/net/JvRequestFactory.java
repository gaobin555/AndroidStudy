package com.android.xthink.ink.launcherink.manager.net;

/**
 * Created by liyuyan on 2016/12/23.
 */

public class JvRequestFactory {
    public static JvIRequestManager getRequestManager() {
        return JvOkHttpManagerImpl.getInstance();
    }
}
