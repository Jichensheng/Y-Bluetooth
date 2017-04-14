package com.heshun.hslibrary.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.robinhood.ticker.TickerView;


/**
 * author：Jics
 * 2016/7/26 14:25
 */
public class TickerViewPlus extends TickerView {
	private String contentText="";
	public TickerViewPlus(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}
	@Override
	public void setText(String text) {
		super.setText(text);
		contentText=text;

	}
	public  String getText(){
		return contentText;
	}
	public static char[] getCustomerNumberList() {
		// : · ·
		final char[] charList = new char[15];
		charList[0] = (char) 0;
		charList[11]='.';
		charList[12]='·';
		charList[13]=':';
		charList[14]=' ';
		for (int i = 0; i < 10; i++) {
			charList[i + 1] = (char) (i + 48);
		}
		return charList;
	}
}
