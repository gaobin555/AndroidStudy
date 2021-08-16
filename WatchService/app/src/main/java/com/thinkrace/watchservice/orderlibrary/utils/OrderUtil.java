package com.thinkrace.watchservice.orderlibrary.utils;
/*
 *  @项目名：  RootStartAuto
 *  @包名：    com.thinkrace.orderlibrary
 *  @文件名:   OrderUtil.this
 *  @创建者:   win10
 *  @创建时间:  2017/7/20 16:30
 *  @描述：    TODO
 */


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.thinkrace.watchservice.KApplication;
import com.thinkrace.watchservice.R;
import com.thinkrace.watchservice.ReceiverConstant;
import com.thinkrace.watchservice.function.alarm.AlarmTimer;
import com.thinkrace.watchservice.function.classforbidden.ClassForbidden;
import com.thinkrace.watchservice.function.classforbidden.ClassForbiddenAlarm;
import com.thinkrace.watchservice.function.classforbidden.ForbiddenTime;
import com.thinkrace.watchservice.function.location.AMapLocationManager;
import com.thinkrace.watchservice.function.location.LocationUploadManager;
import com.thinkrace.watchservice.orderlibrary.LocationService;
import com.thinkrace.watchservice.orderlibrary.RedirectException;
import com.thinkrace.watchservice.orderlibrary.data.HandShake;
import com.thinkrace.watchservice.orderlibrary.data.MsgDataBean;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.orderlibrary.data.PulseBean;
import com.thinkrace.watchservice.orderlibrary.data.TcpMsg;
import com.thinkrace.watchservice.parser.MsgRecService;
import com.thinkrace.watchservice.parser.MsgSender;
import com.thinkrace.watchservice.receiver.ClassForbiddenAlarmReceiver;
import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.basic.protocol.IWNormalReaderProtocol;
import com.xuhao.android.common.constant.OrderConstans;
import com.xuhao.android.common.constant.SPConstant;
import com.xuhao.android.common.constant.TcpConstans;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.libsocket.impl.client.PulseManager;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.action.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.client.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.client.connection.DefaultReconnectManager;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.thinkrace.watchservice.orderlibrary.LocationService.RESERVE_POWER_THRESHOLD;
import static com.thinkrace.watchservice.orderlibrary.utils.DigitalConvert.byte2HexStr;
import static com.thinkrace.watchservice.orderlibrary.utils.DigitalConvert.hexStringToString;
import static com.thinkrace.watchservice.orderlibrary.utils.UnicodeUtils.stringToUnicode;
import static com.thinkrace.watchservice.orderlibrary.utils.UnicodeUtils.unicodeNo0xuToString;

import static com.thinkrace.watchservice.receiver.ClassForbiddenAlarmReceiver.CLASS_FORBIDDEN_STATE;

public class OrderUtil {

    private WifiManager mWifiManager;
    private TelephonyManager mTelephonyManager;
    private AudioManager mAudioManager;
    private AlarmManager mAlarmManager;
    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;

