package com.heshun.hslibrary.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.http.HttpConnection;
import com.heshun.hslibrary.widget.SwipeRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
/**
 * 带刷新、加载更多的列表fragment抽象
 *
 * @author huangxz
 */
public abstract class BaseRecyclerFragment<T, V extends BaseRecyclerViewAdapter<T>> extends BaseFragment {

	private static final int MIN_ITEM_NUM = 5;
	protected ImageView scrollBtn;
//	protected BaseApplication baseApplication;
	protected int defaultIndex = 1;
	protected int pageSize = 10;
	protected int pageIndex = defaultIndex;

	private int total = 0;

	protected boolean freshEnable = true;

	protected boolean requestOnStart = true;

	protected List<T> list = new ArrayList<T>();

	protected SwipeRefreshRecyclerView<T> mSwiperefreshRecyclerView;

	protected BaseRecyclerViewAdapter<T> mAdapter = null;

	protected int layout = R.layout.fragment_base_refresh_recycler;

	protected BaseRecyclerFragment() {
		freshEnable = true;
	}

	protected Handler mhandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HttpConnection.REQUEST_SUCCESS:
					ArrayList<T> datalist = (ArrayList<T>) msg.obj;
					total = msg.arg1;
					if (datalist.size() == 0)
						pageIndex = --pageIndex < 0 ? 0 : --pageIndex;
					mSwiperefreshRecyclerView.onDataChanged(datalist, total, true);
					break;
				case HttpConnection.REQUEST_FAILED:
					mSwiperefreshRecyclerView.onDataChanged(new ArrayList<T>(), 0, false);
					pageIndex--;
					break;
				case HttpConnection.REQUEST_ERROR_NET:
					mSwiperefreshRecyclerView.onDataChanged(new ArrayList<T>(), 0, false);
					pageIndex--;
					break;
				case HttpConnection.REQUEST_TIMEOUT:
					mSwiperefreshRecyclerView.onDataChanged(new ArrayList<T>(), 0, false);
					pageIndex--;
					break;
				case HttpConnection.REQUEST_ERROR_SERVER:
					mSwiperefreshRecyclerView.onDataChanged(new ArrayList<T>(), 0, false);
					pageIndex--;
					break;
			}
			mSwiperefreshRecyclerView.hideRefreshing();

		}
	};

	protected void pushMessage(int status, int total, List<T> data) {
		Message m = mhandler.obtainMessage();
		m.what = status;
		m.arg1 = total;
		if (data != null)
			m.obj = data;
		else
			m.obj = new ArrayList<T>();
		mhandler.sendMessage(m);
	}

	protected void pushMessage(int status, int total) {
		pushMessage(status, total, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(layout, container, false);
		scrollBtn = (ImageView) view.findViewById(R.id.scroll_btn);

		mSwiperefreshRecyclerView = (SwipeRefreshRecyclerView<T>) view.findViewById(R.id.recycleView_base_refresh);

		mAdapter = getAdapter();

		mSwiperefreshRecyclerView.init(mAdapter, list);
		mSwiperefreshRecyclerView.setRefreshable(freshEnable);
		scrollBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSwiperefreshRecyclerView.getRecyclerView().smoothScrollToPosition(0);
			}
		});
		mSwiperefreshRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				doRefresh();
			}
		});
		mSwiperefreshRecyclerView.setOnLastItemVisibleListener(new SwipeRefreshRecyclerView.LastItemVisibleListener() {
			@Override
			public void lastItemVisible() {

				if (!isLastPage(++pageIndex)) {
					if (!mSwiperefreshRecyclerView.isRefreshing()) {
						mSwiperefreshRecyclerView.loadingMore();
						sendRequest(pageIndex);
					}
				}
			}
		});
		mSwiperefreshRecyclerView.setOnScollBtn(new SwipeRefreshRecyclerView.OnScollBtn() {

			@Override
			public void firstItemPosition(int position) {
				if (position > MIN_ITEM_NUM) {
					scrollBtn.setVisibility(View.VISIBLE);
				} else
					scrollBtn.setVisibility(View.GONE);
			}
		});
		mSwiperefreshRecyclerView.setOnReloadClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pageIndex = defaultIndex;
				sendRequest(pageIndex);
			}
		});
		if (requestOnStart) {
			pageIndex = defaultIndex;
			sendRequest(pageIndex);
		}
		return view;
	}


	protected void doRefresh() {
		pageIndex = defaultIndex;
		// list.clear();
		sendRequest(pageIndex);
	}

	/**
	 * 是否为最后一页
	 *
	 * @param pageIndex
	 * @return
	 */
	protected boolean isLastPage(int pageIndex) {
		int totalPages;
		if (total % pageSize == 0) {
			totalPages = total / pageSize;
		} else
			totalPages = total / pageSize + 1;
		return pageIndex > totalPages ? true : false;
	}

	protected abstract void sendRequest(int startIndex);

	// public abstract void onItemClick(int position);
//	public abstract void showTopButton();

	protected abstract V getAdapter();
}
