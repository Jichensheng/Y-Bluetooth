package com.heshun.canvasdemo;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * author：Jics
 * 2017/3/1 10:37
 */
public class PgAdapter extends PagerAdapter {
	private  List<View> viewList;

	public PgAdapter(List<View> viewList ){
		this.viewList=viewList;
	}
	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==object;
	}

	/**
	 * 必须重写否则报错
	 * java.lang.UnsupportedOperationException: Required method destroyItem was not overridden
	 * @param container
	 * @param position
	 * @return
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		Log.e("***********", "instantiateItem: "+position );
		return viewList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}
}
