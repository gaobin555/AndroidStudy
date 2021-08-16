package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by liyuyan on 2017/1/12.
 */

public class ChooseViewPager extends LazyViewPager {

    /**
     * 上一次x坐标
     */
    private float beforeX;

    public ChooseViewPager(Context context) {
        super(context);
    }

    public ChooseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == 2) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beforeX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float motionValue = ev.getX() - beforeX;
                    if (motionValue < 0) {//禁止左滑
                        return true;
                    }
                    beforeX = ev.getX();
                    break;
            }
            return super.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}
