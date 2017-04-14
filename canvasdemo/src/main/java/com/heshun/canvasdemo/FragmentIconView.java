package com.heshun.canvasdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * author：Jics
 * 2017/3/1 09:45
 */
public class FragmentIconView extends Fragment {
	private View view;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.layout_static_fragment_make, null);
		TextView textView= (TextView) view.findViewById(R.id.text_fr);
		textView.setText("Icon");
		return view;
	}
}
