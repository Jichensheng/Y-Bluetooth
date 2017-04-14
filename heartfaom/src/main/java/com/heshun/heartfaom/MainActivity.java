package com.heshun.heartfaom;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity {


	// 心型气泡
	private PeriscopeLayout periscopeLayout;
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//调用添加泡泡的方法
			periscopeLayout.addHeart();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化
		periscopeLayout = (PeriscopeLayout) findViewById(R.id.periscope);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true){
					try {
						Thread.sleep(2000);
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
