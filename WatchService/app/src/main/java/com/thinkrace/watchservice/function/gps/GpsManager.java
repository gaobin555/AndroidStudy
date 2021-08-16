package com.thinkrace.watchservice.function.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;

import com.thinkrace.watchservice.KApplication;
import com.thinkrace.watchservice.orderlibrary.utils.LogUtils;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;

import java.util.Iterator;


/**
 * 类描述：GPS定位的管理类
 */
public class GpsManager {
    private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;
    private static GpsManager sGpsManager;
    private static String mLocateType = LocationManager.GPS_PROVIDER;//定位类型：GPS
    private LocationManager locationManager;
    private long mMinTime = 1000;//默认定位时间间隔为1000ms
    private float mMinDistance = 0;//默认位置可更新的最短距离为0m
    private GpsRespondListener mGpsLocationListener;

    private GpsManager() {
        initData();
    }

    private void initData() {
        locationManager = (LocationManager) (KApplication.sContext.getSystemService(Context.LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addGpsStatusListener(mGpsStatusListener);//必须主线程初始化

        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        locationManager.requestLocationUpdates(mLocateType, mMinTime, mMinDistance, locationListener);
    }

    public static GpsManager instance() {
        if (sGpsManager == null) {
            sGpsManager = new GpsManager();
        }
        return sGpsManager;
    }

    /**
     * 设置发起定位请求的间隔时长
     *
     * @param minTime 定位间隔时长（单位ms）
     */
    public void setScanSpan(long minTime) {
        this.mMinTime = minTime;
    }

    /**
     * 设置位置更新的最短距离
     *
     * @param minDistance 最短距离（单位m）
     */
    public void setMinDistance(float minDistance) {
        this.mMinDistance = minDistance;
    }

    /**
     * 设置最晚等待时间
     *
     * @param maxWaitingTimeMillis 定位最大等待时长
     */
    public void setMaxWaitingTimeMillis(long maxWaitingTimeMillis) {
        sMaxWaitingTimeMillis = maxWaitingTimeMillis;
    }

    /**
     * 请求GPS定位信息
     *
     * @param gpsLocationListener q
     */
    public void requestLocation(final GpsRespondListener gpsLocationListener) {
        if (null == mGpsLocationListener) {
            this.mGpsLocationListener = gpsLocationListener;
        }
        boolean isGpsEnabled = locationManager.isProviderEnabled(GPS_LOCATION_NAME);
        LogUtils.d("requestLocation isGpsEnabled " + isGpsEnabled);
        // 为获取地理位置信息时设置查询条件
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        if (ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);// 获取位置信息
        locationListener.onLocationChanged(lastKnownLocation);
        //        locationManager.removeUpdates(locationListener);//先取消上次定位结果
        mTimeOutTimer.cancel();
        mTimeOutTimer.start();
    }

    private int getSatelliteNum(GpsStatus gpsStatus) {
        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> it = gpsStatus.getSatellites().iterator();
        int satellitesCount = 0;
        while (it.hasNext() && satellitesCount <= maxSatellites) {
            if (it.next().getSnr() != 0)//只有信躁比不为0的时候才算搜到了星
            {
                satellitesCount++;
            }
        }
        return satellitesCount;
    }

    /**
     * 方法描述：终止GPS定位,该方法最好在onPause()中调用
     */
    public void stop() {
        stopTimer();
        locationManager.removeUpdates(locationListener);
        if (mGpsStatusListener != null) {
            locationManager.removeGpsStatusListener(mGpsStatusListener);
        }
    }

    private int mSatellite = 0;//当前卫星个数
    private GpsLocationInfo mLocationInfo;
    private GpsErrorType mGpsErrorType;//失败类型
    private static long sMaxWaitingTimeMillis = 30 * 1000;
    private int mLocationInfoLogCount = 0;
    private static final int PICK_NUM = 8;
    private final CountDownTimer mTimeOutTimer = new CountDownTimer(sMaxWaitingTimeMillis, sMaxWaitingTimeMillis) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (null != mGpsLocationListener) {
                if (mLocationInfo == null) {
                    mGpsLocationListener.toLocateFailure(GpsErrorType.LOCATE_TIMEOUT);
                } else {
                    mGpsLocationListener.onLocateSuccess(mLocationInfo);
                }
                stop();
            }
        }
    };

    private void stopTimer() {
        if (mTimeOutTimer != null) {
            mTimeOutTimer.cancel();
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            boolean isSuccess = false;
            LogUtils.e("onLocationChanged " + (null == location ? "" : location.toString()));
            GpsErrorType errorType = GpsErrorType.LOCATE_ERROR;
            GpsLocationInfo mTempLocationInfo = null;
            if (location != null) {
                errorType = GpsErrorType.LOCATE_TEMPORARILY_UNAVAILABLE;
                if (mLocationInfo == null) {
                    mLocationInfoLogCount = 0;
                    mLocationInfo = new GpsLocationInfo(location.getLongitude(), location.getLatitude(),
                            location.getAccuracy(), location.getSpeed(), location.getAltitude(), location.getBearing(), mSatellite);
                } else {
                    if (mTempLocationInfo == null) {
                        mTempLocationInfo = new GpsLocationInfo(location.getLongitude(), location.getLatitude(),
                                location.getAccuracy(), location.getSpeed(), location.getAltitude(), location.getBearing(), mSatellite);
                    } else {
                        mTempLocationInfo.setLocationInfo(location.getLongitude(), location.getLatitude(),
                                location.getAccuracy(), location.getSpeed(), location.getAltitude(), location.getBearing(), mSatellite);
                    }
                    if (mLocationInfo.getAccuracy() < mTempLocationInfo.getAccuracy()) {
                        mLocationInfo = mTempLocationInfo;
                    }
                }
                mLocationInfoLogCount++;
                if (mLocationInfoLogCount >= PICK_NUM) {
                    isSuccess = true;
                }
            }
            if (isSuccess) {
                mGpsLocationListener.onLocateSuccess(mLocationInfo);
                stop();
            } else {
                mGpsLocationListener.toLocateFailure(errorType);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            GpsErrorType mGPSStatus = null;
            switch (status) {
                case LocationProvider.AVAILABLE:
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    mGPSStatus = GpsErrorType.LOCATE_OUT_OF_SERVICE;
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    mGPSStatus = GpsErrorType.LOCATE_TEMPORARILY_UNAVAILABLE;
                    break;
                default:
                    break;
            }

            //TODO
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            mGpsErrorType = GpsErrorType.LOCATE_DISABLED;
            mGpsLocationListener.toLocateFailure(mGpsErrorType);
            stop();
        }
    };

    private GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            LogUtils.i("onGpsStatusChanged event " + event);
            //获取当前状态
            if (ActivityCompat.checkSelfPermission(Utils.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            //获取卫星颗数的默认最大值
            int maxSatellites = gpsStatus.getMaxSatellites();
            //创建一个迭代器保存所有卫星
            Iterator<GpsSatellite> it = gpsStatus.getSatellites().iterator();
            int satellitesCount = 0;
            while (it.hasNext() && satellitesCount <= maxSatellites) {
                if (it.next().getSnr() != 0) {//只有信躁比不为0的时候才算搜到了星
                    satellitesCount++;
                }
            }
            mSatellite = satellitesCount;
            LogUtils.e("搜索到：" + satellitesCount + "颗卫星");
        }
    };

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
