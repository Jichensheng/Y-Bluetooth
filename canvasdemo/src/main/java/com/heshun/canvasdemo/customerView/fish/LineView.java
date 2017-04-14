package com.heshun.canvasdemo.customerView.fish;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.List;


public class LineView extends View {
    public static final int STROKE_WIDTH = 2;

    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext;

    private Paint mPaint;    private Path mPath;
private List<PointF> resultList;


    public LineView(Context context) {
        super(context);
        this.mContext = context;
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initStuff();
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initStuff();
    }

    public void setResultList(List<PointF> resultList) {
        this.resultList = resultList;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mScreenWidth, mScreenHeight);
    }

    private void initStuff() {
        getScreenParams();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setARGB(0,255,0,0);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (resultList != null) {
            mPath.moveTo(resultList.get(0).x, resultList.get(0).y);
            mPath.cubicTo(resultList.get(2).x, resultList.get(2).y, resultList.get(3).x, resultList.get(3).y, resultList.get(1).x, resultList.get(1).y);
            canvas.drawPath(mPath, mPaint);

        }
    }
    /**
     * 获取屏幕宽高
     */
    public void getScreenParams() {
        WindowManager WM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(mDisplayMetrics);
        mScreenWidth = mDisplayMetrics.widthPixels;
        mScreenHeight = mDisplayMetrics.heightPixels;

    }
}
