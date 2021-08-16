package com.thinkrace.watchservice.function.location;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.thinkrace.watchservice.KApplication;
import com.thinkrace.watchservice.model.FlagFormat;
import com.thinkrace.watchservice.model.location.SmartLocationBean;
import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.orderlibrary.utils.BatteryUtils;
import com.thinkrace.watchservice.orderlibrary.utils.GSMCellLocationUtils;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;
import com.thinkrace.watchservice.orderlibrary.utils.TimeUtils;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;

/**
 * @author mare
 * @Description:TODO 高德地图功能
 * @csdnblog http://blog.csdn.net/mare_blue
 * @date 2017/10/16
 * @time 19:35
 */
public class AMapLocationManager implements AMapLocationListener {

    private boolean isLocationRunning = false;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocationListener locationListener;

    private final static String LOCATION_WAKEUP_TAG = "location_wakeup";
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private AMapLocationManager() {
    }

    public static AMapLocationManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public void initLocationSDK() {
    }

    public void start() {
        LogUtils.d("开始定位了....");
        if (!isLocationRunning) {
            if (null == locationClient) {
                locationClient = new AMapLocationClient(KApplication.sContext);
            }
            mPowerManager = (PowerManager) KApplication.sContext.getSystemService(Context.POWER_SERVICE);
            //keep running when device sleep
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCATION_WAKEUP_TAG);
            mWakeLock.acquire();
            startLocation();
        }
        isLocationRunning = true;
    }

    private void startLocation() {
        initOption();//获取默认定位参数
        locationClient.setLocationOption(locationOption);// 设置定位参数
        locationClient.setLocationListener(this);
        locationClient.startLocation(); // 启动定位
    }

    private void initOption() {
        if (locationOption == null) {
            locationOption = new AMapLocationClientOption();
        }
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        locationOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        locationOption.setHttpTimeOut(20000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        locationOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        locationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        locationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        locationOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        locationOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        locationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        locationOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
    }

    public void stop() {
        // 停止定位
        if (null != locationClient) {
            locationClient.stopLocation();
        }
        mWakeLock.release();
        isLocationRunning = false;
        LogUtils.d("定位停止了~~~~~~~");
    }

    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null != location) {
            LogUtils.i(AMapLog.getLocationStr(location));
            int errorCode = location.getErrorCode();
            LogUtils.i("onLocationChanged errorCode" + errorCode);
            if (errorCode == 0) {
                final long time = TimeUtils.getUTCTimeCalendar();
                LogUtils.d("time " + time);
                int locType = location.getLocationType();
                String provider = location.getProvider();
                //                String description = location.getLocTypeDescription();
                final double latitude = location.getLatitude();// 纬度
                final double lontitude = location.getLongitude();
                LogUtils.d("原始坐标  la-lon : " + latitude + " - " + lontitude);
                final float accuracy = location.getAccuracy();//获取定位精准度
                //                String countryCode = location.getCountryCode();
                String country = location.getCountry();// 国家名称
                final String province = location.getProvince();// 省
                String citycode = location.getCityCode();// 城市编码
                final String city = location.getCity();// 城市
                final String district = location.getDistrict();// 区县信息
                final String adCode = location.getAdCode();// 区域 码
                //                String streetNum = location.getStreetNumber();// 获取街道码
                final String street = location.getStreet();// 街道
                final String addr = location.getAddress();// 地址信息
                final float speed = location.getSpeed(); //速度
                LogUtils.i("addr   ", addr);
                String provinceCityDistrict = province + city + district;
                LogUtils.i("provinceCityDistrict " + provinceCityDistrict);
                final StringBuffer formatStreet = new StringBuffer();
                if (!TextUtils.isEmpty(addr) && addr.length() > provinceCityDistrict.length()) {
                    String subAddr = addr.substring(provinceCityDistrict.length());
                    formatStreet.append(subAddr);
                } else {
                    formatStreet.append(street);
                }
                LogUtils.i("formatAddress " + formatStreet.toString());
                //                int userIndoorState = location.getUserIndoorState();// *****(1：室内，0：室外，这个判断不一定是100%准确的)*****
                //                LogUtils.i("userIndoorState " + userIndoorState);
                float direction = location.getBearing();// 方向
                String locationDetail = location.getLocationDetail();//获取当前位置描述信息
                String aoiName = location.getAoiName();//
                String buildingId = location.getBuildingId();//室内精准定位下，获取楼宇ID
                LogUtils.d("locationDetail=" + locationDetail + " , aoiName= " + aoiName +
                        " ,bnuildingName= " + buildingId);
                String floor = location.getFloor();//室内精准定位下，获取当前位置所处的楼层信息
                final String poi = location.getPoiName();
                AMapLocationQualityReport locationQualityReport = location.getLocationQualityReport();
                String beanDate = FlagFormat.utcLong2Server(time)[0];
                String beanTime = FlagFormat.utcLong2Server(time)[1];
                SmartLocationBean bean = new SmartLocationBean(beanDate, beanTime, latitude, lontitude, accuracy,
                        province, city, district, formatStreet.toString());
                final String formatBean = bean.format();
                LogUtils.d(formatBean);
                LogUtils.d("位置信息 ： " + formatBean);
//                String formatBeanUpload = UnicodeUtils.str2UnicodeNo0xu(formatBean).toUpperCase();
                String formatBeanUpload = "";
                LogUtils.d("转换后的位置信息 ： " + formatBeanUpload);



                /*--------------------------------iw协议-----------------------------------*/
                String azimuth = "0";
                if (locType == AMapLocation.LOCATION_TYPE_GPS) {
                    LogUtils.d("GPS定位成功" + "==" + location.getAddress());
                    azimuth = "999";
                } else if (locType == AMapLocation.LOCATION_TYPE_OFFLINE) {
                    LogUtils.d("离线定位成功" + "==" + location.getAddress());
                } else if (locType == AMapLocation.LOCATION_TYPE_WIFI) {
                    LogUtils.d("网络定位成功");
                    azimuth = "888";
                }

                String gsm = GSMCellLocationUtils.getMobileDbm(Utils.getContext());
                String battery = BatteryUtils.getBatteryLevel(Utils.getContext());

                formatBeanUpload = beanDate + "," + beanTime + "," + latitude + "," + lontitude + "," + accuracy + "," + azimuth + "," + speed + ",gcj02," + gsm + "," + battery + "#";
                /*--------------------------------iw协议-----------------------------------*/

                OrderUtil.getInstance().resend(MsgType.IWAPT1 + GlobalSettings.MSG_CONTENT_SEPERATOR + formatBeanUpload);

                if(locationListener != null) {
                    locationListener.location(bean,gsm,battery);
                }
            } else {
                //定位失败
                LogUtils.e("定位失败 errorCode: " + errorCode);
            }

        } else {//高德没定位到
        }
        stop();
    }

    private static class SingletonHolder {
        private static final AMapLocationManager INSTANCE = new AMapLocationManager();
    }

    public interface LocationListener {
        void location(SmartLocationBean bean, String gsm, String battery);
    }

    public void setLocationListener(LocationListener l) {
        locationListener = l;
    }


}
