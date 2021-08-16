package com.android.xthink.ink.launcherink.manager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.xthink.ink.launcherink.common.utils.FileUtils;
import com.android.xthink.ink.launcherink.common.utils.MyLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liyuyan on 2016/12/23.
 */

public class InkAppManager {

    private static String systemRootDir = "launcherink";
    private static String sdcardDir = "";

    private final static String SYSTEMIMAGEDIR = "image";
    private final static String SYSTEMFILEDIR = "file";
    private final static String SYSTEMCACHEDIR = "cache";
    private final static String SYSTEMLOGDIR = "log";
    private final static String SYSTEMAPKDIR = "apk";


    /**
     * the root path
     */
    public static String getSystemRootDir() {
        getSdcardDir();
        String fileDir = sdcardDir + File.separator + systemRootDir + File.separator;
        createDir(fileDir);
        return fileDir;
    }

    /**
     * sdcard directory
     */
    public static String getSdcardDir() {
        if (TextUtils.isEmpty(sdcardDir)) {
            sdcardDir = getExternalSdCardPath();
        }

        return sdcardDir;
    }

    /**
     * file directory
     */
    public static String getSystemfiledir() {
        String fileDir = getSystemRootDir() + SYSTEMFILEDIR + File.separator;
        createDir(fileDir);
        return fileDir;
    }

    /**
     * cache directory
     */
    public static String getSystemcachedir() {
        String fileDir = getSystemRootDir() + SYSTEMCACHEDIR + File.separator;
        createDir(fileDir);
        return fileDir;
    }

    /**
     * image directory
     */
    public static String getSystemimagedir() {
        String imageDir = getSystemRootDir() + SYSTEMIMAGEDIR + File.separator;
        createDir(imageDir);
        return imageDir;
    }

    /**
     * log directory
     */
    public static String getSystemlogdir() {
        String logDir = getSystemRootDir() + SYSTEMLOGDIR + File.separator;
        createDir(logDir);
        return logDir;
    }

    /**
     * apk directory
     */

    public static String getSystemApkDir() {
        String logDir = getSystemRootDir() + SYSTEMAPKDIR + File.separator;
        createDir(logDir);
        return logDir;
    }

    public static void createDir(String fileDir) {
        File localFile = new File(fileDir);

        if (!localFile.exists()) {
            localFile.mkdirs();
//            if () {

//            }
        }

    }

    /**
     * 获取SD卡路径
     *
     * @return 如果sd卡不存在则返回null
     */
    public static File getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir;
    }

    /**
     * 获取扩展SD卡存储目录
     * <p>
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     *
     * @return
     */
    public static String getExternalSdCardPath() {

        if (isSDCardEnable()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            long megAvailable = bytesAvailable / 1048576;
            if (megAvailable > 0.5) {
                File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                String path = "";
                if (sdCardFile.isDirectory() && sdCardFile.canWrite()) {
                    path = sdCardFile.getAbsolutePath();

                    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.CHINA).format(new Date());
                    File testWritable = new File(sdCardFile, "test_" + timeStamp);

                    if (testWritable.mkdirs()) {
                        testWritable.delete();
                    } else {
                        path = "/mnt/sdcard";
                    }
                }
                return path;
            }

        }

        String sdCardFile1 = getNewSdcard();
        if (sdCardFile1 != null) return sdCardFile1;

        return "/mnt/sdcard";
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @Nullable
    private static String getNewSdcard() {
        String path = null;

        File sdCardFile = null;

        ArrayList<String> devMountList = getDevMountList();

        for (String devMount : devMountList) {
            File file = new File(devMount);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.CHINA).format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
            return sdCardFile.getAbsolutePath();
        }
        return null;
    }

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     *
     * @return
     */
    private static ArrayList<String> getDevMountList() {
        String[] toSearch = FileUtils.readFileByLines("/etc/vold.fstab").split(" ");
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    /**
     * 获取版本名称
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            MyLog.e("VersionInfo", "Exception" + e.getMessage());
        }
        return versionName;
    }

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = -1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            MyLog.e("VersionInfo", "Exception" + e.getMessage());
        }
        return versioncode;
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }
}
