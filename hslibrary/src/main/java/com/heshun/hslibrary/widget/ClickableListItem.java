package com.heshun.hslibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.util.DoubleClickUtil;


/**
 * 基础的带图片的列表菜单
 *
 * @author huangxz
 */
public class ClickableListItem extends LinearLayout {

	private boolean containIcon, clickable;

	private String title, subTitle;

	private int iconSrc;

	private int textColor, subTitleColor;

	private int textSize, subTitleSize;

	private LinearLayout container;

	TextView subTv;

	// px
	private int paddingContainer = dip2px(getContext(), 10);

	private int tvPadding = dip2px(getContext(), 5);

	int count = 0;
	long firClick = 0;
	long secClick = 0;
	;

	public ClickableListItem(Context context) {
		super(context);
		init();
	}

	public ClickableListItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClickableListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClickableListItem, defStyle, 0);

		containIcon = a.getBoolean(R.styleable.ClickableListItem_containIcon, false);

		clickable = a.getBoolean(R.styleable.ClickableListItem_clickable, true);

		title = a.getString(R.styleable.ClickableListItem_text);

		subTitle = a.getString(R.styleable.ClickableListItem_subText);

		iconSrc = a.getResourceId(R.styleable.ClickableListItem_imgsrc, R.drawable.ic_default);

		textColor = a.getColor(R.styleable.ClickableListItem_textColor, ContextCompat.getColor(context,R.color.gray));

		textSize = a.getDimensionPixelSize(R.styleable.ClickableListItem_textSize, 14);

		subTitleColor = a.getColor(R.styleable.ClickableListItem_subTextColor,
				context.getResources().getColor(R.color.gray));

		subTitleSize = a.getDimensionPixelSize(R.styleable.ClickableListItem_subTextSize, 14);

		a.recycle();

		init();

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN && DoubleClickUtil.isFastClick()) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}


	private void init() {

		container = new LinearLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		container.setLayoutParams(params);
		container.setGravity(Gravity.CENTER_VERTICAL);
		container.setPadding(paddingContainer, paddingContainer, paddingContainer, paddingContainer);
		container.setBackgroundResource(R.color.white);
		// 添加tv元素
		TextView tv = new TextView(getContext());
		LayoutParams tvParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvParam.weight = 1;
		tv.setLayoutParams(tvParam);

		tv.setPadding(tvPadding, tvPadding, tvPadding, tvPadding);
		tv.setText(title);
		tv.setTextColor(textColor);
		tv.setTextSize(px2sp(getContext(), textSize));
		tv.setGravity(Gravity.CENTER_VERTICAL);
		if (containIcon) {
			Drawable d = ContextCompat.getDrawable(getContext(), iconSrc);
			d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());// 必须设置图片大小，否则不显示
			tv.setCompoundDrawables(d, null, null, null);
		}
		//
		subTv = new TextView(getContext());
		LayoutParams stvParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		subTv.setLayoutParams(stvParam);

		subTv.setPadding(tvPadding, tvPadding, tvPadding, tvPadding);
		subTv.setText(subTitle);
		subTv.setTextColor(subTitleColor);
		subTv.setTextSize(px2sp(getContext(), subTitleSize));
		subTv.setGravity(Gravity.CENTER_VERTICAL);

		//限制输入内容
		subTv.setSingleLine(true);
		subTv.setMaxEms(15);
		subTv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));

		container.addView(tv);
		container.addView(subTv);
		// 添加img元素
		if (clickable) {
			ImageView iv = new ImageView(getContext());
			LayoutParams ivParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			iv.setLayoutParams(ivParam);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_pre_point_normal));
			container.addView(iv);
		}

		addView(container);
	}

	public void setSubTitle(String t) {
		subTv.setText(t);

	}

	public CharSequence getSubTitle() {
		return subTv.getText();
	}

	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}
}
