package com.heshun.canvasdemo.customerView.fish;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 1、点击某点生成一个食物并生成一片涟漪动画
 * 2、鱼到食物坐标 食物消失
 * 3、涟漪类型（单个涟漪散开、2个涟漪散开属性动画）直径和透明度会变化，初始直径随机值
 * Created by Jcs on 2017/2/25.
 */

public class FoodView extends View {
    public static final int STROKE_WIDTH = 8;
    public static final float DEFAULT_RADIUS = 150;

    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext;

    private Paint mPaint;
    private int alpha = 100;
    private Canvas canvas;

    private float x=0;
    private float y=0;
    private float radius=0;

    private OnFoodClickListener listener;

    public FoodView(Context context) {
        super(context);
        this.mContext = context;
    }

    public FoodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initStuff();
    }

    public FoodView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initStuff();
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
        mPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.canvas == null) {
            this.canvas=canvas;
        }
        //方便刷新透明度
        mPaint.setARGB(alpha, 0, 125, 251);

        canvas.drawCircle(x,y,radius,mPaint);
    }

    /**
     * 给鱼传递事件
     * @param listener
     */
    public void setListener(OnFoodClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=event.getX();
        y=event.getY();
        listener.onFoodClick(x,y);
        ObjectAnimator animator=ObjectAnimator.ofFloat(this,"radius",0f,1f).setDuration(1000);
        animator.start();
        return super.onTouchEvent(event);
    }

   /**
     * ObjectAnimators自动执行
     * @param currentValue
     */
    public void setRadius(float currentValue){
        alpha= (int) (100*(1-currentValue)/2);
        radius=DEFAULT_RADIUS*currentValue;
        postInvalidate();
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
    public interface OnFoodClickListener{
        void onFoodClick(float x, float y);
    }
}
