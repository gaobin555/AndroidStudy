package com.thinkrace.watchservice.orderlibrary.utils;
/*
 *  @项目名：  WatchService
 *  @包名：    com.thinkrace.watchservice.orderlibrary.utils
 *  @文件名:   UploadFileUtils
 *  @创建者:   win10
 *  @创建时间:  2018/5/26 11:36
 *  @描述：    TODO
 */

import android.content.Intent;
import android.os.SystemClock;

import com.thinkrace.watchservice.ReceiverConstant;
import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.xuhao.android.common.constant.OrderConstans;
import com.xuhao.android.common.utils.NetUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class VoiceFileUtils {

    private static final String key = "8DF33147C5554F70AA9B0E41933631D7";
    /**
     * IMEI
     * Long 录音时长
     * Type 群聊或单聊 1 为群聊 2 为单聊
     * Filetype  文件类型： 0 图片, 1 聊天语音, 5 视频
     * Target 单聊目标，群聊不填
     */
    private static final String uploadURL = "http://api.kingrow.net/api/uploadFile";
    private static final String voiceListURL = "http://api.kingrow.net/api/Files/VoiceFileListByTimeForDevice";
    private static final String readFileURL = "http://api.kingrow.net/api/Files/UpdateFileForReadForDevice";

    /**
     * /**
     * 语音上传
     *
     * @param voiceFilePath 语音文件绝对路径
     * @param aLong         录音时长
     * @param type          1 为群聊 2 为单聊
     * @param fileType      0 图片, 1 聊天语音, 5 视频
     */
    public static void upLoadVoiceFile(String voiceFilePath, String aLong, String type, String fileType,String identity) {
        if (NetUtils.netIsAvailable(Utils.getContext())) {
            File file = new File(voiceFilePath);
            LogUtils.e("语音文件路径====" + voiceFilePath + ", 文件大小：" + aLong);
            if (!file.exists()) {
                LogUtils.e("语音文件不存在，请修改文件路径");
                return;
            }
            String filename = file.getName();
            Map<String, String> params = new HashMap<>();
            params.put("IMEI", GlobalSettings.instance().getImei());
            params.put("Long", aLong);
            params.put("Type", type);
            params.put("Filetype", fileType);
            params.put("Identity", identity);
            params.put("Target", identity);
            OkHttpUtils.post()
                    .url(uploadURL)
                    .addFile("File", filename, file)
                    .params(params)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {

                            LogUtils.e("语音文件上传失败");
                            Intent uploadIntent = new Intent(ReceiverConstant.ACTION_VOICE_UPLOAD);
                            uploadIntent.putExtra(ReceiverConstant.EXTRA_UPLOAD, String.valueOf(0));
                            Utils.getContext().sendBroadcast(uploadIntent);
                        }

                        @Override
                        public void onResponse(String s, int i) {

                            LogUtils.e("语音文件上传成功");
                            Intent uploadIntent = new Intent(ReceiverConstant.ACTION_VOICE_UPLOAD);
                            uploadIntent.putExtra(ReceiverConstant.EXTRA_UPLOAD, String.valueOf(1));
                            Utils.getContext().sendBroadcast(uploadIntent);
                        }
                    });
        }
    }

    /**
     * 好友发语音
     * IWAP95,353456789012345,20140818064408,6,1,1024,XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX#
     * IWAP95:命令字符
     * 353456789012345: 好友设备ID
     * 20140818064408 :yyyyMMddHHmmss 格式的年月日时分秒，设备录音时间
     * 6：语音包分包总数
     * 1 ：当前包数，第一包为1，第二包为2，。。。。。。同一个语音包的语音时间相同
     * 1024 :语音包长度
     * XXXXXXXXX：当前音频数据, 每个音频数据包大小为1024字节，最后一包不足1024的取实际字节数
     * 语音包上传规则：
     * 按包次序依次上传，上传后如果没有收到服务器的响应包，则继续上传该语音包。
     * 收到设备回复确认数据包接收成功状态后，继续开始依次上传下一个数据包。。。。。。
     * 如果设备回复确认数据包接收失败状态，则重复发送上一个数据包
     * @param voiceFilePath 语音文件绝对路径
     * @param aLong         录音时长
     * @param target      好友设备ID
     */
    public static void sendVoiceToFriend(String voiceFilePath, String aLong, String target) {
        byte[] bVoice = FileIOUtils.readFile2BytesByStream(voiceFilePath);
        String voiceString = DigitalConvert.byte2HexStrNoSpace(bVoice);
        LogUtils.d( "voiceString len = " + voiceString.length() + ", " + voiceString);

        String[] vStrings = StringUtils.stringSpilt(voiceString,1024);

        Intent intent = new Intent(OrderConstans.AP95);
        intent.putExtra("amr_data", vStrings);
        intent.putExtra("voice_len", aLong);
        intent.putExtra("target", target);

        Utils.getContext().sendBroadcast(intent);

//        for (String s : vStrings) {
//            LogUtils.d( "voiceString split len " + s.length() + ", " + s);
//        }
    }



    /**
     * 图片上传
     *
     * @param picFilePath 图片文件绝对路径
     * @param type        1 为群聊 2 为单聊
     * @param fileType    0 图片, 1 聊天语音, 5 视频
     */
    public static void upLoadPicFile(String picFilePath, String type, String fileType) {
        if (NetUtils.netIsAvailable(Utils.getContext())) {
            LogUtils.e("图片文件路径====" + picFilePath);
            File file = new File(picFilePath);
            if (!file.exists()) {
                LogUtils.e("图片文件不存在，请修改文件路径");
                return;
            }
            String filename = file.getName();
            Map<String, String> params = new HashMap<>();
            params.put("IMEI", GlobalSettings.instance().getImei());
            params.put("Type", type);
            params.put("Filetype", fileType);
            OkHttpUtils.post()
                    .url(uploadURL)
                    .addFile("File", filename, file)
                    .params(params)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {

                            LogUtils.e("图片文件上传失败");
                            Intent uploadIntent = new Intent(ReceiverConstant.ACTION_PIC_UPLOAD);
                            uploadIntent.putExtra(ReceiverConstant.EXTRA_UPLOAD, String.valueOf(0));
                            Utils.getContext().sendBroadcast(uploadIntent);
                        }

                        @Override
                        public void onResponse(String s, int i) {

                            LogUtils.e("图片文件上传成功");
                            Intent uploadIntent = new Intent(ReceiverConstant.ACTION_PIC_UPLOAD);
                            uploadIntent.putExtra(ReceiverConstant.EXTRA_UPLOAD, String.valueOf(1));
                            Utils.getContext().sendBroadcast(uploadIntent);
                        }
                    });
        }
    }

    /**
     * 获取语音文件列表
     */
    public static void getVoiceList(String pageNo,String pageCount) {
        long customerTime = SystemClock.currentThreadTimeMillis();
        String str = key + "thinkrace" + customerTime;
        String md5 = "";
        try {
            md5 = Md5.getMD5(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Map<String, String> header = new HashMap<>();
        header.put("AuthKey", md5);
        header.put("AuthTime", String.valueOf(customerTime));
        Map<String, String> params = new HashMap<>();
        params.put("Imei", GlobalSettings.instance().getImei());
        params.put("pageNo", pageNo);
        params.put("pageCount", pageCount);
        OkHttpUtils.post().url(voiceListURL).headers(header).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                LogUtils.e("获取语音文件列表失败====");
                Intent voiceListJsonIntent = new Intent(ReceiverConstant.ACTION_VOICE_LIST);
                voiceListJsonIntent.putExtra(ReceiverConstant.EXTRA_VOICE_LIST, "");
                Utils.getContext().sendBroadcast(voiceListJsonIntent);
            }

            @Override
            public void onResponse(String s, int i) {
                LogUtils.i("获取语音文件列表成功====" + s);
                Intent voiceListJsonIntent = new Intent(ReceiverConstant.ACTION_VOICE_LIST);
                voiceListJsonIntent.putExtra(ReceiverConstant.EXTRA_VOICE_LIST, s);
                Utils.getContext().sendBroadcast(voiceListJsonIntent);
            }
        });
    }

    /**
     * 语音已读标记
     * @param fileId 语音fileId
     */
    public static void updateReadFile(String fileId) {
        Map<String, String> params = new HashMap<>();
        params.put("FileId", fileId);
        OkHttpUtils.post().url(readFileURL).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                LogUtils.e("语音已读标记失败!");
            }

            @Override
            public void onResponse(String s, int i) {
                LogUtils.i("语音已读标记成功!" + s);
            }
        });
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String readFileToString(String path) {
        StringBuilder voice = new StringBuilder();
        File f1 = isSpace(path) ? null : new File(path);
        if (f1 == null) {
            return null;
        }
        FileInputStream fis = null;
        LogUtils.d("读取文件大小 = " + f1.length());
        try {
            fis = new FileInputStream(f1);
            byte[] bytes= new byte[1024];
            //得到实际读取的长度  
            int n = 0;
            //循环读取  
            while((n = fis.read(bytes)) != -1){
                LogUtils.d("读取文件流长度 = " + n);
                for (byte b : bytes)
                voice.append(b);
            }
        }catch(Exception e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }finally{
            //最后一定要关闭文件流  
            try {
                if (fis != null)
                    fis.close();
            } catch(IOException e){
                // TODO Auto-generated catch block  
                e.printStackTrace();
            }
        }
        return voice.toString().trim();
    }

}
