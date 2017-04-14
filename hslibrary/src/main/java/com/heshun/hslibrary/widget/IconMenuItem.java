package com.heshun.hslibrary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heshun.hslibrary.R;


public class IconMenuItem extends LinearLayout {

	private String title, subTitle;

	private int iconSrc;

	private int textColor, subTitleColor;

	private int textSize, subTitleSize;

	private LayoutInflater mInflater;

	private MarqueeTextView tvSubTitle;

	private TextView tvTitle;

	public IconMenuItem(Context context) {
		super(context);
	}

	public IconMenuItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IconMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);

		mInflater = LayoutInflater.from(context);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconMenuItem, defStyleAttr, 0);

		title = a.getString(R.styleable.IconMenuItem_text_ico);

		subTitle = a.getString(R.styleable.IconMenuItem_subText_ico);

		iconSrc = a.getResourceId(R.styleable.IconMenuItem_imgsrc_ico, R.drawable.ic_default);

		textColor = a.getColor(R.styleable.IconMenuItem_textColor_ico, ContextCompat.getColor(context,R.color.gray));

		subTitleColor = a.getColor(R.styleable.IconMenuItem_subTextColor_ico,  ContextCompat.getColor(context,R.color.gray));

		textSize = a.getDimensionPixelSize(R.styleable.IconMenuItem_textSize_ico, 14);

		subTitleSize = a.getDimensionPixelSize(R.styleable.IconMenuItem_subTextSize_ico, 14);

		a.recycle();

		init();

	}

	@SuppressLint("NewApi")
	public IconMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	private void init() {

		View cv = mInflater.inflate(R.layout.lv_item_iconmenu, null);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		cv.setLayoutParams(params);

		tvTitle = (TextView) cv.findViewById(R.id.tv_icon_menu_title);
		tvSubTitle = (MarqueeTextView) cv.findViewById(R.id.tv_icon_menu_subtitle);

		ImageView ivIcon = (ImageView) cv.findViewById(R.id.iv_icon_menu);

		tvTitle.setText(title);
		tvTitle.setTextColor(textColor);
		tvTitle.setTextSize(px2sp(getContext(), textSize));
		//
		tvSubTitle.setText(subTitle);
		tvSubTitle.setTextColor(subTitleColor);
		tvSubTitle.setTextSize(px2sp(getContext(), subTitleSize));
		//
		ivIcon.setImageResource(iconSrc);

		addView(cv);

	}

	public void setSubTitle(String text) {
		tvSubTitle.setText(text);
	}

	private int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}
}
