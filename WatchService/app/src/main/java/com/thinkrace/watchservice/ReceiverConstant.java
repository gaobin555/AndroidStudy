package com.thinkrace.watchservice;

/**
 * @author mare
 * @Description:
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/8/31
 * @time 11:29
 */
public class ReceiverConstant {
    /**
     * 接收到语音文件链接并广播
     */
    public static final String ACTION_VOICE_LINK = "com.thinkrace_broadcast.VOICE_LINK";
    public static final String EXTRA_VOICE_LINK = "VOICE_LINK";/**

     /**
     * 获取语音列表
     */
    public static final String ACTION_VOICE_LIST = "com.thinkrace_broadcast.VOICE_LIST";
    public static final String ACTION_REQUEST_VOICE_LIST = "com.thinkrace_broadcast.REQUEST_VOICE_LIST";
    public static final String EXTRA_VOICE_LIST = "VOICE_LIST";/**

     * 语音已读标记
     */
    public static final String ACTION_UPDATE_READ = "com.thinkrace_broadcast.VOICE_UPDATE_READ";

    /**
     * 上传语音或图片
     */
    public static final String ACTION_VOICE_UPLOAD = "com.thinkrace_broadcast.VOICE_UPLOAD";
    public static final String ACTION_PIC_UPLOAD = "com.thinkrace_broadcast.PIC_UPLOAD";
    public static final String EXTRA_UPLOAD = "UPLOAD_RESULT";

    /**
     * sos号码广播
     **/
    public static final String ACTION_SOS_LIST = "com.thinkrace_broadcast.SOS_LIST";
    public static final String EXTRA_SOS_LIST = "SOS_LIST";

    /**
     * 设置联系人白名单广播
     */
    public static final String ACTION_CALL_WHITELIST_NUM = "com.thinkrace_broadcast.ACTION_CALL_EMERGENCY";
    public static final String EXTRA_CALL_WHITELIST_NUM = "white";

    /**
     * 设置网址白名单广播
     */
    public static final String ACTION_URL_WHITELIST = "com.thinkrace_broadcast.ACTION_URL_WHITELIST";
    public static final String EXTRA_URL_WHITELIST = "url";

    /**
     * 设置表盘广播
     */
    public static final String ACTION_CLOCKDIAL = "com.thinkrace_broadcast.ACTION_CLOCKDIAL";
    public static final String EXTRA_CLOCKDIAL = "ClockDial";

    /**
     * 天气广播
     */
    public static final String ACTION_WEATHER = "com.thinkrace_broadcast.ACTION_WEATHER";
    public static final String ACTION_REQUEST_WEATHER = "com.thinkrace_request.ACTION_WEATHER";
    public static final String EXTRA_WEATHER = "weather";

    /**
     * 低电提醒
     */
    public static final String ACTION_BP04 = "com.thinkrace.ACTION_BP04";
    public static final String ACTION_AP04 = "com.thinkrace.ACTION_AP04";
    public static final String EXTRA_BP04 = "BP04";


    /**
     * 语音上行
     */
    public static final String ACTION_BP07 = "com.thinkrace.ACTION_BP07";
    public static final String ACTION_AP07 = "com.thinkrace.ACTION_AP07";
    public static final String EXTRA_BP07_Path = "Path"; //文件路径
    public static final String EXTRA_BP07_Long = "Long"; //录制时长
    public static final String EXTRA_BP07_Type = "Type"; //1群聊或2单聊                                                                                                                                                                                                                                                                                                                                                                                           aaaaaaaaaaaaaaa 1 为群聊 2 为单聊
    public static final String EXTRA_BP07_Filetype = "Filetype"; //文件类型： 0 图片, 1 聊天语音, 5 视频
    public static final String EXTRA_BP07_Identity = "Identity"; //本地文件标识

    /**
     * 群聊语音(文本)下行
     */
    public static final String ACTION_BP28 = "com.thinkrace.ACTION_BP28";
    public static final String ACTION_AP28 = "com.thinkrace.ACTION_AP28";
    public static final String EXTRA_BP28 = "BP28";

    /**
     * 语音消息提醒
     */
    public static final String ACTION_BP27 = "com.thinkrace.ACTION_BP27";
    public static final String ACTION_AP27 = "com.thinkrace.ACTION_AP27";
    public static final String EXTRA_BP27 = "BP27";

