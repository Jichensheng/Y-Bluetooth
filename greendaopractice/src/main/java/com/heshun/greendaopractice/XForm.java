package com.heshun.greendaopractice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * author：Jics
 * 2017/3/8 16:02
 */
public class XForm extends View {
	private Paint paint;

	public XForm(Context context) {
		super(context);
		paint = new Paint();
	}

	public XForm(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();

	}

	public XForm(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//设置背景色
		canvas.drawARGB(255, 139, 197, 186);

		int canvasWidth = canvas.getWidth();
		int canvasHeight = canvas.getHeight();
		int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
			int r = canvasWidth / 3;
			//绘制黄色的圆形
			paint.setColor(0xFFFFCC44);
			canvas.drawCircle(r, r, r, paint);
			//绘制蓝色的矩形
			// 使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			paint.setColor(0xFF66AAFF);
			canvas.drawRect(r, r, r * 2.7f, r * 2.7f, paint);
			//最后将画笔去除Xfermode
			paint.setXfermode(null);
		canvas.restoreToCount(layerId);
	}
}
