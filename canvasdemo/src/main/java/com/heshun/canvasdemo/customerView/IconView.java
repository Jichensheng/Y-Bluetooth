package com.heshun.canvasdemo.customerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * author：Jics
 * 2017/2/27 15:41
 */
public class IconView extends View {
	private Paint mPaint;
	private float blockWidth = 100;
	private float blockWidthA = 100;
	private float blockWidthB = 100;
	private float blockWidthC = 100;
	private float searchRadius = 40;
	private float gapwidth = 20;
	private PointF startPoint;
	private AnimatorSet set;
	private PointF secondPoint, thirdPoint, searchCenter, bar1, bar2;

	public IconView(Context context) {
		super(context);
		initStuff();
	}

	public IconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStuff();

	}


	public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initStuff();
	}


	private void initStuff() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(10);
		mPaint.setColor(Color.rgb(246, 105, 68));

		startPoint = new PointF(100, 100);
		secondPoint = new PointF(startPoint.x + gapwidth + blockWidth, startPoint.y);
		thirdPoint = new PointF(startPoint.x, startPoint.y + gapwidth + blockWidth);
		searchCenter = new PointF(startPoint.x + gapwidth + searchRadius + blockWidth / 2, startPoint.y + gapwidth + searchRadius + blockWidth / 2);

		ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "widthA", 1f, 0.6f, 1f);
		animator1.setDuration(1000).setStartDelay(300);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "widthB", 1f, 0.6f, 1f);
		animator2.setDuration(1000).setStartDelay(200);
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(this, "widthC", 1f, 0.6f, 1f);
		animator3.setDuration(1000);
		ObjectAnimator animator4 = ObjectAnimator.ofFloat(this, "radius", 0f, 1f);
		animator4.setDuration(1100);
		set = new AnimatorSet();
		set.playTogether(animator3, animator4, animator2, animator1);
		set.start();

	}

	public void setWidthA(float current) {
		blockWidthA = 100 * current;
		postInvalidate();
	}

	public void setWidthB(float current) {
		blockWidthB = 100 * current;
		postInvalidate();
	}

	public void setWidthC(float current) {
		blockWidthC = 100 * current;
		postInvalidate();
	}

	public void setRadius(float current) {
		searchCenter = calculatPoint(new PointF(210, 210), searchRadius*0.5f, 360 * (1-current));
		postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (set != null&&!set.isRunning()) {
			set.start();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(startPoint.x - blockWidthA / 2, startPoint.y - blockWidthA / 2, startPoint.x + blockWidthA / 2, startPoint.y + blockWidthA / 2, mPaint);
		canvas.drawRect(secondPoint.x - blockWidthB / 2, secondPoint.y - blockWidthB / 2, secondPoint.x + blockWidthB / 2, secondPoint.y + blockWidthB / 2, mPaint);
		canvas.drawRect(thirdPoint.x - blockWidthC / 2, thirdPoint.y - blockWidthC / 2, thirdPoint.x + blockWidthC / 2, thirdPoint.y + blockWidthC / 2, mPaint);
		canvas.drawCircle(searchCenter.x, searchCenter.y, searchRadius, mPaint);
		bar1 = calculatPoint(searchCenter, searchRadius, 45);
		bar2 = calculatPoint(searchCenter, 2 * searchRadius, 45);
		canvas.drawLine(bar1.x, bar1.y, bar2.x, bar2.y, mPaint);
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
