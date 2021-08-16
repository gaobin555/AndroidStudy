package com.thinkrace.watchservice.orderlibrary.data;


import com.xuhao.android.common.interfacies.client.msg.ISendable;

import java.nio.charset.Charset;

/**
 * Created by Tony on 2017/10/24.
 */

public class MsgDataBean implements ISendable {
    private String content = "";

    public MsgDataBean(String content) {
        this.content = content;
    }

    @Override
    public byte[] parse() {

        return content.getBytes(Charset.defaultCharset());
    }
}
