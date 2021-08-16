package com.android.xthink.ink.launcherink.ui.notificationManager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.base.BaseActivity;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.receiver.NotificationReceiver;
import com.android.xthink.ink.launcherink.utils.InkDeviceUtils;
import com.eink.swtcon.SwtconControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NotificationManagerActivity extends BaseActivity implements View.OnClickListener, NotificationManager {
    private static final String TAG = "NotificationManagerActivity";

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final int EVENT_SHOW_CREATE_NOS = 0;
    private static final int EVENT_LIST_CURRENT_NOS = 1;

    private NotificationReceiver mNotificationReceiver = null;

    private boolean isEnabledNLS = false;

    private List<StatusBarNotification> mNotificationList = new ArrayList<>();

    private View mBtBack;
    private View mBtDelete;
    private TextView nt_notice;
    private ListView mNoticesList;

    public static void start(Context context) {
        Intent starter = new Intent(context, NotificationManagerActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.d(TAG, "onResume");
        isEnabledNLS = isEnabled();
        if (!isEnabledNLS) {
            showConfirmDialog();
        }
        showNotificationList();

        initNotificationBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNotificationReceiver != null) {
            mNotificationReceiver.unRegisterNotificationReceiver(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_manager;
    }

    @Override
    protected void initView() {
        mBtBack = findViewById(R.id.bt_back);
        mBtDelete = findViewById(R.id.bt_delete);
        nt_notice = (TextView) findViewById(R.id.nt_notice);
        mNoticesList = (ListView) findViewById(R.id.nt_notification_list);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        MyLog.d(TAG, "initData");
    }

    @Override
    protected void setListener() {
        mBtBack.setOnClickListener(this);
        mBtDelete.setOnClickListener(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_SHOW_CREATE_NOS:
//                    showCreateNotification();
                    showNotificationList();
                    break;
                case EVENT_LIST_CURRENT_NOS:
//                    listCurrentNotification();
                    showNotificationList();
                    break;

                default:
                    break;
            }
        }
    };

    private void showNotificationList() {
        StatusBarNotification[] currentNos = NotificationMonitor.getCurrentNotifications();
        if (currentNos != null && currentNos.length > 0) {
//            mNotificationList.clear();
            mNotificationList = Arrays.asList(currentNos);
            MyLog.d(TAG, "showNotificationList Notification size = " + mNotificationList.size());
            MyLog.d(TAG, "PackageName " + mNotificationList.get(0).getPackageName() + " key = " + mNotificationList.get(0).getKey());
            NotificationsAdapter adapter = new NotificationsAdapter(this, mNotificationList, this);
            mNoticesList.setAdapter(adapter);
            nt_notice.setVisibility(View.GONE);
            mNoticesList.setVisibility(View.VISIBLE);
        } else {
            nt_notice.setVisibility(View.VISIBLE);
            mNoticesList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back: {
                //返回按钮的处理
                finish();
                break;
            }
            case R.id.bt_delete: {
                MyLog.d(TAG, "on Click bt_delete");
                cancelNotification(this, true);
//                createNotification(this);
//                mHandler.sendMessageDelayed(mHandler.obtainMessage(EVENT_SHOW_CREATE_NOS), 50);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void cancelNotification(Context context, boolean isCancelAll) {
        Intent intent = new Intent();
        intent.setAction(NotificationMonitor.ACTION_NLS_CONTROL);
        if (isCancelAll) {
            intent.putExtra("command", "cancel_all");
        }else {
            intent.putExtra("command", "cancel_last");
        }
        context.sendBroadcast(intent);
    }

    private void cancelNotificationbyKey(Context context, String key){
        Intent intent = new Intent();
        intent.setAction(NotificationMonitor.ACTION_NLS_CONTROL);
        intent.putExtra("command", key);
        context.sendBroadcast(intent);
    }

    private void showCreateNotification() {
        if (NotificationMonitor.mPostedNotification != null) {
            String result = NotificationMonitor.mPostedNotification.getPackageName()+"\n"
                    + NotificationMonitor.mPostedNotification.getNotification().extras.getString(Notification.EXTRA_TITLE)+"\n"
                    + NotificationMonitor.mPostedNotification.getNotification().extras.getString(Notification.EXTRA_TEXT)+"\n"
                    + NotificationMonitor.mPostedNotification.getKey()+"\n"+"\n"
                    + nt_notice.getText();
            result = "Create notification:"+"\n"+result;
           nt_notice.setText(result);
        }
    }

    private void listCurrentNotification() {
    String result = "";
        if (NotificationMonitor.getCurrentNotifications() == null) {
            MyLog.d(TAG,"mCurrentNotifications.get(0) is null");
            return;
        }
        int n = NotificationMonitor.mCurrentNotificationsCounts;
        if (n == 0) {
            result = getResources().getString(R.string.empty_notic);
        }else {
            result = String.format(getResources().getQuantityString(R.plurals.active_notification_count_nonzero, n, n));
        }
        result = result + "\n" + getCurrentNotificationString();
        nt_notice.setText(result);
    }

    private String getCurrentNotificationString() {
        String listNos = "";
//        StringBuilder listNos = new StringBuilder();
        StatusBarNotification[] currentNos = NotificationMonitor.getCurrentNotifications();
        if (currentNos != null) {

            for (int i = 0; i < currentNos.length; i++) {
                listNos = i + " " + currentNos[i].getPackageName() + "\n" + listNos;
//                listNos.append(i + " " + currentNos[i].getPackageName() + "\n" + listNos.toString());
            }
        }
        return listNos;
    }

    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
//            for (int i = 0; i < names.length; i++) {
//                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Please enable NotificationMonitor access")
                .setTitle("Notification Access")
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openNotificationAccess();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        })
                .create().show();
    }

    private void openNotificationAccess() {
        InkDeviceUtils.setUpdateModeforActivity(SwtconControl.WF_MODE_DU2);
        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void createNotification(Context context) {
        android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder ncBuilder = new NotificationCompat.Builder(context);
        ncBuilder.setContentTitle("My Notification");
        ncBuilder.setContentText("Notification Listener Service Example");
        ncBuilder.setTicker("Notification Listener Service Example");
        ncBuilder.setSmallIcon(R.drawable.ic_launcher);
        ncBuilder.setAutoCancel(true);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), ncBuilder.build());
        }
    }

    @Override
    public void onClickNotification(Notification notification, String key) {
        // 再打开应用
        PendingIntent intent = notification.contentIntent;
        if (intent != null) {
            MyLog.d(TAG, "open intent = " + intent.getCreatorPackage());
            try {
                intent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            cancelNotificationbyKey(this, key);
        } else {
            cancelNotificationbyKey(this, key);
        }
    }

    private void initNotificationBroadcast() {
        if (mNotificationReceiver == null){
            mNotificationReceiver = new NotificationReceiver();
            mNotificationReceiver.registerNotificationReceiver(this, new NotificationReceiver.NotificationOnListener() {
                @Override
                public void updateNotification() {
                    showNotificationList();
                }
            });
        }
    }
}
