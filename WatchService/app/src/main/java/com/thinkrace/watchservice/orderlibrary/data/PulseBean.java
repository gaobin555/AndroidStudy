package com.thinkrace.watchservice.orderlibrary.data;


import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.thinkrace.watchservice.orderlibrary.function.step.StepUtils;
import com.thinkrace.watchservice.orderlibrary.utils.BatteryUtils;
import com.thinkrace.watchservice.orderlibrary.utils.GSMCellLocationUtils;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;
import com.xuhao.android.libsocket.sdk.client.bean.IPulseSendable;

import java.nio.charset.Charset;

public class PulseBean implements IPulseSendable {
    private String str = "";

    public PulseBean() {
    }

    @Override
    public byte[] parse() {
        getInfo();
        return str.getBytes(Charset.defaultCharset());
    }

    private void getInfo() {
        String gsm = GSMCellLocationUtils.getMobileDbm(Utils.getContext());
        String battery = BatteryUtils.getBatteryLevel(Utils.getContext());
        LogUtils.d("battery=" + battery);
        String content = gsm + "000" + battery + "00000" + GlobalSettings.MSG_CONTENT_SEPERATOR + StepUtils.getStepCount(Utils.getContext()) + GlobalSettings.MSG_CONTENT_SEPERATOR + "30";
        str = MsgType.IWAP03
                + GlobalSettings.MSG_CONTENT_SEPERATOR
                + content
                + GlobalSettings.MSG_SUFFIX_ESCAPE;
    }
}