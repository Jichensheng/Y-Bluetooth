package com.heshun.hslibrary.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.heshun.hslibrary.R;

/**
 * 敬请期待页面
 * 
 * @author huangxz
 *
 */
@SuppressLint("ValidFragment")
public class DefaultFragment extends BaseFragment {

	protected String title;

	protected int iconId;

	public DefaultFragment(String text, int id) {
		this.title = text;
		this.iconId = id;
	}

	public DefaultFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_order_details, container, false);

		TextView tv = (TextView) view.findViewById(R.id.tv_hint);
		if (!TextUtils.isEmpty(title))
			tv.setText(title);
		return view;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

}
