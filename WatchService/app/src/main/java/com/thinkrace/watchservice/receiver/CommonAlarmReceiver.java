package com.thinkrace.watchservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thinkrace.watchservice.KApplication;
import com.thinkrace.watchservice.ReceiverConstant;
import com.thinkrace.watchservice.function.alarm.AlarmEntity;
import com.thinkrace.watchservice.function.alarm.AlarmTimer;
import com.thinkrace.watchservice.function.location.AMapLocationManager;
import com.thinkrace.watchservice.model.location.SmartLocationBean;
import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.thinkrace.watchservice.orderlibrary.call.CallFragmentActivity;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.orderlibrary.utils.GSMCellLocationUtils;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.NetworkUtil;
import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;
import com.thinkrace.watchservice.orderlibrary.utils.TimeUtils;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;
import com.thinkrace.watchservice.orderlibrary.utils.VoiceFileUtils;
import com.thinkrace.watchservice.parser.MsgRecService;
import com.thinkrace.watchservice.parser.MsgSender;
import com.xuhao.android.libsocket.sdk.client.action.IAction;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.BATTERY_SERVICE;
import static com.thinkrace.watchservice.orderlibrary.utils.UnicodeUtils.stringToUnicode;
import static com.thinkrace.watchservice.orderlibrary.utils.VoiceFileUtils.readFileToString;

