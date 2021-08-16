/* *
   * Copyright (C) 2018 X-Thinks Tech. Co., Ltd, LLC - All Rights Reserved.
   *
   * Confidential and Proprietary.
   * Unauthorized copying of this file, via any medium is strictly prohibited.
   * */

package com.android.xthink.ink.launcherink.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.xthink.ink.launcherink.common.utils.CommonUtils;
import com.android.xthink.ink.launcherink.common.utils.DensityUtil;

/**
 * custom corner imageView
 *
 * @author zhangzhenqiu@X-Thinks.com
 * @version 1.0, 2017/3/23
 */
public class CPYImageView extends ImageView {
    private Paint mPressPaint;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mShapeType;

    public CPYImageView(Context context) {
        this(context, null);
    }

    public CPYImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CPYImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init the various
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        // get the attrs of widget;
        if (attrs != null) {
            mRadius = DensityUtil.dipToPx(context, 4);
            mShapeType = 1;
        }

        // init paint
        mPressPaint = new Paint();
        mPressPaint.setAntiAlias(true);
        mPressPaint.setStyle(Paint.Style.FILL);
        mPressPaint.setAlpha(0);
        mPressPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        setClickable(true);
        setDrawingCacheEnabled(true);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // get the drawable of current widget
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        // the wide and height is get from ）
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        // get bitmap AnimationDrawable
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = CommonUtils.drawableToBitmap(drawable);
        }
        drawDrawable(canvas, bitmap);
    }

    /**
     * @param canvas
     * @param bitmap
     */
    private void drawDrawable(Canvas canvas, Bitmap bitmap) {
        // init paint
        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        int saveFlags = Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
        canvas.saveLayer(0, 0, mWidth, mHeight, null, saveFlags);
        if (mShapeType == 0) {
            //draw a circle
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, paint);
        } else {
            // draw a corner rectangle
            RectF rectf = new RectF(0, 0, getWidth(), getHeight());
            canvas.drawRoundRect(rectf, mRadius, mRadius, paint);
        }
        paint.setXfermode(xfermode);
        // get the scaling factor
        float scaleWidth = ((float) getWidth()) / bitmap.getWidth();
        float scaleHeight = ((float) getHeight()) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // scale the bitmap and draw it 
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }
}
