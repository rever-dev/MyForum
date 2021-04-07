package com.rever.myforum.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;


public class BitmapTool {

    public static Bitmap getCirleBitmap(Bitmap bmp) {
        //获取bmp的宽高 小的一个做为圆的直径r
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int r = Math.min(w, h);
        //创建一个paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap newBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        BitmapShader bitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(bitmapShader);
        canvas.drawCircle(r / 2, r / 2, r / 2, paint);

        return newBitmap;
    }
}
