package com.heshun.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.heshun.bluetoothrecive.R;
import com.heshun.blutoothUtils.BluetoothService;

import java.util.Random;

/**
 * author：Jics
 * 2017/3/13 09:26
 */
public class MainActivityServeiceRefactor extends AppCompatActivity implements View.OnClickListener, BluetoothService.OnServerDataChangeListener {

	private BluetoothService bluetoothService;

	private Button btn_clear;
	private Button btn_data;
	private TextView result;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainrecive);
		initDevice();
	}

	/**
	 * 初始化控件和设备
	 */
	private void initDevice() {
		bluetoothService = new BluetoothService(this);
		bluetoothService.setOnServerDataChangeListener(this);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_data = (Button) findViewById(R.id.btn_data);
		result = (TextView) findViewById(R.id.result);


		btn_clear.setOnClickListener(this);
		btn_data.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_clear:
				result.setText("");
				break;
			case R.id.btn_data:
				if (count++ % 2 == 0) {
					bluetoothService.sendToClient("J");
				} else
					bluetoothService.sendToClient("LZ=VOL:220." + new Random().nextInt(10) + "V,CUR:16.0A,ELC:3.41Kwh,TIME:23MIN,STATE:0=JLZ");
				break;
		}
	}

	@Override
	public void onTextChange(final String data) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				result.append(data);

			}
		});
	}

	@Override
	public void onToastChange(final String data) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivityServeiceRefactor.this, data, Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bluetoothService.unregBroadcast();
	}
}
