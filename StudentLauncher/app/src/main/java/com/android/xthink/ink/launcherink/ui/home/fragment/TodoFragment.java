package com.android.xthink.ink.launcherink.ui.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.othershe.calendarview.bean.DateBean;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.listener.OnSingleChooseListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.weiget.CalendarView;

import java.util.HashMap;

public class TodoFragment extends NativeFragment implements View.OnClickListener {
    private static final String TAG = "TodoFragment";
    private static final String TAG_LIFE = "FragmentLife";

    private CalendarView mCalendarView;
    private TextView mDate;
    private ImageView mLastMonth;
    private ImageView mNextMonth;
    private int[] cDate = CalendarUtil.getCurrentDate();

    public static TodoFragment newInstance() {
        MyLog.d(TAG_LIFE, "newInstance" + " TodoFragment");
        return new TodoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(TAG, "onCreate" + "TodoFragment");

    }

    @Override
    public void switchPage(boolean isVisibleToUser) {
        super.switchPage(isVisibleToUser);
        MyLog.i(TAG, "switchPage: " + isVisibleToUser);
        if (isVisibleToUser) {
            mCalendarView.today();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        super.initView(inflater, container);
        View fragmentContentView;
        fragmentContentView = inflater.inflate(R.layout.fragment_todo, container, false);
        mCalendarView = (CalendarView) fragmentContentView.findViewById(R.id.calendar);
        mDate = (TextView) fragmentContentView.findViewById(R.id.date_title);
        mLastMonth = (ImageView) fragmentContentView.findViewById(R.id.last_month);
        mNextMonth = (ImageView) fragmentContentView.findViewById(R.id.next_month);
        mDate.setOnClickListener(this);
        mLastMonth.setOnClickListener(this);
        mNextMonth.setOnClickListener(this);
        return fragmentContentView;
    }

    @Override
    protected void initData() {
        mCalendarView
                .setStartEndDate("2007.1", "2037.12")
                .setDisableStartEndDate("2007.1.1", "2037.12.31")
                .setInitDate(cDate[0] + "." + cDate[1])
                .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                .init();

        mDate.setText(cDate[0] + "年" + cDate[1] + "月");
    }

    @Override
    protected void setListener() {
        mCalendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                mDate.setText(date[0] + "年" + date[1] + "月");
            }
        });

        mCalendarView.setOnSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date) {
                mDate.setText(date.getSolar()[0] + "年" + date.getSolar()[1] + "月");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_title:
                mCalendarView.today();
                break;

            case R.id.last_month:
                mCalendarView.lastMonth();
                break;

            case R.id.next_month:
                mCalendarView.nextMonth();
                break;
        }
    }
}
