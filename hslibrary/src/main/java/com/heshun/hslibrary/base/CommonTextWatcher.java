package com.heshun.hslibrary.base;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.heshun.hslibrary.common.util.UiUtil;


/**
 * textwatcher通用封装类
 * 
 * @author huangxz
 * 
 */
public class CommonTextWatcher implements TextWatcher {

	private int maxSize = 20;
	private EditText mEditText;

	public CommonTextWatcher(int maxSize, EditText et) {
		this.maxSize = maxSize;
		this.mEditText = et;
	}

	private CharSequence temp;

	@Override
	public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		temp = s;
	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		if (temp.length() > maxSize) {
			UiUtil.toast(String.format("最多能输入%s个字符", maxSize));
			mEditText.setText(temp.subSequence(0, 10));
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
