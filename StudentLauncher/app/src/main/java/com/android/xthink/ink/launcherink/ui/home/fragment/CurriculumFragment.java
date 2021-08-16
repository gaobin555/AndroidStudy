package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.constants.LauncherConstants;
import com.android.xthink.ink.launcherink.utils.Tool;

import java.util.Arrays;

public class CurriculumFragment extends NativeFragment {
    private static final String TAG = "CurriculumFragment";
    private static final String TAG_LIFE = "FragmentLife";

    SharedPreferences.Editor mEditor;

    private TextView[] line_time = new TextView[10];
    private TextView[] line_1 = new TextView[10];
    private TextView[] line_2 = new TextView[10];
    private TextView[] line_3 = new TextView[10];
    private TextView[] line_4 = new TextView[10];
    private TextView[] line_5 = new TextView[10];
    private TextView[] line_6 = new TextView[10];
    private TextView[] line_7 = new TextView[10];
    private TextView Saturday;
    private TextView Sunday;
    private LinearLayout Night_1;
    private LinearLayout Night_2;
    private LinearLayout Start;
    private LinearLayout NoCurriculum;
    private View line_night1;
    private View line_night2;
    private View line_night3;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d( TAG," mBroadcastReceiver...action="+action);
            if (LauncherConstants.ACTION_CURRICULUM.equals(action)) {
                String responseContent = intent.getStringExtra(LauncherConstants.EXTRA_CURRICULUM);
                Log.d(TAG, "收到课程表数据：" + responseContent);
                if (!TextUtils.isEmpty(responseContent)) {
                    initCurriculumCard(responseContent);
                    NoCurriculum.setVisibility(View.GONE);
                    Start.setVisibility(View.VISIBLE);
                    // 保存课程表数据
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(context).edit();
                    editor.putString(LauncherConstants.EXTRA_CURRICULUM, responseContent);
                    editor.apply();
                }
            } else if (LauncherConstants.ACTION_BPD9.equals(action)) {
                String data = intent.getStringExtra(LauncherConstants.EXTRA_BPD9);
                classForbidden(data);
            }
        }
    };

    public static CurriculumFragment newInstance() {
        MyLog.d(TAG_LIFE, "newInstance" + "CurriculumFragment");
        return new CurriculumFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(TAG, "onCreate" + "CurriculumFragment");
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View fragmentContentView;
        fragmentContentView = inflater.inflate(R.layout.fragment_curriculum, container, false);
        Saturday = fragmentContentView.findViewById(R.id.saturday);
        Sunday = fragmentContentView.findViewById(R.id.sunday);
        Night_1 = fragmentContentView.findViewById(R.id.night_1);
        Night_2 = fragmentContentView.findViewById(R.id.night_2);
        line_night1 = fragmentContentView.findViewById(R.id.line_night1);
        line_night2 = fragmentContentView.findViewById(R.id.line_night2);
        line_night3 = fragmentContentView.findViewById(R.id.line_night3);
        Start = fragmentContentView.findViewById(R.id.start);
        NoCurriculum = fragmentContentView.findViewById(R.id.no_curriculum);
        // 时间
        line_time[0] = fragmentContentView.findViewById(R.id.time_1);
        line_time[1] = fragmentContentView.findViewById(R.id.time_2);
        line_time[2] = fragmentContentView.findViewById(R.id.time_3);
        line_time[3] = fragmentContentView.findViewById(R.id.time_4);
        line_time[4] = fragmentContentView.findViewById(R.id.time_5);
        line_time[5] = fragmentContentView.findViewById(R.id.time_6);
        line_time[6] = fragmentContentView.findViewById(R.id.time_7);
        line_time[7] = fragmentContentView.findViewById(R.id.time_8);
        line_time[8] = fragmentContentView.findViewById(R.id.time_9);
        line_time[9] = fragmentContentView.findViewById(R.id.time_10);
        // 周一
        line_1[0] = fragmentContentView.findViewById(R.id.line_11);
        line_1[1] = fragmentContentView.findViewById(R.id.line_12);
        line_1[2] = fragmentContentView.findViewById(R.id.line_13);
        line_1[3] = fragmentContentView.findViewById(R.id.line_14);
        line_1[4] = fragmentContentView.findViewById(R.id.line_15);
        line_1[5] = fragmentContentView.findViewById(R.id.line_16);
        line_1[6] = fragmentContentView.findViewById(R.id.line_17);
        line_1[7] = fragmentContentView.findViewById(R.id.line_18);
        line_1[8] = fragmentContentView.findViewById(R.id.line_19);
        line_1[9] = fragmentContentView.findViewById(R.id.line_10);
        // 周二
        line_2[0] = fragmentContentView.findViewById(R.id.line_21);
        line_2[1] = fragmentContentView.findViewById(R.id.line_22);
        line_2[2] = fragmentContentView.findViewById(R.id.line_23);
        line_2[3] = fragmentContentView.findViewById(R.id.line_24);
        line_2[4] = fragmentContentView.findViewById(R.id.line_25);
        line_2[5] = fragmentContentView.findViewById(R.id.line_26);
        line_2[6] = fragmentContentView.findViewById(R.id.line_27);
        line_2[7] = fragmentContentView.findViewById(R.id.line_28);
        line_2[8] = fragmentContentView.findViewById(R.id.line_29);
        line_2[9] = fragmentContentView.findViewById(R.id.line_20);
        // 周三
        line_3[0] = fragmentContentView.findViewById(R.id.line_31);
        line_3[1] = fragmentContentView.findViewById(R.id.line_32);
        line_3[2] = fragmentContentView.findViewById(R.id.line_33);
        line_3[3] = fragmentContentView.findViewById(R.id.line_34);
        line_3[4] = fragmentContentView.findViewById(R.id.line_35);
        line_3[5] = fragmentContentView.findViewById(R.id.line_36);
        line_3[6] = fragmentContentView.findViewById(R.id.line_37);
        line_3[7] = fragmentContentView.findViewById(R.id.line_38);
        line_3[8] = fragmentContentView.findViewById(R.id.line_39);
        line_3[9] = fragmentContentView.findViewById(R.id.line_30);
        // 周四
        line_4[0] = fragmentContentView.findViewById(R.id.line_41);
        line_4[1] = fragmentContentView.findViewById(R.id.line_42);
        line_4[2] = fragmentContentView.findViewById(R.id.line_43);
        line_4[3] = fragmentContentView.findViewById(R.id.line_44);
        line_4[4] = fragmentContentView.findViewById(R.id.line_45);
        line_4[5] = fragmentContentView.findViewById(R.id.line_46);
        line_4[6] = fragmentContentView.findViewById(R.id.line_47);
        line_4[7] = fragmentContentView.findViewById(R.id.line_48);
        line_4[8] = fragmentContentView.findViewById(R.id.line_49);
        line_4[9] = fragmentContentView.findViewById(R.id.line_40);
        // 周五
        line_5[0] = fragmentContentView.findViewById(R.id.line_51);
        line_5[1] = fragmentContentView.findViewById(R.id.line_52);
        line_5[2] = fragmentContentView.findViewById(R.id.line_53);
        line_5[3] = fragmentContentView.findViewById(R.id.line_54);
        line_5[4] = fragmentContentView.findViewById(R.id.line_55);
        line_5[5] = fragmentContentView.findViewById(R.id.line_56);
        line_5[6] = fragmentContentView.findViewById(R.id.line_57);
        line_5[7] = fragmentContentView.findViewById(R.id.line_58);
        line_5[8] = fragmentContentView.findViewById(R.id.line_59);
        line_5[9] = fragmentContentView.findViewById(R.id.line_50);
        // 周六
        line_6[0] = fragmentContentView.findViewById(R.id.line_61);
        line_6[1] = fragmentContentView.findViewById(R.id.line_62);
        line_6[2] = fragmentContentView.findViewById(R.id.line_63);
        line_6[3] = fragmentContentView.findViewById(R.id.line_64);
        line_6[4] = fragmentContentView.findViewById(R.id.line_65);
        line_6[5] = fragmentContentView.findViewById(R.id.line_66);
        line_6[6] = fragmentContentView.findViewById(R.id.line_67);
        line_6[7] = fragmentContentView.findViewById(R.id.line_68);
        line_6[8] = fragmentContentView.findViewById(R.id.line_69);
        line_6[9] = fragmentContentView.findViewById(R.id.line_60);
        // 周日
        line_7[0] = fragmentContentView.findViewById(R.id.line_71);
        line_7[1] = fragmentContentView.findViewById(R.id.line_72);
        line_7[2] = fragmentContentView.findViewById(R.id.line_73);
        line_7[3] = fragmentContentView.findViewById(R.id.line_74);
        line_7[4] = fragmentContentView.findViewById(R.id.line_75);
        line_7[5] = fragmentContentView.findViewById(R.id.line_76);
        line_7[6] = fragmentContentView.findViewById(R.id.line_77);
        line_7[7] = fragmentContentView.findViewById(R.id.line_78);
        line_7[8] = fragmentContentView.findViewById(R.id.line_79);
        line_7[9] = fragmentContentView.findViewById(R.id.line_70);
        return fragmentContentView;
    }

    @Override
    protected void initData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String curriculumString = prefs.getString(LauncherConstants.EXTRA_CURRICULUM, null);
        //curriculumString = "1$语文@数学@外语@政治@历史@音乐@自习|2$语@数@外@政@史@会@习@习@习@习|3$语@数@外@政@史@会@习@习@习@习|4$语@数@外@政@史@会@习@习@习@习|5$语@数@外@政@史@会@习@习@习@习";
        MyLog.d(TAG, "xxx initData()" + "\n" + "curriculumString = " + curriculumString);
        if (curriculumString != null) {
            // 有缓存数据时直接解析天气数据
            initCurriculumCard(curriculumString);
            NoCurriculum.setVisibility(View.GONE);
            Start.setVisibility(View.VISIBLE);
        } else {
            NoCurriculum.setVisibility(View.VISIBLE);
            Start.setVisibility(View.GONE);
        }
    }

    private void initCurriculumCard(String curriculumString) {
        String[] content = curriculumString.split("\\|");
        String[] time = content[0].replace("0$", "").split("@");
        if(!TextUtils.isEmpty(content[0])) {// 时间
            content[0] = content[0].replace("0$", "");
            MyLog.d(TAG, "xxx 时间 = " + content[0]);
            if(!TextUtils.isEmpty(content[0])) {
                String[] curriculum = content[0].split("@");
                MyLog.d(TAG, "time = " + Arrays.toString(time));
                for(int i=0; i < line_time.length; i++) {
                    if(!TextUtils.isEmpty(curriculum[i])) {
                        String time_temp = curriculum[i].split("-")[0];
                        line_time[i].setTextSize(12);
                        line_time[i].setText(time_temp);
                    } else {
                        line_time[i].setText("");
                    }
                }
                for(int i=curriculum.length; i < line_time.length; i++) {
                    line_time[i].setText("");
                }
            }
        } else {
            for (TextView t : line_time) {
                t.setText("");
            }
        }

        StringBuilder curriculumForbidden = new StringBuilder();

        if(!TextUtils.isEmpty(content[1])) {// 周一
            content[1] = content[1].replace("1$", "");
            MyLog.d(TAG, "xxx 周一 = " + content[1]);
            if(!TextUtils.isEmpty(content[1])) {
                String[] curriculum = content[1].split("@");
                curriculumForbidden.append("1@");
                for(int i=0; i < line_1.length; i++) {
                    if (i < curriculum.length){
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_1[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_1[i].setText("");
                        }
                    }
                }
                curriculumForbidden.append("|");
                for(int i=curriculum.length; i < line_1.length; i++) {
                    line_1[i].setText("");
                }
            }
        } else {
            for (TextView t : line_1) {
                t.setText("");
            }
        }

        if(!TextUtils.isEmpty(content[2])) {// 周二
            content[2] = content[2].replace("2$", "");
            MyLog.d(TAG, "xxx 周二 = " + content[2]);
            if(!TextUtils.isEmpty(content[2])) {
                String[] curriculum = content[2].split("@");
                curriculumForbidden.append("2@");
                for(int i=0; i < line_2.length; i++) {
                    if (i < curriculum.length) {
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_2[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_2[i].setText("");
                        }
                    }
                }
                curriculumForbidden.append("|");
                for(int i=curriculum.length; i < line_2.length; i++) {
                    line_2[i].setText("");
                }
            }
        } else {
            for (TextView t : line_2) {
                t.setText("");
            }
        }

        if(!TextUtils.isEmpty(content[3])) {// 周三
            content[3] = content[3].replace("3$", "");
            MyLog.d(TAG, "xxx 周三 = " + content[3]);
            if(!TextUtils.isEmpty(content[3])) {
                String[] curriculum = content[3].split("@");
                curriculumForbidden.append("3@");
                for(int i=0; i < line_3.length; i++) {
                    if (i < curriculum.length) {
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_3[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_3[i].setText("");
                        }
                    }
                }
                curriculumForbidden.append("|");
                for(int i=curriculum.length; i < line_3.length; i++) {
                    line_3[i].setText("");
                }
            }
        } else {
            for (TextView t : line_3) {
                t.setText("");
            }
        }

        if(!TextUtils.isEmpty(content[4])) {// 周四
            content[4] = content[4].replace("4$", "");
            MyLog.d(TAG, "xxx 周四 = " + content[4]);
            if(!TextUtils.isEmpty(content[4])) {
                String[] curriculum = content[4].split("@");
                curriculumForbidden.append("4@");
                for(int i=0; i < line_4.length; i++) {
                    if (i < curriculum.length) {
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_4[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_4[i].setText("");
                        }
                    }
                }
                curriculumForbidden.append("|");
                for(int i=curriculum.length; i < line_4.length; i++) {
                    line_4[i].setText("");
                }
            }
        } else {
            for (TextView t : line_4) {
                t.setText("");
            }
        }

        if(!TextUtils.isEmpty(content[5])) {// 周五
            content[5] = content[5].replace("5$", "");
            MyLog.d(TAG, "xxx 周五 = " + content[5]);
            if(!TextUtils.isEmpty(content[5])) {
                String[] curriculum = content[5].split("@");
                curriculumForbidden.append("5@");
                for(int i=0; i < line_5.length; i++) {
                    if (i < curriculum.length) {
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_5[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_5[i].setText("");
                        }
                    }
                }
                curriculumForbidden.append("|");
                for(int i=curriculum.length; i < line_5.length; i++) {
                    line_5[i].setText("");
                }
            }
        }

        if(!TextUtils.isEmpty(content[6])) {// 周六
            content[6] = content[6].replace("6$", "");
            MyLog.d(TAG, "xxx 周六 = " + content[6]);
            if(!"@@@@@@@@@".equals(content[6])) {
                String[] curriculum = content[6].split("@");
                curriculumForbidden.append("6@");
                for(int i=0; i < line_6.length; i++) {
                    if (i < curriculum.length) {
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_6[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_6[i].setText("");
                        }
                    }
                }
                curriculumForbidden.append("|");
                for(int i=curriculum.length; i < line_6.length; i++) {
                    line_6[i].setText("");
                }
                Saturday.setVisibility(View.VISIBLE);
                for (TextView t : line_6) {
                    t.setVisibility(View.VISIBLE);
                }
            } else {
                Saturday.setVisibility(View.GONE);
                for (TextView t : line_6) {
                    t.setVisibility(View.GONE);
                }
            }
        }

        if(!TextUtils.isEmpty(content[7])) {// 周日
            content[7] = content[7].replace("7$", "");
            MyLog.d(TAG, "xxx 周日 = " + content[7]);
            if (!"@@@@@@@@@".equals(content[7])) {
                curriculumForbidden.append("7@");
                String[] curriculum = content[7].split("@");
                for (int i = 0; i < line_7.length; i++) {
                    if (i < curriculum.length) {
                        if (!TextUtils.isEmpty(curriculum[i])) {
                            line_7[i].setText(Tool.unicodeNo0xuToString(curriculum[i]));
                            curriculumForbidden.append(time[i]).append("@");
                        } else {
                            line_7[i].setText("");
                        }
                    }
                }
                for (int i = curriculum.length; i < line_7.length; i++) {
                    line_7[i].setText("");
                }
                Sunday.setVisibility(View.VISIBLE);
                for (TextView t : line_7) {
                    t.setVisibility(View.VISIBLE);
                }
            } else {
                Sunday.setVisibility(View.GONE);
                for (TextView t : line_7) {
                    t.setVisibility(View.GONE);
                }
            }
        }

        if (content[1].split("@").length < 9 && content[2].split("@").length < 9
                && content[3].split("@").length < 9 && content[4].split("@").length < 9
                && content[5].split("@").length < 9 && content[6].split("@").length < 9
                && content[7].split("@").length < 9) {
            Night_1.setVisibility(View.GONE);
            Night_2.setVisibility(View.GONE);
            line_night1.setVisibility(View.GONE);
            line_night2.setVisibility(View.GONE);
            line_night3.setVisibility(View.GONE);
            Start.setPadding(0, 50, 0, 0);
        } else {
            Night_1.setVisibility(View.VISIBLE);
            Night_2.setVisibility(View.VISIBLE);
            line_night1.setVisibility(View.VISIBLE);
            line_night2.setVisibility(View.VISIBLE);
            line_night3.setVisibility(View.VISIBLE);
            Start.setPadding(0, 10, 0, 0);
        }

//        curriculumForbidden.append(content[0]).append("|");
//        curriculumForbidden.append(content[1].split("@").length).append("@")
//                .append(content[2].split("@").length).append("@")
//                .append(content[3].split("@").length).append("@")
//                .append(content[4].split("@").length).append("@")
//                .append(content[5].split("@").length).append("@")
//                .append(content[6].split("@").length).append("@")
//                .append(content[7].split("@").length);
        MyLog.d(TAG, "curriculumForbidden = " + curriculumForbidden.toString());

        // 保存课程表禁用数据
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(mContext).edit();
        editor.putString(LauncherConstants.EXTRA_CURRICULUM_FORBIDDEN_DATA, curriculumForbidden.toString());
        editor.apply();
    }

    @Override
    protected void setListener() {
        IntentFilter filter = new IntentFilter(LauncherConstants.ACTION_CURRICULUM);
        filter.addAction(LauncherConstants.ACTION_BPD9);
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

    private void classForbidden(String data) {
        if (TextUtils.isEmpty(data)) {
            MyLog.e(TAG,"上课禁用 Data is null = " + data);
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String[] arr = data.split(",");
        if (TextUtils.equals(arr[0], "1")) {// 开启
            MyLog.d(TAG,"开启课程表禁用 = " + arr[0]);
            String curriculumForbiddenData = prefs.getString(LauncherConstants.EXTRA_CURRICULUM_FORBIDDEN_DATA, null);
            if (curriculumForbiddenData == null) {
                MyLog.e(TAG,"没有设置课程表!");
                return;
            }
            MyLog.d(TAG, "curriculumForbiddenData = " + curriculumForbiddenData);
            Intent intent = new Intent(LauncherConstants.ACTION_CURRICULUM_FORBIDDEN);
            intent.putExtra(LauncherConstants.EXTRA_CURRICULUM_FORBIDDEN_DATA, curriculumForbiddenData);
            mContext.sendBroadcast(intent);
        }
    }
}
