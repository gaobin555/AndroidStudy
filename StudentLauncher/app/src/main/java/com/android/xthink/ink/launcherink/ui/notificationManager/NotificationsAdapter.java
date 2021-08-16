package com.android.xthink.ink.launcherink.ui.notificationManager;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.DateUtil;
import com.android.xthink.ink.launcherink.common.utils.MyLog;

import java.util.List;

import static android.view.View.VISIBLE;

public class NotificationsAdapter extends BaseAdapter {
    private static final String TAG = "NotificationsAdapter";
    private List<StatusBarNotification> mNotificationBeanList;
    private final LayoutInflater mInflater;
    private Context mContext;
    PackageManager mPm;
    private NotificationManager mNotificationManager;

    NotificationsAdapter(Context context, List<StatusBarNotification> objects, NotificationManager notificationManager) {
        mNotificationBeanList = objects;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mPm = mContext.getPackageManager();
        mNotificationManager = notificationManager;
    }

    @Override
    public int getCount() {
        return mNotificationBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotificationBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        //MyLog.d("gaobin", "NotificationsAdapter getView");
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.notification_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.nt_name);
            viewHolder.title = (TextView) convertView.findViewById(R.id.nt_title);
            viewHolder.content = (TextView)convertView.findViewById(R.id.nt_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Notification notification = mNotificationBeanList.get(position).getNotification();
        long when = notification.when;
        String timeString;
        timeString = when>0?DateUtil.getDateWith24Mode(when, mContext):"";
        viewHolder.name.setText(getAppName(mNotificationBeanList.get(position).getPackageName()) + " " + timeString);
        CharSequence title = resolveTitle(notification);
        CharSequence text = resolveText(notification);
        viewHolder.title.setText(title);
        if (TextUtils.isEmpty(text)){
            viewHolder.content.setVisibility(View.GONE);
            viewHolder.content.setText(null);
        } else {
            viewHolder.content.setVisibility(VISIBLE);
            viewHolder.content.setText(text.toString());
        }

        MyLog.d(TAG, "xxx key = " + mNotificationBeanList.get(position).getKey());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO:处理点击通知消息事件
                if (mNotificationManager != null) {
                    MyLog.d(TAG, "Click notification notification package:"+ mNotificationBeanList.get(position).getPackageName());
                    mNotificationManager.onClickNotification(mNotificationBeanList.get(position).getNotification(), mNotificationBeanList.get(position).getKey());
                }
            }
        });

        return convertView;
    }

    private CharSequence resolveText(Notification notification) {
        CharSequence contentText = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        if (contentText == null) {
            contentText = notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }
        return contentText;
    }

    private CharSequence resolveTitle(Notification notification) {
        CharSequence titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        if (titleText == null) {
            titleText = notification.extras.getCharSequence(Notification.EXTRA_TITLE_BIG);
        }
        return titleText;
    }

    private static class ViewHolder {
//        ImageView icon;
        TextView name;
        TextView title;
        TextView content;
    }

    /**
     * get application name from package name
     * @param packageName
     * @return app name
     */
    private String getAppName(String packageName) {
        String appName = "";
        try {
            appName=mPm.getApplicationLabel(mPm.getApplicationInfo(packageName,PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }
}
