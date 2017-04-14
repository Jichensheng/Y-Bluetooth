package com.heshun.hslibrary.base;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author：Jics
 * 2016/8/29 11:01
 */
public abstract class BaseSlideAdapter<T> extends BaseRecyclerViewAdapter<T> {
	public static final int NORMAL = 1000;
	public static final int SLIDE = 2000;
	public int mState = NORMAL;
	public List<T> mItemBeans;
	//checkBox选中的坐标集合
	protected List<Integer> checkPositionlist = new ArrayList<>();
	//选中的item的bean集合
//	protected List<T> ids = new ArrayList<>();
	//item资源ID
	protected int itemLayoutId;
	@android.support.annotation.IdRes
	protected int cbxId = 888;

	@android.support.annotation.IdRes
	protected int itemViewId = 555;
	//是否全选的标志，给初始化checkbox用
	protected boolean isSelectAll;
	protected int itemTotal = -1;

	public BaseSlideAdapter(Context context, int itemLayoutId) {
		super(context);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.itemLayoutId = itemLayoutId;
	}

	@Override
	public void setList(List<T> list) {
		this.mItemBeans = list;
		notifyDataSetChanged();
	}

	/**
	 * 返回所选的item对应的bean集合
	 *
	 * @return
	 */
	public List<T> getDeletList() {
		List<T> deletList = new ArrayList<>();
		for (Integer integer : checkPositionlist) {
			deletList.add(mItemBeans.get(integer));
		}
		return deletList;
	}


	public void openItem() {
		mState = SLIDE;
		onSlideOptionClickListener.showButton(true);
		onButtonShowListener.isShow(true);
		notifyDataSetChanged();
	}

	public void closeItem() {
		isSelectAll = false;
		mState = NORMAL;
//		ids = new ArrayList<>();
		onSlideOptionClickListener.showButton(false);
		onButtonShowListener.isShow(false);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mItemBeans == null ? 0 : mItemBeans.size();
	}


	//控制底部按钮的显示与否(Fragment用)
	public interface OnSlideOptionClickListener {
		void showButton(boolean show);
	}

	private OnSlideOptionClickListener onSlideOptionClickListener;

	public void setOnSlideOptionClickListener(OnSlideOptionClickListener onItemClickListener) {
		this.onSlideOptionClickListener = onItemClickListener;
	}

	//item展开时回调（toolbar上的button状态）
	public interface OnButtonShowListener {
		void isShow(boolean isShow);
	}

	protected OnButtonShowListener onButtonShowListener;

	public void setOnButtonShowListener(OnButtonShowListener onButtonShowListener) {
		this.onButtonShowListener = onButtonShowListener;
	}

	//是否选择了全部item的监听
	public interface OnSelectAllChangedListener {
		void isSelectAll(boolean isSelectAll);
	}

	protected OnSelectAllChangedListener onSelectAllChangedListener;

	public void setOnSelectAllChangedListener(OnSelectAllChangedListener onSelectAllChangedListener) {
		this.onSelectAllChangedListener = onSelectAllChangedListener;
	}


	public void selectAll() {
		isSelectAll = true;
		checkPositionlist.removeAll(checkPositionlist);
		for (int i = 0; i < getItemCount(); i++) {
			checkPositionlist.add(i);
		}
		notifyDataSetChanged();
	}

	/**
	 * 传入获取的item的总数用来判断是否全选
	 *
	 * @param itemTotal
	 */
	public void setItemTotal(int itemTotal) {
		this.itemTotal = itemTotal;
	}

	//全不选
	public void deselectAll() {
		isSelectAll = false;
		checkPositionlist.removeAll(checkPositionlist);
		notifyDataSetChanged();
	}

	/**
	 * 给item添加checkBox
	 *
	 * @param parent
	 * @return
	 */
	protected View createItemView(ViewGroup parent) {
		View itemView = mInflater.inflate(itemLayoutId, parent, false);
		itemView.setId(itemViewId);
		LinearLayout linearLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(layoutParams);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		CheckBox checkBox = new CheckBox(mContext);
		checkBox.setClickable(false);
		checkBox.setEnabled(false);
		checkBox.setVisibility(View.GONE);
		checkBox.setFocusable(false);
		checkBox.setButtonDrawable(R.drawable.selector_checkbox);
		checkBox.setId(cbxId);
		LinearLayout.LayoutParams cbxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		cbxParams.gravity = Gravity.CENTER_VERTICAL;
		cbxParams.leftMargin = UiUtil.dip2px(mContext, 10);
		linearLayout.addView(checkBox, cbxParams);

		LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

		linearLayout.addView(itemView, itemParams);
		linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (mState == NORMAL) {
					openItem();
				}
				return true;
			}
		});
		return linearLayout;
	}

	protected void showCheckboxAnimation(final View itemView) {
		ValueAnimator valueAnimator = new ValueAnimator();
		valueAnimator.setIntValues(0, 1);
		valueAnimator.setDuration(300);
		final CheckBox checkBox = (CheckBox) itemView.findViewById(cbxId);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				float fraction = valueAnimator.getAnimatedFraction();
				checkBox.setScaleX(fraction);
				checkBox.setScaleY(fraction);
				checkBox.setAlpha(fraction * 255);
			}
		});
		valueAnimator.start();
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		//加上标志防止乱显示
		((BaseSlideViewHolder) holder).mCheckBox.setTag(position);
		if (checkPositionlist != null) {
			((BaseSlideViewHolder) holder).mCheckBox.setChecked((checkPositionlist.contains(position)));
		} else {
			((BaseSlideViewHolder) holder).mCheckBox.setChecked(false);
		}
		((BaseSlideViewHolder) holder).bind(mItemBeans.get(position));
	}

	abstract public class BaseSlideViewHolder extends BaseViewHolder implements View.OnClickListener {
		private CheckBox mCheckBox;
		protected T mItemBean;

		public BaseSlideViewHolder(View itemView) {
			super(itemView);
			mCheckBox = (CheckBox) itemView.findViewById(cbxId);
			itemView.setOnClickListener(this);
		}

		public void bind(T itemBean) {
			mItemBean = itemBean;
			switch (mState) {
				case NORMAL:
					mCheckBox.setVisibility(View.GONE);
					checkPositionlist.removeAll(checkPositionlist);
					break;

				case SLIDE:
					if (isSelectAll) {
						Integer indexTag = (Integer) mCheckBox.getTag();
						if (!checkPositionlist.contains(indexTag)) {
							mCheckBox.setChecked(true);
							checkPositionlist.add(indexTag);
						}
					}
					showCheckboxAnimation(itemView);
					mCheckBox.setVisibility(View.VISIBLE);
					break;
			}
			bindData(itemBean);
		}

		@Override
		public void onClick(View v) {
			if (mState == SLIDE) {
				if (mCheckBox.isChecked()) {
					if (isSelectAll) {
						isSelectAll = false;
						onSelectAllChangedListener.isSelectAll(false);//
					}
					checkPositionlist.remove((Integer) mCheckBox.getTag());
					mCheckBox.setChecked(false);
				} else {
					checkPositionlist.add((Integer) mCheckBox.getTag());
					if (itemTotal != -1) {
						if (checkPositionlist.size() == itemTotal) {
							isSelectAll = true;
							onSelectAllChangedListener.isSelectAll(true);
						}
					}
					mCheckBox.setChecked(true);
				}
			} else {
				onItemClick();
			}
		}

		abstract protected void bindData(T itemBean);

		abstract protected void onItemClick();

	}
}
