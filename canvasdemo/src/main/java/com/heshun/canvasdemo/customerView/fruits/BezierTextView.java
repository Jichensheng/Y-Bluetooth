package com.heshun.canvasdemo.customerView.fruits;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.heshun.canvasdemo.R;

public class BezierTextView extends View {

	private String text;
	private Paint textPaint;
	//贝塞尔常量
	private static final float BEZIER_NUM = 0.015f;
	//动画周期
	private static final int TOTAL_TIME = 1200;
	//每帧间隔时间
	private static final int INTERVAL = 20;
	private int textSize = 36;
	private int initTextY = textSize + 3;
	private int textWidth;
	/**
	 * 最大的回弹长度
	 */
	private float maxRebounce = 60;
	/**
	 * 阻尼振动的控制时间
	 */
	private float goingTime = 0;
	private Path mPath;
	private Point startPoint;
	private Point endPoint;
	// 贝塞尔控制点
	private Point assistPoint;


	public BezierTextView(Context context) {
		this(context, null);
	}

	public BezierTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BezierTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.BezierTextView);
		if (attrArray.getString(R.styleable.BezierTextView_bezier_text) != null) {
			text = attrArray.getString(R.styleable.BezierTextView_bezier_text);
		} else
			text = "新鲜每一天";
		maxRebounce = attrArray.getInt(R.styleable.BezierTextView_max_rebounce, 60);
		init();
	}

	private void init() {
		mPath = new Path();
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setDither(true);
		textPaint.setStrokeWidth(2);
		textPaint.setColor(Color.rgb(140, 140, 140));
		textPaint.setTextSize(textSize);
		textWidth = (int) textPaint.measureText(text);

		//初始化路径位置
		startPoint = new Point(0, initTextY);
		endPoint = new Point(textWidth, initTextY);
		assistPoint = new Point(textWidth / 2, initTextY);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(INTERVAL);
						goingTime += BEZIER_NUM;
						assistPoint.y = (int) (initTextY + waweLen(goingTime));
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 重置路径
		mPath.reset();
		mPath.moveTo(startPoint.x, startPoint.y);
		mPath.quadTo(assistPoint.x, assistPoint.y, endPoint.x, endPoint.y);
		canvas.drawTextOnPath(text, mPath, 0, 0, textPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(textWidth, textSize + 20);
	}

	public int getTextSize() {
		return textSize;
	}

	public int getTextWidth() {
		return textWidth;
	}

	public void setText(String text) {
		this.text = text;
	}

	//图像动画与文字动画周期同步的关键步骤
	public void resetGoingTime() {
		goingTime = 0;
	}

	private float waweLen(float time) {
		float wl = (float) ((1 - Math.exp(-5 * (time + 0.052)) * Math.cos(30 * (time + 0.052))) - 1) * maxRebounce;
		return wl;
	}
}
