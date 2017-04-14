package com.heshun.hslibrary.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 解决android5.0瀑布流的card没间距
 * 
 * @author Jics
 *
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
	private int space;

	public SpacesItemDecoration(int space) {
		this.space = space;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.top = space;
		outRect.left = space;
		outRect.right = space;
		outRect.bottom = space;

		// Add top margin only for the first item to avoid double space between
		// items
		if (parent.getChildLayoutPosition(view) == 0) {
			outRect.top = space;
		} else {
			outRect.top = space;
		}
	}
}
