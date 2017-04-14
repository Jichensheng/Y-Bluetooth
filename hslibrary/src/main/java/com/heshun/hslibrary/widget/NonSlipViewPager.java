package com.heshun.hslibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 禁用手势滑动的ViewPager
 * 
 * @author huangxz
 *
 */
public class NonSlipViewPager extends ViewPager {

	public NonSlipViewPager(Context context) {
		super(context);
	}

	public NonSlipViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}

}
