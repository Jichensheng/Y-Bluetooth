package com.heshun.canvasdemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	private ViewPager viewPager;
	private View v1, v2, v3, v4;
	private List<View> viewList;

	//private IndicatorView indicatorView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		// 设置Page间间距
		viewPager.setPageMargin(100);
		// 设置缓存的页面数量
		viewPager.setOffscreenPageLimit(3);
		LayoutInflater inflater = getLayoutInflater();
		v1 = inflater.inflate(R.layout.layout_clock_view, null);
		v2 = inflater.inflate(R.layout.layout_static_fragment_use, null);
		v3 = inflater.inflate(R.layout.layout_fish, null);
		v4 = inflater.inflate(R.layout.layout_fruits_father, null);

		viewList = new ArrayList<>();
		viewList.add(v1);
		viewList.add(v2);
		viewList.add(v3);
		viewList.add(v4);

		viewPager.setAdapter(new PgAdapter(viewList));

//		indicatorView= (IndicatorView) findViewById(R.id.id_indicator);
//		indicatorView.setViewPager(viewPager);
	}

}
