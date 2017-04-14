package com.heshun.hslibrary.base;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.heshun.hslibrary.R;

import java.util.List;

/**
 *
 * @param <T>
 * @param <V>
 */
public abstract class BaseSlideFragment<T, V extends BaseSlideAdapter<T>> extends BaseRecyclerFragment<T, V> implements View.OnClickListener, BaseSlideAdapter.OnSlideOptionClickListener, BaseSlideAdapter.OnSelectAllChangedListener {
	protected CheckBox btnAll;
	protected ImageView btnDel;
	protected int mPageSize = 10;
	protected V slideAdapter;
	protected LinearLayout btnLayout;
	protected View listView;
	protected FrameLayout parentLayout;
	protected boolean firstMeasure = true;
	protected float btnLayouY;
	protected int btnHeight;
	protected RelativeLayout.LayoutParams listViewSelfParams;
	protected FrameLayout.LayoutParams listParams;

	/**
	 * 将recyclerView和删除按钮组装
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		@android.support.annotation.IdRes final
		int btnlayoutId = 123;
		parentLayout = new FrameLayout(getContext());
		parentLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		listView = super.onCreateView(inflater, container, savedInstanceState);
		//动态控制listview离下边的距离
		listViewSelfParams = (RelativeLayout.LayoutParams) listView.getLayoutParams();
		btnLayout = (LinearLayout) inflater.inflate(R.layout.del_button_layout, null);
		btnLayout.setId(btnlayoutId);
		btnLayout.post(new Runnable() {
			@Override
			public void run() {
				btnHeight = btnLayout.getHeight();
				FrameLayout.LayoutParams btnParams = (FrameLayout.LayoutParams) btnLayout.getLayoutParams();
				btnParams.setMargins(0, 0, 0, -btnHeight);
				btnLayout.setLayoutParams(btnParams);
			}
		});
		listParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		listParams.gravity = Gravity.TOP;

		FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		btnParams.gravity = Gravity.BOTTOM;

		parentLayout.addView(listView, listParams);
		parentLayout.addView(btnLayout, btnParams);
		btnAll = (CheckBox) btnLayout.findViewById(R.id.cb_all);
		btnDel = (ImageView) btnLayout.findViewById(R.id.imv_del);
		btnDel.setOnClickListener(this);
		btnAll.setOnClickListener(this);
		slideAdapter.setOnSlideOptionClickListener(this);//showButton
		slideAdapter.setOnSelectAllChangedListener(this);//isSelectAll
		parentLayout.setFocusable(true);
		parentLayout.setFocusableInTouchMode(true);
		parentLayout.setOnKeyListener(backListener);
		return parentLayout;
	}
	@Override
	protected V getAdapter() {
		slideAdapter=setAdapter();
		return slideAdapter;
	}

	public V get() {
		return slideAdapter;
	}
	abstract protected V setAdapter();
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if (id== R.id.cb_all) {
			if (btnAll.isChecked()) {
				slideAdapter.selectAll();
			} else {
				slideAdapter.deselectAll();
			}
		}else if(id==R.id.imv_del){
			List<T> mItemBeans = slideAdapter.getDeletList();
			if (mItemBeans.size() > 0) {
				deletItems(mItemBeans);
			}
		}
	}

	private View.OnKeyListener backListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View view, int i, KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
				if (i == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
					if (slideAdapter.mState==slideAdapter.SLIDE) {
						slideAdapter.closeItem();
						return true;
					}
					return false;
				}
			}
			return false;
		}
	};

	@Override
	public void showButton(boolean show) {
		if (firstMeasure) {
			btnLayouY = btnLayout.getY();
			firstMeasure = false;
		}
		if (show) {
			ObjectAnimator.ofFloat(btnLayout, "y", btnLayouY,
					btnLayouY - btnLayout.getHeight()).setDuration(200).start();

			setListBottomMargin(btnHeight);
		} else {
			btnAll.setChecked(false);
			ObjectAnimator.ofFloat(btnLayout, "y", btnLayouY,
					btnLayouY + btnLayout.getHeight()).setDuration(200).start();
			setListBottomMargin(0);
		}
	}

	protected void setListBottomMargin(int bottomMargin) {
		listParams.setMargins(0, 0, 0, bottomMargin);
		parentLayout.removeView(listView);
		parentLayout.addView(listView, listParams);
	}

	@Override
	public void isSelectAll(boolean isSelectAll) {
		btnAll.setChecked(isSelectAll);
	}

	@Override
	protected void doRefresh() {
		super.doRefresh();
		if (slideAdapter.mState == slideAdapter.SLIDE) {
			slideAdapter.closeItem();
		}
	}
	//删除操作逻辑
	abstract protected void deletItems(List<T> mItemBeans);

	//设置条目总数，用于判断是否选择全部
	abstract protected void setTotal(int itemTotal);
}
