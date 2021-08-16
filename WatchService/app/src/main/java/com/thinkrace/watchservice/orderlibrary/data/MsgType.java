package com.thinkrace.watchservice.orderlibrary.data;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * 消息头部标记位
 * Created by fanyang on 2017/8/4.
 */
public class MsgType {
    /*链路保持*/
    public static final String IWAP00 = "IWAP00";
    public static final String IWBP00 = "IWBP00";
    /*链路保持(带电量 步数 翻滚信息)*/
    public static final String IWAPLN = "IWAPLN";
    public static final String IWBPLN = "IWBPLN";
    /*链路保持(心跳包)*/
    public static final String IWAP03 = "IWAP03";
    public static final String IWBP03 = "IWBP03";
    /*位置数据上报(智能机)*/
    public static final String IWAPT1 = "IWAPT1";
    public static final String IWBPT1 = "IWBPT1";
    /*定位指令*/
    public static final String IWBP16 = "IWBP16";
    public static final String IWAP16 = "IWAP16";
    /*恢复出厂设置*/
    public static final String IWBP17 = "IWBP17";
    public static final String IWAP17 = "IWAP17";
    /*获取天气-智能机*/
    public static final String IWAPTQ = "IWAPTQ";
    public static final String IWBPTQ = "IWBPTQ";
    /*设置表盘*/
    public static final String IWAPS1 = "IWAPS1";
    public static final String IWBPS1 = "IWBPS1";
    /*数据上传间隔设置*/
    public static final String IWBP15 = "IWBP15";
    public static final String IWAP15 = "IWAP15";
    /*设置3个SOS号码*/
    public static final String IWBP12 = "IWBP12";
    public static final String IWAP12 = "IWAP12";
    /*设置联系人白名单(10个)*/
    public static final String IWBP14 = "IWBP14";
    public static final String IWAP14 = "IWAP14";
    /*网址白名单*/
    public static final String IWBPD4 = "IWBPD4";
    public static final String IWAPD4 = "IWAPD4";
    /*短信代收*/
    public static final String IWAPTB = "IWAPTB";
    public static final String IWBPTB = "IWBPTB";
    /*低电提醒*/
    public static final String IWBP04 = "IWBP04";
    public static final String IWAP04 = "IWAP04";
    /*语音上行*/
    public static final String IWAP07 = "IWAP07";
    public static final String IWBP07 = "IWBP07";
    /*语音下行- URL*/
    public static final String IWAPVU = "IWAPVU";
    public static final String IWBPVU = "IWBPVU";
    /*语音消息提醒*/
    public static final String IWAP27 = "IWAP27";
    public static final String IWBP27 = "IWBP27";
    /*图片上行*/
    public static final String IWAP42 = "IWAP42";
    public static final String IWBP42 = "IWBP42";
    /*亲情号*/
    public static final String IWAP60 = "IWAP60";
    public static final String IWBP60 = "IWBP60";
    /*远程关机*/
    public static final String IWAP31 = "IWAP31";
    public static final String IWBP31 = "IWBP31";
    /*工作模式*/
    public static final String IWAP33 = "IWAP33";
    public static final String IWBP33 = "IWBP33";
    /*学生端APP控制*/
    public static final String IWAPD2 = "IWAPD2";
    public static final String IWBPD2 = "IWBPD2";
    public static final String IWAPTE = "IWAPTE";
    public static final String IWBPTE = "IWBPTE";
    public static final String IWAP86 = "IWAP86";
    /*定时开关机*/
    public static final String IWAPD5 = "IWAPD5";
    public static final String IWBPD5 = "IWBPD5";
    /*挂失处理*/
    public static final String IWAPD6 = "IWAPD6";
    public static final String IWBPD6 = "IWBPD6";
    /*预留电量*/
    public static final String IWAPD7 = "IWAPD7";
    public static final String IWBPD7 = "IWBPD7";
    /*上课禁用*/
    public static final String IWAPD9 = "IWAPD9";
    public static final String IWBPD9 = "IWBPD9";
    /*上学守护*/
    public static final String IWAPDA = "IWAPDA";
    public static final String IWBPDA = "IWBPDA";
    /*重启手表*/
    public static final String IWAP18 = "IWAP18";
    public static final String IWBP18 = "IWBP18";
    /*找手表*/
    public static final String IWAP88 = "IWAP88";
    public static final String IWBP88 = "IWBP88";
    /*设置上课隐身时间段*/
    public static final String IWAP26 = "IWAP26";
    public static final String IWBP26 = "IWBP26";
    /*设置上课隐身时间段*/
    public static final String IWAP25 = "IWAP25";
    public static final String IWBP25 = "IWBP25";
    /*定时开关机*/
    public static final String IWAP79 = "IWAP79";
    public static final String IWBP79 = "IWBP79";
    /*查询话费*/
    public static final String IWAP78 = "IWAP78";
    public static final String IWBP78 = "IWBP78";
    /*远程监拍*/
    public static final String IWAP46 = "IWAP46";
    public static final String IWBP46 = "IWBP46";
    /*点赞手表*/
    public static final String IWAP43 = "IWAP43";
    public static final String IWBP43 = "IWBP43";
    /*搜索wifi*/
    public static final String IWAPNS = "IWAPNS";
    public static final String IWBPNS = "IWBPNS";
    /*设置WIFI*/
    public static final String IWAPWS = "IWAPWS";
    public static final String IWBPWS = "IWBPWS";

