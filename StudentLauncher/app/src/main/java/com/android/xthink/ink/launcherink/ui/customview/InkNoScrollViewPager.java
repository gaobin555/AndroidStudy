/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.ui.customview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.android.xthink.ink.launcherink.R;
import com.android.xthink.ink.launcherink.common.utils.MyLog;
import com.android.xthink.ink.launcherink.receiver.ParentalControlReceiver;


/**
 * 无滑动动画的ViewPager
 *
 * @author renxu
 * @version 1.0, 2017/3/23
 */
public class InkNoScrollViewPager extends ViewPager {
    public static final String TAG = "InkNoScrollViewPager2";

    private static final int MIN_FLING_VELOCITY = 400; // dips
    int mTouchSlop = 32;
    boolean mIsBeingDragged;
    boolean mIsDragDone = false;
    int mActivePointerId = 1;
    int mScreenWidth = 720; //默认屏幕宽度720,下面通过获取系统屏幕宽度
    float mOffSet = 0.3f;//屏幕偏移比例
    float mMinOffSetWidth = mScreenWidth * mOffSet; //最小偏移比例, 在initViewpager中重新计算
    /**
     * X-Thinks begin, add
     * what(reason) 事件拦截和分发,滑动事件和点击事件
     * liuwenrong, 1.0, 2017/5/13
     */
    float mLastMotionX, mInitialMotionX, mLastMotionY, mInitialMotionY;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private GestureDetectorCompat mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());
    private boolean mIsRecycle = false;
    private boolean mIsShortSlide = false; //是否短滑翻页,桌面管理设置为短滑

    private SharedPreferences mSp;

    public InkNoScrollViewPager(Context context) {
        this(context, null);
        initViewPager(context);
    }

    public InkNoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.InkNoScrollViewPager);
        mIsRecycle = mTypedArray.getBoolean(R.styleable.InkNoScrollViewPager_isRecycle, false);
        mIsShortSlide = mTypedArray.getBoolean(R.styleable.InkNoScrollViewPager_isShortSlide, false);
        //获取资源后要及时回收
        mTypedArray.recycle();
        initViewPager(context);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

    void initViewPager(Context context) {

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mSp = context.getSharedPreferences("control_prefs", Context.MODE_PRIVATE);//
        if (mIsShortSlide) {
            mMinOffSetWidth = mScreenWidth * mOffSet * 0.5f;
            mMinimumVelocity = (int) (0.2f * (MIN_FLING_VELOCITY * density) / 2);
            // 屏幕点击是会有一定的跳屏(通过系统获取),当大于这个值才会认为是滑动事件
            mTouchSlop = (int)(configuration.getScaledPagingTouchSlop() * 0.5f);
        } else {
            mMinOffSetWidth = mScreenWidth * mOffSet;
            mMinimumVelocity = (int) ((MIN_FLING_VELOCITY * density) / 2);
            // 屏幕点击是会有一定的跳屏(通过系统获取),当大于这个值才会认为是滑动事件
            mTouchSlop = configuration.getScaledPagingTouchSlop();
        }

        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //复写测量方法,使其支持wrapContent matchParent
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            //定义高度,取子view的最大值
            MyLog.d(TAG, "warpContent");
            int height = 0;
            //下面遍历所有child的高度,采用最大的view的高度。
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) {
                    height = h;
                }
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mIsBeingDragged = false;
                mIsDragDone = false;
                MyLog.d(TAG, "onInterceptTouchEvent: down x = " + mLastMotionX);
                break;
            case MotionEvent.ACTION_MOVE:

                final float x = ev.getX();
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = ev.getY();
                final float yDiff = Math.abs(y - mInitialMotionY);
                //MyLog.d(TAG, "onIntercept Moved x to " + x + "," + y + " xDiff = " + xDiff + ",yDiff = " + yDiff + ", 倍数 = " + (yDiff / xDiff));
                //MyLog.d(TAG, "onIntercept 跳屏值mTouchSlop = " + mTouchSlop);

                //x轴上的位移大于 mTouchSlop,且x的位移的一半大于y轴上的位移,才认为是ViewPager的滑动事件,最终return true,拦截事件,自己处理(onTouchEvent)
                if (xDiff > mTouchSlop && xDiff * 2f > yDiff) {
                    //MyLog.d(TAG, "onIntercept Starting drag!");
                    mIsBeingDragged = true;
                    mLastMotionX = dx > 0 ? mInitialMotionX + mTouchSlop :
                            mInitialMotionX - mTouchSlop;
                    mLastMotionY = y;
                }
                break;
        }
        //MyLog.d(TAG, "onInterceptTouchEvent: mIsBeingDragged = " + mIsBeingDragged);
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        return mIsBeingDragged;
    }

    /**
     * X-Thinks end
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int reportLossState = mSp.getInt(ParentalControlReceiver.REPORT_LOSS_STATE, 0);
        int reservePowerState = mSp.getInt(ParentalControlReceiver.RESERVE_POWER_STATE, 0);
        int classForbiddenState = mSp.getInt(ParentalControlReceiver.CLASS_FORBIDDEN_STATE, 0);
        MyLog.d(TAG, "reportLossState = " + reportLossState + ", reservePowerState = " + reservePowerState);
        if (reportLossState == 1 || reservePowerState == 1 || classForbiddenState == 1) {
            MyLog.d(TAG, "手机当前处于禁用状态，无法滑动");
            return false;
        }

        /**
         * X-Thinks begin, add
         * what(reason) 分情况处理touch事件,当事件有冲突时可能需要
         * gaob@xthinks.com, 1.0, 2018/5/13 */
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //MyLog.d(TAG, "onTouchEvent: Down x = " + ev.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                //MyLog.d(TAG, "onTouchEvent: Move x = " + ev.getX());
                /**
                 * 解决任栩的桌面管理 不调InterceptedEvent 的Move,导致mIsBeingDragged=false的问题
                 * */
                final float xMove = ev.getX();
                final float xDiffMove = xMove - mLastMotionX;
                final float yMove = ev.getY();
                final float yDiffMove = yMove - mLastMotionY;
                if (!mIsBeingDragged) {
                    //MyLog.d(TAG, "onTouchEvent: Moved x to " + xMove + "," + yMove + " xDiff = " + xDiffMove + ", yDiff = " + yDiffMove + ", 倍数 = " + (yDiffMove / xDiffMove));
                    if (Math.abs(xDiffMove) > mTouchSlop && Math.abs(xDiffMove * 2f) > yDiffMove) {
                        //MyLog.d(TAG, "onTouchEvent: Starting drag!");
                        mIsBeingDragged = true;
                    }
                } else if (!mIsDragDone) {  // 正在滑动且 未完成 一次滑动事件 , 判断距离去滑动


                    if (Math.abs(xDiffMove) * 2f < Math.abs(yDiffMove)) { // 如果x上总位移的2倍 小于y方向的位移,则不滑动
                    } else {
                        if (calculateSlide(xDiffMove, false)) return true;
                    }

                }

                break;
            case MotionEvent.ACTION_UP:

                float x = ev.getX();

                final float xDiff = x - mLastMotionX;
                float y = ev.getY();
                final float yDiff = y - mLastMotionY;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                //MyLog.d(TAG, "227---onTouchEvent: UP  mIsBeingDragged = " + mIsBeingDragged + "速度 = " + mVelocityTracker.getXVelocity());

                if (Math.abs(xDiff) * 2f < Math.abs(yDiff)) { // 松手时如果x上总位移的2倍 小于y方向的位移,则不滑动
                    mIsBeingDragged = false;
                }

                if (mIsBeingDragged && !mIsDragDone) { //开始正在滑动,且未完成一次滑动
                    if (calculateSlide(xDiff, true)) return true;
                }
                mIsBeingDragged = false;
                //MyLog.d(TAG, "onTouchEvent: UP end");
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                break;
        }
        /** X-Thinks end */

        return true;
    }

    /**
     * 计算是否需要滑动翻页
     *
     * @param xDiff 从down开始x轴上的位移
     * @param isUP  是否是从UP事件中调用的该方法
     * @return true 时,不往下执行了, false不处理,继续往下执行该法之后的代码
     */
    private boolean calculateSlide(float xDiff, boolean isUP) {
        if (null == getAdapter()) {
            return false;
        }
        int count = getAdapter().getCount();
        //先通过位移 大于 最小偏移宽度 去翻页
        int prePos = getCurrentItem();
        if (Math.abs(xDiff) >= mMinOffSetWidth) {

            if (mIsRecycle) {
                if (xDiff >= mMinOffSetWidth) {
                    setCurrentItem((prePos + count - 1) % count);
                    MyLog.d("fullUpdate", "setCurrentItem  " + (prePos + count - 1) % count);
                    mIsDragDone = true;
                    if (mOnMScrollChangeListener != null) {
                        mOnMScrollChangeListener.onScrollChange(prePos, (prePos + count - 1) % count);
                    }
                } else if (xDiff <= -mMinOffSetWidth) {
                    setCurrentItem((prePos + 1) % count);
                    MyLog.d("fullUpdate", "setCurrentItem  " + (prePos + 1) % count);
                    mIsDragDone = true;
                    if (mOnMScrollChangeListener != null) {
                        mOnMScrollChangeListener.onScrollChange(prePos, (prePos + 1) % count);
                    }
                }
            } else {
                if (xDiff >= mMinOffSetWidth) {
                    if (prePos > 0) {
                        setCurrentItem(prePos - 1);
                        if (mOnMScrollChangeListener != null) {
                            mOnMScrollChangeListener.onScrollChange(prePos, prePos - 1);
                        }
                        mIsDragDone = true;
                    }
                } else if (xDiff <= -mMinOffSetWidth) {
                    if (prePos < count) {
                        setCurrentItem(prePos + 1);
                        if (mOnMScrollChangeListener != null) {
                            mOnMScrollChangeListener.onScrollChange(prePos, prePos + 1);
                        }
                        mIsDragDone = true;
                    }
                }
            }


        } else {

            if (isUP) {
                //抬手后, 当位移过小,去通过速度去翻页
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                int velocityY = (int) velocityTracker.getYVelocity();
                //MyLog.d(TAG, "314----Up onTouchEvent:Min = " + mMinimumVelocity + ", Max = " + mMaximumVelocity + ", velocityX = " + velocityX + ", velocityY = " + velocityY);
                if (Math.abs(velocityX) * 5 < Math.abs(velocityY)) {
                    //屏蔽纵向滑动
                    mIsDragDone = true;
                    return true;
                }
                if (mIsRecycle) {
                    if (velocityX >= mMinimumVelocity) {
                        setCurrentItem((prePos + count - 1) % count);
                        MyLog.d("fullUpdate", "setCurrentItem  " + (prePos + count - 1) % count);
                        mIsDragDone = true;
                        if (mOnMScrollChangeListener != null) {
                            mOnMScrollChangeListener.onScrollChange(prePos, (prePos + count - 1) % count);
                        }
                    } else if (velocityX <= -mMinimumVelocity) {
                        setCurrentItem((prePos + 1) % count);
                        MyLog.d("fullUpdate", "setCurrentItem  " + (prePos + 1) % count);
                        mIsDragDone = true;
                        if (mOnMScrollChangeListener != null) {
                            mOnMScrollChangeListener.onScrollChange(prePos, prePos + 1);
                        }
                    }
                } else {
                    if (velocityX >= mMinimumVelocity) {
                        if (prePos > 0) {
                            setCurrentItem(prePos - 1);
                            mIsDragDone = true;
                            if (mOnMScrollChangeListener != null) {
                                mOnMScrollChangeListener.onScrollChange(prePos, prePos - 1);
                            }
                        }
                    } else if (velocityX <= -mMinimumVelocity) {
                        if (prePos < count) {
                            setCurrentItem(prePos + 1);
                            mIsDragDone = true;
                            if (mOnMScrollChangeListener != null) {
                                mOnMScrollChangeListener.onScrollChange(prePos, prePos + 1);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * SimpleOnGestureListener 手势处理类,处理滑动手势
     */
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            MyLog.d(TAG, "onFling: velocityX = " + velocityX);

            if (Math.abs(velocityX) < Math.abs(velocityY)) {
                //屏蔽纵向滑动
                return true;
            }
            int count = getAdapter().getCount();
            if (mIsRecycle) {
                if (velocityX > 0) {
                    setCurrentItem((getCurrentItem() + count - 1) % count);
                } else {
                    setCurrentItem((getCurrentItem() + 1) % count);
                }
            } else {
                if (velocityX > 0) {
                    if (getCurrentItem() > 0) {
                        setCurrentItem(getCurrentItem() - 1);
                    }
                } else {
                    if (getCurrentItem() < count) {
                        setCurrentItem(getCurrentItem() + 1);
                    }
                }
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * ViewPager Scroll回调
     */
    public interface OnMScrollChangeListener {
        void onScrollChange(int prePosition, int curPosition);
    }

    private OnMScrollChangeListener mOnMScrollChangeListener;

    public void setOnMScrollChangeListener(OnMScrollChangeListener onMScrollChangeListener) {
        mOnMScrollChangeListener = onMScrollChangeListener;
    }

    public OnMScrollChangeListener getOnMScrollChangeListener() {
        return mOnMScrollChangeListener;
    }

    public void callONScrollChange(int prePosition, int curPosition) {
        if (null != mOnMScrollChangeListener) {
            mOnMScrollChangeListener.onScrollChange(prePosition, curPosition);
        }
    }
}
