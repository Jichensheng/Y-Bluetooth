package com.heshun.tablayou;

import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView iv= (ImageView) findViewById(R.id.iv);
		ImageView iv2= (ImageView) findViewById(R.id.iv2);
		StateListDrawable drawable1=new StateListDrawable();
		drawable1.addState(new int[]{android.R.attr.state_selected},getResources().getDrawable(R.drawable.h_1,getTheme()));
		drawable1.addState(new int[]{android.R.attr.state_pressed},getResources().getDrawable(R.drawable.h_2,getTheme()));
		drawable1.addState(new int[]{android.R.attr.state_window_focused},getResources().getDrawable(R.drawable.h_1,getTheme()));
		drawable1.addState(new int[]{},getResources().getDrawable(R.drawable.h_3,getTheme()));
		iv.setImageDrawable(drawable1);
		iv.setClickable(true);
		iv2.setClickable(true);
	}
}