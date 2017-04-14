package com.heshun.tablayou;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * author：Jics
 * 2017/3/6 09:24
 */
public class SelectView extends LinearLayout {
	public SelectView(Context context) {
		super(context);
		this.addView(View.inflate(context,R.layout.layout_se,null));
	}

	public SelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.addView(View.inflate(context,R.layout.layout_se,null));
	}

	public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.addView(View.inflate(context,R.layout.layout_se,null));
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}
}
