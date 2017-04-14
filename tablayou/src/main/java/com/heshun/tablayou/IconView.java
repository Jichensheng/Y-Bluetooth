package com.heshun.tablayou;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * author：Jics
 * 2017/3/4 16:48
 */
public class IconView extends View{
	private Paint mPaint;
	private PointF point;
	private float total=200;
	private float deltaL =total;
	private RectF rectF;
	private RectF rectF2;
	private Canvas canvas;
	float radius=100;
	public IconView(Context context) {
		super(context);
		init();
	}

	public IconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	private  void init(){
		rectF=new RectF();
		rectF2=new RectF();
		point=new PointF(500,500);
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(3);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.rgb(0,128,128));
		mPaint.setDither(true);
		ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(this,"change",1f,0f);
		objectAnimator.setDuration(1000).start();


	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas=canvas;

		rectF.left=point.x-radius- deltaL;
		rectF.top=point.y;
		rectF.right=point.x+radius-deltaL;
		rectF.bottom=point.y+radius*2;
		canvas.drawArc(rectF,-90,-180,false,mPaint);

		rectF.left=point.x-radius+ deltaL;
		rectF.top=point.y;
		rectF.right=point.x+radius+deltaL;
		rectF.bottom=point.y+radius*2;
		canvas.drawArc(rectF,-90,180,false,mPaint);

		canvas.drawLine(point.x- deltaL,point.y,point.x,point.y,mPaint);
		canvas.drawLine(point.x+ deltaL,point.y,point.x,point.y,mPaint);
		canvas.drawLine(point.x- deltaL,point.y+radius*2,point.x,point.y+radius*2,mPaint);
		canvas.drawLine(point.x+ deltaL,point.y+radius*2,point.x,point.y+radius*2,mPaint);

		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(point.x-0.4f*radius,point.y+0.6f*radius,point.x+0.4f*radius,point.y+1.4f*radius,mPaint);

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(5);
		canvas.drawArc(new RectF(point.x-radius,point.y,point.x+radius,point.y+2*radius),-90,180,false,mPaint);

		mPaint.setStrokeWidth(3);
		mPaint.setStyle(Paint.Style.STROKE);


	}
	public void setChange(float current){
		deltaL=total*current;
		postInvalidate();
	}
}
