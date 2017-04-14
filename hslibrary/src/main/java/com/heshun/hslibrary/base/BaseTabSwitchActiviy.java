package com.heshun.hslibrary.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.heshun.hslibrary.R;

import java.util.List;

/**
 * 带滑动切换Tab的抽象页面
 * 
 * @author huangxz
 *
 */
public abstract class BaseTabSwitchActiviy extends ToolBarActivity {

	private String[] titles;

	private ViewPager mViewPager;

	private List<BaseFragment> mFragments;

	private TabSwitchFragmentAdapter mAdapter;

	private TabLayout mTabLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		titles = getTitles();
		mFragments = getFragments();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_tab_switch);
	}

	@Override
	public void initView() {

		mTabLayout = (TabLayout) findViewById(R.id.tablayout);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdapter = new TabSwitchFragmentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);

		mViewPager.setPageTransformer(true, new DepthPageTransformer());

		mTabLayout.setupWithViewPager(mViewPager);
		mTabLayout.setTabTextColors(getResources().getColor(R.color.title_text_gray),
				getResources().getColor(R.color.tab_orange));
		mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_orange));
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);
	}

	protected abstract String[] getTitles();

	protected abstract List<BaseFragment> getFragments();

	/**
	 * viewpager的默认适配器
	 * 
	 * @author huangxz
	 *
	 */
	class TabSwitchFragmentAdapter extends FragmentPagerAdapter {

		public TabSwitchFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if (null != mFragments && null != mFragments.get(i)) {
				return mFragments.get(i);
			}
			return new DefaultFragment();
		}

		@Override
		public int getCount() {
			return mFragments != null ? mFragments.size() : 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

	}
}
