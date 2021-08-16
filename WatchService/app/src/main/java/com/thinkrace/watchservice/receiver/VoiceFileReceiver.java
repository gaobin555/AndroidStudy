package com.thinkrace.watchservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.thinkrace.watchservice.ReceiverConstant;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;
import com.thinkrace.watchservice.orderlibrary.utils.TimeUtils;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;
import com.thinkrace.watchservice.parser.MsgSender;
import com.xuhao.android.common.constant.OrderConstans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.thinkrace.watchservice.orderlibrary.utils.DigitalConvert.combineString;
import static com.thinkrace.watchservice.orderlibrary.utils.DigitalConvert.hexStr2Bytes;

public class VoiceFileReceiver extends BroadcastReceiver {
    private static boolean RECEIVING = false;

    private static List<VoiceMessages> mVoiceMsgs = new ArrayList<VoiceMessages>();

    // send amr voice to friend
    private static String[] amrData_send = null;

    public static void voiceMsgDataReset() {
        RECEIVING = false;
        mVoiceMsgs = new ArrayList<VoiceMessages>();
    }

    public static boolean getReceiving() {
        LogUtils.d("getReceiving RECEIVING = " + RECEIVING);
        return RECEIVING;
    }

//    public static void setReceiving(boolean bReceiving) {
//        RECEIVING = bReceiving;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d("VoiceFileReceiver:" + action);
        if (OrderConstans.BPCD.equals(action)) {// 接收单聊语音消息：IWBPCD,6,72387238,074542(流水号),1,9,1,1024,,#!AMR,data
            RECEIVING = true;
            String content = intent.getStringExtra("protocol_head");
            LogUtils.d("BPCD receive content = " + content);

            String[] split = content.split(",");

            VoiceMessages singleMessage = getVoiceMsgBySender(split[1]);
            LogUtils.d("Tag = " + split[1]);
            if (singleMessage == null) {// 开始接受第一段消息
                singleMessage = new VoiceMessages(split[1]);
                mVoiceMsgs.add(singleMessage);
            }

            if ("1".equals(split[6])) {
                singleMessage.setIndex(0);
                singleMessage.amrData = null;
            }

            int index = singleMessage.getIndex() + 1;
            LogUtils.d("single msg index = " + index + ", sender = " + singleMessage.sender);
            if (!String.valueOf(index).equals(split[6])) {
                LogUtils.e("single msg index error!!");
                RECEIVING = false;
                return;
            } else {
                singleMessage.setIndex(index);
            }

            if (split[5].equals(split[6])) {
                singleMessage.amrData = combineString(singleMessage.amrData, intent.getStringArrayExtra("amr_data"));
                MsgSender.sendTxtMsg(MsgType.IWAPCD, split[1] + "," + split[3] + "," + split[4] + "," + split[5] + "," + split[6] + ",1");
                saveRecordingFile(split, singleMessage);
            } else {
                String[] temp = new String[intent.getStringArrayExtra("amr_data").length];
                System.arraycopy(intent.getStringArrayExtra("amr_data"), 0, temp, 0, temp.length);
                singleMessage.amrData = combineString(singleMessage.amrData, temp);
                LogUtils.d("BPCD receive amrData length = " + singleMessage.amrData.length);
                MsgSender.sendTxtMsg(MsgType.IWAPCD, split[1] + "," + split[3] + "," + split[4] + "," + split[5] + "," + split[6] + ",1");
            }
//            LogUtils.d("BPCD amrData = " + Arrays.toString(singleMessage.amrData));
        } else if (OrderConstans.BP28.equals(action)) {// 接收群聊语音消息
            RECEIVING = true;
            String content = intent.getStringExtra("protocol_head");
            LogUtils.d("BP28 receive content = " + content);// IWBP28,72387238,000001(流水号),15,1,1024,#!AMR

            String[] split = content.split(",");
            VoiceMessages groupMessage = getVoiceMsgBySender(split[1]);
            LogUtils.d("Sender = " + split[1]);
            if (groupMessage == null) {
                groupMessage = new VoiceMessages(split[1]);
                mVoiceMsgs.add(groupMessage);
            }

            if ("1".equals(split[4])) {
                groupMessage.setIndex(0);
                groupMessage.amrData = null;
            }

            int index = groupMessage.getIndex() + 1;
            LogUtils.d("group msg index = " + index + ", sender = " + groupMessage.sender);
            if (!String.valueOf(index).equals(split[4])) {
                LogUtils.e("group msg index error!! ");
                requestNextVoiceMsg();
                return;
            } else {
                groupMessage.setIndex(index);
            }

            if (split[3].equals(split[4])) {
                groupMessage.amrData = combineString(groupMessage.amrData, intent.getStringArrayExtra("amr_data"));
                MsgSender.sendTxtMsg(MsgType.IWAP28, split[1] + "," + split[2] + "," + split[3] + "," + split[4] + ",1");
                saveRecordingFile(split, groupMessage);
            } else {
                String[] temp = new String[intent.getStringArrayExtra("amr_data").length];
                System.arraycopy(intent.getStringArrayExtra("amr_data"), 0, temp, 0, temp.length);
                groupMessage.amrData = combineString(groupMessage.amrData, temp);
                LogUtils.d("BP28 receive amrData length = " + groupMessage.amrData.length);
                MsgSender.sendTxtMsg(MsgType.IWAP28, split[1] + "," + split[2] + "," + split[3] + "," + split[4] + ",1");
            }
        } else if (OrderConstans.BP96.equals(action)) { // 接收好友语音消息
            RECEIVING = true;
            String content = intent.getStringExtra("protocol_head");
            String tempData = intent.getStringExtra("amr_data");

            LogUtils.d("BP96 receive content = " + content);// IWBP96,868872039000008,5AE35AE3,055700,4,1,1024,
            LogUtils.d("BP96 receive amrData = " + tempData);
            String[] split = content.split(",");
            LogUtils.d("Sender = " + split[1]);
            VoiceMessages friendMessage = getVoiceMsgBySender(split[1]);
            if (friendMessage == null) {// 开始时清空
                friendMessage = new VoiceMessages(split[1]);
                mVoiceMsgs.add(friendMessage);
            }

            if ("1".equals(split[5])) {
                friendMessage.setIndex(0);
                friendMessage.amrData_friend.delete(0, friendMessage.amrData_friend.length());
            }

            int index = friendMessage.getIndex() + 1;
            LogUtils.d("friend msg index = " + index + ", sender = " + friendMessage.sender);
            if (!String.valueOf(index).equals(split[5])) {
                LogUtils.e("friend msg index error!! ");
                requestNextVoiceMsg();
                return;
            } else {
                friendMessage.setIndex(index);
            }

            if (split[4].equals(split[5])) {
                friendMessage.amrData_friend.append(tempData);
//                LogUtils.d("BP96 receive amrData = " + friendMessage.amrData_friend.toString());
                LogUtils.d("BP96 receive amrData length = " + friendMessage.amrData_friend.toString().length());
                MsgSender.sendTxtMsg(MsgType.IWAP96, split[1] + "," + split[2] + "," + split[3] + "," + split[4] + "," + split[5] + ",1");
                saveFriendRecordingFile(split, friendMessage);
            } else {
                friendMessage.amrData_friend.append(tempData);
                //LogUtils.d("BP96 amrData_friendChat = " + amrData_friendChat.toString());
                LogUtils.d("BP96 amrData_friendChat length = " + friendMessage.amrData_friend.toString().length());
                MsgSender.sendTxtMsg(MsgType.IWAP96, split[1] + "," + split[2] + "," + split[3] + "," + split[4] + "," + split[5] + ",1");
            }
        } else if (OrderConstans.AP95.equals(action)) {
            amrData_send = intent.getStringArrayExtra("amr_data");
            String sendTime = TimeUtils.millis2String(System.currentTimeMillis(), new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()));
            String target = intent.getStringExtra("target");
            LogUtils.d( "target"+ target + ", send_time = " + sendTime + ", amrData_send.lenth:" + amrData_send.length);
            // 发第一条
            MsgSender.sendTxtMsg(MsgType.IWAP95, target + "," + sendTime + ","
                    + amrData_send.length + "," + 1 + "," + amrData_send[0].length() + "," + amrData_send[0]);
        } else if (OrderConstans.BP95.equals(action)) {// content:868872039000024,20191118151431,2,1,1
            String content = intent.getStringExtra("BP95_content");
            String[] split = content.split(",");
            if (split.length != 5) { // 错误指令直接返回
                LogUtils.e("BP95 wrong content:" + content);
                return;
            }
            int index = Integer.valueOf(split[3]);
            if ("1".equals(split[4])) { // 接收成功，发下一条
                if (!split[2].equals(split[3])) {
                    MsgSender.sendTxtMsg(MsgType.IWAP95, split[0] + "," + split[1] + ","
                            + split[2] + "," + (index + 1) + "," + amrData_send[index].length() + "," + amrData_send[index]);
                }
            } else { // 发送失败，重发
                LogUtils.e("BP95 receive failed:" + split[4]);
                MsgSender.sendTxtMsg(MsgType.IWAP95, split[0] + "," + split[1] + ","
                        + split[2] + "," + (index) + "," + amrData_send[index-1].length() + "," + amrData_send[index-1]);
            }
        }
    }

    public void saveRecordingFile(String[] split, VoiceMessages voiceMessage) {
        File dir = new File(Environment.getExternalStorageDirectory(), "chat");
        if (!dir.exists()) {
            boolean result = dir.mkdir();
            if (!result) {
                LogUtils.e("创建文件夹chat失败！");
            }
        }
        String filename = null;
        if (TextUtils.equals(split[0], MsgType.IWBPCD) || TextUtils.equals(split[0], MsgType.IWBP96)) {
            filename = split[1] + "_" + split[3] + "_" + System.currentTimeMillis();
        }  else if (TextUtils.equals(split[0], MsgType.IWBP28)) {
            filename = split[1] + "_" + split[2]+ "_G_" + System.currentTimeMillis();
        }
        FileOutputStream fos = null;
        try {
            Intent msgIntent = new Intent(ReceiverConstant.ACTION_VOICE_MSG);
            File file = new File(dir, filename + ".amr");
            fos = new FileOutputStream(file);
            LogUtils.e("full amrData length = " + voiceMessage.amrData.length);
            StringBuilder sb = new StringBuilder(voiceMessage.amrData.length *2);
            for (String str : voiceMessage.amrData) {
                sb.append(str);
            }
//            LogUtils.e("full amrData Hex = " + sb.toString());
            LogUtils.e("full amrData Hex length = " + sb.toString().length());
            byte[] bs = hexStr2Bytes(sb.toString());
//            LogUtils.e("full amrData byte  = " + Arrays.toString(bs));
            fos.write(bs);
            if (TextUtils.equals(split[0], MsgType.IWBPCD)) {// IWBPCD,214969,54E554E5,122339,1,2,2,518,0�
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_PATH, file.getAbsolutePath());
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_TYPE, 1); // 单聊
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_TARGET, split[1]);
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_SEND, split[2]);
//                MsgSender.sendTxtMsg(MsgType.IWAPCD, split[1] + "," + split[3] + "," + split[4] + "," + split[5] + "," + split[6] + ",1");
            } else if (TextUtils.equals(split[0], MsgType.IWBP28)) {//IWBP28,T�T�,000001,2,2,166
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_PATH, file.getAbsolutePath());
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_TYPE, 0); // 群聊
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_SEND, split[1]);
//                MsgSender.sendTxtMsg(MsgType.IWAP28, split[1] + "," + split[2] + "," + split[3] + "," + split[4] + ",1");
            }
            voiceMessage.amrData = null;
            voiceMessage.setIndex(0);
            Utils.getContext().sendBroadcast(msgIntent);
            requestNextVoiceMsg();
        } catch (Exception e) {
            voiceMessage.amrData = null;
            voiceMessage.setIndex(0);
            requestNextVoiceMsg();
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();//关闭流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveFriendRecordingFile(String[] split, VoiceMessages voiceMessage) {
        File dir = new File(Environment.getExternalStorageDirectory(), "chat");
        if (!dir.exists()) {
            boolean result = dir.mkdir();
            if (!result) {
                LogUtils.e("创建文件夹chat失败！");
            }
        }
        String filename = split[1] + "_" + split[3] + "_" + System.currentTimeMillis();
        FileOutputStream fos = null;
        try {
            Intent msgIntent = new Intent(ReceiverConstant.ACTION_VOICE_MSG);
            File file = new File(dir, filename + ".amr");
            fos = new FileOutputStream(file);
            LogUtils.e("full amrData length = " + voiceMessage.amrData_friend.length());
//            LogUtils.e("full amrData Hex = " + voiceMessage.amrData_friend);
            byte[] bs = hexStr2Bytes(voiceMessage.amrData_friend.toString());
//            LogUtils.e("full amrData byte  = " + Arrays.toString(bs));
            fos.write(bs);
            if (TextUtils.equals(split[0], MsgType.IWBP96)) {
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_PATH, file.getAbsolutePath());
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_TYPE, 1); // 单聊
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_TARGET, split[1]);
                msgIntent.putExtra(ReceiverConstant.EXTRA_VOICE_SEND, split[2]);
            }
            LogUtils.d("path = " + file.getAbsolutePath());
            voiceMessage.amrData_friend.delete(0, voiceMessage.amrData_friend.length());
            voiceMessage.setIndex(0);
            Utils.getContext().sendBroadcast(msgIntent);
            requestNextVoiceMsg();
        } catch (Exception e) {
            voiceMessage.amrData_friend.delete(0, voiceMessage.amrData_friend.length());
            voiceMessage.setIndex(0);
            requestNextVoiceMsg();
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();//关闭流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 请求下一条信息是加5ms延时
     */
    private void requestNextVoiceMsg() {
        RECEIVING = false;
        OrderUtil.getInstance().voiceQueryReceive("1");// 1 下载语音
    }

    private VoiceMessages getVoiceMsgBySender (String Sender) {
        for (VoiceMessages voiceMessages : mVoiceMsgs) {
            if (voiceMessages.getSender().equals(Sender)) {
                return voiceMessages;
            }
        }
        return null;
    }

    private static class VoiceMessages {
        String[] amrData = null;
        StringBuilder amrData_friend = new StringBuilder();

        int index = 0;
        String sender = null;

        VoiceMessages(String sender) {
            this.sender = sender;
        }

        void setIndex(int index) {
            this.index = index;
        }

        int getIndex() {
            return this.index;
        }

        String getSender() {
            return sender;
        }
    }
}
