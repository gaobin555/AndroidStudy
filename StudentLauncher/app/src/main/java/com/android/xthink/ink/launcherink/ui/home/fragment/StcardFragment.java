package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.common.view.MyImageView;
import com.android.xthink.ink.launcherink.constants.LauncherConstants;
import com.android.xthink.ink.launcherink.ui.home.bean.Student;
import com.android.xthink.ink.launcherink.utils.Tool;
import com.android.xthink.ink.launcherink.utils.Utility;

public class StcardFragment extends NativeFragment {
    private static final String TAG = "StcardFragment";
    private static final String TAG_LIFE = "FragmentLife";

    private ImageView image_photo_bk;
    private MyImageView image_photo;
    private TextView text_name;
    private TextView text_no_card;
    private TextView text_school;
    private TextView text_class;
    private TextView text_addr;
    private LinearLayout stcard_content;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            Log.d( TAG," mBroadcastReceiver...action="+action);
            switch (action) {
                case LauncherConstants.ACTION_STUDENT: {
                    String responseContent = intent.getStringExtra(LauncherConstants.EXTRA_STUDENT);
                    Log.d(TAG, "收到学生证数据：" + responseContent);
                    if (!TextUtils.isEmpty(responseContent)) {
                        // 保存学生数据
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(context).edit();
                        Student student = initStudentCard(responseContent);
                        String birthday = student.s_birthday;
                        editor.putString(LauncherConstants.EXTRA_STUDENT, responseContent);
                        editor.putString(LauncherConstants.EXTRA_NAME, student.s_name);
                        editor.putString(LauncherConstants.EXTRA_AVATAR, student.url_photo);
                        if (birthday != null && !TextUtils.isEmpty(birthday)) {
                            String[] temp = birthday.split("-");
                            editor.putString(LauncherConstants.EXTRA_BIRTHYEAR, temp[0]);
                            Intent updateClockIntent = new Intent(LauncherConstants.ACTION_UPDATA_CLOCKTYPE);
                            context.sendBroadcast(updateClockIntent);
                        }
                        editor.apply();
                    }
                    break;
                }
                case LauncherConstants.ACTION_SOS_LIST: {
                    String responseContent = intent.getStringExtra(LauncherConstants.EXTRA_SOS_LIST);
                    Log.d(TAG, "收到SOS号码：" + responseContent);
                    if (!TextUtils.isEmpty(responseContent)) {
                        // 保存SOS号码
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(context).edit();
                        editor.putString(LauncherConstants.EXTRA_SOS_LIST, responseContent);
                        editor.apply();
                    }
                    break;
                }
                case LauncherConstants.ACTION_GET_NAME:
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String studentName = prefs.getString(LauncherConstants.EXTRA_NAME, null);
                    String studentAvatar = prefs.getString(LauncherConstants.EXTRA_AVATAR, null);
                    Log.d(TAG, "studentName：" + studentName);
                    Intent sendStudentName = new Intent(LauncherConstants.ACTION_SEND_NAME);
                    sendStudentName.putExtra(LauncherConstants.EXTRA_NAME, studentName);
                    sendStudentName.putExtra(LauncherConstants.EXTRA_AVATAR, studentAvatar);
                    context.sendBroadcast(sendStudentName);
                    break;
            }
        }
    };

    public static StcardFragment newInstance() {
        MyLog.d(TAG_LIFE, "newInstance" + "StcardFragment");
        return new StcardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(TAG, "onCreate" + "StcardFragment");
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View fragmentContentView;
        fragmentContentView = inflater.inflate(R.layout.fragment_stcard, container, false);
        image_photo_bk = fragmentContentView.findViewById(R.id.image_photo_bk);
        image_photo = fragmentContentView.findViewById(R.id.image_photo);
        text_name = fragmentContentView.findViewById(R.id.text_name);
        text_school = fragmentContentView.findViewById(R.id.text_school);
        text_class = fragmentContentView.findViewById(R.id.text_class);
        text_addr = fragmentContentView.findViewById(R.id.text_addr);

        text_no_card = fragmentContentView.findViewById(R.id.stcard_no_card);

        stcard_content = fragmentContentView.findViewById(R.id.stcard_content);

        image_photo.setListener(new MyImageView.OnSetImageListener() {
            @Override
            public void onSetSuccess(Bitmap bitmap) {
                SaveOnlinePhoto(bitmap);
            }
        });
        return fragmentContentView;
    }

    // 保存图像数据
    private void SaveOnlinePhoto(Bitmap bitmap) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(mContext).edit();
        String avatarBtmap = Tool.imageToBase64(bitmap);
        editor.putString(LauncherConstants.EXTRA_AVATAR_BITMAP, avatarBtmap);
        editor.apply();
    }

    private Bitmap getOnlinePhoto() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String avatarBtmap = prefs.getString(LauncherConstants.EXTRA_AVATAR_BITMAP, null);
        Bitmap bitmap = null;
        if (avatarBtmap != null) {
            bitmap = Tool.base64ToImage(avatarBtmap);
        }
        return bitmap;
    }

    @Override
    protected void initData() {
        initStudentCard();
    }

    private void initStudentCard() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String studentString = prefs.getString(LauncherConstants.EXTRA_STUDENT, null);
        //studentString = "佩奇,实验小学,二年级一班,花园小区,http://www.beehome360.com/Image/touxiang.jpg";
        MyLog.d(TAG, "xxx initStudentCard()" + "\n" + "studentString = " + studentString);
        if (studentString != null) {
            // 有缓存数据时直接解析数据
            initStudentCard(studentString);
        } else {
            text_no_card.setVisibility(View.VISIBLE);
            text_name.setText(R.string.no_sdcard_1);
            stcard_content.setVisibility(View.GONE);
        }
    }

    private Student initStudentCard(String studentString) {
        Student student = Utility.handleStudentResponse(mContext, studentString);
        if (student != null) {
            stcard_content.setVisibility(View.VISIBLE);
            text_name.setText(student.s_name);
            text_school.setText(student.s_school);
            text_class.setText(student.s_class);
            text_addr.setText(student.s_addr);
            if (student.url_photo != null) {
                Bitmap bitmap = getOnlinePhoto();
                if (bitmap != null) {
                    image_photo.setImageURL(student.url_photo, new BitmapDrawable(Resources.getSystem(), bitmap));
                } else {
                    image_photo.setImageURL(student.url_photo, mContext.getDrawable(R.drawable.stcard_photo_bk));
                }
                image_photo.setVisibility(View.VISIBLE);
                image_photo_bk.setVisibility(View.GONE);
            } else {
                image_photo_bk.setVisibility(View.VISIBLE);
                image_photo.setVisibility(View.GONE);
            }
            text_no_card.setVisibility(View.GONE);
        } else {
            text_no_card.setVisibility(View.VISIBLE);
            text_name.setText(R.string.no_sdcard_1);
            stcard_content.setVisibility(View.GONE);
        }

        return student;
    }

    @Override
    protected void setListener() {
        IntentFilter filter = new IntentFilter(LauncherConstants.ACTION_STUDENT);
        filter.addAction(LauncherConstants.ACTION_SOS_LIST);
        filter.addAction(LauncherConstants.ACTION_GET_NAME);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyLog.i(TAG, "onDestroyView");
        try {
            if (mBroadcastReceiver != null) {
                mContext.unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "onDestroy " + e);
        }
    }
}