    private static OrderUtil instance;
    private boolean isParseDomain = false;
    private String domainName = TcpConstans.DOMAIN;
    private String ip = TcpConstans.IP;
    private int port = TcpConstans.PORT;
    private OrderListener orderListener;
    private Context context;
    private ServiceListener serviceListener;
    private SharedPreferences sp;
    private ConnectionInfo mInfo;
    private OkSocketOptions mOkOptions;
    private IConnectionManager mManager;
    private String TAG = "socket";
    private SocketActionAdapter adapter = new SocketActionAdapter() {

        //连接成功
        @Override
        public void onSocketConnectionSuccess(Context context, final ConnectionInfo info, String action) {
            SLog.e("已经连上服务器O(∩_∩)O~\"" + "       对方IP地址: " + info.getIp() + "  " + info.getPort());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    mManager.send(new HandShake()); //握手-登录包
                    PulseManager pulseManager = mManager.getPulseManager();//心跳包
                    if (pulseManager != null) {
                        pulseManager.setPulseSendable(new PulseBean()).pulse();
                    } else {
                        SLog.e("pulseManager is null!!!");
                        pulseManager = PulseManager.getInstance(mManager, mOkOptions);
                        pulseManager.setPulseSendable(new PulseBean()).pulse();
                    }
                    runGuardPolicy();
                    //定位一下获取最新位置信息
                    AMapLocationManager.instance().start();
                    AlarmTimer.startConfirmedFrequencyUpload(KApplication.sContext);//开启固定频率上传(定位信息)
                    scanRecordAPTEFile();
                }
            }).start();
        }

        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                if (e instanceof RedirectException) {
                    SLog.e("正在重定向连接...");
                    mManager.switchConnectionInfo(((RedirectException) e).redirectInfo);
                    mManager.connect();
                } else {
                    SLog.e("异常断开:" + e.getMessage());
                }
            } else {
                SLog.e("正常断开");
            }
            OkSocket.getContext().sendBroadcast(new Intent(ReceiverConstant.LOCATION_STOP)); //停止定位
        }

        //连接失败
        @Override
        public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
            SLog.e(info.getIp() + "  " + info.getPort() + "      连接失败");
        }

        //接收成功
        @Override
        public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
            super.onSocketReadResponse(context, info, action, data);
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            String originHexStr = byte2HexStr(data.getBodyBytes());
            SLog.e("原始数据字节(HEX)：" + originHexStr);
            String originStr = hexStringToString(originHexStr);
            SLog.e("原始数据字符串：" + originStr);

            TcpMsg tcpMsg = new TcpMsg();
            tcpMsg.setSourceDataBytes(data.getBodyBytes());
            tcpMsg.setSourceDataString(str);
            tcpMsg.contentStr = str;
            receiveMsg(tcpMsg, data);
        }

        //发送成功
        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            super.onSocketWriteResponse(context, info, action, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            SLog.e(str + "      发送成功");
            if (str.startsWith("IWAPTE") && str.endsWith("#")) {
                currentAPTERespone = str;
                //deleteRecordAPTEFile(str);
            }
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            super.onPulseSend(context, info, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            SLog.e("心跳发送");
        }
    };

    private String parseBP28Sender(String str, String[] temp1) {
        StringBuilder sendbulid = new StringBuilder();
        String[] temp2 = str.split(",");
        boolean bStart = false;
        for (String s : temp1) {
            if (s.equals("2C") && !bStart) {
                bStart = true;
                continue;
            } else if (s.equals("2C")) {
                break;
            }

            if (bStart){
                sendbulid.append(s);
            }
        }

        StringBuilder BP28Content = new StringBuilder();

        for (int i = 0; i < temp2.length; i++) {
            if (i == 1) {
                BP28Content.append(sendbulid);
            } else {
                BP28Content.append(temp2[i]);
            }
            if (i != temp2.length -1) {
                BP28Content.append(",");
            }
        }

        return BP28Content.toString();
    }

    private void parseRecordingFile(String str, String[] temp1, String[] temp2) {
        int flag = 0;
        String amrlength = "";
        Intent dlintent =  new Intent();
        if (str.contains(MsgType.IWBPCD)) {
            flag = 8;
            amrlength = temp2[7];
            dlintent.setAction(OrderConstans.BPCD);
        } else if (str.contains(MsgType.IWBP28)) {
            flag = 6;
            amrlength = temp2[5];
            dlintent.setAction(OrderConstans.BP28);
        }
        int index = 0;
        int count = 0;
        for (char c : str.toCharArray()) {
            index ++;
            if (c == ',') {
                count ++;
            }
            if (count == flag) {
                break;
            }
        }
        String[] amrArr = new String[Integer.valueOf(amrlength)];
        String protocolHead = str.substring(0, index);
        SLog.e("protocolHead：" + protocolHead);
        try {
            System.arraycopy(temp1, index, amrArr, 0, amrArr.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            if (protocolHead.contains(MsgType.IWAPCD)) {
                MsgSender.sendTxtMsg(MsgType.IWAPCD, temp2[1] + "," + temp2[3] + "," + temp2[4] + "," + temp2[5] + "," + temp2[6] + ",0");
            } else if (protocolHead.contains(MsgType.IWBP28)) {
                MsgSender.sendTxtMsg(MsgType.IWAP28, temp2[1] + "," + temp2[2] + "," + temp2[3] + "," + temp2[4] + ",0");
            }
            e.printStackTrace();
            return;
        }
        if (str.contains(MsgType.IWBP28)) {
            protocolHead = parseBP28Sender(protocolHead, temp1);
            SLog.e("protocolHead BP28：" + protocolHead);
        }
        //SLog.e("IWBPCD amrArr：" + Arrays.toString(amrArr));
        dlintent.putExtra("protocol_head", protocolHead);
        dlintent.putExtra("amr_data", amrArr);
        Utils.getContext().sendBroadcast(dlintent);
    }

    private void parseRecordingFile(String str) {// only for IWBP96
        int flag = 7;
        String amrlength = "";
        Intent dlintent =  new Intent();
        // IWBP96,868872039000008,5AE35AE3,034558,4,1,1024,49794642545
        dlintent.setAction(OrderConstans.BP96);

        int index = 0;
        int count = 0;
        for (char c : str.toCharArray()) {
            index ++;
            if (c == ',') {
                count ++;
            }
            if (count == flag) {
                break;
            }
        }
        String protocolHead = str.substring(0, index);
        String voiceString = str.substring(index, str.length()-1);

        SLog.e("protocolHead：" + protocolHead);
        SLog.e("voiceString：" + voiceString);

        dlintent.putExtra("protocol_head", protocolHead);
        dlintent.putExtra("amr_data", voiceString);
        Utils.getContext().sendBroadcast(dlintent);
    }

    private OrderUtil() {
        context = OkSocket.getContext();
        sp = context.getSharedPreferences(SPConstant.CURRENT_USR_NAME, Context.MODE_PRIVATE);
        mSp = context.getSharedPreferences(SPConstant.PARENTAL_CONTROL, Context.MODE_PRIVATE);//added by chenjia
        mEditor = mSp.edit();

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public synchronized static OrderUtil getInstance() {
        if (instance == null) {
            instance = new OrderUtil();
            instance.runClassForbiddenPolicy();// 启动上课禁用
        }
        return instance;
    }

    public void setServiceStateStart() {
        if (serviceListener != null)
            serviceListener.serviceStart(instance);
    }

    /**
     * 登录包
     *
     * @param imei
     */
    public void loginPkg(String imei) {
        String s = "";
        s = "IWAP00" + imei + "#";
        sendMsg(s);

    }

    /**
     * 登录包带IMSI
     *
     * @param imei
     */
    public void loginPkgIMSI(String imei, String imsi) {
        String s = "";
        s = "IWAPLN," + imei + "," + imsi + "#";
        sendMsg(s);
        ;
    }

    /**
     * C网设备登录包
     *
     * @param deviceId
     */
    public void deviceLoginPkg_C(String deviceId) {
        String s = "";
        s = "IWAPLO" + deviceId + "#";
        sendMsg(s);
        ;
    }

    /**
     * 双向通话
     *
     * @param imei        设备IMEI号,固定15位
     * @param imsi        IMSI
     * @param phoneNumber 被叫号码
     *                    <p>
     *                    设备每次与服务器连接,都需要重新发登陆包主要用于使用物联网卡，需第三方进行呼叫的
     */
    public void twoWayCall(String imei, String imsi, String phoneNumber) {
        String s = "";
        s = "IWAPCM" + imei + "," + imsi + "," + phoneNumber + "#";
        sendMsg(s);
        ;
    }

    /**
     * 定位数据包
     *
     * @param order
     * @param time
     * @param AorV
     * @param latitude
     * @param longitude
     * @param speed
     * @param GreenwichTime
     * @param DirectionAngle
     * @param gsm
     * @param lbs
     */
    public void sendLocationOrder(String order, String time, String AorV, String latitude, String longitude, String speed, String GreenwichTime, String DirectionAngle, String gsm, String lbs) {
        String s = "";
        s = order + time + AorV + latitude + longitude + speed + GreenwichTime + DirectionAngle + gsm + "," + lbs + "#";
        time = time + GreenwichTime;
        sendMsg(s);
    }

    /**
     * GPRS间隔
     *
     * @param order
     * @param imei
     * @param randomNumber
     * @param Interval
     */
    public void sendGPRSIntervalOrder(String order, String imei, String randomNumber, String Interval) {
        String s = "";
        s = order + "," + imei + "," + randomNumber + "," + Interval + "#";
        sendMsg(s);
        ;
    }

    /**
     * 多基站定位
     *
     * @param language
     * @param flag
     * @param LBSNumber
     * @param MCC
     * @param MNC
     * @param LBSInfo
     * @param wifiNumber
     */
    public void MultipleLBSLogocation(String language, String flag, String LBSNumber, String MCC, String MNC, String LBSInfo, String wifiNumber) {
        String s = "";
        s = "IWAP02," + language + "," + flag + "," + LBSNumber + "," + MCC + "," + MNC + "," + LBSInfo + "," + wifiNumber + "#";
        sendMsg(s);
        ;
    }

    /**
     * 交好友
     *
     * @param time
     * @param AorV
     * @param latitude
     * @param longitude
     * @param speed
     * @param GreenwichTime
     * @param DirectionAngle
     * @param gsm
     * @param LBSNumber
     * @param MCC
     * @param MNC
     * @param LBSInfo
     * @param wifiNumber
     */
    public void makeFriends(String time, String AorV, String latitude, String longitude, String speed, String GreenwichTime, String DirectionAngle, String gsm, String LBSNumber, String MCC, String MNC, String LBSInfo, String wifiNumber) {
        String s = "";
        s = "IWAPFD" + time + AorV + latitude + longitude + speed + GreenwichTime + DirectionAngle + gsm + LBSNumber + "," + MCC + "," + MNC + "," + LBSInfo + "," + wifiNumber + "#";
        sendMsg(s);
        ;

    }

    /**
     * 报警与地址回复包
     *
     * @param time
     * @param AorV
     * @param latitude
     * @param longitude
     * @param speed
     * @param GreenwichTime
     * @param DirectionAngle
     * @param gsm
     * @param lbs
     * @param AlarmStatus
     */
    public void alertAndAddress(String time, String AorV, String latitude, String longitude, String speed, String GreenwichTime, String DirectionAngle, String gsm, String lbs, String AlarmStatus) {
        String s = "";
        s = "IWAP10" + time + AorV + latitude + longitude + speed + GreenwichTime + DirectionAngle + gsm + "," + lbs + "," + AlarmStatus + "#";
        sendMsg(s);
        ;
    }

    /**
     * 心跳包
     *
     * @param gsm
     * @param satelliteNumber
     * @param electricity
     * @param FortificationState
     * @param WorkingModel
     * @param step
     * @param RollNumber
     */
    public void heartbeatPacket(String gsm, String satelliteNumber, String electricity, String FortificationState, String WorkingModel, String step, String RollNumber) {
        String s = "";
        s = "IWAP03," + gsm + satelliteNumber + electricity + FortificationState + WorkingModel + "," + step + "," + RollNumber + "#";
        sendMsg(s);
        ;
    }

    /**
     * 低电量报警上报数据包
     *
     * @param electricity
     */
    public void lowPowerAlarm(String electricity) {
        String s = "";
        s = "IWAP04" + electricity + "#";
        sendMsg(s);
        ;
    }

    /**
     * 语音查询接收协议
     *
     * @param order
     */
    public void voiceQueryReceive(String order) {
        String s = "";
        s = "IWAP05," + order + "#";
        sendMsg(s);
        ;
    }

    /**
     * AGPS辅助定位包
     *
     * @param lbs
     */
    public void AGPSLogocation(String lbs) {
        String s = "";
        s = "IWAP06," + lbs + "#";
        sendMsg(s);
        ;
    }

    /**
     * 同步天气，空气，老黄历信息(拓展)
     */
    public void syncWeatherCalendar() {
        String s = "IWAP39#";
        sendMsg(s);
        ;
    }

    /**
     * 心率上行
     *
     * @param heartRate
     */
    public void heartRateUplink(String heartRate) {
        String s = "";
        s = "IWAP49," + heartRate + "#";
        sendMsg(s);
        ;
    }

    /**
     * 基站校时
     *
     * @param MCC
     * @param MNC
     * @param LAC
     * @param CID
     */
    public void baseStationTiming(String MCC, String MNC, String LAC, String CID) {
        String s = "";
        s = "IWAP53," + MCC + "," + MNC + "," + LAC + "," + CID + "#";
        sendMsg(s);
        ;
    }

    /**
     * 设备上报事件
     *
     * @param eventID
     * @param eventParam
     */
    public void deviceReportingEvents(String eventID, String eventParam) {
        String s = "";
        s = "IWAP54," + eventID + "," + eventParam + "#";
        sendMsg(s);
        ;
    }

    /**
     * 表情消息上传
     *
     * @param time
     * @param expression
     */
    public void expressionMsgUplink(String time, String expression) {
        String s = "";
        s = "IWAP70," + time + "," + expression + "#";
        sendMsg(s);
        ;
    }

    /**
     * 守护报警
     *
     * @param time
     * @param guardianType
     * @param alarmType
     * @param MAC
     * @param theme
     */
    public void guardAlarm(String time, String guardianType, String alarmType, String MAC, String theme) {
        String s = "";
        s = "IWAP86," + time + "," + guardianType + "," + alarmType + "," + MAC + "," + theme + "#";
        sendMsg(s);
        ;
    }

    /**
     * 通过基站校时并获取经纬度协议
     *
     * @param MCC
     * @param MNC
     * @param LAC
     * @param CID
     */
    public void baseStationTimingAndgetAgreement(String MCC, String MNC, String LAC, String CID) {
        String s = "";
        s = "IWAPTM," + MCC + "," + MNC + "," + LAC + "," + CID + "#";
        sendMsg(s);
        ;
    }

    /**
     * 上传心率和血压
     *
     * @param heartRate
     * @param hypotension
     * @param hypertension
     */
    public void uploadHeartRateAndBloodPressure(String heartRate, String hypotension, String hypertension) {
        String s = "";
        s = "IWAPHT," + heartRate + "," + hypotension + "," + hypertension + "#";
        sendMsg(s);
        ;
    }

    /**
     * 请求天气
     *
     * @param baseStationInfo
     * @param language
     */
    public void requestWeather(String baseStationInfo, String language) {
        String s = "";
        s = "IWAPTQ," + baseStationInfo + "," + language + "#";
        sendMsg(s);
        ;
    }

    /**
     * 请求设备二维码
     *
     * @param imei
     */
    public void requestDeviceQR(String imei) {
        String s = "";
        s = "IWAP87" + imei + "#";
        sendMsg(s);
        ;
    }

    public void resend(String message) {
        sendMsg(message);
    }

    /*--------------------------------服务器--设备-----------------------------------*/

    /**
     * 睡眠数据上传
     *
     * @param second
     * @param startTime
     * @param endTime
     * @param SLogeepData
     */
    public void uploadSLogeepData(String second, String startTime, String endTime, String SLogeepData) {
        String s = "";
        s = "IWAP91," + second + "," + startTime + "," + endTime + "," + SLogeepData + "#";
        sendMsg(s);
        ;
    }

    /**
     * 设备回复服务器
     *
     * @param order
     * @param param
     */
    public void send(String order, String param) {
        String s = "";
        s = order + "," + param;
        sendMsg(s);
        ;
    }

    public void addContact(final Context context, final String[] contacts) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (contacts == null || contacts.length ==0) {
                    LogUtils.e("[addContact]contacts is empty");
                    return;
                }

                deleteAllContacts(context);

                ContentValues values = new ContentValues();
                for (String contact : contacts) {
                    String[] strs = contact.split("\\|");
                    String contactName =  unicodeNo0xuToString(strs[0]);
                    String contactNumber = strs[1];
                    LogUtils.e("添加白名单(contact)："+ "名称 = " + contactName + ", 号码 = " + contactNumber);

                    Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
                    long rawContactId = ContentUris.parseId(rawContactUri);
                    values.clear();

                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    // 内容类型
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    // 联系人名字
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contactName);
                    // 向联系人URI添加联系人名字
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    values.clear();

                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    // 联系人的电话号码
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber);
                    // 电话类型
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    // 向联系人电话号码URI添加电话号码
                    context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    values.clear();
                }
            }
        });
        thread.start();
    }

    public void deleteAllContacts(Context context) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            //获取ID
            String contactsId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            final Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactsId));
            context.getContentResolver().delete(contactUri, null, null);
        }
        cursor.close();
    }

    public void addUrl(final Context context, final String whiteUrls) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(whiteUrls)) {
                    LogUtils.e("[addUrl]urls is empty");
                }

                deleteAllWebSites(context);

                ContentValues values = new ContentValues();
                if (!TextUtils.isEmpty(whiteUrls)) {
                    String[] urls = whiteUrls.split(",");
                    for (String url : urls) {
                        String[] strs = url.split("@");
                        values.put("title", unicodeNo0xuToString(strs[0]));
                        values.put("url", strs[1]);
                        LogUtils.e("添加白名单(url)：" + unicodeNo0xuToString(strs[0]) + "@" + strs[1]);
                        context.getContentResolver().insert(Uri.parse("content://com.android.browser.site_navigation/websites"), values);
                        values.clear();
                    }
                }

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Bitmap bm = BitmapFactory.decodeResource(context.getResources(),  R.raw.k1_add_sitenavigation_thumbnail);
                bm.compress(Bitmap.CompressFormat.PNG, 100, os);
                values.put("thumbnail", os.toByteArray());
                values.put("url", "about:blank");
                context.getContentResolver().insert(Uri.parse("content://com.android.browser.site_navigation/websites"), values);
                values.clear();
            }
        });
        thread.start();
    }

    public void deleteAllWebSites(Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.browser.site_navigation/websites"), null, null, null, null);
        while(cursor.moveToNext()){
            //获取ID
            String websiteId = cursor.getString(cursor.getColumnIndex("_id"));
            final Uri websiteUri = ContentUris.withAppendedId(Uri.parse("content://com.android.browser.site_navigation/websites"), Long.valueOf(websiteId));
            LogUtils.e("删除白名单(url)："+ websiteUri.toString());
            context.getContentResolver().delete(websiteUri, null, null);
        }
        cursor.close();
    }

    public void setSchedulePowerOnOff(final Context context, final String[] powerDatas) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (powerDatas == null || powerDatas.length ==0) {
                    LogUtils.e("[setSchedulePowerOnOff]powerDatas is empty");
                    return;
                }

                String[] arr1 = powerDatas[1].split(":");
                Intent schpwronoff = new Intent("android.intent.action.REMOTE_SET_PWR_ON_OFF");
                schpwronoff.setFlags(schpwronoff.getFlags()| 0x01000000);
                Bundle b_on = new Bundle();
                b_on.putInt("_id", 1);
                b_on.putString("hour",arr1[0]);
                b_on.putString("minutes", arr1[1]);
                b_on.putString("daysofweek", "127");
                b_on.putString("enabled", powerDatas[0]);
                schpwronoff.putExtra("schpwr_on", b_on);
                LogUtils.e("设置定时开机：" + powerDatas[0] + "," + arr1[0] + ":" + arr1[1]);

                String[] arr2 = powerDatas[2].split(":");
                Bundle b_off = new Bundle();
                b_off.putInt("_id", 2);
                b_off.putString("hour",arr2[0]);
                b_off.putString("minutes", arr2[1]);
                b_off.putString("daysofweek", "127");
                b_off.putString("enabled", powerDatas[0]);
                schpwronoff.putExtra("schpwr_off", b_off);
                LogUtils.e("设置定时关机：" + powerDatas[0] + ","  + arr2[0] + ":" + arr2[1]);

                context.sendBroadcast(schpwronoff);
            }
        });
        thread.start();
    }

    private String currentAPTERespone = "";
    public String getCurrentAPTERespone() {
        return currentAPTERespone;
    }
    public void setCurrentAPTERespone() {
        currentAPTERespone = "";
    }

    private HashMap<String, String> controllingApps = new HashMap<String, String>();
    public HashMap<String, String> getControllingApps() {
        return controllingApps;
    }
    public void appControl(final Context context, final String appData) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (appData == null || appData.length() ==0) {
                    LogUtils.e("[appControl]appData is empty");
                    return;
                }
                String[] datas = appData.split(",");
                String appInfo = datas[0];
                String iconUrl = datas[1];
                LogUtils.e("app控制数据[appControl]iconUrl = " + iconUrl);

                String[] apkInfos = appInfo.split("\\|");
                for (String apkInfo : apkInfos) {
                    String[] temp = apkInfo.split("@");
                    String id = temp[0];
                    String operate = temp[1];
                    String apkName = unicodeNo0xuToString(temp[2]);
                    String iconName = unicodeNo0xuToString(temp[3]);
                    String content = unicodeNo0xuToString(temp[4]);
                    String apkIconUrl = iconUrl + iconName;
                    controllingApps.put(id, operate);
                    LogUtils.e("app控制数据[appControl] id = " + id + ", operate = " + operate + ", apkName = " + apkName + ", iconName = " + iconName + ", content = " + content);
                    if (TextUtils.equals(operate, "1")) {
                        boolean installResult = downloadAPK(context, id, content, apkName + ".apk", apkIconUrl);
                        LogUtils.e("app控制数据[appControl] id = " + id + ", installResult = " + installResult);
                        if (installResult) {
                            controllingApps.remove(id);
                        }
                    } else if (TextUtils.equals(operate, "2")) {
                        boolean uninstallResult = uninstallClientApp(id, content);
                        LogUtils.e("app控制数据[appControl] id = " + id + ", uninstallResult = " + uninstallResult);
                        if (uninstallResult) {
                            controllingApps.remove(id);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public String getUnintallApkPackageName(Context context, String apkPath) {
        String pn = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            pn = info.packageName;
        }
        return pn;
    }

    private boolean downloadAPK(final Context context, String appId, final String apk_file_url, final String apkName, final String apkIconUrl) {
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos  = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                String sdPath = Environment.getExternalStorageDirectory() + "/Download/";
                LogUtils.e("app控制数据[appControl]sdPath = " + sdPath);
                File dir = new File(sdPath);
                if (!dir.exists()){
                    dir.mkdir();
                }
                // 下载文件
                conn = (HttpURLConnection) new URL(apk_file_url).openConnection();
                conn.connect();
                int length = conn.getContentLength();
                is = conn.getInputStream();

                File apkFile = new File(sdPath,  apkName);
                fos = new FileOutputStream(apkFile);

                int count = 0;
                boolean isCancel = false;
                byte[] buffer = new byte[10240];
                while (!isCancel){
                    int numread = is.read(buffer);
                    fos.write(buffer, 0, numread);

                    count += numread;
                    int progress = (int) (((float)count/length) * 100);
                    if (progress == 25 || progress == 65 || progress == 100) {
                        LogUtils.e("app控制数据[appControl]" + apkName + " 下载进度......" + progress + "%");
                    }
                    // 下载完成
                    if (progress == 100){
                        isCancel = true;
                    }
                }
                fos.close();
                is.close();
                return installClientApp(context, appId, sdPath, apkName, apkIconUrl);
            }
        } catch(Exception e){
            e.printStackTrace();
            //if (e instanceof SocketException && e.getMessage().contains("Software caused connection abort")) {
                recordAPTEFile(appId, "1", "2");
            //} else {
                MsgSender.sendTxtMsg(MsgType.IWAPTE, appId + ",1,2");//download failed response
            //}
        } finally {
            conn.disconnect();
        }
        return false;
    }

    public void recordAPTEFile(String appId, String operate, String state) {
        ObjectOutputStream oos = null;
        String fileName = appId + "_" + operate + "_" + state + ".apte";
        try {
            LogUtils.e("app控制数据[appControl]" + "由于网络异常，保存应用"+ appId + "处理失败现场，待网络恢复后重新通知服务器该应用状态");
            File dir = new File(Environment.getExternalStorageDirectory() + "/temp/");
            if (!dir.exists()){
                dir.mkdir();
            }
            oos = new ObjectOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/temp/", fileName)));
            oos.writeObject(fileName);
            oos.flush();
            oos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            LogUtils.e("app控制数据[appControl]" + "记录APTE失败");
        }
    }

    public void scanRecordAPTEFile() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/temp/");
        if (directory.exists()) {
            File [] filelist = directory.listFiles();
            LogUtils.e("app控制数据[appControl]扫描recordAPTEFile...开始");
            if (filelist != null) {
                for (File file : filelist) {
                    if (file.getName().endsWith("apte")) {
                        String[] arr1 = file.getName().split("\\.");
                        String[] arr2 = arr1[0].split("_");
                        LogUtils.e("app控制数据[appControl]" + "IWAPTE," + arr2[0] + "," + arr2[1] + "," + arr2[2]);
                        MsgSender.sendTxtMsg(MsgType.IWAPTE, arr2[0] + "," + arr2[1] + "," + arr2[2]);//download failed response
                        try {
                            Thread.sleep(600);//暂停600ms
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            LogUtils.e("app控制数据[appControl]扫描recordAPTEFile...结束");
        } else {
            LogUtils.e("app控制数据[appControl]扫描recordAPTEFile路径不存在");
        }
    }

    public void deleteRecordAPTEFile(String order) {
        if (!TextUtils.isEmpty(order) && order.startsWith("IWAPTE")) {
            String[] arr = order.split(",");
            String appId = arr[1];
            File directory = new File(Environment.getExternalStorageDirectory() + "/temp/");
            if (directory.exists()) {
                File [] filelist = directory.listFiles();
                if (filelist != null) {
                    for (File file : filelist) {
                        if (file.getName().startsWith(appId)) {
                            if(file.delete()) {
                                LogUtils.e("app控制数据[appControl]删除APTE文件：" + file.getName() + "成功" );
                            } else {
                                LogUtils.e("app控制数据[appControl]删除APTE文件：" + file.getName() + "失败" );
                            }
                        }
                    }
                }
            } else {
                LogUtils.e("app控制数据[appControl]删除APTE文件：扫描路径不存在");
            }
        }
    }

    public boolean installClientApp(Context context, String appId, String sdPath, String apkName, String apkIconUrl) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        LogUtils.e("app控制数据[appControl]installClientApp start...");
        String apkUrl = sdPath + apkName;
        try {
            String packageName = context.getPackageName();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                process = new ProcessBuilder("pm", "install", "-i", packageName, "-r", apkUrl).start();
            } else {
                process = new ProcessBuilder("pm", "install", "-r", apkUrl).start();
            }
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            LogUtils.e("app控制数据[appControl]installClientApp failed(e)");
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e1) {
                LogUtils.e("app控制数据[appControl]installClientApp failed(e1)");
            }
        }
        if (TextUtils.isEmpty(errorMsg.toString()) || successMsg.toString().equalsIgnoreCase("Success")) {
            recordAPTEFile(appId, "1", "1");
            MsgSender.sendTxtMsg(MsgType.IWAPTE, appId + ",1,1");//install success response
            LogUtils.e("app控制数据[appControl]installClientApp success !!!");
            String apkPN = getUnintallApkPackageName(context, apkUrl);
            LogUtils.e("app控制数据[appControl]send "  + apkPN +" icon to xLauncher: " + apkIconUrl);
            Intent intent =  new Intent("com.android.hotpper.REPLACE_ICON");
            intent.setFlags(intent.getFlags()| 0x01000000);
            intent.putExtra("packagename", apkPN);
            intent.putExtra("apkiconurl", apkIconUrl);
            context.sendBroadcast(intent);
            return true;
        } else {
            recordAPTEFile(appId, "1", "2");
            MsgSender.sendTxtMsg(MsgType.IWAPTE, appId + ",1,2");//install failed response
            LogUtils.e("app控制数据[appControl]installClientApp failed !!!");
            return false;
        }
    }

    public boolean uninstallClientApp(String appId, String packageName) {
        LogUtils.e("app控制数据[appControl]uninstallClientApp start...");
        boolean isAppInstalled = isAppInstalled(packageName);
        if (!isAppInstalled) {
            recordAPTEFile(appId, "2", "1");
            MsgSender.sendTxtMsg(MsgType.IWAPTE, appId + ",2,1");//uninstall failed response
            LogUtils.e("app控制数据[appControl]uninstallClientApp:" + packageName + " is not installed, so break!");
            return false;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent sender = PendingIntent.getActivity(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PackageInstaller mPackageInstaller = context.getPackageManager().getPackageInstaller();
            mPackageInstaller.uninstall(packageName, sender.getIntentSender());
        }
        recordAPTEFile(appId, "2", "1");
        MsgSender.sendTxtMsg(MsgType.IWAPTE, appId + ",2,1");//uninstall success response
        LogUtils.e("app控制数据[appControl]uninstallClientApp end");
        return true;
    }

    private boolean isAppInstalled(String packageName){
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch(PackageManager.NameNotFoundException e){
            installed = false;
        }
        return installed;
    }


    public void reportDeviceLoss() {
        int systemMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int systemCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        if (systemCurrent < systemMax) {
            mEditor.putBoolean("stream_system_max", true).apply();
            mEditor.putInt("stream_system", systemCurrent).apply();
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemMax, 0);
        }

        int ringMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int ringCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        if (ringCurrent < ringMax) {
            mEditor.putBoolean("stream_ring_max", true).apply();
            mEditor.putInt("stream_ring", ringCurrent).apply();
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, ringMax, 0);
        }

        int notificationMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        int notificationCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (notificationCurrent < notificationMax) {
            mEditor.putBoolean("stream_notification_max", true).apply();
            mEditor.putInt("stream_notification", notificationCurrent).apply();
            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationMax, 0);
        }
    }

    public void cancelReportDeviceLoss() {
        boolean needRecoverSystem = mSp.getBoolean("stream_system_max", false);
        if (needRecoverSystem) {
            int streamSystem = mSp.getInt("stream_system", 0);
            mEditor.putBoolean("stream_system_max", false).apply();
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, streamSystem, 0);
        }

        boolean needRecoverRing = mSp.getBoolean("stream_ring_max", false);
        if (needRecoverRing) {
            int streamRing = mSp.getInt("stream_ring", 0);
            mEditor.putBoolean("stream_ring_max", false).apply();
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, streamRing, 0);
        }

        boolean needRecoverNotification = mSp.getBoolean("stream_notification_max", false);
        if (needRecoverNotification) {
            int streamNotification = mSp.getInt("stream_notification", 0);
            mEditor.putBoolean("stream_notification_max", false).apply();
            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, streamNotification, 0);
        }
    }

    public void runReservePower(Context context, int batteryLevel) {
        int enable = Settings.Global.getInt(context.getContentResolver(), "reserve_power", 0);
        Intent i =  new Intent("com.android.hotpeper.RESERVE_POWER");
        i.setFlags(i.getFlags()| 0x01000000);
        if (enable == 1 && batteryLevel <= RESERVE_POWER_THRESHOLD) {
            SLog.e("手机处于预留电量模式");
            i.putExtra("isReservePower", 1);
            context.sendBroadcast(i);
            closeNetwork();
        } else {
            SLog.e("手机退出预留电量模式");
            i.putExtra("isReservePower", 0);
            context.sendBroadcast(i);
            recoverNetwork();
        }
    }

    public void closeNetwork() {
        if (mTelephonyManager.isDataEnabled()) {
            mTelephonyManager.setDataEnabled(false);
            mEditor.putBoolean("needRecoverData", true).apply();
        }
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
            mEditor.putBoolean("needRecoverWiFi", true).apply();
        }
    }

    public void recoverNetwork() {
        boolean needRecoverData = mSp.getBoolean("needRecoverData", false);
        boolean needRecoverWiFi = mSp.getBoolean("needRecoverWiFi", false);
        if (needRecoverData) {
            mTelephonyManager.setDataEnabled(true);
            mEditor.putBoolean("needRecoverData", false).apply();
        }
        if (needRecoverWiFi) {
            mWifiManager.setWifiEnabled(true);
            mEditor.putBoolean("needRecoverWiFi", false).apply();
        }
    }

    public void parseGuardData(String guardData) {
        if (TextUtils.isEmpty(guardData)) {
            LogUtils.e("上学守护[parseGuardData]guardData is null");
            return;
        }
        LogUtils.e("上学守护[parseGuardData]guardData = " + guardData);//1,08:00-11:30,14:00-16:30,wifi_name@wifi_mac,18:00,12345
        String[] arr = guardData.split(",");
        if (TextUtils.equals(arr[0], "0")) {//使能状态
            mEditor.putBoolean("guard_state", false).apply();
        } else if (TextUtils.equals(arr[0], "1")) {
            mEditor.putBoolean("guard_state", true).apply();
        }

        if (!TextUtils.equals(arr[3], "@")) {//家庭WiFi信息
            String[] arrWifi = arr[3].split("@");
            if (arrWifi.length == 2) {
                mEditor.putString("guard_wifi_name", arrWifi[0]).apply();
                mEditor.putString("guard_wifi_mac", arrWifi[1]).apply();
            }
        }

        mEditor.putString("guard_repeat_day", arr[5]).apply();//重复周期
        calculateGuardTime(arr[1], arr[2], arr[4]);

        runGuardPolicy();
    }

    public void calculateGuardTime(String amTime, String pmTime, String deadlineTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String guardAmTime = "";
        String guardPmTime = "";

        String[] amTimes = amTime.split("-");
        String[] amStartTimes = amTimes[0].split(":");
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(amStartTimes[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(amStartTimes[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MILLISECONDS_IN_DAY, 10 * 60 * 1000);
        Date date = calendar.getTime();
        String guardEnd = simpleDateFormat.format(date);
        date.setTime(date.getTime() - 30 * 60 * 1000);
        String guardStart = simpleDateFormat.format(date);
        guardAmTime = guardStart + "-" + guardEnd;

        String[] pmTimes = pmTime.split("-");
        guardPmTime = pmTimes[1] + "-" + deadlineTime;

        LogUtils.e("上学守护[parseGuardData]calculateGuardTime am guard time = " + guardAmTime + ", pm guard time = " + guardPmTime);
        mEditor.putString("guard_am_time", guardAmTime).apply();//上午守护时间
        mEditor.putString("guard_pm_time", guardPmTime).apply();//下午守护时间
    }

    public void runGuardPolicy() {
        boolean guardState = mSp.getBoolean("guard_state", false);
        if (guardState) {
            setGuardAlarm();
            GuardAlarmUtils.getInstance().reportGuard(context);
        } else {
            LocationUploadManager.instance().parseInteraval(LocationUploadManager.NORMAL_TYPE);
            GuardAlarmUtils.getInstance().cancelGuardAlarm(context);
        }
    }

    public void setGuardAlarm() {
        String repeat_day = mSp.getString("guard_repeat_day", "");
        String am_time = mSp.getString("guard_am_time", "");
        String[] amTimes = am_time.split("-");
        String pm_time = mSp.getString("guard_pm_time", "");
        String[] pmTimes = pm_time.split("-");

        GuardAlarmUtils.getInstance().setGuardAlarmByDays(context, mAlarmManager, repeat_day, amTimes, pmTimes);
    }

    public String getWifiInfo(){
        mWifiManager.startScan();
        List<ScanResult> list = mWifiManager.getScanResults();
        StringBuffer result = new StringBuffer();
        if (list == null) {
            LogUtils.e("上学守护[getWifiInfo]未检索到附近的WiFi");
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                String wifiName = list.get(i).SSID;
                String macAddress = list.get(i).BSSID;
                wifiName = stringToUnicode(wifiName);
                if (i == list.size() -1) {
                    result.append(wifiName + "@" + macAddress);
                } else {
                    /*if (list.size() > 6 && i == 6) {
                        result.append(wifiName + "@" + macAddress);
                        break;
                    }*/
                    result.append(wifiName + "@" + macAddress + "|");
                }
            }
        }
        LogUtils.e("上学守护[getWifiInfo]result = " + result.toString());
        return result.toString();
    }

    private void runClassForbiddenPolicy() {
        String data = mSp.getString("class_forbidden_data", null);
        LogUtils.d("启动上课禁用 data = " + data);

        if (data != null) {
            String[] arr = data.split(",");
            if (TextUtils.equals(arr[0], "0")) {// 时间段禁用
                setClassForbiddenAlarm(data);
            } else if (TextUtils.equals(arr[0], "1")) {// 课程表禁用
                String curriculumForbiddenData = mSp.getString(ReceiverConstant.EXTRA_CURRICULUM_FORBIDDEN_DATA, null);
                if (TextUtils.isEmpty(curriculumForbiddenData)) {
                    LogUtils.e("runClassForbiddenPolicy 课程表Forbidden Data is null");
                    return;
                }
                setCurriculumForbiddenAlarm(curriculumForbiddenData);
            }
        }
    }

    public void parseClassForbiddenData (String data) {
        if (TextUtils.isEmpty(data)) {
            LogUtils.e("上课禁用 Data is null");
            return;
        }
        mEditor.putString("class_forbidden_data", data).apply();

        setClassForbiddenAlarm(data);
    }

    // 比较时间的的大小，time1 > time2 返回true
    public static boolean compareTime(String[] time1, String[] time2) {
        if (Integer.parseInt(time1[0]) > Integer.parseInt(time2[0])) {
            return true;
        } else if (Integer.parseInt(time1[0]) == Integer.parseInt(time2[0])) {
            return Integer.parseInt(time1[1]) > Integer.parseInt(time2[1]);
        }

        return false;
    }

    // 生成每天的禁用时间段
    private ClassForbidden makeDayClassForbiden(List<ClassForbidden> classForbiddenList, String repeat) {
        ClassForbidden classForbidden = new ClassForbidden();
        List<String[]> Starts = new ArrayList<>();
        List<String[]> Stops = new ArrayList<>();

        for (ClassForbidden temp : classForbiddenList) {
            if (temp.amStart != null) {
                LogUtils.d("temp.amStart = " + Arrays.toString(temp.amStart)
                        + ", temp.amStop = " + Arrays.toString(temp.amStop));
                Starts.add(temp.amStart);
                Stops.add(temp.amStop);
            }

            if (temp.pmStart != null) {
                LogUtils.d("temp.pmStart = " + Arrays.toString(temp.pmStart)
                        + ", temp.pmStop = " + Arrays.toString(temp.pmStop));
                Starts.add(temp.pmStart);
                Stops.add(temp.pmStop);
            }

            if (temp.nightStart != null) {
                LogUtils.d("temp.nightStart = " + Arrays.toString(temp.nightStart)
                        + ", temp.nightStop = " + Arrays.toString(temp.nightStop));
                Starts.add(temp.nightStart);
                Stops.add(temp.nightStop);
            }
        }

        // 排序
        LogUtils.d("Starts size = " + Starts.size() + ", Stops size = " + Stops.size());
        Collections.sort(Starts, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] strings, String[] t1) {
                        if (compareTime(strings, t1)) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });

        Collections.sort(Stops, new Comparator<String[]>() {
            @Override
            public int compare(String[] strings, String[] t1) {
                if (compareTime(strings, t1)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        List < ForbiddenTime > forbiddenTimes = new ArrayList<ForbiddenTime>();
        int i = 0;
        int j = 0;
        do {
            LogUtils.d("a: i = " + i + " , j = " + j);
            ForbiddenTime forbiddenTime = new ForbiddenTime();
            forbiddenTime.start = Starts.get(i);
            do {
                LogUtils.d("b: i = " + i + " , j = " + j);
                if (i == Starts.size() - 1) { // 最后一个stop直接是停止时间
                    forbiddenTime.stop = Stops.get(j);
                    forbiddenTimes.add(forbiddenTime);
                    i = Starts.size();// 完成时间遍历
                    break;
                } else if (compareTime(Starts.get(i + 1), Stops.get(i))) {// start2 > stop1
                    forbiddenTime.stop = Stops.get(i);
                    forbiddenTimes.add(forbiddenTime);
                    i++;
                    j = i;
                    break;
                } else {
                    i++;
                }
            } while (i <= Starts.size() - 1);

        } while (i < Starts.size());


        classForbidden.repeat = repeat;
        classForbidden.forbiddenTimes = forbiddenTimes;

        for (int times = 0; times < forbiddenTimes.size(); times ++) {
            ForbiddenTime forbiddenTime = forbiddenTimes.get(times);
            LogUtils.d("第" + (times+1) + "个时间段:" + Arrays.toString(forbiddenTime.start) + "-" + Arrays.toString(forbiddenTime.stop));
        }

        return classForbidden;
    }

    private void setClassForbiddenAlarm(String data) {
        String[] arr = data.split(",");
        if (TextUtils.equals(arr[0], "0")) {// 关闭课程表禁用
            ClassForbiddenAlarm.cancelForbiddenAlarm(context, mAlarmManager);// 先删除所有闹钟
            enableClassForbidden(context,0);
            if (data.equals("0,")) {
                return;
            }
            LogUtils.d("关闭课程表禁用 = " + arr[1]);
            // 1@7981752865F695F46BB5@08:00-11:30$14:00-16:30$@12345|1@7981752865F695F46BB5@08:00-11:30$14:00-16:30$@12345|1@7981752865F695F46BB5@08:00-11:30$14:00-16:30$@12345
            String[] forbidden_item = arr[1].split("\\|");
            int forbidden_num = forbidden_item.length;
            List<ClassForbidden> classForbiddens = new ArrayList<ClassForbidden>();
            LogUtils.d("禁用项目数量 = " + forbidden_num);
            for(String s : forbidden_item) {
                String[] forbidden = s.split("@");
                int forbidden_size = forbidden.length;
                if ("1".equals(forbidden[0])) {
                    ClassForbidden classForbidden = new ClassForbidden();
                    classForbidden.name = unicodeNo0xuToString(forbidden[1]);
                    classForbidden.repeat = forbidden[forbidden_size-1];//StringUtils.stringSpilt(forbidden[forbidden_size-1], 1);
                    LogUtils.d("禁用项目名称 = " + classForbidden.name + ", repeat = " + classForbidden.repeat);
                    String[] times = forbidden[2].split("\\$");
                    for (String stime : times) {
                        LogUtils.d("time = " + stime);
                        if (TextUtils.isEmpty(stime)){
                            continue;
                        }
                        String[] temp = stime.split("-");
                        String timeStart = temp[0];// 08:00
                        String[] timeStartTemp = timeStart.split(":");
                        String timeStop = temp[1];// 11:30
                        String[] timeStopTemp = timeStop.split(":");
                        //LogUtils.d("timeStartTemp[0] = " + timeStartTemp[0]);
                        LogUtils.d( "timeStartTemp = " + timeStartTemp[0] + ":" + timeStartTemp[1]);
                        LogUtils.d( "timeStopTemp = " + timeStopTemp[0] + ":" + timeStopTemp[1]);
                        if (classForbidden.amStart == null) {
                            classForbidden.amStart = timeStartTemp;
                            classForbidden.amStop = timeStopTemp;
                        } else if (classForbidden.pmStart == null){
                            if (compareTime(classForbidden.amStart, timeStopTemp)) {
                                classForbidden.pmStart = classForbidden.amStart;
                                classForbidden.pmStop = classForbidden.amStop;
                                classForbidden.amStart = timeStartTemp;
                                classForbidden.amStop = timeStopTemp;
                            } else if (compareTime(timeStartTemp, classForbidden.amStop)) {
                                classForbidden.pmStart = timeStartTemp;
                                classForbidden.pmStop = timeStopTemp;
                            } else if (compareTime(classForbidden.amStart, timeStartTemp)) {
                                classForbidden.amStart = timeStartTemp;
                                if (compareTime(timeStopTemp, classForbidden.amStop)) {
                                    classForbidden.amStop = timeStopTemp;
                                }
                            } else if (compareTime(timeStopTemp, classForbidden.amStop)){
                                classForbidden.amStop = timeStopTemp;
                            }
                        } else {
                            if (compareTime(classForbidden.amStart, timeStopTemp) || compareTime(timeStartTemp, classForbidden.pmStop)
                            || (compareTime(timeStartTemp, classForbidden.amStop) && compareTime(classForbidden.pmStart, timeStopTemp))) {
                                classForbidden.nightStart = timeStartTemp;
                                classForbidden.nightStop = timeStopTemp;
                            } else if (compareTime(classForbidden.amStart, timeStartTemp)) {
                                if (compareTime(timeStopTemp, classForbidden.pmStop)) {
                                    classForbidden.amStart = timeStartTemp;
                                    classForbidden.amStop = timeStopTemp;
                                    classForbidden.pmStart = null;
                                    classForbidden.pmStop = null;
                                } else if (compareTime(timeStopTemp, classForbidden.pmStart) && compareTime(classForbidden.pmStop, timeStopTemp)) {
                                    classForbidden.amStart = timeStartTemp;
                                    classForbidden.amStop = classForbidden.pmStop;
                                    classForbidden.pmStart = null;
                                    classForbidden.pmStop = null;
                                } else if (compareTime(timeStopTemp, classForbidden.amStop) && compareTime(classForbidden.pmStart, timeStopTemp)) {
                                    classForbidden.amStart = timeStartTemp;
                                    classForbidden.amStop = timeStopTemp;
                                } else if (compareTime(timeStopTemp, classForbidden.amStart) && compareTime(classForbidden.amStop, timeStopTemp)) {
                                    classForbidden.amStart = timeStartTemp;
                                }
                            } else if (compareTime(classForbidden.amStop, timeStartTemp)) {
                                if (compareTime(timeStopTemp, classForbidden.pmStop)) {
                                    classForbidden.amStop = timeStopTemp;
                                    classForbidden.pmStart = null;
                                    classForbidden.pmStop = null;
                                } else if (compareTime(timeStopTemp, classForbidden.pmStart) && compareTime(classForbidden.pmStop, timeStopTemp)) {
                                    classForbidden.amStop = classForbidden.pmStop;
                                    classForbidden.pmStart = null;
                                    classForbidden.pmStop = null;
                                } else if (compareTime(timeStopTemp, classForbidden.amStop) && compareTime(classForbidden.pmStart, timeStopTemp)) {
                                    classForbidden.amStop = timeStopTemp;
                                }
                            } else if (compareTime(classForbidden.pmStart, timeStartTemp)) {
                                if (compareTime(timeStopTemp, classForbidden.pmStop)) {
                                    classForbidden.pmStart = timeStartTemp;
                                    classForbidden.pmStop = timeStopTemp;
                                } else if (compareTime(timeStopTemp, classForbidden.pmStart) && compareTime(classForbidden.pmStop, timeStopTemp)) {
                                    classForbidden.pmStart = timeStartTemp;
                                }
                            } else if (compareTime(timeStartTemp, classForbidden.pmStart) && compareTime(classForbidden.pmStop, timeStartTemp)
                                    && compareTime(timeStopTemp, classForbidden.pmStop)) {
                                classForbidden.pmStop = timeStopTemp;
                            }
                        }
                    }

                    classForbiddens.add(classForbidden);
                }
            }


            List<ClassForbidden> classForbiddensMondays = getClassForbiddenByRepeat(classForbiddens, "1");
            List<ClassForbidden> classForbiddensTuesdays = getClassForbiddenByRepeat(classForbiddens, "2");
            List<ClassForbidden> classForbiddensWednesdays = getClassForbiddenByRepeat(classForbiddens, "3");
            List<ClassForbidden> classForbiddensThursdays = getClassForbiddenByRepeat(classForbiddens, "4");
            List<ClassForbidden> classForbiddensFridays = getClassForbiddenByRepeat(classForbiddens, "5");
            List<ClassForbidden> classForbiddensSaturdays = getClassForbiddenByRepeat(classForbiddens, "6");
            List<ClassForbidden> classForbiddensSundays = getClassForbiddenByRepeat(classForbiddens, "7");
            if (classForbiddensMondays.size() > 0) {// 设置周一禁止使用时间
                ClassForbidden classForbiddensMonday = makeDayClassForbiden(classForbiddensMondays, "1");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensMonday, true);
            }
            if (classForbiddensTuesdays.size() > 0) {// 周二
                ClassForbidden classForbiddensTuesday = makeDayClassForbiden(classForbiddensTuesdays, "2");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensTuesday, true);
            }
            if (classForbiddensWednesdays.size() > 0) {// 周三
                ClassForbidden classForbiddensWednesday = makeDayClassForbiden(classForbiddensWednesdays, "3");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensWednesday, true);
            }
            if (classForbiddensThursdays.size() > 0) {// 周四
                ClassForbidden classForbiddensThursday = makeDayClassForbiden(classForbiddensThursdays, "4");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensThursday, true);
            }
            if (classForbiddensFridays.size() > 0) {// 周五
                ClassForbidden classForbiddensFriday = makeDayClassForbiden(classForbiddensFridays, "5");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensFriday, true);
            }
            if (classForbiddensSaturdays.size() > 0) {// 周六
                ClassForbidden classForbiddensSaturday = makeDayClassForbiden(classForbiddensSaturdays, "6");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensSaturday, true);
            }
            if (classForbiddensSundays.size() > 0) {// 周日
                ClassForbidden classForbiddensSunday = makeDayClassForbiden(classForbiddensSundays, "7");
                ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, classForbiddensSunday, true);
            }
        } else if (TextUtils.equals(arr[0], "1")) {// 开启
            LogUtils.d("开启课程表Forbidden = " + arr[0]);
            ClassForbiddenAlarm.cancelForbiddenAlarm(context, mAlarmManager);
            enableClassForbidden(context,0);
        } else {
            LogUtils.e("上课Forbidden 使能值错误（0,1）, error = " + arr[0]);
        }
    }

    public void parseCurriculumForbiddenData (String data) {
        if (TextUtils.isEmpty(data)) {
            LogUtils.e("课程表Forbidden Data is null");
            return;
        }
        mEditor.putString(ReceiverConstant.EXTRA_CURRICULUM_FORBIDDEN_DATA, data).apply();

        setCurriculumForbiddenAlarm(data);
    }

    private void setCurriculumForbiddenAlarm(String data) {
        // 1@08:00@09:00@10:00@11:00@14:00@15:00@16:00@|2@08:00@09:00@10:00@11:00@14:00@15:00@|3@08:00@09:00@10:00@11:00@14:00@15:00@|4@08:00@09:00@10:00@11:00@14:00@15:00@16:00@|5@08:00@09:00@10:00@11:00@14:00@15:00@|6@09:00@|7@10:00@
        String[] curriculumForbiddenData = data.split("\\|");

        for (String forbiddenData : curriculumForbiddenData) {
            LogUtils.d(TAG, "Forbidden data = " + forbiddenData);
            if (!forbiddenData.contains(":")) {
                continue;
            }
            String[] forbiddenDaTa = forbiddenData.split("@");
            ClassForbidden curriculumForbidden = new ClassForbidden();
            curriculumForbidden.repeat = forbiddenDaTa[0];
            for (int j = 1; j < forbiddenDaTa.length; j++) {
                String time = forbiddenDaTa[j];
                String[] arrTime = time.split(":");
                if (Integer.parseInt(arrTime[0]) > 0 && Integer.parseInt(arrTime[0]) <= 12) {
                    if (curriculumForbidden.amStart == null || (Integer.parseInt(arrTime[0]) < Integer.parseInt(curriculumForbidden.amStart[0]))) {
                        curriculumForbidden.amStart = arrTime;
                    }

                    if (curriculumForbidden.amStop == null || (Integer.parseInt(arrTime[0]) > Integer.parseInt(curriculumForbidden.amStop[0]))) {
                        curriculumForbidden.amStop = arrTime;
                    }
                } else if (Integer.parseInt(arrTime[0]) > 12 && Integer.parseInt(arrTime[0]) <= 18) {
                    if (curriculumForbidden.pmStart == null || (Integer.parseInt(arrTime[0]) < Integer.parseInt(curriculumForbidden.pmStart[0]))) {
                        curriculumForbidden.pmStart = arrTime;
                    }

                    if (curriculumForbidden.pmStop == null || (Integer.parseInt(arrTime[0]) > Integer.parseInt(curriculumForbidden.pmStop[0]))) {
                        curriculumForbidden.pmStop = arrTime;
                    }
                } else {
                    if (curriculumForbidden.nightStart == null || (Integer.parseInt(arrTime[0]) < Integer.parseInt(curriculumForbidden.nightStart[0]))) {
                        curriculumForbidden.nightStart = arrTime;
                    }

                    if (curriculumForbidden.nightStop == null || (Integer.parseInt(arrTime[0]) > Integer.parseInt(curriculumForbidden.nightStop[0]))) {
                        curriculumForbidden.nightStop = arrTime;
                    }
                }
            }
            // 停止时间需要加40分钟
            if (curriculumForbidden.amStart != null && curriculumForbidden.amStop != null) {
                curriculumForbidden.amStop = calculateStopTime(curriculumForbidden.amStop);
            }

            if (curriculumForbidden.pmStart != null && curriculumForbidden.pmStop != null) {
                curriculumForbidden.pmStop = calculateStopTime(curriculumForbidden.pmStop);
            }

            if (curriculumForbidden.nightStop != null && curriculumForbidden.nightStart != null) {
                curriculumForbidden.nightStop = calculateStopTime(curriculumForbidden.nightStop);
            }

            List < ForbiddenTime > forbiddenTimes = new ArrayList<ForbiddenTime>();
            ForbiddenTime forbiddenTimeAm = new ForbiddenTime();
            forbiddenTimeAm.start = curriculumForbidden.amStart;
            forbiddenTimeAm.stop = curriculumForbidden.amStop;
            forbiddenTimes.add(forbiddenTimeAm);
            ForbiddenTime forbiddenTimePm = new ForbiddenTime();
            forbiddenTimePm.start = curriculumForbidden.pmStart;
            forbiddenTimePm.stop = curriculumForbidden.pmStop;
            forbiddenTimes.add(forbiddenTimePm);
            ForbiddenTime forbiddenTimeNt = new ForbiddenTime();
            forbiddenTimeNt.start = curriculumForbidden.nightStart;
            forbiddenTimeNt.stop = curriculumForbidden.nightStop;
            forbiddenTimes.add(forbiddenTimeNt);

            curriculumForbidden.forbiddenTimes = forbiddenTimes;

            ClassForbiddenAlarm.setClassForbiddenAlarm(context, mAlarmManager, curriculumForbidden, true);
        }
    }

    private String[] calculateStopTime(String[] time) {
        LogUtils.d(TAG, "Forbidden 计算前：stopTime = " + Arrays.toString(time));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MILLISECONDS_IN_DAY, 40 * 60 * 1000);// 加40 分钟

        Date date = calendar.getTime();
        String stopTime = simpleDateFormat.format(date);
        LogUtils.d(TAG, "Forbidden 计算后：stopTime = " + stopTime);
        return stopTime.split(":");
    }

    /**
     * @param state 0:disable,1:enable
     */
    public static void enableClassForbidden(Context context, int state) {
        LogUtils.d("enableClassForbidden state = " + state);
        Intent classForbidden = new Intent();
        if (state == 0) {
            classForbidden.setAction(ClassForbiddenAlarmReceiver.ACTION_SET_CLASSFORBIDDEN_ALARM_STOP);
        } else {
            classForbidden.setAction(ClassForbiddenAlarmReceiver.ACTION_SET_CLASSFORBIDDEN_ALARM_START);
        }
        classForbidden.putExtra(CLASS_FORBIDDEN_STATE, state);
        context.sendBroadcast(classForbidden);
    }

    private List<ClassForbidden> getClassForbiddenByRepeat(List<ClassForbidden> classForbiddens, String repeat) {
        List<ClassForbidden> dayClassForbiddensList = new ArrayList<ClassForbidden>();
        for (ClassForbidden classForbidden : classForbiddens) {
            if (classForbidden.repeat.contains(repeat)) {
                dayClassForbiddensList.add(classForbidden);
            }
        }
        return dayClassForbiddensList;
    }

    /*--------------------------------ip-----------------------------------*/

    public void setOnOrderListener(OrderListener l) {
        orderListener = l;
    }

    public void setServiceListener(ServiceListener l) {
        serviceListener = l;
    }

    public void startSocket() {
        if (!OkSocket.isIsSetIPAndPort() && !isParseDomain()) { //未解析
            getIP(domainName);
            sp.edit().clear().apply();
        } else {
            String ip = sp.getString(SPConstant.CURRENT_IP, "");
            int port = sp.getInt(SPConstant.CURRENT_PORT, -1);
            isParseDomain = true;
            creatSocket(ip, port);
        }
    }

    public void stopSocket() {
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
        SLog.e("Socket已停止");
    }

    /*---------------------------------socket----------------------------------*/

    public boolean isParseDomain() {
        return isParseDomain;
    }

    /**
     * 解析域名
     *
     * @param domain 待解析域名
     */
    private void getIP(String domain) {
        new MyTask().execute(domain);

    }

    public IConnectionManager getmManager() {
        if (mManager == null) {
            return null;
        }
        return mManager;
    }

    /**
     * 发送
     *
     * @param msg 待发送的文本信息
     */
    private void sendMsg(String msg) {
        MsgDataBean msgDataBean = new MsgDataBean(msg);
        if (mManager != null) {
            SLog.e(msg + "      正在发送中...");
            mManager.send(msgDataBean);
        } else {
            SLog.e("未创建连接");
            LocationService.pull(context);// 重新连接socket服务
        }
    }

    //接收
    private StringBuffer mStringBuffer = new StringBuffer();
    private StringBuffer mOriginStringBuffer = new StringBuffer();
    /*数据包拼接步骤：
      1.接受服务器的数据，先判断数据包是否为一条完整的指令；
      2.若是一条完整的指令，则执行5，并清空缓存；若不是一条完整的指令，则执行3；
      3.缓存不完整的指令，判断当前缓存指令中是否有完整的指令；
      4.若缓存指令中存在完整的指令，截取完整的指令并执行5，剩余数据继续缓存，并执行1；若不存在完整的指令，则执行1
      5.执行指令解析程序。*/
    private void receiveMsg(TcpMsg msg, OriginalData data) {
        if (msg.contentStr.trim().length() > 0) {
            SLog.e("接收到数据=" + msg.contentStr);
            SLog.e("缓存的数据=" + mStringBuffer.toString());
            SLog.e("缓存的原始数据=" + mOriginStringBuffer.toString());

            String originHexStr = byte2HexStr(data.getBodyBytes());
            SLog.e("receiveMsg 原始数据字节(HEX)：" + originHexStr);
            String originStr = hexStringToString(originHexStr);
            SLog.e("receiveMsg 原始数据字符串：" + originStr);

            try {
                if (msg.contentStr.contains(MsgType.IWBP03)) {
                    mManager.getPulseManager().feed();
                }
                if (isOneFullOrder(msg.contentStr)) {//step 1
                    if (!parsVoiceMsg(msg.contentStr, originHexStr)) {
                        MsgRecService.instance().handleRecvMsg(Utils.getContext(), msg);//step 2 and 5
                    }
                    mStringBuffer.delete(0, mStringBuffer.length());
                    mOriginStringBuffer.delete(0, mOriginStringBuffer.length());
                    return;
                } else { //多包粘合
                    SLog.e("多包粘合数据=" + msg.contentStr);//step 3
                    List<String> dataPackages = new ArrayList<String>();
                    StringBuilder order = new StringBuilder();
                    for (int i = 0; i < msg.contentStr.length(); i++) {
                        char temp = msg.contentStr.charAt(i);
                        order.append(temp);
                        if (i == msg.contentStr.length() - 1 ||
                                (temp == '#' && msg.contentStr.charAt(i+1) == 'I' && msg.contentStr.charAt(i+2) == 'W')) { // 中间是“#IW”
                            dataPackages.add(order.toString());
                            order.setLength(0);
                        }
                    }
                    // 原生数据分包
                    List<String> originStrings = new ArrayList<>();
                    StringBuilder origin = new StringBuilder();
                    String[] originTemp = originHexStr.split(" ");
                    for (int i = 0; i < originTemp.length; i++) {
                        origin.append(originTemp[i]);
                        if ((i == originTemp.length - 1) ||
                                ("23".equals(originTemp[i]) && "49".equals(originTemp[i+1]) && "57".equals(originTemp[i+2]))) { // 中间是“#IW”
                            originStrings.add(origin.toString());
                            origin.setLength(0);
                        } else {
                            origin.append(" ");// 原始数据用空格隔开
                        }
                    }
                    SLog.e("分包数据大小=" + dataPackages.size());
                    for (String dataPackage : dataPackages) {
                        SLog.e("分包数据=" + dataPackage);
                        if (dataPackage.startsWith("IW") && dataPackage.endsWith("#")) {
                            if (!parsVoiceMsg(dataPackage, originStrings.get(dataPackages.indexOf(dataPackage)))) {
                                MsgRecService.instance().handleRecvMsg(Utils.getContext(), buildTcpMsg(dataPackage));
                            }
                        } else {
                            mStringBuffer.append(dataPackage);
                            mOriginStringBuffer.append(originStrings.get(dataPackages.indexOf(dataPackage)));
                        }
                    }
                }

                if (mStringBuffer.length() > 0) {
                    List<String> cacheDataPackages = new ArrayList<String>();
                    List<String> cacheOriginDataPackages = new ArrayList<String>();
                    StringBuilder cacheOrder = new StringBuilder();
                    StringBuilder cacheOrigin = new StringBuilder();
                    String[] originTemp = mOriginStringBuffer.toString().split(" ");
                    for (int i = 0; i < mStringBuffer.length(); i++) {
                        char temp = mStringBuffer.charAt(i);
                        cacheOrder.append(temp);
                        if ((i == msg.contentStr.length() - 1) ||
                                (temp == '#' && mStringBuffer.charAt(i+1) == 'I' && mStringBuffer.charAt(i+2) == 'W')) { // 中间是“#IW”
                            cacheDataPackages.add(cacheOrder.toString());
                            cacheOrder.delete(0, cacheOrder.length());
                        }
                    }
                    for (int i = 0; i < originTemp.length; i++) {
                        cacheOrigin.append(originTemp[i]);
                        if ((i == originTemp.length - 1) ||
                                ("23".equals(originTemp[i]) && "49".equals(originTemp[i+1]) && "57".equals(originTemp[i+2]))) { // 中间是“#IW”
                            cacheOriginDataPackages.add(cacheOrigin.toString());
                            cacheOrigin.delete(0, cacheOrigin.length());
                        } else {
                            cacheOrigin.append(" ");
                        }
                    }

                    if (mStringBuffer.toString().contains(MsgType.IWBP03)) {
                        mManager.getPulseManager().feed();
                    }

                    mStringBuffer.delete(0, mStringBuffer.length());
                    mOriginStringBuffer.delete(0, mOriginStringBuffer.length());
                    for (String cacheDataPackage : cacheDataPackages) {
                        SLog.e("缓存分包数据=" + cacheDataPackage);
                        if (cacheDataPackage.startsWith("IW") && cacheDataPackage.endsWith("#")) {//step 4
                            if (!parsVoiceMsg(cacheDataPackage, cacheOriginDataPackages.get(cacheDataPackages.indexOf(cacheDataPackage)))) {
                                MsgRecService.instance().handleRecvMsg(Utils.getContext(), buildTcpMsg(cacheDataPackage));
                            }
                        } else {
                            mStringBuffer.append(cacheDataPackage);
                            mOriginStringBuffer.append(cacheOriginDataPackages.get(cacheDataPackages.indexOf(cacheDataPackage)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "没有数据返回不更新");
        }
    }

    /*
     * 处理群聊文本和好友语音聊天消息：
     * 群聊和单聊语音要使用最原始的数据，此处无法转换
     */
    private boolean parsVoiceMsg(String str, String originHexStr) {
        boolean bRet = false;
        SLog.e("parseChatMsg 命令字符串：" + str);
        SLog.e("parseChatMsg 原始数据字节(HEX)：" + originHexStr);
        String originStr = hexStringToString(originHexStr);
        SLog.e("parseChatMsg 原始数据字符串：" + originStr);
        String[] temp1 = originHexStr.split(" ");
        String[] temp2 = originStr.split(",");

        if (str.contains(MsgType.IWBPCD)) {
            if (!temp2[4].equals("3")) {// 单聊语音和图片IWBPCD,214969,72387238,014310,1,3,1,1024,#!AMR
                parseRecordingFile(str, temp1, temp2);
                bRet = true;
            }
        } else if (str.contains(MsgType.IWBP28)) {
            if (!temp2[3].equals("0")) {// 群聊语音和图片IWBP28,214969,xxxxx,3,1,1024,#!AMR
                parseRecordingFile(str, temp1, temp2);
            } else {//  文字下行
                String bp28Str = parseBP28Sender(str, temp1);
                Intent qlintent;
                String content = bp28Str.replace("IWBP28,", "");
                LogUtils.d("gaobin content:" + content);
                String[] qltemp = content.split(",");
                MsgSender.sendTxtMsg(MsgType.IWAP28, qltemp[0] + "," + qltemp[1] + ",0,0,1");
                qlintent = new Intent(ReceiverConstant.ACTION_BP28);
                qlintent.putExtra(ReceiverConstant.EXTRA_BP28, content);
                Utils.getContext().sendBroadcast(qlintent);
            }
            bRet = true;
        } else if (str.contains(MsgType.IWBP96)){
            temp2 = str.split(",");
            if (!temp2[5].equals("0")) {// 好友语音：IWBP96,353456789012345,XXXX,D3590D54,XXXX,6,1,1024,XXX
                parseRecordingFile(str);
                bRet = true;
            }
        }

        return bRet;
    }

    private boolean isOneFullOrder(String str) {
        return str.startsWith("IW") && str.endsWith("#") && !str.contains("#IW");
    }

    private TcpMsg buildTcpMsg(String str) {
        TcpMsg tcpMsg = new TcpMsg();
        OriginalData originalData = new OriginalData();
        originalData.setBodyBytes(str.getBytes());
        tcpMsg.setSourceDataBytes(originalData.getBodyBytes());
        tcpMsg.setSourceDataString(str);
        tcpMsg.contentStr = str;
        return tcpMsg;
    }

    private void creatSocket(String ip, int port) {
        mInfo = new ConnectionInfo(ip, port);
        if(mOkOptions == null){
            mOkOptions = new OkSocketOptions.Builder()
                    .setReconnectionManager(DefaultReconnectManager.getInstance())
                    .setReaderProtocol(new IWNormalReaderProtocol())
                    .setReadPackageBytes(2048)
                    .setWritePackageBytes(2048)
                    .setPulseFrequency(3 * 60 * 1000)
                    .build();
        }
        if(mManager == null)mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);
        mManager.connect();
    }

    public interface OrderListener {

        void sendOrderSuccess(String result);

        void sendOrderFail(String result);

        void receiverOrder(TcpMsg tcpMsg);
    }


    public interface ServiceListener {

        void serviceStart(OrderUtil instance);
    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        String IPAddress = "";
        InetAddress ReturnStr1 = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String domain = params[0];
            try {
                ReturnStr1 = InetAddress.getByName(domain);
                IPAddress = ReturnStr1.getHostAddress();
            } catch (UnknownHostException e) {
                return "";
            }
            return IPAddress;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.isEmpty()) {
                isParseDomain = false;
                SLog.e("域名解析失败..." + s);
                SLog.e("开始设置默认IP地址..." + ip);
            } else {
                SLog.e("域名解析成功..." + s);
                ip = s;
                isParseDomain = true;
            }
            creatSocket(ip, port);
            super.onPostExecute(s);
        }
    }
}
