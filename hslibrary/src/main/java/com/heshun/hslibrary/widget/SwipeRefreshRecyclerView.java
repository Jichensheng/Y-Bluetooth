package com.heshun.hslibrary.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.base.BaseRecyclerViewAdapter;
import com.heshun.hslibrary.base.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义recyclerview 基于 SwipeRefreshLayout + RecyclerView实现的可以下拉刷新，上拉加载更多
 *
 * @author huangxz
 */
public class SwipeRefreshRecyclerView<T> extends LinearLayout implements SwipeRefreshLayout.OnRefreshListener {
	protected OnScollBtn onScollBtn;
	RecyclerView recycleview;
	SwipeRefreshLayout swiperefreshlayout;

	SwipeRefreshLayout emptySwipeRefreshlayout;

	LinearLayout emptyContainerLayout;

	private LayoutInflater mInflater = null;

	private List<T> mList = new ArrayList<T>();

	private int currentState = STATE_AIDL;

	private LayoutParams LAYOUT_PARAMS = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	private Context mContext = null;
	private View rootView;
	private BaseRecyclerViewAdapter<T> mAdapter;

	/**
	 * 无数据时候展示的View
	 */
	private EmptyView emptyView = null;

	/**
	 * 请求失败，重新加载View
	 */
	private ReloadView reloadView = null;

	private BaseRecyclerViewAdapter.OnItemClickListener mOnItemClickListener = null;

	private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = null;

	/**
	 * 最下面一项可见
	 */
	private LastItemVisibleListener mLastItemVisibleListener = null;

	public SwipeRefreshRecyclerView(Context context) {
		super(context);
		this.mContext = context;
	}

