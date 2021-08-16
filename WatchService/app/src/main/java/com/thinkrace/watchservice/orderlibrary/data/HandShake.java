package com.thinkrace.watchservice.orderlibrary.data;

import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.xuhao.android.common.interfacies.client.msg.ISendable;

import java.nio.charset.Charset;

/**
 * Created by xuhao on 2017/5/22.
 */

public class HandShake implements ISendable {
    private String content = "";

    public HandShake() {
       //连接成功发一次登录包
        content = MsgType.IWAPLN
                + GlobalSettings.MSG_CONTENT_SEPERATOR
                +GlobalSettings.instance().getImei()
                +GlobalSettings.MSG_CONTENT_SEPERATOR
                +GlobalSettings.instance().getImsi()
                +GlobalSettings.MSG_SUFFIX_ESCAPE;
    }

    @Override
    public byte[] parse() {
        return content.getBytes(Charset.defaultCharset());
    }
}
