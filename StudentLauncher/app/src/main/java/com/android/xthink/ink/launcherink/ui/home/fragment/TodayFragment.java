package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.Manifest;
import android.annotation.NonNull;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.receiver.TimeReceiver;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.utils.TextUtils;
import com.android.xthink.ink.launcherink.common.view.TimeView;
import com.android.xthink.ink.launcherink.manager.image.ImageManagerFactory;
import com.android.xthink.ink.launcherink.receiver.NotificationReceiver;
import com.android.xthink.ink.launcherink.receiver.ParentalControlReceiver;
import com.android.xthink.ink.launcherink.service.AutoUpdateService;
import com.android.xthink.ink.launcherink.ui.home.bean.Weathers;
//import com.android.xthink.ink.launcherink.ui.notificationManager.NotificationManagerActivity;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.android.xthink.ink.launcherink.utils.Tool;
import com.android.xthink.ink.launcherink.utils.Utility;
import com.eink.swtcon.SwtconControl;

import java.util.Calendar;

import static com.android.xthink.ink.launcherink.base.mvp.MainBaseActivity.requestPermisson;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.ACTION_BPLN;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.ACTION_REQUEST_WEATHER;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.ACTION_UNREAD_MSG;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.ACTION_UPDATA_CLOCKTYPE;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.ACTION_WEATHER;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.EXTRA_UNREAD;
import static com.android.xthink.ink.launcherink.constants.LauncherConstants.EXTRA_WEATHER;


/**
 * today屏Fragment
 * Created by gaobin on 2018/12/22.
 */

public class TodayFragment extends NativeFragment implements View.OnClickListener{

    private static final String TAG = "TodayFragment";
    private static final String TAG_LIFE = "FragmentLife";

    private TextView tv_tem;
    private TextView tv_location_city;
    private TextView tv_location_weather;
    private TextView tv_weather_detail;
    private TextView fragment_phone;
    private TextView fragment_message;
    private TextView fragment_email;
    private TextView fragment_notice;
    private TextView fragment_alert_content;
    private TextView fragment_notice_num;
    private TextView fragment_call_num;
    private TextView fragment_sms_num;
    private ImageView iv_weather;
    private ImageView iv_bg_today;
    private RelativeLayout rl_weather;
    private TimeView min_depart_time_clock_view;
    private TimeSetChangeReceiver mTimeSetChangeReceiver;
    private TimeReceiver mBroadcastReceiver;
    private IntentFilter mMinuteFilter;
    private boolean mHasRegisterTimeReceiver;
    private boolean mIsShowing = false;
    private boolean mHasFocus;
    private boolean mIsForbiden = false;

    private NotificationReceiver mNotificationReceiver = null;

    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;

    public static TodayFragment newInstance() {
        MyLog.d(TAG_LIFE, "newInstance" + "TodayFragment");
        return new TodayFragment();
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View fragmentContentView;
        fragmentContentView = inflater.inflate(R.layout.fragment_today, container, false);
        rl_weather = (RelativeLayout) fragmentContentView.findViewById(R.id.rl_weather);
        tv_tem = (TextView) fragmentContentView.findViewById(R.id.tv_tem);
        tv_location_city = (TextView) fragmentContentView.findViewById(R.id.tv_location_city);
        tv_location_weather = (TextView) fragmentContentView.findViewById(R.id.tv_location_weather);
        iv_weather = (ImageView) fragmentContentView.findViewById(R.id.iv_weather);
        tv_weather_detail = (TextView) fragmentContentView.findViewById(R.id.tv_weather_detail);

        iv_bg_today = (ImageView) fragmentContentView.findViewById(R.id.iv_bg_today);

        min_depart_time_clock_view = (TimeView) fragmentContentView.findViewById(R.id.min_depart_time_clock_view);

        // add click for four app button.add by gaob@x-thinks.com start +++
        fragment_phone = (TextView) fragmentContentView.findViewById(R.id.fragment_phone);
        fragment_message = (TextView) fragmentContentView.findViewById(R.id.fragment_message);
        fragment_email = (TextView) fragmentContentView.findViewById(R.id.fragment_email);
        fragment_notice = (TextView) fragmentContentView.findViewById(R.id.fragment_notice);
        fragment_alert_content = (TextView) fragmentContentView.findViewById(R.id.fragment_alert_content);
        fragment_phone.setOnClickListener(this);
        fragment_message.setOnClickListener(this);
        fragment_email.setOnClickListener(this);
        fragment_notice.setOnClickListener(this);
        rl_weather.setOnClickListener(this);
        fragmentContentView.invalidate();
        fragment_notice_num = (TextView) fragmentContentView.findViewById(R.id.fragment_notice_num);
        fragment_call_num = (TextView) fragmentContentView.findViewById(R.id.fragment_dailer_num);
        fragment_sms_num = (TextView) fragmentContentView.findViewById(R.id.fragment_message_num);
        // end +++
        return fragmentContentView;
    }

