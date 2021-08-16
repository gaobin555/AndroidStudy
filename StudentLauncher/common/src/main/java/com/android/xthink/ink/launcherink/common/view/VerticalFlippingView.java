package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 可以上下翻页的页面，页面不滑动。需要子View的高度一致。调用addView添加子View即可。
 * <p>
 * 原理
 * 1：onTouch返回true，Touch事件不会抛回最上层
 * 2：检测到上下滑动以后onIntercept返回true，这样不会在子View获取了Touch Down以后滑动无效。
 * Created by wanchi on 2017/3/8.
 */

public class VerticalFlippingView extends LinearLayout {

    private static final int MOVE_VALUE = 10;//判断是否滑动了的临界值

    /**
     * 每页有多少个child
     */
    private int mChildCountEachPage = 0;

    /**
     * 当前处于第几页
     */
    private int mCurrentPage = 0;

    /**
     * down事件X
     */
    private float mDownX;

    /**
     * down事件Y
     */
    private float mDownY;

    public VerticalFlippingView(Context context) {
        this(context, null);
    }

    public VerticalFlippingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalFlippingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 这段代码的目的是将没有显示出来的View也会全部测量大小。
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; ++i) {
            getChildAt(i).measure(widthMeasureSpec, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 根据高度开始划分页数。
        final int parentHeight = getMeasuredHeight();

        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        final int childHeight = getChildAt(0).getMeasuredHeight();
        final int childWidth = getChildAt(0).getMeasuredWidth();
        if (childHeight == 0) {
            return;
        }
        mChildCountEachPage = parentHeight / childHeight;

        // Y轴偏移量
        int heightOffset = childHeight * mChildCountEachPage * mCurrentPage;
        for (int i = 0; i < childCount; i++) {
            int top = i * childHeight - heightOffset;
            int bottom = top + childHeight;
            getChildAt(i).layout(0, top, childWidth, bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float upY = event.getY();
                if (mDownY - upY > MOVE_VALUE) {
                    // 向上滑动的手势
                    boolean canMoveUp = getChildCount() > (mCurrentPage + 1) * mChildCountEachPage;
                    if (canMoveUp) {
                        mCurrentPage++;
                        requestLayout();
                    }
                } else if (upY - mDownY > MOVE_VALUE) {
                    // 向下滑动的手势
                    if (mCurrentPage > 0) {
                        mCurrentPage--;
                        requestLayout();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 在这里记录down是因为onTouchEvent方法并不一定会及时调用
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断上下滑动了以后就截取touch事件
                if (isMoveVertical(ev)) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 判断当前传入的坐标跟初始值对比，是否为滑动
     */
    private boolean isMoved(MotionEvent ev) {
        return Math.abs(ev.getX() - mDownX) > MOVE_VALUE || Math.abs(ev.getY() - mDownY) > MOVE_VALUE;
    }

    /**
     * 判断是否上下滑动
     */
    private boolean isMoveVertical(MotionEvent ev) {
        float gapX = Math.abs(ev.getX() - mDownX);
        float gapY = Math.abs(ev.getY() - mDownY);
        return gapY / gapX > 3 && isMoved(ev);
    }
}
