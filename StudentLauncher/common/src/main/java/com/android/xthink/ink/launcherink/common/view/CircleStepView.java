package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.android.xthink.ink.launcherink.common.R;
import com.android.xthink.ink.launcherink.common.utils.DisplayUtil;


/**
 * Created by liyuyan on 2017/2/24.
 */

public class CircleStepView extends View {

    private float density;

    private int mViewWidth, mViewHeight;

    private int mCenterX, mCenterY;

    private float mStrokeWidth;

    /**
     * 是否已经初始化
     */
    private boolean mIsInitialized = false;

    /**
     * 圆笔
     */
    private Paint mPaintCircle;

    /**
     * 弧形画笔
     */
    private Paint mPaintArc;

    private Paint mPaint;

    /**
     * 弧形的参考矩形
     */
    private RectF mRectF;

    /**
     * 表盘半径
     */
    private float mCircleRadius;
    private float mPercent = 0;

    private Context mContext;


    public CircleStepView(Context context) {
        super(context);
        initBaseData(context);
    }

    public CircleStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBaseData(context);
    }

    public CircleStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseData(context);
    }

    private void initBaseData(Context context) {
        density = getResources().getDisplayMetrics().density;
        mContext = context;
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 初始化
        if (!mIsInitialized) {
            initialize(canvas);
            mIsInitialized = true;
        }

        Bitmap bitmap = DisplayUtil.drawableToBitmap(mContext, R.drawable.ic_person);

        canvas.drawBitmap(bitmap, mCenterX - bitmap.getWidth() / 2, mCenterY - bitmap.getHeight() / 2, mPaint);

        float mCircleRadius = mViewWidth / 2 - density * 3;

        mRectF.set(mCenterX - mCircleRadius, mCenterY - mCircleRadius
                , mCenterX + mCircleRadius, mCenterY + mCircleRadius);

        canvas.drawArc(mRectF, 135, 270, false, mPaintArc);

        float length = 270 * mPercent;
        canvas.drawArc(mRectF, 135, length, false, mPaintCircle);
    }

    public void setArcPercent(float percent) {
        mPercent = percent;
        postInvalidate();
    }

    private void initialize(Canvas canvas) {

        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;

        initPaint();
        mIsInitialized = true;
    }

    private void initPaint() {
        mStrokeWidth = 2 * density;
        mPaintArc = new Paint();
        mPaintArc.setColor(getResources().getColor(R.color.white_trans30));
        mPaintArc.setStrokeWidth(mStrokeWidth);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setAntiAlias(true);

        mPaint = new Paint();

        mPaintCircle = new Paint();
        mPaintCircle.setColor(getResources().getColor(R.color.white));
        float mCircleWidth = 3 * density;
        mPaintCircle.setStrokeWidth(mCircleWidth);
        mPaintCircle.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int WRAP_WIDTH = (int) (density * 58);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, WRAP_WIDTH);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, WRAP_WIDTH);
        }


        mViewWidth = WRAP_WIDTH;
        mViewHeight = WRAP_WIDTH;
    }

}
