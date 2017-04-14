package com.heshun.hslibrary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.heshun.hslibrary.base.FragmentTabSwitchable;
import com.heshun.hslibrary.project.viewpagerindicator.IconPagerAdapter;

import java.util.List;

/**
 * @author huangxz
 *
 */
public class MenuAdapter<T extends FragmentTabSwitchable> extends FragmentPagerAdapter implements IconPagerAdapter {

	public MenuAdapter(FragmentManager fm) {
		super(fm);
	}

	private List<T> mData;

	@Override
	public Fragment getItem(int index) {
		return (Fragment) mData.get(index);
	}

	@Override
	public int getCount() {
		return null == mData ? 0 : mData.size();
	}

	public void setList(List<T> list) {
		this.mData = list;
	}

	@Override
	public int getIconResId(int index) {
		return mData.get(index).getIconId();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mData.get(position).getTitle();
	}

}
