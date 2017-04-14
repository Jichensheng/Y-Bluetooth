package com.heshun.hslibrary.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heshun.hslibrary.R;

/**
 * Toolbar帮助类
 * 
 * @author huangxz
 */
public class ToolBarHelper {

	private Context mContext;

	private FrameLayout mContentView;

	private View mUserView;

	private Toolbar mToolBar;

	private LayoutInflater mInflater;

	private TextView tvTbTitle;
	private TextView tvCity;

	private ProgressBar pbLoc;

	private int tbHeight = 0;

	private static int[] ATTRS = { R.attr.windowActionBarOverlay, R.attr.actionBarSize };

	public ToolBarHelper(Context context, int layoutId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		initContentView();
		initUserView(layoutId);
		initToolBar();
	}

	private void initContentView() {

		mContentView = new FrameLayout(mContext);

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mContentView.setLayoutParams(params);

	}

	private void initToolBar() {
		View toolbar = mInflater.inflate(R.layout.toolbar, mContentView);

		mToolBar = (Toolbar) toolbar.findViewById(R.id.toolbar);

		tvTbTitle = (TextView) toolbar.findViewById(R.id.tb_title);

		tvCity = (TextView) toolbar.findViewById(R.id.tv_city);

		pbLoc = (ProgressBar) toolbar.findViewById(R.id.progress_loc);

		initLocation();

		mToolBar.setBackgroundColor(mContext.getResources().getColor(R.color.toolbar_bg));

		mToolBar.setTitle("");
	}

	private void initLocation() {
		tvCity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Intent i = new Intent(mContext, RegionSelectActivity.class);
//				mContext.startActivity(i);
			}
		});
	}
	@SuppressWarnings("ResourceType")
	private void initUserView(int id) {
		mUserView = mInflater.inflate(id, null);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(ATTRS);
		boolean overly = typedArray.getBoolean(0, false);
		tbHeight = (int) typedArray.getDimension(1,(int) mContext.getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
		typedArray.recycle();
		params.topMargin = overly ? 0 : tbHeight;
		mContentView.addView(mUserView, params);

	}

	public FrameLayout getContentView() {
		return mContentView;
	}

	public FrameLayout getContainer() {
		return mContentView;
	}

	public int getTbHeight() {
		return tbHeight;
	}

	public Toolbar getToolBar() {
		return mToolBar;
	}

	public void setTitle(String s) {
		tvTbTitle.setText(s);
	}

	public TextView getCityTv() {
		return tvCity;
	}

	public ProgressBar getProgressBar() {
		return pbLoc;
	}

	public void hideProgressBar() {
		pbLoc.setVisibility(View.GONE);
	}

	public void showProgressBar() {
		pbLoc.setVisibility(View.VISIBLE);

	}

	public void hideCity() {
		tvCity.setVisibility(View.GONE);
	}

	public void showCity() {
		tvCity.setVisibility(View.VISIBLE);

	}
}