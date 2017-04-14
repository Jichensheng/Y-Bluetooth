package com.heshun.hslibrary.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heshun.hslibrary.R;


/**
 * 空布局，当数据为空时显示
 *
 * @author wuch
 */
public class EmptyView extends LinearLayout {

	private Context context;
	private final String EMPTY_MESSAGE = "未查询到数据";

	private TextView messageView;

	public EmptyView(Context context) {
		super(context);
		this.context = context;
		setupView();

		setText(EMPTY_MESSAGE);
		setImage(R.drawable.icon_error);
	}

	public EmptyView(Context context, int resId, String message) {
		super(context);
		this.context = context;
		setupView();

		setText(message);
		setImage(resId);
	}

	private void setupView() {
		LayoutInflater.from(context).inflate(R.layout.view_empty, this, true);

		messageView = (TextView) findViewById(R.id.text_message);

	}

	public void setText(String message) {
		if (!TextUtils.isEmpty(message)) {
			messageView.setText(message);
		}
	}

	public void setImage(int resId) {
		if (resId != 0) {
			messageView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
		}
	}
}
