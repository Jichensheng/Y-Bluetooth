package com.heshun.hslibrary.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.util.UiUtil;
import com.heshun.hslibrary.project.viewpagerindicator.LinePageIndicator;
import com.heshun.hslibrary.widget.wheel.adapter.BannerAdapter;
import com.heshun.hslibrary.widget.wheel.entity.BannerItem;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自动滚动的广告栏
 * 
 * @author huangxz
 *
 */
public class SwitchBannerView<T> extends FrameLayout implements BannerAdapter.OnBannerItemClickListener{

	private int INDICATOR_HEIGHT = 2;
	private int INDICATOR_MINWIDTH = 20;

	public SwitchBannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public SwitchBannerView(Context context) {
		super(context);
		this.mContext = context;
	}

	public SwitchBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
	}

	private List<T> mData;

	private ViewPager mViewPager;

	private LinePageIndicator mIndicator;

	private Context mContext;

	private BannerAdapter<T> mAdapter;

	private final int SCROLL_TIME = 5;

	private ScheduledExecutorService mSchedule;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			mViewPager.setCurrentItem(what);
		}
	};

	public void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.banner_switch_item, this, true);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mIndicator = (LinePageIndicator) findViewById(R.id.indicator);

		float density=mContext.getResources().getDisplayMetrics().density;
		mIndicator.setUnselectedColor(Color.WHITE);// 未选中色
		mIndicator.setMinimumWidth((int)(INDICATOR_MINWIDTH* density));
		mIndicator.setStrokeWidth(INDICATOR_HEIGHT * density);
		mAdapter = new BannerAdapter<T>(mContext);
		mAdapter.setData(mData);
		mAdapter.setOnBannerItemClickListener(this);
		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager);

	}

	/**
	 * 更新数据源
	 * 
	 * @param data
	 */
	public void setData(List<T> data) {
		this.mData = data;
		initView();
		invalidate();
		startPlay();
	}

	private void startPlay() {
		stopPlay();
		mSchedule = Executors.newSingleThreadScheduledExecutor();
		mSchedule.scheduleAtFixedRate(new SlideShowTask(), 1, SCROLL_TIME, TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	private void stopPlay() {
		if (mSchedule != null)
			mSchedule.shutdown();
	}

	@Override
	public void onBannerClick(BannerItem item) {
		UiUtil.toast(item.indexImage+"\n"+item.title);
	}

	/**
	 * 执行轮播图切换任务
	 * 
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			synchronized (mViewPager) {
				int currentIndex = mViewPager.getCurrentItem();
				int nextIndex = (currentIndex + 1) % mAdapter.getCount();
				Message m = mHandler.obtainMessage();
				m.what = nextIndex;
				mHandler.sendMessage(m);
			}
		}

	}

}
