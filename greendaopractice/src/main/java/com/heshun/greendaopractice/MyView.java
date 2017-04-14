package com.heshun.greendaopractice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * 孙群
 * http://blog.csdn.net/iispring
 */
public class MyView extends View {

    Paint paint;
    float cellSize = 0;
    float cellHorizontalOffset = 0;
    float cellVerticalOffset = 0;
    float circleRadius = 0;
    float rectSize = 0;
    int circleColor = 0xffffcc44;//黄色
    int rectColor = 0xff66aaff;//蓝色
    float textSize = getResources().getDimensionPixelSize(R.dimen.textSize);

    private static final Xfermode[] sModes = {
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC),
            new PorterDuffXfermode(PorterDuff.Mode.DST),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.XOR),
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    };

    private static final String[] sLabels = {
            "Clear", "Src", "Dst", "SrcOver",
            "DstOver", "SrcIn", "DstIn", "SrcOut",
            "DstOut", "SrcATop", "DstATop", "Xor",
            "Darken", "Lighten", "Multiply", "Screen"
    };

    public MyView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if(Build.VERSION.SDK_INT >= 11){
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置背景色
        canvas.drawARGB(255, 139, 197, 186);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        for(int row = 0; row < 4; row++){
            for(int column = 0; column < 4; column++){
                canvas.save();
                int layer = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
                paint.setXfermode(null);
                int index = row * 4 + column;
                float translateX = (cellSize + cellHorizontalOffset) * column;
                float translateY = (cellSize + cellVerticalOffset) * row;
                canvas.translate(translateX, translateY);
                //画文字
                String text = sLabels[index];
                paint.setColor(Color.BLACK);
                float textXOffset = cellSize / 2;
                float textYOffset = textSize + (cellVerticalOffset - textSize) / 2;
                canvas.drawText(text, textXOffset, textYOffset, paint);
                canvas.translate(0, cellVerticalOffset);
                //画边框
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(0xff000000);
                canvas.drawRect(2, 2, cellSize - 2, cellSize - 2, paint);
                paint.setStyle(Paint.Style.FILL);
                //画圆
                paint.setColor(circleColor);
                float left = circleRadius + 3;
                float top = circleRadius + 3;
                canvas.drawCircle(left, top, circleRadius, paint);
                paint.setXfermode(sModes[index]);
                //画矩形
                paint.setColor(rectColor);
                float rectRight = circleRadius + rectSize;
                float rectBottom = circleRadius + rectSize;
                canvas.drawRect(left, top, rectRight, rectBottom, paint);
                paint.setXfermode(null);
                //canvas.restore();
                canvas.restoreToCount(layer);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = w / 4.5f;
        cellHorizontalOffset = cellSize / 6;
        cellVerticalOffset = cellSize * 0.426f;
        circleRadius = cellSize / 3;
        rectSize = cellSize * 0.6f;
    }
}