    /**
     * 图片上行
     */
    public static final String ACTION_BP42 = "com.thinkrace.ACTION_BP42";
    public static final String ACTION_AP42 = "com.thinkrace.ACTION_AP42";
    public static final String EXTRA_BP42_Path = "Path"; //文件路径
    public static final String EXTRA_BP42_Type = "Type"; //群聊或单聊 1 为群聊 2 为单聊
    public static final String EXTRA_BP42_Filetype = "Filetype"; //文件类型： 0 图片, 1 聊天语音, 5 视频
    /**
     * 亲情号
     */
    public static final String ACTION_BP60 = "com.thinkrace.ACTION_BP60";
    public static final String ACTION_AP60 = "com.thinkrace.ACTION_AP60";
    public static final String  EXTRA_BP60 = "BP60";
    /**
     * 远程关机
     */
    public static final String ACTION_BP31 = "com.thinkrace.ACTION_BP31";
    public static final String ACTION_AP31 = "com.thinkrace.ACTION_AP31";
    public static final String  EXTRA_BP31 = "BP31";
    /**
     * 学生端APP控制
     */
    public static final String ACTION_BPD2 = "com.thinkrace.ACTION_BPD2";
    public static final String ACTION_APD2 = "com.thinkrace.ACTION_APD2";
    public static final String  EXTRA_BPD2 = "BPD2";
    public static final String  EXTRA_APD2 = "APD2";
    /**
     * 定时开关机
     */
    public static final String ACTION_BPD5 = "com.thinkrace.ACTION_BPD5";
    public static final String ACTION_APD5 = "com.thinkrace.ACTION_APD5";
    public static final String  EXTRA_BPD5 = "BPD5";
    /**
     * 挂失处理
     */
    public static final String ACTION_BPD6 = "com.thinkrace.ACTION_BPD6";
    public static final String ACTION_APD6 = "com.thinkrace.ACTION_APD6";
    public static final String  EXTRA_BPD6 = "BPD6";
    /**
     * 预留电量
     */
    public static final String ACTION_BPD7 = "com.thinkrace.ACTION_BPD7";
    public static final String ACTION_APD7 = "com.thinkrace.ACTION_APD7";
    public static final String  EXTRA_BPD7 = "BPD7";
    /**
     * 上课禁用
     */
    public static final String ACTION_BPD9 = "com.thinkrace.ACTION_BPD9";
    public static final String ACTION_APD9 = "com.thinkrace.ACTION_APD9";
    public static final String  EXTRA_BPD9 = "BPD9";
    // 课程表禁用
    public static final String ACTION_CURRICULUM_FORBIDDEN = "com.thinkrace.ACTION_CURRICULUM_FORBIDDEN";
    public static final String EXTRA_CURRICULUM_FORBIDDEN_DATA = "curriculum_forbidden_data";
    /**
     * 上学守护
     */
    public static final String ACTION_BPDA = "com.thinkrace.ACTION_BPDA";
    public static final String ACTION_APDA = "com.thinkrace.ACTION_APDA";
    public static final String  EXTRA_BPDA = "BPDA";
    /**
     * 重启手表
     */
    public static final String ACTION_BP18 = "com.thinkrace.ACTION_BP18";
    public static final String ACTION_AP18 = "com.thinkrace.ACTION_AP18";
    public static final String  EXTRA_BP18 = "BP18";
    /**
     * 找手表
     */
    public static final String ACTION_BP88 = "com.thinkrace.ACTION_BP88";
    public static final String ACTION_AP88 = "com.thinkrace.ACTION_AP88";
    public static final String  EXTRA_BP88 = "BP88";

    /**
     * 设置上课隐身时间段
     */
    public static final String ACTION_BP26 = "com.thinkrace.ACTION_BP26";
    public static final String ACTION_AP26 = "com.thinkrace.ACTION_AP26";
    public static final String  EXTRA_BP26 = "BP26";

    /**
     * 设置闹钟
     */
    public static final String ACTION_BP25 = "com.thinkrace.ACTION_BP25";
    public static final String ACTION_AP25 = "com.thinkrace.ACTION_AP25";
    public static final String  EXTRA_BP25 = "BP25";

    /**
     * 定时开关机
     */
    public static final String ACTION_BP79 = "com.thinkrace.ACTION_BP79";
    public static final String ACTION_AP79 = "com.thinkrace.ACTION_AP79";
    public static final String  EXTRA_BP79 = "BP79";
    /**
     * 查询话费
     */
    public static final String ACTION_BP78 = "com.thinkrace.ACTION_BP78";
    public static final String ACTION_AP78 = "com.thinkrace.ACTION_AP78";
    public static final String  EXTRA_BP78 = "BP78";

    /**
     * 远程监拍
     */
    public static final String ACTION_BP46 = "com.thinkrace.ACTION_BP46";
    public static final String ACTION_AP46 = "com.thinkrace.ACTION_AP46";
    public static final String  EXTRA_BP46 = "BP46";

    /**
     * 点赞手表
     */
    public static final String ACTION_BP43 = "com.thinkrace.ACTION_BP43";
    public static final String ACTION_AP43 = "com.thinkrace.ACTION_AP43";
    public static final String  EXTRA_BP43 = "BP43";
    /**
     * 搜索wifi
     */
    public static final String ACTION_BPNS = "com.thinkrace.ACTION_BPNS";
    public static final String ACTION_APNS = "com.thinkrace.ACTION_APNS";
    public static final String  EXTRA_BPNS = "BPNS";
    /**
     * 设置WIFI
     */
    public static final String ACTION_BPWS = "com.thinkrace.ACTION_BPWS";
    public static final String ACTION_APWS = "com.thinkrace.ACTION_APWS";
    public static final String  EXTRA_BPWS = "BPWS";
    /**
     * 语音聊天账号获取
     */
    public static final String ACTION_BPVA = "com.thinkrace.ACTION_BPVA";
    public static final String ACTION_APVA = "com.thinkrace.ACTION_APVA";
    public static final String  EXTRA_BPVA = "BPVA";

