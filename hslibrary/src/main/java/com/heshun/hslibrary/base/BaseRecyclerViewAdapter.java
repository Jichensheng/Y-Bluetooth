package com.heshun.hslibrary.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.widget.SwipeRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 适配器抽象类
 * 
 * @author huangxz
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

	/**
	 * 数据项
	 */
	public static final int TYPE_ITEM = 2;

	/**
	 * 添加的布局
	 */
	public static final int TYPE_FOOTERVIEW = 3;

	protected boolean hasFootView = true;

	protected Context mContext;

	protected LayoutInflater mInflater = null;

	/**
	 * 列表的itemClickLister
	 */
	protected OnItemClickListener mOnItemClickListener = null;

	protected List<T> mList = new ArrayList<T>();

	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	protected DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(false)
			.showImageForEmptyUri(R.drawable.list_img_default)
			.showImageOnFail(R.drawable.list_img_default)
			.showImageOnLoading(R.drawable.list_img_default)
			.cacheOnDisk(true)
			.build();

	protected int currentState = SwipeRefreshGridView.STATE_AIDL;

	/**
	 * 设置当前的列表状态 ，初始、加载更多、加载完成、刷新
	 *
	 * @param state
	 */
	public void setCurrentDataState(int state) {
		currentState = state;
	}

	public BaseRecyclerViewAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	public void removeAtIndex(int index) {
		mList.remove(index);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		int base = 0;
		if (hasFootView)
			base = 1;
		if (mList != null && mList.size() > 0)
			return mList.size() + base;
		return base;
	}

	public T getItem(int index) {
		if (null != mList)
			return mList.get(index);
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		if (!hasFootView) {
			return TYPE_ITEM;
		}
		if (position + 1 == getItemCount()) {// 加载到最底部item，显示添加的布局，可以是FooterView或者是全部加载完成的提示View
			return TYPE_FOOTERVIEW;
		} else {// 显示列表项数据
			return TYPE_ITEM;
		}
	}

	/**
	 * itemclick 回调
	 */
	public static interface OnItemClickListener {
		public void click(int position);
	}

	/**
	 * footerView ViewHolder
	 */
	public static class FooterViewHolder extends BaseViewHolder {
		public View listview_footer_more;
		public View listview_footer_loading;
		public View listview_footer_error;

		public FooterViewHolder(View view) {
			super(view);
			listview_footer_more = view.findViewById(R.id.listview_footer_more);
			listview_footer_loading = view.findViewById(R.id.listview_footer_loading);
			listview_footer_error = view.findViewById(R.id.listview_footer_error);
		}
	}

	protected void initFootView(FooterViewHolder holder) {
		FooterViewHolder footerViewHolder = holder;
		if (currentState == SwipeRefreshGridView.STATE_LOADMORE) {
			footerViewHolder.listview_footer_error.setVisibility(View.GONE);
			footerViewHolder.listview_footer_loading.setVisibility(View.VISIBLE);
			footerViewHolder.listview_footer_more.setVisibility(View.GONE);
		} else {
			footerViewHolder.listview_footer_error.setVisibility(View.GONE);
			footerViewHolder.listview_footer_loading.setVisibility(View.GONE);
			footerViewHolder.listview_footer_more.setVisibility(View.GONE);
		}
	}
}
