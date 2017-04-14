package com.heshun.greendaopractice;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * author：Jics
 * 2017/3/9 10:14
 */
public class RoundImageViewByXfermode extends ImageView {
	private Paint mPaint;
	private Xfermode xfermode=new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
	private Bitmap mMaskBitmap;

	private WeakReference<Bitmap> mWeakBitmap;

	/**
	 * 图片类型，圆角或圆形
	 */
	private int type;
	public static final int TYPE_CIRCLE=0;
	public static final int TYPE_ROUND=1;

	/**
	 * 默认圆角大小
	 */
	private static final int BODER_RADIUS_DEFAULT=10;
	/**
	 * 圆角大小
	 */
	private int mBorderRadius;

	public RoundImageViewByXfermode(Context context) {
		this(context,null);
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
	}

	public RoundImageViewByXfermode(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint=new Paint();
		mPaint.setAntiAlias(true);

		TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.RoundImageViewByXfermode);

		mBorderRadius=a.getDimensionPixelSize(R.styleable.RoundImageViewByXfermode_borderRadius,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,BODER_RADIUS_DEFAULT,getResources().getDisplayMetrics()));//第二个为默认值

		type=a.getInt(R.styleable.RoundImageViewByXfermode_type,TYPE_CIRCLE);//默认circle

		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(type==TYPE_CIRCLE){
			int width=Math.min(getMeasuredHeight(),getMeasuredWidth());
			setMeasuredDimension(width,width);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap bitmap=mWeakBitmap ==null?null:mWeakBitmap.get();
		if(null==bitmap||bitmap.isRecycled()){
			Drawable drawable =getDrawable();
			int dWidth=drawable.getIntrinsicWidth();
			int dHeight=drawable.getIntrinsicHeight();
			if(drawable!=null){
				bitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
				float scale =1.0f;
				Canvas drawCanvas=new Canvas(bitmap);
				if(type==TYPE_ROUND){
					scale = Math.max(getWidth() * 1.0f / dWidth, getHeight()
							* 1.0f / dHeight);
				}else {
					scale=getWidth()*1.0f / Math.min(dWidth,dHeight);
				}
				drawable.setBounds(0,0,(int)(scale*dWidth),(int)(scale*dHeight));
				drawable.draw(drawCanvas);
				if(mMaskBitmap==null||mMaskBitmap.isRecycled()){
					mMaskBitmap=getBitmap();
				}
				mPaint.reset();
				//对位图进行滤波处理
				mPaint.setFilterBitmap(false);
				mPaint.setXfermode(xfermode);

				drawCanvas.drawBitmap(mMaskBitmap,0,0,mPaint);
				mPaint.setXfermode(null);
				canvas.drawBitmap(bitmap,0,0,null);
				mWeakBitmap=new WeakReference<Bitmap>(bitmap);
			}
		}
		if(bitmap!=null){
			mPaint.setXfermode(null);
			canvas.drawBitmap(bitmap,0.0f,0.0f,mPaint);
			return;
		}
	}
	public Bitmap getBitmap(){
		Bitmap bitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bitmap);
		Paint paint =new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		if(type==TYPE_ROUND){
			canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),
					mBorderRadius, mBorderRadius, paint);
		}else {
			canvas.drawCircle(getWidth()/2,getWidth()/2,getWidth()/2,paint);

		}
		return bitmap;

	}

	@Override
	public void invalidate() {
		mWeakBitmap=null;
		if(mMaskBitmap!=null){
			mMaskBitmap.recycle();
			mMaskBitmap=null;
		}
		super.invalidate();
	}
}
