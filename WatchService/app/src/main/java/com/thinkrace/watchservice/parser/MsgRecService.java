package com.thinkrace.watchservice.parser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.thinkrace.watchservice.function.location.LocationUploadManager;
import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.orderlibrary.data.TcpMsg;
import com.thinkrace.watchservice.orderlibrary.utils.DeviceInfoUtils;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;
import com.thinkrace.watchservice.ReceiverConstant;
import com.thinkrace.watchservice.receiver.VoiceFileReceiver;
import com.xuhao.android.common.constant.OrderConstans;
import com.xuhao.android.libsocket.sdk.OkSocket;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * 处理接收的信息
 * Created by fanyang on 2017/8/8.
 */
public class MsgRecService {

    public static String tempSearchWifiSerialNumber;
    public static String bp46SerialNumber;
    private static int tempMsgNumber = 0; // 未收信息数量

    private MsgRecService() {

    }

    public static MsgRecService instance() {
        return SingletonHolder.INSTANCE;
    }

    @SuppressLint("MissingPermission")
    public void handleRecvMsg(Context ctx, TcpMsg msg) {
        ParseSourceDataBytes parseBytes = null;
        try {
            parseBytes = new ParseSourceDataBytes(msg).invoke();
        } catch (Exception e) {
            LogUtils.e("handleRecvMsg " + e);
        } finally {
            if (null == parseBytes) {
                LogUtils.e("消息解析异常!!!!!! ");
                return;
            }
        }
        if (parseBytes.isNullResult())
            return;
        final String headerType = parseBytes.getHeaderType();
        String content = parseBytes.getContent();//包含IMEI和之后的内容
        String orderContent = parseBytes.getOrderContent();//指令流水号之后的内容(不含流水号,imei)
        byte[] contentBytes = parseBytes.getContentBytes();
        LogUtils.d(" headerType= " + headerType);
        LogUtils.d(" orderContent= " + orderContent);
        switch (headerType) {
            case MsgType.IWBPLN:// 登录包
                Intent bplnIntent = new Intent(ReceiverConstant.ACTION_BPLN);
                Utils.getContext().sendBroadcast(bplnIntent);
                VoiceFileReceiver.voiceMsgDataReset();
                // 登录30S后查询未读信息 by gaobin start +++
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(30000);
                        if (!VoiceFileReceiver.getReceiving()) {// 空闲时下载语音
                            OrderUtil.getInstance().voiceQueryReceive("1");// 1:接收 0 查询数量 2 查询列表
                        }
                    }
                }).start();
                // end +++
                break;
            case MsgType.IWBPTQ: //获得天气返回数据后广播
                Intent weatherIntent = new Intent(ReceiverConstant.ACTION_WEATHER);
                weatherIntent.putExtra(ReceiverConstant.EXTRA_WEATHER, parseBytes.getContent());
                Utils.getContext().sendBroadcast(weatherIntent);
                break;
            case MsgType.IWBP33://工作模式（上传位置时间间隔设置）
                MsgSender.sendTxtMsg(MsgType.IWAP33, parseBytes.getResponseContent());
                String[] split = content.split(",");
                String mode = split[split.length - 1];
                LogUtils.d(" 工作模式 = " + mode);
                LocationUploadManager.instance().parseInteraval(mode);
                break;
            case MsgType.IWBP12: //sos号码
                MsgSender.sendTxtMsg(MsgType.IWAP12, parseBytes.getResponseContent());
                if (TextUtils.isEmpty(parseBytes.getOrderContent()))
                    return;
                LogUtils.d(" sos号码 = " + parseBytes.getOrderContent());
                Settings.Global.putString(OkSocket.getContext().getContentResolver(), "SOS_LIST", parseBytes.getOrderContent());
                Intent sosIntent = new Intent(ReceiverConstant.ACTION_SOS_LIST);
                sosIntent.putExtra(ReceiverConstant.EXTRA_SOS_LIST, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(sosIntent);
                break;
            case MsgType.IWBP16: //立即定位
                MsgSender.sendTxtMsg(MsgType.IWAP16, parseBytes.getSerialNumber());
                OkSocket.getContext().sendBroadcast(new Intent(ReceiverConstant.LOCATION_START));
                break;
            case MsgType.IWBP17: //恢复出厂设置
                MsgSender.sendTxtMsg(MsgType.IWAP17, parseBytes.getSerialNumber());
                Intent rstIntent = new Intent("android.intent.action.FACTORY_RESET");
                rstIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                rstIntent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
                rstIntent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", false);
                rstIntent.setPackage("android");
                OkSocket.getContext().sendBroadcast(rstIntent);
                break;
            case MsgType.IWBP14: //设置联系人白名单
                MsgSender.sendTxtMsg(MsgType.IWAP14, parseBytes.getResponseContent());
                Intent whiteListIntent = new Intent(ReceiverConstant.ACTION_CALL_WHITELIST_NUM);
                whiteListIntent.putExtra(ReceiverConstant.EXTRA_CALL_WHITELIST_NUM, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(whiteListIntent);
                break;
            case MsgType.IWBPD2: //学生端APP控制
                MsgSender.sendTxtMsg(MsgType.IWAPD2, parseBytes.getResponseContent());
                Intent appControlIntent = new Intent(ReceiverConstant.ACTION_BPD2);
                appControlIntent.putExtra(ReceiverConstant.EXTRA_BPD2, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(appControlIntent);
                break;
            case MsgType.IWBPD4: //设置网址白名单
                MsgSender.sendTxtMsg(MsgType.IWAPD4, parseBytes.getResponseContent());
                Intent whiteListUrlsIntent = new Intent(ReceiverConstant.ACTION_URL_WHITELIST);
                whiteListUrlsIntent.putExtra(ReceiverConstant.EXTRA_URL_WHITELIST, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(whiteListUrlsIntent);
                break;
            case MsgType.IWBPD5: //设置定时开关机
                MsgSender.sendTxtMsg(MsgType.IWAPD5, parseBytes.getResponseContent());
                Intent powerIntent = new Intent(ReceiverConstant.ACTION_BPD5);
                powerIntent.putExtra(ReceiverConstant.EXTRA_BPD5, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(powerIntent);
                break;
            case MsgType.IWBPD6: //挂失处理
                MsgSender.sendTxtMsg(MsgType.IWAPD6, parseBytes.getResponseContent());
                Intent loseIntent = new Intent(ReceiverConstant.ACTION_BPD6);
                loseIntent.putExtra(ReceiverConstant.EXTRA_BPD6, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(loseIntent);
                break;
            case MsgType.IWBPD7: //预留电量
                MsgSender.sendTxtMsg(MsgType.IWAPD7, parseBytes.getResponseContent());
                Intent reservePowerIntent = new Intent(ReceiverConstant.ACTION_BPD7);
                reservePowerIntent.putExtra(ReceiverConstant.EXTRA_BPD7, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(reservePowerIntent);
                break;
            case MsgType.IWBPD9: //上课禁用
                MsgSender.sendTxtMsg(MsgType.IWAPD9, parseBytes.getSerialNumber());
                Intent classForbiddenIntent = new Intent(ReceiverConstant.ACTION_BPD9);
                classForbiddenIntent.putExtra(ReceiverConstant.EXTRA_BPD9, orderContent);
                Utils.getContext().sendBroadcast(classForbiddenIntent);
                break;
            case MsgType.IWBPDA: //上学守护
                MsgSender.sendTxtMsg(MsgType.IWAPDA, parseBytes.getResponseContent());
                Intent guardIntent = new Intent(ReceiverConstant.ACTION_BPDA);
                guardIntent.putExtra(ReceiverConstant.EXTRA_BPDA, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(guardIntent);
                break;
            case MsgType.IWBPS1: //设置表盘
                MsgSender.sendTxtMsg(MsgType.IWAPS1, parseBytes.getResponseContent());
                String[] clock_dial = parseBytes.getResponseContent().split(GlobalSettings.MSG_CONTENT_SEPERATOR);

                Intent clockDialIntent = new Intent(ReceiverConstant.ACTION_CLOCKDIAL);
                clockDialIntent.putExtra(ReceiverConstant.EXTRA_CLOCKDIAL, clock_dial[clock_dial.length - 1]);
                Utils.getContext().sendBroadcast(clockDialIntent);
                break;
            case MsgType.IWBPCL://获取监护人列表
                Intent glIntent = new Intent(ReceiverConstant.ACTION_BPCL);
                glIntent.putExtra(ReceiverConstant.EXTRA_BPCL, parseBytes.getContent());
                Utils.getContext().sendBroadcast(glIntent);
                break;
            case MsgType.IWBPTD://同步监护人列表
                Intent jhrIntent = new Intent(ReceiverConstant.ACTION_BPTD);
                jhrIntent.putExtra(ReceiverConstant.EXTRA_BPTD, parseBytes.getContent());
                Utils.getContext().sendBroadcast(jhrIntent);
                break;
            case MsgType.IWBPT3://获取好友列表
                Intent hyIntent = new Intent(ReceiverConstant.ACTION_BPT3);
                hyIntent.putExtra(ReceiverConstant.EXTRA_BPT3, parseBytes.getContent());
                Utils.getContext().sendBroadcast(hyIntent);
                break;
            case MsgType.IWBP04: //低电提醒
                // do nothing;
                break;
            case MsgType.IWBPVU://语音下行   //TODO 是我这边下载(下载后保存在哪里,约定好), 还是硬件那边下载
                //接收到语音文件链接并广播  一条语音
                MsgSender.sendTxtMsg(MsgType.IWAPVU, parseBytes.getSerialNumber());
                Intent voiceLinkIntent = new Intent(ReceiverConstant.ACTION_VOICE_LINK);
                voiceLinkIntent.putExtra(ReceiverConstant.EXTRA_VOICE_LINK, parseBytes.getResponseContent());
                Utils.getContext().sendBroadcast(voiceLinkIntent);
                break;
            case MsgType.IWBP27://语音消息提醒 // IWBP27,868872039000008,541058,2#
                MsgSender.sendTxtMsg(MsgType.IWAP27, parseBytes.getResponseContent());//回复
                if (!VoiceFileReceiver.getReceiving() && Integer.valueOf(orderContent) > 0) {// 空闲时下载语音
                    OrderUtil.getInstance().voiceQueryReceive("1");// 1:接收 0 查询数量 2 查询列表
                }
                break;
            case MsgType.IWBP60://亲情号
                MsgSender.sendTxtMsg(MsgType.IWAP60, parseBytes.getSerialNumber() + GlobalSettings.MSG_CONTENT_SEPERATOR + "1");//回复
                //广播
                Intent familyNumIntent = new Intent(ReceiverConstant.ACTION_BP60);
                familyNumIntent.putExtra(ReceiverConstant.EXTRA_BP60, parseBytes.getResponseContent());
                Utils.getContext().sendBroadcast(familyNumIntent);
                break;
            case MsgType.IWBP31://远程关机
                MsgSender.sendTxtMsg(MsgType.IWAP31, parseBytes.getSerialNumber());//回复
                //广播
                Intent BP31Intent = new Intent(ReceiverConstant.ACTION_BP31);
                Utils.getContext().sendBroadcast(BP31Intent);
                break;
            case MsgType.IWBP18://重启手表
                MsgSender.sendTxtMsg(MsgType.IWAP18, parseBytes.getSerialNumber());//回复
                //广播
                Intent BP18Intent = new Intent(ReceiverConstant.ACTION_BP18);
                Utils.getContext().sendBroadcast(BP18Intent);
                break;
            case MsgType.IWBP88://寻找设备
                MsgSender.sendTxtMsg(MsgType.IWAP88, parseBytes.getSerialNumber());//回复
                //广播
                Intent BP88Intent = new Intent(ReceiverConstant.ACTION_BP88);
                Utils.getContext().sendBroadcast(BP88Intent);
                break;
            case MsgType.IWBP26://设置上课隐身时间段
                MsgSender.sendTxtMsg(MsgType.IWAP26, parseBytes.getResponseContent());//回复
                //广播
                Intent invisibleIntent = new Intent(ReceiverConstant.ACTION_BP26);
                invisibleIntent.putExtra(ReceiverConstant.EXTRA_BP26, parseBytes.getResponseContent());
                Utils.getContext().sendBroadcast(invisibleIntent);
                break;
            case MsgType.IWBP25://设置闹钟
                MsgSender.sendTxtMsg(MsgType.IWAP25, parseBytes.getResponseContent());//回复
                //广播
                Intent alarmIntent = new Intent(ReceiverConstant.ACTION_BP25);
                alarmIntent.putExtra(ReceiverConstant.EXTRA_BP25, parseBytes.getResponseContent());
                Utils.getContext().sendBroadcast(alarmIntent);
                break;
            case MsgType.IWBP79://定时开关机
                MsgSender.sendTxtMsg(MsgType.IWAP79, parseBytes.getSerialNumber() + GlobalSettings.MSG_CONTENT_SEPERATOR + "1");//回复
                //广播
                Intent timeSwitchIntent = new Intent(ReceiverConstant.ACTION_BP79);
                timeSwitchIntent.putExtra(ReceiverConstant.EXTRA_BP79, parseBytes.getResponseContent());
                Utils.getContext().sendBroadcast(timeSwitchIntent);
                break;
            case MsgType.IWBP78://查询话费
                MsgSender.sendTxtMsg(MsgType.IWAP78, parseBytes.getSerialNumber() + GlobalSettings.MSG_CONTENT_SEPERATOR + "1");//回复
                //广播
                Intent BP78Intent = new Intent(ReceiverConstant.ACTION_BP78);
                Utils.getContext().sendBroadcast(BP78Intent);
                break;
            case MsgType.IWBP46://立即拍照
                bp46SerialNumber = parseBytes.getSerialNumber();
                MsgSender.sendTxtMsg(MsgType.IWAP46, parseBytes.getSerialNumber() + GlobalSettings.MSG_CONTENT_SEPERATOR + "1");//回复
                //广播
                Intent photographIntent = new Intent(ReceiverConstant.ACTION_BP46);
                photographIntent.putExtra(ReceiverConstant.EXTRA_BP46, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(photographIntent);
                break;
            case MsgType.IWBP43://点赞手表     为避免麻烦, 现在暂时回复1默认表示成功
                MsgSender.sendTxtMsg(MsgType.IWAP43, parseBytes.getSerialNumber() + GlobalSettings.MSG_CONTENT_SEPERATOR + "1");//回复
                //广播
                Intent linkIntent = new Intent(ReceiverConstant.ACTION_BP43);
                linkIntent.putExtra(ReceiverConstant.EXTRA_BP43, parseBytes.getOrderContent()); //1为增加, 0为减去
                Utils.getContext().sendBroadcast(linkIntent);
                break;
            case MsgType.IWBPNS://搜索wifi   广播通知手表搜索   -手表需广播通知我回复
                tempSearchWifiSerialNumber = parseBytes.getSerialNumber();
                String wifiInfo = OrderUtil.getInstance().getWifiInfo();
                //广播
                Intent searchWifiIntent = new Intent(ReceiverConstant.ACTION_BPNS);
                searchWifiIntent.putExtra(ReceiverConstant.EXTRA_BPNS, wifiInfo);
                Utils.getContext().sendBroadcast(searchWifiIntent);
                break;
            case MsgType.IWBPWS://设置wifi   广播通知手表设置wifi
                MsgSender.sendTxtMsg(MsgType.IWAPWS, parseBytes.getResponseContent());
                //广播
                Intent setWifiIntent = new Intent(ReceiverConstant.ACTION_BPWS);
                setWifiIntent.putExtra(ReceiverConstant.EXTRA_BPWS, parseBytes.getOrderContent());
                Utils.getContext().sendBroadcast(setWifiIntent);
                break;

            case MsgType.IWBPD3:// 学生证
                MsgSender.sendTxtMsg(MsgType.IWAPD3, parseBytes.getResponseContent());
                Intent studentIntent = new Intent(ReceiverConstant.ACTION_STUDENT);
                studentIntent.putExtra(ReceiverConstant.EXTRA_STUDENT, orderContent);
                Utils.getContext().sendBroadcast(studentIntent);
                break;

            case MsgType.IWBPD1:// 课程表
                MsgSender.sendTxtMsg(MsgType.IWAPD1, parseBytes.getResponseContent());
                Intent curriculumIntent = new Intent(ReceiverConstant.ACTION_CURRICULUM);
                curriculumIntent.putExtra(ReceiverConstant.EXTRA_CURRICULUM, orderContent);
                Utils.getContext().sendBroadcast(curriculumIntent);
                break;

            case MsgType.IWBPTC:// 扫码交友
                Intent addfriend = new Intent(ReceiverConstant.ACTION_ADD_FRIEND);
                addfriend.putExtra(ReceiverConstant.EXTRA_IWBPTC, content);
                LogUtils.d("gaobin 请求添加好友返回:" + content);
                Utils.getContext().sendBroadcast(addfriend);
                break;

            case MsgType.IWBPD8:// 添加好友通知设备
                MsgSender.sendTxtMsg(MsgType.IWAPD8, parseBytes.getResponseContent());
                Intent addFriendNotify = new Intent(ReceiverConstant.ACTION_ADD_FRIEND_NOTIFY);
                addFriendNotify.putExtra(ReceiverConstant.EXTRA_IWBPD8, orderContent);
                LogUtils.d("gaobin 添加好友通知设备:" + orderContent);
                Utils.getContext().sendBroadcast(addFriendNotify);
                break;

            case MsgType.IWBPRF:// 删除好友
                Intent deleteFriend = new Intent(ReceiverConstant.ACTION_DELETE_FRIEND_SUCEED);
                Utils.getContext().sendBroadcast(deleteFriend);
                break;

            case MsgType.IWBPDF:// 删除好友下行
                String[] tempBPDF = content.split(",");
                Intent deleteFriend2 = new Intent(ReceiverConstant.ACTION_DELETE_FRIEND_SUCEED);
                deleteFriend2.putExtra(ReceiverConstant.EXTRA_IMEI, tempBPDF[0]);
                Utils.getContext().sendBroadcast(deleteFriend2);
                break;

            case MsgType.IWBPCD:// 单聊下行语音图片文字
                String[] temp = content.split(",");
                Intent dlintent;
                if (temp[3].equals("3")) { // 文本
                    MsgSender.sendTxtMsg(MsgType.IWAPCD, temp[0] + "," + temp[2] + "," + temp[3] + ",1");
                    dlintent = new Intent(ReceiverConstant.ACTION_BPCD);
                } else {// 语音和图片IWBPCD,214969,72387238,014310,1,3,1,1024,#!AMR
                    dlintent = new Intent(OrderConstans.BPCD);
                }
                dlintent.putExtra(ReceiverConstant.EXTRA_IWBPCD, content);
                Utils.getContext().sendBroadcast(dlintent);
                break;

            case MsgType.IWBP28:// 语音(文本)下行 72387238,,0,0,12,75306D0B6D0B
                Intent qlintent;
                LogUtils.d("gaobin content:" + content);
                String[] qltemp = content.split(",");
                if ("0".equals(qltemp[2])) {// 文字下行
                    MsgSender.sendTxtMsg(MsgType.IWAP28, qltemp[0] + "," + qltemp[1] + ",0,0,1");
                    qlintent = new Intent(ReceiverConstant.ACTION_BP28);
                } else { // content:,,3,1,1024,#!AMR
                    qlintent = new Intent(OrderConstans.BP28);
                }
                qlintent.putExtra(ReceiverConstant.EXTRA_BP28, content);
                Utils.getContext().sendBroadcast(qlintent);
                break;

            case MsgType.IWBPVL:// content：1,818707@1@2019-10-30 03:15:28
                String[] vltemp = content.split(",");
                tempMsgNumber = Integer.parseInt(vltemp[0]);
                if (tempMsgNumber > 0) {
                    LogUtils.d("gaobin IWBPVL 未接收消息数:" + tempMsgNumber);
                    Intent vlintent = new Intent(OrderConstans.BPVL);
                    vlintent.putExtra(ReceiverConstant.EXTRA_BPVLN, tempMsgNumber);
                    vlintent.putExtra(ReceiverConstant.EXTRA_BPVL, vltemp[1]);// 818707@1@2019-10-30 03:15:28
                    Utils.getContext().sendBroadcast(vlintent);
                }
                break;

            case MsgType.IWBP05:
                tempMsgNumber = Integer.parseInt(content);
                LogUtils.d("gaobin IWBP05 未接收消息数:" + tempMsgNumber);
//                if (tempMsgNumber > 0) {
//                    VoiceFileReceiver.setReceiving(true);
//                }
                break;

            case MsgType.IWBP68:
                MsgSender.sendTxtMsg(MsgType.IWAP68, parseBytes.getSerialNumber());

                break;

            case MsgType.IWBP95:// content:353456789012345,20140818064408,6,1,1#
                Intent intent_bp95 = new Intent(OrderConstans.BP95);
                intent_bp95.putExtra("BP95_content", content);
                Utils.getContext().sendBroadcast(intent_bp95);
                break;
            case MsgType.IWBPTE:// content:IWBPTE,1#
                OrderUtil orderUtil = OrderUtil.getInstance();
                orderUtil.deleteRecordAPTEFile(orderUtil.getCurrentAPTERespone());
                //orderUtil.setCurrentAPTERespone();
                break;
            default:
                LogUtils.e("通用广播 ： " + (msg == null ? "msg = null " : msg.getSourceDataString()));
                //通用广播
                /*Intent currencyIntent = new Intent(parseBytes.getHeaderType()); //parseBytes.getHeaderType()  例如：IWBP03
                currencyIntent.putExtra(parseBytes.getHeaderType(), parseBytes.getContent());
                Utils.getContext().sendBroadcast(currencyIntent);*/
                break;
        }
    }

    private static class SingletonHolder {
        private static final MsgRecService INSTANCE = new MsgRecService();
    }

    private class ParseSourceDataBytes {
        private boolean isNullResult;
        private String serialNumber; //指令流水号
        private String responseContent; //设备响应内容(包含指令流水号和之后的内容)(不包含imei)
        private String orderContent; //指令流水号之后的内容(不含流水号,imei)
        private TcpMsg msg;
        private String headerType; //IWBP00
        private String content; //包含IMEI和之后的内容
        private byte[] contentBytes;

        private ParseSourceDataBytes(TcpMsg msg) {
            this.msg = msg;
        }

        boolean isNullResult() {
            return isNullResult;
        }

        private String getSerialNumber() {
            return serialNumber;
        }

        private String getResponseContent() {
            return responseContent;
        }

        private String getOrderContent() {
            return orderContent;
        }

        private String getHeaderType() {
            return headerType;
        }

        public String getContent() {
            return content;
        }

        private byte[] getContentBytes() {
            return contentBytes;
        }

        private ParseSourceDataBytes invoke() {
            byte[] sourceDataBytes = msg.getSourceDataBytes();//原生字节数组
            int sourceDataLen = sourceDataBytes.length;
            LogUtils.d("sourceDataBytes " + Arrays.toString(sourceDataBytes) + "   length=" + sourceDataBytes.length);
            int indexComma = MsgParser.indexOfStrInBytes(sourceDataBytes, GlobalSettings.MSG_CONTENT_SEPERATOR);//第一个逗号的索引值
            LogUtils.d("indexComma " + indexComma);
            int contentBytesStart;
            int contentBytesIndexEnd;
            boolean hasContent = indexComma >= 0;
            byte[] headerBytes;
            int headerBytesStart, headerBytesEnd;
            if (!hasContent) {//没找到逗号
                contentBytes = null;
                headerBytesStart = 0;
                headerBytesEnd = sourceDataLen - 2;// 从1到length-2位置结束 去掉#
            } else { //找到第一个逗号的索引值
                contentBytesStart = indexComma + 1;
                contentBytesIndexEnd = sourceDataLen - 1; //去掉#
                contentBytes = new byte[contentBytesIndexEnd - contentBytesStart];
                LogUtils.d("contentBytesLength " + contentBytes.length + "    contentBytesStart=" + contentBytesStart + "   contentBytesIndexEnd=" + contentBytesIndexEnd);
                //实现将数组复制到其他数组中，把从索引0开始的contentBytes.length个数据复制到目标的索引为0的位置上
                System.arraycopy(sourceDataBytes, contentBytesStart, contentBytes, 0, contentBytes.length);
                headerBytesStart = 0;
                headerBytesEnd = indexComma - 1;
                LogUtils.d("contentBytes " + Arrays.toString(contentBytes));
                LogUtils.d("content " + new String(contentBytes, Charset.defaultCharset()));
            }
            headerBytes = new byte[headerBytesEnd - headerBytesStart + 1]; //IWBP00
            System.arraycopy(sourceDataBytes, headerBytesStart, headerBytes, 0, headerBytes.length);
            LogUtils.d("headerBytes " + Arrays.toString(headerBytes));
            String headerDataString = new String(headerBytes, Charset.defaultCharset());
            LogUtils.i("headerDataString " + headerDataString);
            String[] segment = MsgParser.getRecvHeaderSegments(headerDataString);
            if (segment.length == 0) {
                LogUtils.e("头部解析为空...");
                isNullResult = true;
                return this;
            }
            LogUtils.d("segment " + Arrays.toString(segment));//[IWBP16]
            LogUtils.d("segment.length " + segment.length);
            if (segment.length != 1) {
                LogUtils.e("头部解析格式错误....");
                isNullResult = true;
                return this;
            }
            headerType = segment[segment.length - 1];
            String lenStr = segment[0];
            LogUtils.i("headerType " + headerType);
            if (TextUtils.isEmpty(headerType)) {
                LogUtils.e("头部类型解析为空...");
                isNullResult = true;
                return this;
            }
            headerType = headerType.trim();
            if (null != contentBytes) {
                content = new String(contentBytes, Charset.defaultCharset());
                if (content.contains(GlobalSettings.MSG_CONTENT_SEPERATOR)) {
                    responseContent = content.replace(DeviceInfoUtils.getIMEI(Utils.getContext()) + GlobalSettings.MSG_CONTENT_SEPERATOR, "");
                    serialNumber = content.split(GlobalSettings.MSG_CONTENT_SEPERATOR)[1]; //指令流水号
                    orderContent = responseContent.replace(serialNumber + GlobalSettings.MSG_CONTENT_SEPERATOR, "");
                } else {
                    serialNumber = "";
                    responseContent = "";
                }
            }
            isNullResult = false;
            return this;
        }
    }
}
