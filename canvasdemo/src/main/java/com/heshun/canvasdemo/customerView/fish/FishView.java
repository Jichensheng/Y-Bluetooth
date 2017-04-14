package com.heshun.canvasdemo.customerView.fish;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

/**
 * author：Jics
 * 2017/2/22 12:27
 */
public class FishView extends View {
    public int mScreenWidth;
    public int mScreenHeight;
    private Context mContext;

    private static final float HEAD_RADIUS = 25;
    protected static final float BODY_LENGHT = HEAD_RADIUS * 3.2f;
    private static final int HEAD_ALPHA = 230;
    private static final int BODY_ALPHA = 220;
    private static final int OTHER_ALPHA = 160;
    private static final int FINS_ALPHA = 100;
    private static final int FINS_LEFT = 1;//左鱼鳍
    private static final int FINS_RIGHT = -1;
    private static final float FINS_LENGTH = HEAD_RADIUS * 1.3f;
    public static final float TOTAL_LENGTH = 6.202f * HEAD_RADIUS;

    //控制区域
    private int time = 0;//全局控制标志
    private float fatherAngle = new Random().nextFloat()*360;
    protected ObjectAnimator finsAnimator;
    private float waveFrequence=1;
    //鱼头点
    private PointF headPoint;
    //转弯更自然的中心点
    private PointF middlePoint;
    private float finsAngle=0;
    private Paint mPaint;
    private Paint bodyPaint;
    private Path mPath;

    public FishView(Context context) {
        super(context);
        this.mContext = context;
        initStuff();
    }