public class CommonAlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private boolean makeFriend;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String logTxt = " CommonAlarmReceiver " + action;
        IConnectionManager iConnectionManager = OrderUtil.getInstance().getmManager();

        Intent intentTemp = new Intent();
        LogUtils.d(logTxt);
        switch (action) {
            case ACTION_CONNECTIVITY_CHANGE: //网络变化广播
                LogUtils.e("网络发生变化："+"typeName="+ NetworkUtil.getNetworkTypeName(context)+"  type"+NetworkUtil.getNetworkType(context));
                if (NetworkUtil.isNetworkAvailable(context) && iConnectionManager != null) {
                    LogUtils.e("iCo111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111nnectionManager="+iConnectionManager +"  isParseDomain="+OrderUtil.getInstance().isParseDomain() +" isConnect="+iConnectionManager.isConnect());
                    if (!OrderUtil.getInstance().isParseDomain()) {
                        LogUtils.e("未解析 开始解析");
                        OrderUtil.getInstance().startSocket();
                    } else if (!iConnectionManager.isConnect()) {
                        LogUtils.e("已解析 开始连接");
                        iConnectionManager.connect();
                    }
                } else {
                    if(iConnectionManager!=null){
                        iConnectionManager.disconnect(new Exception(IAction.ACTION_DISCONNECTION));
                    }
                }
                break;
            case ReceiverConstant.LOCATION_START://立即定位开始广播
                AMapLocationManager.instance().start();
                break;
            case ReceiverConstant.LOCATION_STOP://定位停止广播
                AlarmTimer.cancelAlarmTimer(context, new AlarmEntity(AlarmEntity.Type.LocateStop));
                AlarmTimer.cancelAlarmTimer(context, new AlarmEntity(AlarmEntity.Type.LocateStart));
                AlarmTimer.cancelAlarmTimer(context, new AlarmEntity(AlarmEntity.Type.CONFIRMED_FREQUENCY_UPLOAD));
                break;
            case ReceiverConstant.ACTION_REQUEST_WEATHER: //请求天气广播
                OrderUtil.getInstance().requestWeather(GSMCellLocationUtils.getParseBaseStation(Utils.getContext()), "0");
                break;
            case ReceiverConstant.ACTION_CALL_WHITELIST_NUM: //获取联系人白名单
                String whiteData = intent.getStringExtra(ReceiverConstant.EXTRA_CALL_WHITELIST_NUM);
                LogUtils.e("收到待添加处理白名单(contact)："+ whiteData);
                OrderUtil.getInstance().deleteAllContacts(context);
                if (!TextUtils.isEmpty(whiteData)) {
                    String[] contacts = whiteData.split(",");
                    OrderUtil.getInstance().addContact(context, contacts);
                }
                break;
            case ReceiverConstant.ACTION_URL_WHITELIST: //获取网址白名单
                String whiteUrls = intent.getStringExtra(ReceiverConstant.EXTRA_URL_WHITELIST);
                LogUtils.e("收到待添加处理白名单(url)："+ whiteUrls);
                OrderUtil.getInstance().addUrl(context, whiteUrls);
                break;
            case ACTION_SMS_RECEIVED:
                Object[] objects = (Object[]) intent.getExtras().get("pdus");
                int size = objects.length;
                HashMap<String, String> numberMap = new HashMap<>();
                for (int i = 0; i < size; i ++) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) objects[i]);
                    String number = sms.getOriginatingAddress().trim();
                    String content = sms.getMessageBody().trim();
                    if (numberMap.containsKey(number)) {
                        String lastPart = numberMap.get(number);
                        numberMap.put(number, lastPart + content);
                    } else {
                        numberMap.put(number, content);
                    }
                }

                Iterator iter = numberMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String numberKey = entry.getKey().toString().trim();
                    String bodyValue = entry.getValue().toString().trim();
                    String forwardMessage = numberKey + "," + stringToUnicode(bodyValue);
                    Log.e("forward sms ", numberKey + ":" + bodyValue);
                    MsgSender.sendTxtMsg(MsgType.IWAPTB, forwardMessage);
                    Log.e("forwardMessage", forwardMessage);
                }
                numberMap.clear();
                break;
            case ReceiverConstant.ACTION_BP26: //设置上课隐身时间段
                LogUtils.e("设置上课隐身时间段");
                String classTime = intent.getStringExtra(ReceiverConstant.EXTRA_BPD5);
                break;
            case ReceiverConstant.ACTION_BP31: //远程关机
                LogUtils.e("远程关机");
                intentTemp.setAction("com.android.internal.intent.action.REQUEST_SHUTDOWN");
                intentTemp.putExtra("android.intent.extra.KEY_CONFIRM", false);
                intentTemp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                KApplication.getInstance().getApplicationContext().startActivity(intentTemp);
                break;
            case ReceiverConstant.ACTION_BPD2: //学生端APP控制
                LogUtils.e("学生端APP控制");
                String appData = intent.getStringExtra(ReceiverConstant.EXTRA_BPD2);
                LogUtils.e("收到待处理学生端APP控制数据(app control)："+ appData);
                OrderUtil.getInstance().appControl(context, appData);
                break;
            case ReceiverConstant.ACTION_BPD5: //定时开关机
                LogUtils.e("定时开关机");
                String alarmData = intent.getStringExtra(ReceiverConstant.EXTRA_BPD5);
                LogUtils.e("收到待处理开关机数据(powerOnOff)："+ alarmData);
                //alarmData = "1,16:42,16:38";
                if (!TextUtils.isEmpty(alarmData)) {
                    String[] powerDatas = alarmData.split(",");
                    OrderUtil.getInstance().setSchedulePowerOnOff(context, powerDatas);
                }
                break;
            case ReceiverConstant.ACTION_BPD6: //挂失处理
                String enableLoss = intent.getStringExtra(ReceiverConstant.EXTRA_BPD6);
                Settings.Global.putInt(context.getContentResolver(), "report_loss", Integer.valueOf(enableLoss));
                intentTemp.setAction("com.android.hotpeper.REPORT_LOSS");
                intentTemp.setFlags(intent.getFlags()| 0x01000000);
                if (TextUtils.equals("0", enableLoss)) {
                    intentTemp.putExtra("isReportLoss", 0);
                    OrderUtil.getInstance().cancelReportDeviceLoss();
                } else {
                    intentTemp.putExtra("isReportLoss", 1);
                    OrderUtil.getInstance().reportDeviceLoss();
                }
                context.sendBroadcast(intentTemp);
                LogUtils.e("挂失处理 = " + enableLoss);
                break;
            case ReceiverConstant.ACTION_BPD7: //预留电量
                String enable = intent.getStringExtra(ReceiverConstant.EXTRA_BPD7);
                Settings.Global.putInt(context.getContentResolver(), "reserve_power", Integer.valueOf(enable));
                BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
                int batteryPercentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                LogUtils.e("预留电量状态 = " + enable + ", batteryPercentage = " + batteryPercentage);
                OrderUtil.getInstance().runReservePower(context, batteryPercentage);
                break;
            case ReceiverConstant.ACTION_BPD9: //上课禁用
                String BPD9_data = intent.getStringExtra(ReceiverConstant.EXTRA_BPD9);
                LogUtils.e("上课禁用 = " +  BPD9_data);
                OrderUtil.getInstance().parseClassForbiddenData(BPD9_data);
                break;
            case ReceiverConstant.ACTION_CURRICULUM_FORBIDDEN:
                String curriculumForbiddenData = intent.getStringExtra(ReceiverConstant.EXTRA_CURRICULUM_FORBIDDEN_DATA);
                LogUtils.e("课程表禁用 = " +  curriculumForbiddenData);
                OrderUtil.getInstance().parseCurriculumForbiddenData(curriculumForbiddenData);
                break;
            case ReceiverConstant.ACTION_BPDA: //上学守护
                LogUtils.e("上学守护");
                String data = intent.getStringExtra(ReceiverConstant.EXTRA_BPDA);
                OrderUtil.getInstance().parseGuardData(data);
                break;
            case ReceiverConstant.CONFIRMED_FREQUENCY_UPLOAD://固定频率上传位置信息广播
                AMapLocationManager.instance().start();//开始定位
                AlarmTimer.startConfirmedFrequencyUpload(context);//开启下一个闹钟
                break;
            case ReceiverConstant.ACTION_AP04:// 低电提醒
                MsgSender.sendTxtMsg("",
                        MsgType.IWAP04
                                + intent.getStringExtra(ReceiverConstant.EXTRA_BP04));
                break;
            case ReceiverConstant.ACTION_REQUEST_VOICE_LIST:// 获取语音列表
                String pageNo = intent.getStringExtra("pageNo");
                String pageCount = intent.getStringExtra("pageCount");
                VoiceFileUtils.getVoiceList(pageNo, pageCount);
                break;
            case ReceiverConstant.ACTION_UPDATE_READ:// 语音已读标记
                String fileId = intent.getStringExtra("FileId");
                VoiceFileUtils.updateReadFile(fileId);
                break;
            case ReceiverConstant.ACTION_AP07:// 语音上行
                String voiceFilePath = intent.getStringExtra(ReceiverConstant.EXTRA_BP07_Path);
                String Long = intent.getStringExtra(ReceiverConstant.EXTRA_BP07_Long);
                String Type = intent.getStringExtra(ReceiverConstant.EXTRA_BP07_Type);
                String Filetype = intent.getStringExtra(ReceiverConstant.EXTRA_BP07_Filetype);
                String identity = intent.getStringExtra(ReceiverConstant.EXTRA_BP07_Identity);
                LogUtils.i("聊天类型（1群，2单，0好友）：" + Type + " , 文件类型" + Filetype + " , 目标：" + identity);
                if ("1".equals(Filetype)) {
                    if ("1".equals(Type)) { // 群聊语音
                        VoiceFileUtils.upLoadVoiceFile(voiceFilePath, Long, Type, Filetype, "");
                    } else if ("2".equals(Type)){ // 单聊语音
                        VoiceFileUtils.upLoadVoiceFile(voiceFilePath, Long, Type, Filetype, identity);
                    } else { // 好友发语音
                        // TODO:给好友发语音
                        VoiceFileUtils.sendVoiceToFriend(voiceFilePath, Long, identity);
                    }
                } else if ("2".equals(Filetype)) { // 群聊文本
                    MsgSender.sendTxtMsg(MsgType.IWAP07,
                            TimeUtils.millis2String(System.currentTimeMillis(), new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()))
                                    + "," + "0" + "," + "0" + "," + Long + "," + stringToUnicode(voiceFilePath));
                }
                break;
            case ReceiverConstant.ACTION_AP42:// 图片上行
                voiceFilePath = intent.getStringExtra(ReceiverConstant.EXTRA_BP42_Path);
                Type = intent.getStringExtra(ReceiverConstant.EXTRA_BP42_Type);
                Filetype = intent.getStringExtra(ReceiverConstant.EXTRA_BP42_Filetype);
                VoiceFileUtils.upLoadPicFile(voiceFilePath, Type, Filetype);
                break;
            case ReceiverConstant.ACTION_APCU:// 单聊上行语音图片文字
                String msg_id = intent.getStringExtra(ReceiverConstant.EXTRA_APCU_ID);
                String msg_content = intent.getStringExtra(ReceiverConstant.EXTRA_APCU_CONTENT);
                MsgSender.sendTxtMsg(MsgType.IWAPCU,
                        msg_id + "," + "080835" + "," + "3" + ","+ stringToUnicode(msg_content));
                break;
            case ReceiverConstant.ACTION_AP46:// 立即拍照
                MsgSender.sendTxtMsg(MsgType.IWAP46,
                        MsgRecService.bp46SerialNumber   //指令流水号
                                + GlobalSettings.MSG_CONTENT_SEPERATOR
                                + intent.getStringExtra(ReceiverConstant.EXTRA_BP46) //1：表示设备执行成功,0 表示设备执行失败,5表示设备正在上传照片中
                );
                break;
            case ReceiverConstant.ACTION_BPNS:// 搜索wifi
                MsgSender.sendTxtMsg(MsgType.IWAPNS,
                        MsgRecService.tempSearchWifiSerialNumber //指令流水号
                                + GlobalSettings.MSG_CONTENT_SEPERATOR
                                + intent.getStringExtra(ReceiverConstant.EXTRA_BPNS)
                );
                break;
            case ReceiverConstant.ACTION_APCL:// 获取语音聊天列表(监护人列表)
                MsgSender.sendTxtMsg(MsgType.IWAPCL,
                        GlobalSettings.instance().getImei()
                                + GlobalSettings.MSG_CONTENT_SEPERATOR
                                + "1");
                break;
            case ReceiverConstant.ACTION_APTD:// 同步监护人列表(监护人列表)
                MsgSender.sendTxtMsg(MsgType.IWAPTD,
                        GlobalSettings.instance().getImei()
                                + GlobalSettings.MSG_CONTENT_SEPERATOR
                                + "0"
                                + GlobalSettings.MSG_CONTENT_SEPERATOR
                                + "0");
                break;
            case ReceiverConstant.ACTION_APT3:// 获取好友列表(好友列表)
                MsgSender.sendTxtMsg(MsgType.IWAPT3,
                        GlobalSettings.instance().getImei()
                                + GlobalSettings.MSG_CONTENT_SEPERATOR
                                + "0");
                break;
            case ReceiverConstant.ACTION_APCL_ID:// 获取语音聊天通话ID
                String id = intent.getStringExtra(ReceiverConstant.EXTRA_BPCL_ID);
                LogUtils.i("对方id======" + id);
                // 拨打电话
                if (KApplication.bConnect) {
                    Intent mIntent = new Intent(context, CallFragmentActivity.class);
                    mIntent.putExtra("phoneNumber", id);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mIntent);
                } else {
                    Toast.makeText(context, "网络断开等待重连", Toast.LENGTH_SHORT).show();
                }
                break;
                // 请求添加好友
            case ReceiverConstant.ACTION_REQUEST_FRIEND:
                String imei = intent.getStringExtra(ReceiverConstant.EXTRA_IMEI);
                String type = intent.getStringExtra(ReceiverConstant.EXTRA_REQUEST_TYPE);
                String content = type + GlobalSettings.MSG_CONTENT_SEPERATOR + imei;
                LogUtils.d("gaobin 请求添加好友:" + content);
                MsgSender.sendTxtMsg(MsgType.IWAPTC, content);
                break;
                // 删除好友
            case ReceiverConstant.ACTION_DELETE_FRIEND:
                String imei2 = intent.getStringExtra(ReceiverConstant.EXTRA_IMEI);
                MsgSender.sendTxtMsg(MsgType.IWAPRF, imei2);
                break;

            case ReceiverConstant.ACTION_APT4:// 碰碰交友 --- 交友后接收的是2 并且带IMEI表示已经是好友
                makeFriend = true; //开始交友
                AMapLocationManager.instance().start();
                AMapLocationManager.instance().setLocationListener(new AMapLocationManager.LocationListener() {
                    @Override
                    public void location(SmartLocationBean bean, String gsm, String battery) {
                        if (makeFriend) {
                            MsgSender.sendTxtMsg(MsgType.IWAPT4,
                                    bean.mDate
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + bean.mTime
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + bean.mLatitude
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + bean.mLongitude
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + bean.accuracy
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + "0"
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + "0"
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + "gcj02"
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + gsm
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + battery
                                            + GlobalSettings.MSG_CONTENT_SEPERATOR
                                            + "1"
                            );
                            makeFriend = false; //交友完成
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

}