    /*获取语音聊天列表*/
    public static final String IWAPCL = "IWAPCL";
    public static final String IWBPCL = "IWBPCL";
    /*好友列表*/
    public static final String IWAPT3 = "IWAPT3";
    public static final String IWBPT3 = "IWBPT3";

    /*碰碰交友*/
    public static final String IWAPT4 = "IWAPT4";
    public static final String IWBPT4 = "IWBPT4";

    /*学生证*/
    public static final String IWAPD3 = "IWAPD3";
    public static final String IWBPD3 = "IWBPD3";

    /*课程表*/
    public static final String IWAPD1 = "IWAPD1";
    public static final String IWBPD1 = "IWBPD1";

    /*扫码交友*/
    public static final String IWAPTC = "IWAPTC";
    public static final String IWBPTC = "IWBPTC";
    /*删除好友*/
    public static final String IWAPRF = "IWAPRF";// 上行请求
    public static final String IWBPRF = "IWBPRF";
    public static final String IWBPDF = "IWBPDF";// 下行回应

    /*添加好友通知设备*/
    public static final String IWAPD8 = "IWAPD8";
    public static final String IWBPD8 = "IWBPD8";

    /*单聊上行语音图片文字*/
    public static final String IWAPCU = "IWAPCU";
    public static final String IWBPCU = "IWBPCU";

    /*同步监护人列表*/
    public static final String IWAPTD = "IWAPTD";
    public static final String IWBPTD = "IWBPTD";

    /*单聊下行语音图片文字（App下行）*/
    public static final String IWAPCD = "IWAPCD";
    public static final String IWBPCD = "IWBPCD";

    /*群聊语音(文本)下行*/
    public static final String IWBP28 = "IWBP28";
    public static final String IWAP28 = "IWAP28";

    /*好友语音(文本)下行*/
    public static final String IWBP96 = "IWBP96";
    public static final String IWAP96 = "IWAP96";

    /*好友语音(文本)上行*/
    public static final String IWAP95 = "IWAP95";
    public static final String IWBP95 = "IWBP95";

    /*带列表的新语音提醒*/
    public static final String IWBPVL = "IWBPVL";

    /*语音查询接收协议*/
    public static final String IWBP05 = "IWBP05";

    /*设备绑定、解绑通知*/
    public static final String IWBP68 = "IWBP68";
    public static final String IWAP68 = "IWAP68";

    private static HashMap<String, String> mHeaderContent = new HashMap<>();

    static {
        mHeaderContent.put(IWAP00, IWAP00);
        mHeaderContent.put(IWAPLN, IWAPLN);
        mHeaderContent.put(IWAP03, IWAP03);
        mHeaderContent.put(IWAPT1, IWAPT1);
        mHeaderContent.put(IWAPTQ, IWAPTQ);
        mHeaderContent.put(IWAPS1, IWAPS1);
        mHeaderContent.put(IWAPVU, IWAPVU);
        mHeaderContent.put(IWAP15, IWAP15);
        mHeaderContent.put(IWAP12, IWAP12);
        mHeaderContent.put(IWAP16, IWAP16);
        mHeaderContent.put(IWAP14, IWAP14);
        mHeaderContent.put(IWAP04, IWAP04);
        mHeaderContent.put(IWAP07, IWAP07);
        mHeaderContent.put(IWAP27, IWAP27);
        mHeaderContent.put(IWAP42, IWAP42);
        mHeaderContent.put(IWAP60, IWAP60);
        mHeaderContent.put(IWAP31, IWAP31);
        mHeaderContent.put(IWAP18, IWAP18);
        mHeaderContent.put(IWAP88, IWAP88);
        mHeaderContent.put(IWAP26, IWAP26);
        mHeaderContent.put(IWAP25, IWAP25);
        mHeaderContent.put(IWAP79, IWAP79);
        mHeaderContent.put(IWAP78, IWAP78);
        mHeaderContent.put(IWAP46, IWAP46);
        mHeaderContent.put(IWAP43, IWAP43);
        mHeaderContent.put(IWAPNS, IWAPNS);
        mHeaderContent.put(IWAPWS, IWAPWS);
        mHeaderContent.put(IWAPCL, IWAPCL);
        mHeaderContent.put(IWAPT4, IWAPT4);
    }

    private MsgType() {
    }

    private static class SingletonHolder {
        private static final MsgType INSTANCE = new MsgType();
    }

    public static MsgType instance() {
        return SingletonHolder.INSTANCE;
    }

    public HashMap<String, String> getmHeaderContent() {
        return MsgType.mHeaderContent;
    }

    public boolean verifyMsgType(String msgType) { //发送时验证
        if (TextUtils.isEmpty(msgType)) {
            return false;
        }
        return MsgType.mHeaderContent.containsKey(msgType);
    }

}