	public SwipeRefreshRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initUI();
	}

	public SwipeRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		initUI();
	}

	private void initUI() {
		setOrientation(LinearLayout.VERTICAL);

		mInflater = LayoutInflater.from(mContext);

		rootView = mInflater.inflate(R.layout.swiperefreshgridview_layout, null);

		recycleview = (RecyclerView) rootView.findViewById(R.id.recycleview);

		recycleview.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));

		swiperefreshlayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshlayout);

		emptySwipeRefreshlayout = (SwipeRefreshLayout) rootView.findViewById(R.id.emptySwipeRefreshlayout);

		emptyContainerLayout = (LinearLayout) rootView.findViewById(R.id.emptyContainerLayout);

		emptyView = new EmptyView(mContext);

		emptyContainerLayout.addView(emptyView, LAYOUT_PARAMS);

		reloadView = new ReloadView(mContext);

		emptyContainerLayout.addView(reloadView, LAYOUT_PARAMS);

		this.addView(rootView, LAYOUT_PARAMS);
	}

	/**
	 * @param adapter
	 */
	public void init(BaseRecyclerViewAdapter<T> adapter, List<T> list) {
		mAdapter = adapter;
		this.mList = list;
		mAdapter.setList(mList);
		swiperefreshlayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);

		swiperefreshlayout.setProgressViewOffset(false, 0,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

		swiperefreshlayout.setOnRefreshListener(this);

		emptySwipeRefreshlayout.setColorSchemeResources(android.R.color.holo_red_dark, android.R.color.holo_green_dark,
				android.R.color.holo_blue_light, android.R.color.holo_orange_dark);

		emptySwipeRefreshlayout.setProgressViewOffset(false, 0,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

		emptySwipeRefreshlayout.setOnRefreshListener(this);

		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
		linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
		recycleview.setLayoutManager(linearLayoutManager);
		recycleview.setAdapter(mAdapter);

		recycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

			private int lastVisibleItem = -1;

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				// lastVisibleItem =
				// linearLayoutManager.findLastCompletelyVisibleItemPosition();
				onScollBtn.firstItemPosition(getFirstItemPosition());
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

				if (mAdapter != null && currentState != STATE_LOADCOMPLETE)
					mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_LOADMORE);

				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
					if (currentState == STATE_LOADCOMPLETE || currentState == STATE_REFRESHING) // 全部加载完成,正在下拉请求
						return;

					// load Date
					if (mLastItemVisibleListener != null)
						mLastItemVisibleListener.lastItemVisible();
				}
			}
		});
		showRefreshing();
	}

	/**
	 * 下拉或者第一次加载的情况
	 */
	public void showRefreshing() {
		currentState = STATE_REFRESHING;
		if (mAdapter != null)
			mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_AIDL);
		swiperefreshlayout.setRefreshing(true);
	}

	public void hideRefreshing() {
		swiperefreshlayout.setRefreshing(false);
		emptySwipeRefreshlayout.setRefreshing(false);
	}

	/**
	 * 数据全部加载完成
	 */
	public void loadComplete() {
		currentState = STATE_LOADCOMPLETE;
		if (mAdapter != null)
			mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_LOADCOMPLETE);
		emptySwipeRefreshlayout.setVisibility(View.GONE);
		swiperefreshlayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 加载更多
	 */
	public void loadingMore() {

		currentState = STATE_LOADMORE;
		if (mAdapter != null) {
			mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_LOADMORE);
			mAdapter.notifyDataSetChanged();
		}
		emptySwipeRefreshlayout.setVisibility(View.GONE);
		swiperefreshlayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 请求失败，当前数据源为空,显示reload
	 */
	private void reload() {
		currentState = STATE_RELOAD;
		if (mAdapter != null)
			mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_RELOAD);
		emptySwipeRefreshlayout.setVisibility(View.VISIBLE);
		swiperefreshlayout.setVisibility(View.GONE);
		emptyView.setVisibility(View.GONE);
		reloadView.setVisibility(View.VISIBLE);
	}

	private void showEmpty() {
		currentState = STATE_EMPTY;
		if (mAdapter != null)
			mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_EMPTY);
		emptySwipeRefreshlayout.setVisibility(View.VISIBLE);
		swiperefreshlayout.setVisibility(View.GONE);
		emptyView.setVisibility(View.VISIBLE);
		reloadView.setVisibility(View.GONE);
	}

	/**
	 * 刷新列表数据
	 *
	 * @param list
	 * @param total
	 * @param request_succ 是否请求成功
	 */
	public void onDataChanged(List<T> list, int total, boolean request_succ) {
		// 特殊情况,首页使用，加载数据失败，但是要显示按钮
		if (total == -1) {
			if (mAdapter != null) {
				mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_AIDL);
				mAdapter.notifyDataSetChanged();
			}
			return;
		}
		if (!request_succ) {// 请求失败
			if (mList.size() == 0) {// 当前数据源为空,显示reload
				reload();
				return;
			}
		}

		switch (currentState) {
			case STATE_AIDL:// 默认状态
				break;
			case STATE_LOADING:// 第一次进入显示加载进度条
				break;
			case STATE_RELOAD:// 重新加载
				mList.clear();
				mList.addAll(list);
				break;
			case STATE_REFRESHING:// 下拉刷新
				mList.clear();
				mList.addAll(list);
				break;
			case STATE_LOADMORE:
				mList.addAll(list);
				break;
			case STATE_LOADCOMPLETE:
				mList.clear();
				mList.addAll(list);
				break;
		}

		if (total > mList.size()) {// 未加载完成
			if (mList.size() == 0)
				showEmpty();
			else
				resetState();
		} else {
			if (mList.size() == 0) // 数据为空
				showEmpty();
			else if (currentState != STATE_LOADCOMPLETE)
				loadComplete();
		}
		if (mAdapter != null) {
			mAdapter.setCurrentDataState(SwipeRefreshGridView.STATE_AIDL);
			mAdapter.notifyDataSetChanged();
		}

	}


	/**
	 * 底部item可见监听
	 */
	public interface LastItemVisibleListener {
		public void lastItemVisible();
	}

	public void setOnLastItemVisibleListener(LastItemVisibleListener listener) {
		this.mLastItemVisibleListener = listener;
	}

	/**
	 * 设置 OnItemClickListener
	 *
	 * @param listener
	 */
	public void setOnItemClickListener(BaseRecyclerViewAdapter.OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
		if (mAdapter != null)
			mAdapter.setOnItemClickListener(this.mOnItemClickListener);
	}

	// TODO 待优化
	public RecyclerView getRecyclerView() {
		return recycleview;
	}

	public interface OnScollBtn {
		void firstItemPosition(int position);
	}

	public void setOnScollBtn(OnScollBtn onScollBtn) {
		this.onScollBtn = onScollBtn;
	}

	protected int getFirstItemPosition() {
		LinearLayoutManager layoutManager = (LinearLayoutManager) recycleview.getLayoutManager();
		int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
		return firstItemPosition;
	}

	/**
	 * 设置下拉刷新监听
	 *
	 * @param listener
	 */
	public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}

	public void setOnReloadClickListener(OnClickListener listener) {
		if (reloadView != null)
			reloadView.setOnClickListener(listener);
	}

	@Override
	public void onRefresh() {
		currentState = STATE_REFRESHING;
		if (mOnRefreshListener != null)
			mOnRefreshListener.onRefresh();
		if (emptySwipeRefreshlayout.getVisibility() == View.VISIBLE) {
			emptySwipeRefreshlayout.setVisibility(View.GONE);
			swiperefreshlayout.setVisibility(View.VISIBLE);
		}
	}

	public void setRefreshable(boolean flag) {
		swiperefreshlayout.setEnabled(flag);
	}

	public void setTouchListener(OnTouchListener listener) {
		swiperefreshlayout.setOnTouchListener(listener);
	}

	public boolean isRefreshing() {
		return currentState == STATE_REFRESHING || currentState == STATE_ALLLOADVIEW;
	}

	public void resetState() {
		currentState = STATE_AIDL;
	}


	/**
	 * 初始状态
	 */
	public static final int STATE_AIDL = -1;

	/**
	 * 第一次进入时候，加载状态
	 */
	public static final int STATE_LOADING = -2;

	/**
	 * 下拉刷新
	 */
	public static final int STATE_REFRESHING = -3;

	/**
	 * 加载更多
	 */
	public static final int STATE_LOADMORE = -4;

	/**
	 * 加载完成
	 */
	public static final int STATE_LOADCOMPLETE = -5;

	/**
	 * 空数据 emptyView
	 */
	public static final int STATE_EMPTY = 0;

	/**
	 * 全部加载完成View
	 */
	public static final int STATE_ALLLOADVIEW = 3;

	/**
	 * reloadView
	 */
	public static final int STATE_RELOAD = 4;
}
