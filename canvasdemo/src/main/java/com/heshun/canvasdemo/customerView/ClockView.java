package com.heshun.canvasdemo.customerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * author：Jics
 * 2017/2/28 16:11
 */
public class ClockView extends View {
	private Paint mPaint;
	private PointF center=new PointF(540,500);
	private static final  float POINT_RADIUS =10;
	private static final  float RADIUS =400;
	private static final  float LONG_Z =40;
	private static final  float SHORT_Z =20;

	public ClockView(Context context) {
		super(context);
		init();
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(center.x,center.y,POINT_RADIUS,mPaint);
		mPaint.setStyle(Paint.Style.FILL);
		Rect rect=new Rect();
		for (int i = 0; i <= 360; i += 6) {
			canvas.save();
			canvas.rotate(i, center.x, center.y);
			canvas.drawLine(center.x, center.y-RADIUS, center.x, i % 30 == 0 ? center.y-RADIUS+LONG_Z : center.y-RADIUS+SHORT_Z, mPaint);
			if (i != 0 && i % 30 == 0) {
				String text=""+i/30;
				mPaint.getTextBounds(text,0,text.length(),rect);
				canvas.drawText(text,center.x-rect.width()/2,center.y-RADIUS+LONG_Z+rect.height()+10,mPaint);
			}
			canvas.restore();
		}
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setStrokeWidth(1);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setTextSize(30);

	}

}