    @Override
    protected void initData() {
        setImageBG();
        getWeatherInfo();
        initTimeSetChangerBroadcast();
    }

    @Override
    public void onClick(View view) {
        PackageManager packageManager = getContext().getPackageManager();
        switch (view.getId()) {
            case R.id.rl_weather:
                if (Tool.hasNetwork(mContext)) {
                    Log.i(TAG, "xxx onClick rl_weather sendBroadcast:" + ACTION_REQUEST_WEATHER);
                    Intent intent = new Intent(ACTION_REQUEST_WEATHER);
                    mContext.sendBroadcast(intent);
                }
                break;

            case R.id.fragment_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
                startActivity(intent);
                break;

            case R.id.fragment_message:
                Intent intentSms = new Intent(Intent.ACTION_MAIN);
                ComponentName comp = new ComponentName("com.xthinks.xchat", "cn.xthinks.chat.app.main.SplashActivity");
                intentSms.setComponent(comp);
                intentSms.addCategory(Intent.CATEGORY_LAUNCHER);
                InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_A2);// 微聊使用A2方式刷新
                startActivity(intentSms);
                break;

            case R.id.fragment_email:
                Intent intentEmail = packageManager.getLaunchIntentForPackage("com.android.browser");
                if (intentEmail != null) {
                    intentEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
                    startActivity(intentEmail);
                } else {
                    MyLog.e(TAG, "Can't find com.android.email");
                }
                break;

            case R.id.fragment_notice:
                //NotificationManagerActivity.start(mContext);
                break;

            default:
                break;
        }
    }

    /**
     * 设置today背景
     */
    private void setImageBG() {
        ImageManagerFactory.getImageManager().loadImage(R.drawable.today_bg, iv_bg_today, mContext);
    }

    private void initTimeSetChangerBroadcast() {
        //时间被改变的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        // xthink add weather for update by gaob@x-thinks.com start +++
        filter.addAction(ACTION_WEATHER);
        filter.addAction(ACTION_UPDATA_CLOCKTYPE);
        filter.addAction(ACTION_UNREAD_MSG);
        filter.addAction(ACTION_BPLN);
        // end +++
        if (mTimeSetChangeReceiver == null) {
            mTimeSetChangeReceiver = new TimeSetChangeReceiver();
        }
        mContext.registerReceiver(mTimeSetChangeReceiver, filter);
    }

    @Override
    protected void setListener() {
        addNotifcationListener();
    }

    private void getWeatherInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String weatherString = prefs.getString(EXTRA_WEATHER, null);
        MyLog.d(TAG, "xxx getWeatherInfo()" + "\n" + "weatherString = " + weatherString);
        if (weatherString != null) {
            // 有缓存数据时直接解析天气数据
            Weathers weathers = Utility.handleWeatherResponse(weatherString);
            setWeatherOnInk(weathers);
        } else if (Tool.hasNetwork(mContext)) {
            Log.i(TAG, "xxx getWeatherInfo sendBroadcast:" + ACTION_REQUEST_WEATHER);
            Intent intent = new Intent(ACTION_REQUEST_WEATHER);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = mContext.getSharedPreferences("control_prefs", Context.MODE_PRIVATE);//added by chenjia
        mEditor = mSp.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(TAG, "onCreateView");
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView != null) {
            mRootView.getViewTreeObserver().addOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
        }
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyLog.i(TAG, "onDestroyView");
        // 注册的时候是在onCreateView。所以销毁的时候需要在onDestroyView
        try {
            stopUpdateTime();
            if (mTimeSetChangeReceiver != null) {
                mContext.unregisterReceiver(mTimeSetChangeReceiver);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "onDestroy " + e);
        }

        if (mRootView != null) {
            mRootView.getViewTreeObserver().removeOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
        }

        if (mNotificationReceiver != null) {
            mNotificationReceiver.unRegisterNotificationReceiver(mContext);
        }
    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
        MyLog.i(TAG, "switchPage: " + isVisibleToUser);
        mIsShowing = isVisibleToUser;
        if (isZh()) {
            if (isVisibleToUser) {
                getWeatherInfo();
            }
        }
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        MyLog.i(TAG, "lazyInit");
        mIsShowing = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i(TAG, "TodayFragment onResume");
        //refreshStepCount(GlobalDataCache.getInstance().getCurrentStepCount());
        startUpdateTime();
        getWeatherInfo();

        updateControlUI();
        updateIconNumbers();
    }

    public void updateControlUI() {
        int reportLossState = mSp.getInt(ParentalControlReceiver.REPORT_LOSS_STATE, 0);
        int reservePowerState = mSp.getInt(ParentalControlReceiver.RESERVE_POWER_STATE, 0);
        int classForbiddenState = mSp.getInt(ParentalControlReceiver.CLASS_FORBIDDEN_STATE, 0);
        if (reportLossState == 1) {
            fragment_message.setVisibility(View.GONE);
            fragment_phone.setVisibility(View.GONE);
            fragment_email.setVisibility(View.GONE);
            fragment_call_num.setVisibility(View.GONE);
            fragment_sms_num.setVisibility(View.GONE);
            fragment_alert_content.setVisibility(View.VISIBLE);
            fragment_alert_content.setText(R.string.alert_message_report_loss);
            mIsForbiden = true;
            return;
        } else if (reportLossState == 0) {
            fragment_message.setVisibility(View.VISIBLE);
            fragment_phone.setVisibility(View.VISIBLE);
            fragment_email.setVisibility(View.VISIBLE);
            mIsForbiden = false;
            fragment_alert_content.setVisibility(View.GONE);
        }

        if (classForbiddenState == 1) {
            fragment_message.setVisibility(View.GONE);
            fragment_phone.setVisibility(View.GONE);
            fragment_email.setVisibility(View.GONE);
            fragment_call_num.setVisibility(View.GONE);
            fragment_sms_num.setVisibility(View.GONE);
            fragment_alert_content.setVisibility(View.VISIBLE);
            fragment_alert_content.setText(R.string.alert_message_class_forbidden);
            mIsForbiden = true;
            return;
        } else if (classForbiddenState == 0) {
            fragment_message.setVisibility(View.VISIBLE);
            fragment_phone.setVisibility(View.VISIBLE);
            fragment_email.setVisibility(View.VISIBLE);
            mIsForbiden = false;
            fragment_alert_content.setVisibility(View.GONE);
        }

        if (reservePowerState == 1) {
            fragment_message.setVisibility(View.GONE);
            fragment_phone.setVisibility(View.GONE);
            fragment_email.setVisibility(View.GONE);
            fragment_call_num.setVisibility(View.GONE);
            fragment_sms_num.setVisibility(View.GONE);
            fragment_alert_content.setVisibility(View.VISIBLE);
            fragment_alert_content.setText(R.string.alert_message_reserver_power);
            mIsForbiden = true;
        } else if (reservePowerState == 0) {
            fragment_message.setVisibility(View.VISIBLE);
            fragment_phone.setVisibility(View.VISIBLE);
            fragment_email.setVisibility(View.VISIBLE);
            mIsForbiden = false;
            fragment_alert_content.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i(TAG, "TodayFragment onPause");
        stopUpdateTime();
    }

    @Override
    public void onDestroy() {
        if (mBroadcastReceiver != null) {
            MyLog.i("TimeReceiver", "unRegisterMinuteReceiver");
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
        super.onDestroy();
    }

    ViewTreeObserver.OnWindowFocusChangeListener mOnWindowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            MyLog.i(TAG, "onWindowFocusChanged :" + hasFocus + ",fragment:" + this);
            mHasFocus = hasFocus;
        }
    };

    private void sendMinuteBroadCast() {
        MyLog.d("TimeReceiver", "sendMinuteBroadCast");
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent();
        i.setAction("android.intent.action.time_minute");
        PendingIntent pi =
                PendingIntent.getBroadcast(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MINUTE, 1);
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS, pi);
    }

    private void setWeatherOnInk(Weathers weathers) {
        String city = weathers.city;
        String lowhigh = String.format("%s°- %s°", weathers.low, weathers.high);
        String tem = String.format("%s°", weathers.nowTemp);
        String weaCode = weathers.weaCode;
        String weainfo = weathers.weather;

        handleCode(tem, weaCode, weainfo, tv_location_weather, lowhigh, city);
    }

    private void setWeatherError() {
        tv_location_city.setText(R.string.unknown);
        tv_tem.setText(getString(R.string.n));
        tv_location_weather.setText(String.format("%s - %s°", "N", "N"));
        ImageManagerFactory.getImageManager().loadImage(R.drawable.ic_unknown, iv_weather, mContext);
    }

    private void handleCode(String tem, String weaCode, String weaText, TextView tv_location_weather, String lowhigh, String city) {
        int drawableIcon = 0;
        switch (weaCode) {
            case "32": {
                //晴
                drawableIcon = R.drawable.ic_fine_day;
                break;
            }

            case "26":{
                //多云
                drawableIcon = R.drawable.ic_cloudy;
                break;
            }

            case "11": {
                //中雨
                drawableIcon = R.drawable.ic_moderate_rain;
                break;
            }

            case "16": {
                //中雪
                drawableIcon = R.drawable.ic_moderate_snow;
                break;
            }

            default: {
                drawableIcon = R.drawable.ic_unknown;
                break;
            }
        }
        tv_weather_detail.setText(TextUtils.safeText(weaText));
        MyLog.d(TAG, "weatherName  " + weaText);
        tv_location_city.setText(city);
        tv_tem.setText(tem);
        ImageManagerFactory.getImageManager().loadImage(drawableIcon, iv_weather, mContext);
        if (lowhigh != null && !android.text.TextUtils.isEmpty(lowhigh)) {
            tv_location_weather.setText(lowhigh);
        } else {
            setWeatherError();
        }
    }

    /**
     * 开始更新时间
     */
    public void startUpdateTime() {
        MyLog.i(TAG, "startUpdateTime");
        registerTimeMinuteReceiver();
        sendMinuteBroadCast();

        // 立即同步一次时间
        if (min_depart_time_clock_view != null)
            min_depart_time_clock_view.updateTime();
    }

    /**
     * 停止更新时间
     */
    public void stopUpdateTime() {
        MyLog.i(TAG, "stopUpdateTime");
        unRegisterMinuteReceiver();
    }

    /**
     * 监听系统时间变化的广播
     */
    class TimeSetChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // xthink add weather by gaob@x-thinks.com start +++
            if (ACTION_WEATHER.equals(intent.getAction())) {
                String responseContent = intent.getStringExtra(EXTRA_WEATHER);
                Log.d(EXTRA_WEATHER, "收到天气数据：" + responseContent);
                if (!android.text.TextUtils.isEmpty(responseContent)) {
                    // 保存天气数据
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(context).edit();
                    editor.putString(EXTRA_WEATHER, responseContent);
                    editor.apply();
                    getWeatherInfo();
                }
            } else if (ACTION_UNREAD_MSG.equals(intent.getAction())) {
                int unread_msg = intent.getIntExtra(EXTRA_UNREAD, 0);
                Log.d(TAG, "xxx receive unread msg count:" + unread_msg);
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(context).edit();
                editor.putInt(EXTRA_UNREAD, unread_msg);
                editor.apply();
                if (unread_msg > 0 && !mIsForbiden) {
                    fragment_sms_num.setVisibility(View.VISIBLE);
                    if (unread_msg > 9) {
                        fragment_sms_num.setText("9+");
                    } else {
                        fragment_sms_num.setText(String.valueOf(unread_msg));
                    }
                } else {
                    fragment_sms_num.setVisibility(View.GONE);
                }
            } else if (ACTION_BPLN.equals(intent.getAction())) {
                Intent autoupdate = new Intent(context, AutoUpdateService.class);
                context.startForegroundService(autoupdate);
            } else {
                //定时更新刷新
                MyLog.d(TAG, "TimeSetChangeReceiver:" + intent.getAction());
                min_depart_time_clock_view.invalidate();
            }
        }
    }

    /**
     * 动态注册广播
     */
    public void registerTimeMinuteReceiver() {
        if (mHasRegisterTimeReceiver) {
            return;
        }
        MyLog.i("TimeReceiver", "registerTimeMinuteReceiver");
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new TimeReceiver();
            mMinuteFilter = new IntentFilter();
            mMinuteFilter.addAction("android.intent.action.time_minute");
            mBroadcastReceiver.setReceiveListener(new TimeReceiver.ReceiveListener() {
                @Override
                @SuppressLint("InvalidWakeLockTag")
                public void receiveMinuteTick() {
                    //定时更新刷新
                    MyLog.i("TimeReceiver", "receiveMinuteTick");
                    if (mHasFocus && min_depart_time_clock_view != null) {
                        min_depart_time_clock_view.invalidate();
                    }
                }
            });
            mContext.registerReceiver(mBroadcastReceiver, mMinuteFilter);
            mHasRegisterTimeReceiver = true;
        }
    }

    private void unRegisterMinuteReceiver() {

        if (!mHasRegisterTimeReceiver) {
            return;
        }

        if (mBroadcastReceiver != null) {
            MyLog.i("TimeReceiver", "unRegisterMinuteReceiver");
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
        mHasRegisterTimeReceiver = false;
    }

    public static final String mPageName = "TodayFragment";

    /**
     * 用于App统计
     */
    public void onFragmentResume(Context context) {
        MyLog.d(TAG, "cy--=fragment onResume=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
    }

    public void onFragmentPause(Context context) {
        MyLog.d(TAG, "cy--=fragment onPause=" + this.getClass().getSimpleName() + "_mPageName = " + mPageName);
    }

    /**
     * 增加通知的监听
     */
    private void addNotifcationListener() {
        if (mNotificationReceiver == null) {
            mNotificationReceiver = new NotificationReceiver();
            mNotificationReceiver.registerNotificationReceiver(mContext, new NotificationReceiver.NotificationOnListener() {
                @Override
                public void updateNotification() {
                    updateIconNumbers();
                }
            });
        }
    }

    private void updateIconNumbers() {
        // read notification num
//        if (NotificationMonitor.mCurrentNotificationsCounts > 0) {
//            fragment_notice_num.setVisibility(View.GONE);
//            if (NotificationMonitor.mCurrentNotificationsCounts >9) {
//                fragment_notice_num.setText("9+");
//            } else {
//                fragment_notice_num.setText(String.valueOf(NotificationMonitor.mCurrentNotificationsCounts));
//            }
//        } else {
//            fragment_notice_num.setVisibility(View.GONE);
//        }

        // read misscall num
        int missCall = getMissCallCount();
        if (missCall > 0 && !mIsForbiden) {
            fragment_call_num.setVisibility(View.VISIBLE);
            if (missCall > 9) {
                fragment_call_num.setText("9+");
            } else {
                fragment_call_num.setText(String.valueOf(missCall));
            }
        } else {
            fragment_call_num.setVisibility(View.GONE);
        }

        // 微聊未读信息
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int unread_msg = prefs.getInt(EXTRA_UNREAD, 0);
        if (unread_msg > 0 && !mIsForbiden) {
            fragment_sms_num.setVisibility(View.VISIBLE);
            if (unread_msg > 9) {
                fragment_sms_num.setText("9+");
            } else {
                fragment_sms_num.setText(String.valueOf(unread_msg));
            }
        } else {
            fragment_sms_num.setVisibility(View.GONE);
        }

        // get UnreadSms num
//        int UnreadSms = getUnreadSmsCount() + getUnreadwMmsCount();
//        if (UnreadSms > 0) {
//            fragment_sms_num.setVisibility(View.GONE);
//            if (UnreadSms > 9) {
//                fragment_sms_num.setText("9+");
//            } else {
//                fragment_sms_num.setText(String.valueOf(UnreadSms));
//            }
//        } else {
//            fragment_sms_num.setVisibility(View.GONE);
//        }
    }

    private int getMissCallCount() {
        int result = 0;

        if(requestPermisson(getActivity(), Manifest.permission.READ_CALL_LOG, 2)){
            Cursor cursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{
                    CallLog.Calls.TYPE
            }, " type=? and new=?", new String[]{
                    CallLog.Calls.MISSED_TYPE + "", "1"
            }, "date desc");

            if (cursor != null) {
                result = cursor.getCount();
                cursor.close();
            }
        }
        return result;
    }

    //得到未读短信的数量  通过查询数据库得到
    private int getUnreadSmsCount() {
        int result = 0;
        if(requestPermisson(getActivity(), Manifest.permission.READ_SMS, 2)) {
            Cursor csr = mContext.getContentResolver().query(Uri.parse("content://sms"), null,
                    "type = 1 and read = 0", null, null);
            if (csr != null) {
                result = csr.getCount();
                csr.close();
            }
        }

        return result;
    }

    private int getUnreadwMmsCount() {
        int result = 0;
        if(requestPermisson(getActivity(), Manifest.permission.READ_SMS, 2)) {
            Cursor csr = mContext.getContentResolver().query(Uri.parse("content://mms/inbox"),
                    null, "read = 0", null, null);
            if (csr != null) {
                result = csr.getCount();
                csr.close();
            }
        }
        return result;
    }

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            updateIconNumbers();
        }
    }
}