    /**
     * 监护人列表获取
     */
    public static final String ACTION_BPCL = "com.thinkrace.ACTION_BPCL";
    public static final String ACTION_APCL = "com.thinkrace.ACTION_APCL";
    public static final String  EXTRA_BPCL = "BPCL";
    /**
     * 好友列表获取
     */
    public static final String ACTION_BPT3 = "com.thinkrace.ACTION_BPT3";
    public static final String ACTION_APT3 = "com.thinkrace.ACTION_APT3";
    public static final String EXTRA_BPT3 = "BPT3 ";
    /**
     * 监护人列表通话id获取
     */
    public static final String ACTION_BPCL_ID = "com.thinkrace.ACTION_BPCL_ID";
    public static final String ACTION_APCL_ID = "com.thinkrace.ACTION_APCL_ID";
    public static final String  EXTRA_BPCL_ID = "BPCL_ID";

    /**
     * 碰碰交友
     */
    public static final String ACTION_BPT4 = "com.thinkrace.ACTION_BPT4";
    public static final String ACTION_APT4 = "com.thinkrace.ACTION_APT4";
    public static final String  EXTRA_BPT4 = "BPT4";

    /**
     * 学生证
     */
    public static final String ACTION_STUDENT = "com.thinkrace.ACTION_STUDENT";
    public static final String EXTRA_STUDENT = "student";

    /**
     * 课程表
     */
    public static final String ACTION_CURRICULUM = "com.thinkrace.ACTION_CURRICULUM";
    public static final String EXTRA_CURRICULUM = "curriculum";

    /**
     * 扫码交友
     */
    public static final String ACTION_REQUEST_FRIEND = "com.thinkrace.ACTION_REQUEST_FRIEND";
    public static final String EXTRA_IMEI = "imei";
    public static final String EXTRA_REQUEST_TYPE = "type"; // 类型 "1"请求添加好友 "2"同意好友请求
    public static final String EXTRA_IWBPTC = "IWBPTC"; // 扫码交友服务器响应数据
    public static final String EXTRA_IWBPD8 = "IWBPD8"; // 添加好友通知数据
    public static final String ACTION_ADD_FRIEND = "com.thinkrace.ACTION_ADD_FRIEND"; // 扫码交友服务器响应通知
    public static final String ACTION_ADD_FRIEND_NOTIFY = "com.thinkrace.ACTION_ADD_FRIEND_NOTIFY"; // 添加好友通知

    /**
     * 删除好友
     */
    public static final String ACTION_DELETE_FRIEND = "com.thinkrace.ACTION_APRF";
    public static final String ACTION_DELETE_FRIEND_SUCEED = "com.thinkrace.ACTION_DELETE_FRIEND_SUCEED"; // 删除好友完成

    /*单聊下行语音图片文字*/
    public static final String EXTRA_IWBPCD = "IWBPCD";
    public static final String ACTION_BPCD = "com.thinkrace.ACTION_BPCD";

    /* 单聊上行语音图片文字*/
    public static final String ACTION_APCU = "com.thinkrace.ACTION_APCU";
    public static final String EXTRA_APCU_ID = "APCU_id";
    public static final String EXTRA_APCU_CONTENT = "APCU_content";

    /*同步监护人列表*/
    public static final String ACTION_BPTD = "com.thinkrace.ACTION_BPTD";
    public static final String ACTION_APTD = "com.thinkrace.ACTION_APTD";
    public static final String EXTRA_BPTD = "BPTD";

    /**
     * 下行语音
     **/
    public static final String EXTRA_VOICE_PATH = "path";
    public static final String EXTRA_VOICE_TYPE = "type";// 0：群聊；1：单聊
    public static final String EXTRA_VOICE_TARGET = "target";
    public static final String EXTRA_VOICE_SEND = "send";
    public static final String ACTION_VOICE_MSG = "com.thinkrace.ACTION_VOICE_MSG";

    /**
     * 带列表的新语音提醒
     */
    public static final String EXTRA_BPVL= "BPVL";
    public static final String EXTRA_BPVLN= "BPVLN";

    /**
     * 登录返回
     */
    public static final String ACTION_BPLN = "com.thinkrace.ACTION_BPLN";

    /*--------------------------------以上为IW 协议-----------------------------------*/

    /**
     * 定位开始闹钟
     **/
    public static final String LOCATION_START = "com.thinkrace.intent.ACTION_ALARM_LOCATION_START";
    /**
     * 定位停止闹钟
     **/
    public static final String LOCATION_STOP = "com.thinkrace.intent.ACTION_ALARM_LOCATION_STOP";

    /**
     * 固定频率定时上传闹钟
     **/
    public static final String CONFIRMED_FREQUENCY_UPLOAD = "com.thinkrace.intent.ACTION_ALARM_CONFIRMED_FREQUENCY_UPLOAD";

}
