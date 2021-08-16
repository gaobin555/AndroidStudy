package com.thinkrace.watchservice.orderlibrary.utils;
/*
 *  @项目名：  WatchService
 *  @包名：    com.thinkrace.watchservice.utils
 *  @文件名:   GSMCellLocationUtils
 *  @创建者:   win10
 *  @创建时间:  2018/4/20 16:14
 *  @描述：    TODO
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * 功能描述：通过手机信号获取基站信息
 * # 通过TelephonyManager 获取lac:mcc:mnc:cell-id
 * # MCC，Mobile Country Code，移动国家代码（中国的为460）；
 * # MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
 * # LAC，Location Area Code，位置区域码；
 * # CID，Cell Identity，基站编号；
 * # BSSS，Base station signal strength，基站信号强度。
 * @author android_ls
 */

public class GSMCellLocationUtils {

    private static final String TAG = "GSMCellLocation";
    private static final String[] MCCMNC_TABLE_TYPE_CT = {"45502", "46003", "46011", "46012", "46013"};
    public static String getParseBaseStation(Context context) {
        // 获取基站信息
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // 返回值MCC + MNC
        String operator;
        String mcc;
        String mnc;
        int type;
        int lac = 9547;  //获取gsm网络编号  9547 深圳南山区基站信息
        int cellId = 111778818;  //获取gsm基站识别标号

        try {
            operator = mTelephonyManager.getNetworkOperator();
            type = mTelephonyManager.getNetworkType();
            mcc = operator.substring(0, 3);
            mnc = operator.substring(3,5);

            // 中国移动和中国联通获取LAC、CID的方式
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return "";
            }

            if (isCTCard(type)) {
                // 中国电信获取LAC、CID的方式
                CdmaCellLocation cdma = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                if (cdma != null) {
                    lac = cdma.getNetworkId();
                    mnc = String.valueOf(cdma.getSystemId());
                    cellId = cdma.getBaseStationId();
//                    cellId /= 16;
                    Log.d(TAG, "isCTCard, type =" + type);
                }
            } else {
                GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
                if (location != null) {
                    lac = location.getLac();  //获取gsm网络编号
                    cellId = location.getCid();  //获取gsm基站识别标号
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
            LogUtils.i(TAG, " 默认中国联通" );
            mcc = "460";
            mnc = "01";
        }

        LogUtils.i("mare " + " MCC移动国家代码 = " + mcc + "\t MNC移动网络号码 = "
                + mnc + "\t LAC位置区域码 = " + lac + "\t CID基站编号 = " + cellId);

        // 获取邻区基站信息
        List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();
        if (infos != null) {
            StringBuilder sb = new StringBuilder("总数 : " + infos.size() + "\n");
            for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
                sb.append(" LAC : ").append(info1.getLac()); // 取出当前邻区的LAC
                sb.append(" CID : ").append(info1.getCid()); // 取出当前邻区的CID
                sb.append(" BSSS : ").append(-113 + 2 * info1.getRssi()).append("\n"); // 获取邻区基站信号强度
            }
            LogUtils.i(TAG, " 获取邻区基站信息:" + sb.toString());
        }

        return mcc + "," + Integer.valueOf(mnc) + "," + lac + "," + cellId;
    }


    /**
     * 获取手机信号强度，需添加权限 android.permission.ACCESS_COARSE_LOCATION <br>
     * API要求不低于17 <br>
     *
     * @return 当前手机主卡信号强度, 单位 dBm（-1是默认值，表示获取失败）
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getMobileDbm(Context context) {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return "";
            }
            cellInfoList = tm.getAllCellInfo();
            if (null != cellInfoList) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma =
                                ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma =
                                    ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();
                        }
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                    }
                }
            }
        }
        String tempDbm = "";
        dbm = Math.abs(dbm);
        if(dbm < 10) {
            tempDbm = "00"+dbm;
        } else if(dbm < 100) {
            tempDbm = "0" + dbm;
        } else {
            tempDbm = String.valueOf(dbm);
        }
        LogUtils.d("tempDbm=" + tempDbm);
        return tempDbm;
    }

    private static boolean isCTCard(int type) {
        return type == TelephonyManager.NETWORK_TYPE_CDMA        // 电信cdma网
                || type == TelephonyManager.NETWORK_TYPE_1xRTT
                || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                || type == TelephonyManager.NETWORK_TYPE_EVDO_A;
    }
}
