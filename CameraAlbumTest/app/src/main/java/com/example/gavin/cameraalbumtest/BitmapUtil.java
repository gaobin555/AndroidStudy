package com.example.gavin.cameraalbumtest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapUtil {
    /**
     * 添加时间水印
     * @param bitmap
     * @return mewBitmap
     */
    public static Bitmap AddTimeWatermark(Bitmap bitmap) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        // draw bitmap to new bitmap
        canvas.drawBitmap(bitmap,0,0,null);
        // get system time
        Paint paint = new Paint();
        String format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss EEEE").format(new Date());
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        // Watermark position coordinates
        canvas.drawText(format, (bitmapWidth * 1) / 10,(bitmapHeight*14)/15, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return newBitmap;
    }

    public static byte[] addTimeWatermarkToJpeg(byte[] jpeg) {
        LogHelper.d(TAG, "[addTimeWatermarkToJpeg] jpeg = " + jpeg);
        ExifInterface exif = getExif(jpeg);
        int jpegWidth = getJpegWidth(exif);
        Size exifSize = CameraUtil.getSizeFromExif(jpegData);
        Bitmap bitmap = decodeBitmapFromJpeg(jpeg, jpegWidth);

        Bitmap newBitmap = addTimeWatermarkToBitmap(bitmap);

        return getBytesByBitmap(newBitmap);
    }

    /**
     * getBytesByBitmap
     * @author      gaob@x-thinks.com
     * @version     1.0, 1/5/2019
     * @param bitmap
     * */
    private static byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        return buffer.array();
    }
}
