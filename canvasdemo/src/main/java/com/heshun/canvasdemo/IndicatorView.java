package com.heshun.canvasdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * author：Jics
 * 2017/3/2 09:33
 */
public class IndicatorView extends View implements ViewPager.OnPageChangeListener{

	//指示器图标，这里是一个 drawable，包含两种状态，
	//选中和飞选中状态
	private Drawable mIndicator;

	//指示器图标的大小，根据图标的宽和高来确定，选取较大者
	private int mIndicatorSize ;

	//整个指示器控件的宽度
	private int mWidth ;

	/*图标加空格在家 padding 的宽度*/
	private int mContextWidth ;

	//指示器图标的个数，就是当前ViwPager 的 item 个数
	private int mCount ;
	/*每个指示器之间的间隔大小*/
	private int mMargin ;
	/*当前 view 的 item，主要作用，是用于判断当前指示器的选中情况*/
	private int mSelectItem ;
	/*指示器根据ViewPager 滑动的偏移量*/
	private float mOffset ;
	/*指示器是否实时刷新*/
	private boolean mSmooth ;
	/*因为ViewPager 的 pageChangeListener 被占用了，所以需要定义一个
	* 以便其他调用
	* */
	private ViewPager.OnPageChangeListener mPageChangeListener ;


	public IndicatorView(Context context) {
		super(context,null);
	}

	public IndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs,0);
	}

	public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//通过 TypedArray 获取自定义属性
		TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.IndicatorView);
		//获取自定义属性的个数
		int N = typedArray.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
				case R.styleable.IndicatorView_indicator_icon:
					//通过自定义属性拿到指示器
					mIndicator = typedArray.getDrawable(attr);
					break;
				case R.styleable.IndicatorView_indicator_margin:
					float defaultMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics());
					mMargin = (int) typedArray.getDimension(attr , defaultMargin);
					break ;
				case R.styleable.IndicatorView_indicator_smooth:
					mSmooth = typedArray.getBoolean(attr,false) ;
					break;
			}
		}
		//使用完成之后记得回收
		typedArray.recycle();
		initIndicator() ;
	}
	private void initIndicator() {
		//获取指示器的大小值。一般情况下是正方形的，也是时，你的美工手抖了一下，切出一个长方形来了，
		//不用怕，这里做了处理不会变形的
		mIndicatorSize = Math.max(mIndicator.getIntrinsicWidth(),mIndicator.getIntrinsicHeight()) ;
        /*设置指示器的边框*/
		mIndicator.setBounds(0,0,mIndicator.getIntrinsicWidth(),mIndicator.getIntrinsicWidth());
	}


	/**
	 * 测量View 的大小，这个方法我前面的 blog 讲了很多了，
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
	}

	/**
	 * 测量宽度，计算当前View 的宽度
	 * @param widthMeasureSpec
	 * @return
	 */
	private int measureWidth(int widthMeasureSpec){
		int mode = MeasureSpec.getMode(widthMeasureSpec) ;
		int size = MeasureSpec.getSize(widthMeasureSpec) ;
		int width ;
		int desired = getPaddingLeft() + getPaddingRight() + mIndicatorSize*mCount + mMargin*(mCount -1) ;
		mContextWidth = desired ;
		if(mode == MeasureSpec.EXACTLY){
			width = Math.max(desired, size)  ;
		}else {
			if(mode == MeasureSpec.AT_MOST){
				width = Math.min(desired,size) ;
			}else {
				width = desired ;
			}
		}
		mWidth = width ;
		return width ;
	}

	private int measureHeight(int heightMeasureSpec){
		int mode = MeasureSpec.getMode(heightMeasureSpec) ;
		int size = MeasureSpec.getSize(heightMeasureSpec) ;
		int height ;
		if(mode == MeasureSpec.EXACTLY){
			height = size ;
		}else {
			int desired = getPaddingTop() + getPaddingBottom() + mIndicatorSize ;
			if(mode == MeasureSpec.AT_MOST){
				height = Math.min(desired,size) ;
			}else {
				height = desired ;
			}
		}

		return height ;
	}


	/**
	 * 绘制指示器
	 * @param canvas
	 */
	@Override
	protected void onDraw(Canvas canvas) {

        /*
        * 首先得保存画布的当前状态，如果位置行这个方法
        * 等一下的 restore()将会失效，canvas 不知道恢复到什么状态
        * 所以这个 save、restore 都是成对出现的，这样就很好理解了。
        * */
		canvas.save() ;
        /*
        * 这里开始就是计算需要绘制的位置，
        * 如果不好理解，请按照我说的做，拿起
        * 附近的纸和笔，在纸上绘制一下，然后
        * 你就一目了然了，
        *
        * */
		int left = mWidth/2 - mContextWidth/2 +getPaddingLeft() ;
		canvas.translate(left,getPaddingTop());
		for(int i = 0 ; i < mCount ; i++){
            /*
            * 这里也需要解释一下，
            * 因为我们额 drawable 是一个selector 文件
            * 所以我们需要设置他的状态，也就是 state
            * 来获取相应的图片。
            * 这里是获取未选中的图片
            * */
			mIndicator.setState(EMPTY_STATE_SET) ;
            /*绘制 drawable*/
			mIndicator.draw(canvas);
            /*每绘制一个指示器，向右移动一次*/
			canvas.translate(mIndicatorSize+mMargin,0);
		}
        /*
        * 恢复画布的所有设置，也不是所有的啦，
        * 根据 google 说法，就是matrix/clip
        * 只能恢复到最后调用 save 方法的位置。
        * */
		canvas.restore();
        /*这里又开始计算绘制的位置了*/
		float leftDraw = (mIndicatorSize+mMargin)*(mSelectItem + mOffset);
        /*
        * 计算完了，又来了，平移，为什么要平移两次呢？
        * 也是为了好理解。
        * */
		canvas.translate(left,getPaddingTop());
		canvas.translate(leftDraw,0);
        /*
        * 把Drawable 的状态设为已选中状态
        * 这样获取到的Drawable 就是已选中
        * 的那张图片。
        * */
		mIndicator.setState(SELECTED_STATE_SET) ;
        /*这里又开始绘图了*/
		mIndicator.draw(canvas);

	}

	/**
	 * 此ViewPager 一定是先设置了Adapter，
	 * 并且Adapter 需要所有数据，后续还不能
	 * 修改数据
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager){
		if(viewPager == null){
			return;
		}
		PagerAdapter pagerAdapter = viewPager.getAdapter() ;
		if(pagerAdapter == null){
			throw new RuntimeException("请看使用说明");
		}
		mCount = pagerAdapter.getCount() ;
		//set过期了用add
		viewPager.addOnPageChangeListener(this);
		mSelectItem = viewPager.getCurrentItem() ;

		invalidate();
	}


	public void setOnPageChangeListener(ViewPager.OnPageChangeListener mPageChangeListener) {
		this.mPageChangeListener = mPageChangeListener;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		Log.v("zgy","========"+position+",===offset" + positionOffset) ;
		if (mSmooth){
			mSelectItem = position ;
			mOffset = positionOffset ;
			invalidate();
		}
		if(mPageChangeListener != null){
			mPageChangeListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
		}
	}




	@Override
	public void onPageSelected(int position) {
		mSelectItem = position ;
		invalidate();

		if(mPageChangeListener != null){
			mPageChangeListener.onPageSelected(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

		if(mPageChangeListener != null){
			mPageChangeListener.onPageScrollStateChanged(state);
		}
	}
}