    public FishView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initStuff();
    }

    public FishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initStuff();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) ((TOTAL_LENGTH + BODY_LENGHT / 2) * 2 + 0.5), (int) ((TOTAL_LENGTH + BODY_LENGHT / 2) * 2 + 0.5));
    }

    private void initStuff() {
        getScreenParams();
        //路径
        mPath = new Path();
        //画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);//防抖
        mPaint.setColor(Color.argb(OTHER_ALPHA, 244, 92, 71));
        //身体画笔
        bodyPaint = new Paint();
        bodyPaint.setAntiAlias(true);
        bodyPaint.setStyle(Paint.Style.FILL);
        bodyPaint.setDither(true);//防抖
        bodyPaint.setColor(Color.argb(OTHER_ALPHA + 5, 244, 92, 71));
        middlePoint = new PointF(TOTAL_LENGTH + BODY_LENGHT / 2, TOTAL_LENGTH + BODY_LENGHT / 2);

        //鱼鳍灵动动画
        finsAnimator = ObjectAnimator.ofFloat(this, "finsAngle", 0f, 1f,0f);
        finsAnimator.setRepeatMode(ValueAnimator.REVERSE);
        finsAnimator.setRepeatCount(new Random().nextInt(3));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(50);
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 设置身体主轴线方向角度
     *
     * @param fatherAngle
     */
    public void setFatherAngle(float fatherAngle) {
        this.fatherAngle = fatherAngle;
    }

    /**
     * 获取当前角度
     * @return
     */
    public float getFatherAngle() {
        return fatherAngle;
    }
    /**
     * 设置头的位置
     *
     * @param headPoint
     */
    public void setHeadPoint(PointF headPoint) {
        this.headPoint = headPoint;
    }

    public ObjectAnimator getFinsAnimator() {
        return finsAnimator;
    }

    public void setMiddlePoint(PointF middlePoint) {
        this.middlePoint = middlePoint;
    }

    public PointF getMiddlePoint() {
        return middlePoint;
    }

    public static float getTotalLength() {
        return TOTAL_LENGTH;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //生成一个半透明图层，否则与背景白色形成干扰,尺寸必须与view的大小一致否则鱼显示不全
        canvas.saveLayerAlpha(0, 0, (int) ((TOTAL_LENGTH + BODY_LENGHT / 2) * 2 + 0.5), (int) ((TOTAL_LENGTH + BODY_LENGHT / 2) * 2 + 0.5), 240, Canvas.ALL_SAVE_FLAG);
        makeBody(canvas, HEAD_RADIUS);
        mPath.reset();
        mPaint.setColor(Color.argb(HEAD_ALPHA, 244, 92, 71));
        mPaint.setColor(Color.argb(OTHER_ALPHA, 244, 92, 71));
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

    //画身子

    /**
     * 主方向是头到尾的方向跟X轴正方向的夹角（顺时针为正）
     * 前进方向和主方向相差180度
     * @param canvas
     * @param headRadius
     */
    private void makeBody(Canvas canvas, float headRadius) {
        float angle = fatherAngle + (float) Math.sin(Math.toRadians(time * 1.2*waveFrequence)) * 2;//中心轴线和X轴顺时针方向夹角
        headPoint = calculatPoint(middlePoint, BODY_LENGHT / 2, 180 - fatherAngle);
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint);
        //右鳍
        PointF pointFinsRight = calculatPoint(headPoint, headRadius * 0.9f, angle - 70);
        makeFins(canvas, pointFinsRight, FINS_RIGHT, angle);
        //左鳍
        PointF pointFinsLeft = calculatPoint(headPoint, headRadius * 0.9f, angle + 70);
        makeFins(canvas, pointFinsLeft, FINS_LEFT, angle);

        PointF endPoint = calculatPoint(headPoint, BODY_LENGHT, angle);
        //躯干1
        PointF mainPoint = new PointF(endPoint.x, endPoint.y);
        makeSegments(canvas, mainPoint, headRadius * 0.7f, 0.6f, angle);

        PointF point1, point2, point3, point4, contralLeft, contralRight;
        //point1和4的初始角度决定发髻线的高低值越大越低
        point1 = calculatPoint(headPoint, headRadius, 100 + angle);
        point2 = calculatPoint(endPoint, headRadius * 0.7f, 90 + angle);
        point3 = calculatPoint(endPoint, headRadius * 0.7f, angle - 90);
        point4 = calculatPoint(headPoint, headRadius, angle - 100);
        //决定胖瘦
        contralLeft = calculatPoint(headPoint, BODY_LENGHT * 0.56f, angle + 50);
        contralRight = calculatPoint(headPoint, BODY_LENGHT * 0.56f, angle - 50);
        mPath.reset();
        mPath.moveTo(point1.x, point1.y);
        mPath.quadTo(contralLeft.x, contralLeft.y, point2.x, point2.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.quadTo(contralRight.x, contralRight.y, point4.x, point4.y);
        mPath.lineTo(point1.x, point1.y);

        mPaint.setColor(Color.argb(BODY_ALPHA, 244, 92, 71));
        canvas.drawPath(mPath, mPaint);
        mPaint.setColor(Color.argb(OTHER_ALPHA, 244, 92, 71));
    }

    /**
     * 第二节节肢
     *
     * @param canvas
     * @param mainPoint
     * @param segmentRadius
     * @param MP            梯形上边下边长度比
     */
    private void makeSegments(Canvas canvas, PointF mainPoint, float segmentRadius, float MP, float fatherAngle) {
        float angle = fatherAngle + (float) Math.cos(Math.toRadians(time * 1.5*waveFrequence)) * 15;//中心轴线和X轴顺时针方向夹角
        //身长
        float segementLenght = segmentRadius * (MP + 1);
        PointF endPoint = calculatPoint(mainPoint, segementLenght, angle);

        PointF point1, point2, point3, point4;
        point1 = calculatPoint(mainPoint, segmentRadius, 90 + angle);
        point2 = calculatPoint(endPoint, segmentRadius * MP, 90 + angle);
        point3 = calculatPoint(endPoint, segmentRadius * MP, angle - 90);
        point4 = calculatPoint(mainPoint, segmentRadius, angle - 90);

        canvas.drawCircle(mainPoint.x, mainPoint.y, segmentRadius, mPaint);
        canvas.drawCircle(endPoint.x, endPoint.y, segmentRadius * MP, mPaint);
        mPath.reset();
        mPath.moveTo(point1.x, point1.y);
        mPath.lineTo(point2.x, point2.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.lineTo(point4.x, point4.y);
        canvas.drawPath(mPath, mPaint);

        //躯干2
        PointF mainPoint2 = new PointF(endPoint.x, endPoint.y);
        makeSegmentsLong(canvas, mainPoint2, segmentRadius * 0.6f, 0.4f, angle);
    }

    /**
     * 第三节节肢
     *
     * @param canvas
     * @param mainPoint
     * @param segmentRadius
     * @param MP            梯形上边下边长度比
     */
    private void makeSegmentsLong(Canvas canvas, PointF mainPoint, float segmentRadius, float MP, float fatherAngle) {
        float angle = fatherAngle + (float) Math.sin(Math.toRadians(time * 1.5*waveFrequence)) * 35;//中心轴线和X轴顺时针方向夹角
        //身长
        float segementLenght = segmentRadius * (MP + 2.7f);
        PointF endPoint = calculatPoint(mainPoint, segementLenght, angle);

        PointF point1, point2, point3, point4;
        point1 = calculatPoint(mainPoint, segmentRadius, 90 + angle);
        point2 = calculatPoint(endPoint, segmentRadius * MP, 90 + angle);
        point3 = calculatPoint(endPoint, segmentRadius * MP, angle - 90);
        point4 = calculatPoint(mainPoint, segmentRadius, angle - 90);

        makeTail(canvas, mainPoint, segementLenght, segmentRadius, angle);


        canvas.drawCircle(endPoint.x, endPoint.y, segmentRadius * MP, mPaint);
        mPath.reset();
        mPath.moveTo(point1.x, point1.y);
        mPath.lineTo(point2.x, point2.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.lineTo(point4.x, point4.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 鱼鳍
     *
     * @param canvas
     * @param startPoint
     * @param type
     */
    private void makeFins(Canvas canvas, PointF startPoint, int type, float fatherAngle) {
        float contralAngle = 65;//鱼鳍三角控制角度
        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        PointF endPoint = calculatPoint(startPoint, FINS_LENGTH, type == FINS_LEFT ? fatherAngle-finsAngle: fatherAngle+finsAngle);
        PointF contralPoint = calculatPoint(startPoint, FINS_LENGTH * 1.8f, type == FINS_LEFT ?
                fatherAngle + contralAngle-finsAngle : fatherAngle - contralAngle+finsAngle);
        mPath.quadTo(contralPoint.x, contralPoint.y, endPoint.x, endPoint.y);
        mPath.lineTo(startPoint.x, startPoint.y);
        mPaint.setColor(Color.argb(FINS_ALPHA, 244, 92, 71));
        canvas.drawPath(mPath, mPaint);
        mPaint.setColor(Color.argb(OTHER_ALPHA, 244, 92, 71));

    }

    /**
     * 鱼尾及鱼尾张合
     *
     * @param canvas
     * @param mainPoint
     * @param length
     * @param maxWidth
     */
    private void makeTail(Canvas canvas, PointF mainPoint, float length, float maxWidth, float angle) {
        //TODO 鱼尾宽度控制
        time += 15;
        float newWidth = (float) Math.abs(Math.sin(Math.toRadians(time * 1.7*waveFrequence)) * maxWidth + 15);
        if (time > 54000) {
            time = 0;
        }
        PointF endPoint = calculatPoint(mainPoint, length, angle);
        PointF endPoint2 = calculatPoint(mainPoint, length - 10, angle);
        PointF point1, point2, point3, point4;
        point1 = calculatPoint(endPoint, newWidth, 90 + angle);
        point2 = calculatPoint(endPoint, newWidth, angle - 90);
        point3 = calculatPoint(endPoint2, newWidth - 20, 90 + angle);
        point4 = calculatPoint(endPoint2, newWidth - 20, angle - 90);
        //内
        mPath.reset();
        mPath.moveTo(mainPoint.x, mainPoint.y);
        mPath.lineTo(point3.x, point3.y);
        mPath.lineTo(point4.x, point4.y);
        mPath.lineTo(mainPoint.x, mainPoint.y);
        canvas.drawPath(mPath, mPaint);
        //外
        mPath.reset();
        mPath.moveTo(mainPoint.x, mainPoint.y);
        mPath.lineTo(point1.x, point1.y);
        mPath.lineTo(point2.x, point2.y);
        mPath.lineTo(mainPoint.x, mainPoint.y);
        canvas.drawPath(mPath, mPaint);

    }

    private void setFinsAngle(float currentValue) {
        finsAngle=45*currentValue;
    }


    public void setWaveFrequence(float waveFrequence) {
        this.waveFrequence = waveFrequence;
    }

    /**
     * 起点长度角度计算终点
     *
     * @param startPoint
     * @param length
     * @param angle      顺时针角度
     * @return
     */
    private static PointF calculatPoint(PointF startPoint, float length, float angle) {
        float deltaX = (float) Math.cos(Math.toRadians(angle)) * length;
        float deltaY = (float) Math.sin(Math.toRadians(angle)) * length;
        return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
    }
}
