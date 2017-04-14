package com.heshun.hslibrary.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.config.BaseApplication;

/**
 * Activity基类，自带toolbar
 *
 * @author huangxz
 */
public abstract class ToolBarActivity extends AppCompatActivity {

	private ToolBarHelper mToolBarHelper;

	protected Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//将activity注册到完美退出崩溃栈里边
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BaseApplication.getInstance().addActivity(this);
	}

	@Override
	public void setContentView(int layoutResID) {

		mToolBarHelper = new ToolBarHelper(this, layoutResID);

		if (!TextUtils.isEmpty(getTbTitle())) {

			mToolBarHelper.setTitle(getTbTitle());
		}

		toolbar = mToolBarHelper.getToolBar();

		setContentView(mToolBarHelper.getContentView());
		// 把 toolbar 设置到Activity 中
		setSupportActionBar(toolbar);

		// 自定义的一些操作
		onCreateCustomToolBar(toolbar);

		initView();

		setupToolbar();
	}

	public ToolBarHelper getTbHelper() {
		return mToolBarHelper;
	}

	protected void setupToolbar() {

	}

	protected void setTbTitle(String s) {

		if (!TextUtils.isEmpty(s)) {

			mToolBarHelper.setTitle(s);
		}
	}

	protected abstract void initView();

	protected abstract String getTbTitle();

	public void onCreateCustomToolBar(Toolbar toolbar) {
		toolbar.setContentInsetsRelative(0, 0);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}