package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.android.xthink.ink.launcherink.common.network.direct.bean.DirectAppBean;
import com.android.xthink.ink.launcherink.common.utils.TypefaceManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeView extends View {

    public static final String TAG = "TimeView";

    /**
     * 矩形范围
     */
    private Rect mRect;

    /**
     * 弧形的参考矩形
     */
    private RectF mRectF;


    /**
     * 控件宽
     */
    private float mViewWidth;

    /**
     * 控件高
     */
    private float mViewHeight;


    /**
     * 表盘外圈宽度
     */
    private float mStrokeWidth, mCircleWidth;

    /**
     * 表盘半径
     */
    private float mCircleRadiusWatcher;

    /**
     * 当前时角度
     */
    private float mCurrentHourDegree;

    /**
     * 当前分角度
     */
    private float mCurrentMinDegree;
    /**
     * 表盘中心x坐标
     */
    private float mCenterX;

    /**
     * 表盘中心y坐标
     */
    private float mCenterY;

    /**
     * 默认小时点
     */
    private float[] mButtonHourPosition;

    /**
     * 默认分钟点
     */
    private float[] mButtonMinPosition;

    /**
     * 默认分钟点
     */
    private float[] mButtonMinPositionOrigin;

    /**
     * 默认分钟点
     */
    private float[] mButtonHourPositionOrigin;

    /**
     * 表盘背景画笔
     */
    private Paint mPaintCircleBackground;

    /**
     * 移动按钮画笔
     */
    private Paint mPaintMoveButton;

    /**
     * 弧形画笔
     */
    private Paint mPaintArc, mPaintAtcMin;

    /**
     * 显示时间画笔
     */
    private Paint mPaintTime, mPaintTimeBelow;

    /**
     * 效果画笔
     */
    private Paint mPaintGlowEffect;

    /**
     * 拖动中画笔
     */
    private Paint mPaintDragging;

    /**
     * 显示时间
     */
    private String mDisplayCurrentTime, mDisplayCurrentTimeBelow;

    /**
     * 是否已经初始化
     */
    private boolean mIsInitialized = false;
    private Context mContext;

    private int screenWidth;
    private int screenHeight;
    float density;
    float textDensity;
    private int color[] = new int[]{Color.TRANSPARENT, Color.BLACK}; //画时钟分钟的渐变

    private static final String CLOCK_TYPE = "clock_type";
    private static final String EXTRA_BIRTHYEAR = "birthyear";
    private boolean bClockType = false;

    private int zodiac_index = -1;
    private String[] animals = new String[]{"bg_time_shu.png", "bg_time_niu.png", "bg_time_hu.png",
                                            "bg_time_tu.png", "bg_time_long.png", "bg_time_she.png",
                                            "bg_time_ma.png", "bg_time_yang.png", "bg_time_hou.png",
                                            "bg_time_ji.png", "bg_time_dog.png", "bg_time_pig.png"};
    private int[] animals_py = new int[] {170, 150, 200, 200, 150, 100, 190, 160, 190, 180, 220, 210};

    public TimeView(Context context) {
        super(context);
        mContext = context;
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public static Bitmap getBitmapFromBitmapDrawable(Context mContext, String fileName) {
        BitmapDrawable bmpMeizi = null;
        try {
            bmpMeizi = new BitmapDrawable(mContext.getAssets().open(fileName));//"pic_meizi.jpg"
            Bitmap mBitmap = bmpMeizi.getBitmap();
            return mBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        bClockType = prefs.getBoolean(CLOCK_TYPE, true);
        String year = prefs.getString(EXTRA_BIRTHYEAR, "2019");
        Log.d(TAG, "生日年：" + year);
        zodiac_index = getYear(year);
        if (zodiac_index < 0) {
            bClockType = false;
        }
        // 初始化
        if (!mIsInitialized) {
            initialize(canvas);
            mIsInitialized = true;
        }

        // 画表盘背景的圆圈
//        canvas.drawCircle(mCenterX, mCenterY, mCircleRadiusWatcher, mPaintCircleBackground);
        Bitmap bg_time = getBitmapFromBitmapDrawable(mContext, "bg_time_classic.png");
        Bitmap time_num = getBitmapFromBitmapDrawable(mContext, "time_pad.png");
        if (bClockType) {
            bg_time = getBitmapFromBitmapDrawable(mContext, animals[zodiac_index]);
            time_num = getBitmapFromBitmapDrawable(mContext, "time_pad.png");
        }
        float bgY = (mViewHeight - bg_time.getHeight()) / 2 - 20;
        float bgX = (mViewWidth - bg_time.getWidth()) / 2;
        Log.d(TAG, "动物背景：x = " + bgX + ", y = " + bgY);
        canvas.drawBitmap(bg_time, bgX, bgY, mPaintCircleBackground);
        if (bClockType) {
            bgY = bgY + animals_py[zodiac_index];
            bgX = (mViewWidth - time_num.getWidth())/2;
            Log.d(TAG, "时间背景：x = " + bgX + ", y = " + bgY);
            canvas.drawBitmap(time_num, bgX, bgY, mPaintCircleBackground);
        }

        // 设置显示时间
        setDisplayTime();
        //设置当前时
        updateHourDegree();
        //设置当前分
        updateMinDegree();

        canvas.drawText(mDisplayCurrentTimeBelow, mCenterX, mCenterY + 150
                * density, mPaintTimeBelow);

        // 画指针
        Bitmap ic_hour = getBitmapFromBitmapDrawable(mContext, "ic_hour.png");
        Bitmap ic_minute = getBitmapFromBitmapDrawable(mContext, "ic_minute.png");
        if (bClockType) {
            ic_hour = getBitmapFromBitmapDrawable(mContext, "ic_hour2.png");
            ic_minute = getBitmapFromBitmapDrawable(mContext, "ic_minute2.png");
        }
        if (ic_hour != null && ic_minute != null) {
            if (bClockType) {
                bgX = bgX + time_num.getWidth()/2;
                bgY = bgY + time_num.getHeight()/2;
                Log.d(TAG, "指针坐标：x = " + bgX + ", y = " + bgY);
                drawRotateBitmap(canvas, mPaintMoveButton, ic_hour, mCurrentHourDegree, bgX, bgY); //mCurrentHourDegree
                drawRotateBitmap(canvas, mPaintMoveButton, ic_minute, mCurrentMinDegree, bgX, bgY);
            } else {
                canvas.drawText(mDisplayCurrentTime, mCenterX, mCenterY+30, mPaintTime);
            }
        } else {
            Log.d(TAG,"onDraw :" + "can't find ic_hour.png or ic_minute.png");
        }

    }

    /**
     * 绘制自旋转位图
     *
     * @param canvas
     * @param paint
     * @param bitmap
     *            位图对象
     * @param rotation
     *            旋转度数
     * @param posX
     *            在canvas的位置坐标
     * @param posY
     */
    private void drawRotateBitmap(Canvas canvas, Paint paint, Bitmap bitmap,
                                  float rotation, float posX, float posY) {
        Matrix matrix = new Matrix();
        int offsetX = bitmap.getWidth() / 2;
        int offsetY = bitmap.getHeight() / 2;
        matrix.postTranslate(-offsetX, -offsetY);
        matrix.postRotate(rotation);
        if (bClockType) {
            matrix.postTranslate(posX, posY);         
        } else {
            matrix.postTranslate(posX + offsetX, posY + offsetY);
        }
        canvas.drawBitmap(bitmap, matrix, paint);
    }

    private void updateHourDegree() {
        Calendar c = Calendar.getInstance();
        if (bClockType) {
            mCurrentHourDegree = (float) (c.get(Calendar.HOUR) * 30 + c.get(Calendar.MINUTE) / 2);
        } else {
            mCurrentHourDegree = (float) (c.get(Calendar.HOUR) * 30 + c.get(Calendar.MINUTE) / 2) + 180;
        }
        updateDragHourButtonPosition();
    }


    private void updateMinDegree() {
        Calendar c = Calendar.getInstance();
        if (bClockType) {
            mCurrentMinDegree = (float) (c.get(Calendar.MINUTE) * 6);
        } else {
            mCurrentMinDegree = (float) (c.get(Calendar.MINUTE) * 6) + 180;
        }
        updateDragMinButtonPosition();
    }

    /**
     * 更新按钮位置
     */
    private void updateDragHourButtonPosition() {
        // 根据勾股定理已知斜边、正弦余弦，求对应的边 // Math.toRadians： 根据角度转化为弧度
        mButtonHourPosition[0] = (float) (mCenterX + mCircleRadiusWatcher * Math.sin(Math.toRadians(mCurrentHourDegree)));
        mButtonHourPosition[1] = (float) (mCenterY - mCircleRadiusWatcher * Math.cos(Math.toRadians(mCurrentHourDegree)));

        mButtonHourPositionOrigin[0] = (float) (mCenterX);
        mButtonHourPositionOrigin[1] = (float) (mCenterY);
    }

    /**
     * 更新按钮位置
     */
    private void updateDragMinButtonPosition() {
        // 根据勾股定理已知斜边、正弦余弦，求对应的边 // Math.toRadians： 根据角度转化为弧度
        mButtonMinPosition[0] = (float) (mCenterX + mCircleRadiusWatcher * Math.sin(Math.toRadians(mCurrentMinDegree)));
        mButtonMinPosition[1] = (float) (mCenterY - mCircleRadiusWatcher * Math.cos(Math.toRadians(mCurrentMinDegree)));

        mButtonMinPositionOrigin[0] = (float) (mCenterX);
        mButtonMinPositionOrigin[1] = (float) (mCenterY);
    }


    /**
     * 初始化
     *
     * @param canvas canvas
     */
    @SuppressWarnings("deprecation")
    private void initialize(Canvas canvas) {

        density = getResources().getDisplayMetrics().density;
        textDensity = getResources().getDisplayMetrics().scaledDensity;
        mViewWidth = canvas.getWidth();
        mViewHeight = canvas.getHeight();

        mStrokeWidth = (3 * density);
        mCircleWidth = 3 * density + 0.5f;
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        mCircleRadiusWatcher = screenWidth * 25 / 72;
        mCurrentHourDegree = 0;
        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;

        Log.d(TAG, "xxx initialize, mViewWidth = " + mViewWidth + " mViewHeight = " + mViewHeight + ", mCenterX = " + mCenterX + " mCenterY = " + mCenterY);

        // 默认时间位置
        mButtonHourPosition = new float[]{mCenterX, mCenterY - mCircleRadiusWatcher};
        mButtonMinPosition = new float[]{mCenterX, mCenterY - mCircleRadiusWatcher};
        mButtonMinPositionOrigin = new float[]{mCenterX, mCenterY - mCircleRadiusWatcher};
        mButtonHourPositionOrigin = new float[]{mCenterX, mCenterY - mCircleRadiusWatcher};

        mPaintCircleBackground = new Paint();
        mPaintMoveButton = new Paint();
        mPaintArc = new Paint();
        mPaintAtcMin = new Paint();
        mPaintTime = new Paint();
        mPaintTimeBelow = new Paint();
        mPaintGlowEffect = new Paint();
        mPaintDragging = new Paint();

        // 表盘外圈颜色
        int colorWatcher = Color.BLACK;
        mPaintCircleBackground.setColor(colorWatcher);
        mPaintCircleBackground.setStrokeWidth(3 * density);
        mPaintCircleBackground.setStyle(Paint.Style.STROKE);
        mPaintCircleBackground.setAntiAlias(true);

        // 时间颜色
        int colorRemainTime = Color.WHITE;
        mPaintMoveButton.setColor(Color.BLACK);
        mPaintMoveButton.setStyle(Paint.Style.FILL);
        mPaintMoveButton.setAntiAlias(true);

        mPaintArc.setColor(colorRemainTime);
        mPaintArc.setStrokeWidth(mStrokeWidth);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setAntiAlias(true);

        mPaintAtcMin.setStrokeWidth(mStrokeWidth);
        mPaintAtcMin.setStyle(Paint.Style.STROKE);
        mPaintAtcMin.setAntiAlias(true);

        mPaintTime.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTime.setAntiAlias(true);
        mRect = new Rect();
        float densityText = getResources().getDisplayMetrics().scaledDensity;
        mPaintTime.setTextSize(60 * textDensity + 0.5f);
        mPaintTime.setColor(Color.BLACK);
        mPaintTime.setAntiAlias(true);
        mPaintTime.getTextBounds("00:00", 0, "00:00".length(), mRect);
        mPaintTime.setTypeface(TypefaceManager.getPFDinTextCondPro(mContext));
        mPaintTime.setTextAlign(Paint.Align.CENTER);

        mPaintTimeBelow.setStyle(Paint.Style.STROKE);
        mPaintTimeBelow.setAntiAlias(true);
        mPaintTimeBelow.setTextSize(30 * densityText + 0.5f);
        mPaintTimeBelow.setColor(Color.BLACK);
        mPaintTimeBelow.setTypeface(TypefaceManager.getPFDinTextPro(mContext));
        mPaintTimeBelow.setTextAlign(Paint.Align.CENTER);


        //用于绘制圆弧尽头的辉光效果,辉光区域就是dragButton的区域
        // mPaintGlowEffect.setMaskFilter(new BlurMaskFilter(5 * mStrokeWidth, BlurMaskFilter.Blur.NORMAL));
        mPaintGlowEffect.setAntiAlias(true);
        mPaintGlowEffect.setColor(Color.BLACK);
        mPaintGlowEffect.setStyle(Paint.Style.FILL);

        int colorDragging = Color.BLACK;
        mPaintDragging.setColor(colorDragging);
        mPaintDragging.setAntiAlias(true);
        mPaintDragging.setStyle(Paint.Style.FILL);

        mRectF = new RectF(mCenterX - mCircleRadiusWatcher, mCenterY - mCircleRadiusWatcher
                , mCenterX + mCircleRadiusWatcher, mCenterY + mCircleRadiusWatcher);
    }

    private boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    /*获取星期几*/
    private String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case Calendar.SUNDAY:
                return "星期日";
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            default:
                return "";
        }
    }

    public void setDisplayTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        if (!get24HourMode(mContext)) {
            formatter = new SimpleDateFormat("hh:mm");
        }
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        SimpleDateFormat formatterBelow;
        String strBelow = "";
        //String strday = "";
        if (isZh(mContext)) {
            formatterBelow = new SimpleDateFormat("YYYY/MM/dd    EEE");
            Date curDateBelow = new Date(System.currentTimeMillis());//获取当前时间
            strBelow = formatterBelow.format(curDateBelow);
            //strday = getWeek();
            mDisplayCurrentTime = str;
            mDisplayCurrentTimeBelow = strBelow;
        } else {
            formatterBelow = new SimpleDateFormat("EEE , dd MMM", Locale.getDefault());
            Date curDateBelow = new Date(System.currentTimeMillis());//获取当前时间
            strBelow = formatterBelow.format(curDateBelow);
            mDisplayCurrentTime = str;
            mDisplayCurrentTimeBelow = strBelow;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 控件默认宽度（屏幕宽度）
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        int screenMeasureHeight = dm.heightPixels;
        int defaultViewWidth = screenMeasureHeight / 2;
        int width = getDimension(defaultViewWidth, widthMeasureSpec);
        int height = getDimension(width, heightMeasureSpec);

        // 非中文下不显示天气
        if (!isZh(mContext)){
            height = height + 30;
        }

            mViewWidth = width;
        mViewHeight = height;

        setMeasuredDimension(width, height);
    }

    /**
     * 取得尺寸
     *
     * @param defaultDimension 默认尺寸
     * @param measureSpec      measureSpec
     * @return 尺寸
     */
    private int getDimension(int defaultDimension, int measureSpec) {
        int result;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                result = MeasureSpec.getSize(measureSpec);
                break;
            case MeasureSpec.AT_MOST:
                //result = Math.min(defaultDimension, MeasureSpec.getSize(measureSpec));
                result = defaultDimension;
                break;
            default:
                result = defaultDimension;
                break;
        }
        return result;
    }

    public boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    /**
     * 更新时间
     */
    public void updateTime() {
       postInvalidate();
    }

    private int getYear(String year){
        int ye = Integer.parseInt(year);
        int start = 1900;
        if (ye < 1900) {
            return -1;
        }
        return (ye-start)%12;
    }
}
