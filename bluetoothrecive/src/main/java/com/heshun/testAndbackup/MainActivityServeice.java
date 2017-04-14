package com.heshun.testAndbackup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.heshun.YModem.Ymodem;
import com.heshun.bluetoothrecive.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

/**
 * author：Jics
 * 2017/3/13 09:26
 */
public class MainActivityServeice extends AppCompatActivity implements View.OnClickListener {


	private BluetoothAdapter mBluetoothAdapter;
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口模块

	private OutputStream os;//全局输出流，给客户端发数据
	private InputStream is;//全局输入流，服务监听使用

	private Button btn_clear;
	private Button btn_data;
	private TextView result;

	//作为服务端（接收）
	private AcceptThread acceptThread;
	private final String NAME = "Bluetooth_Socket";
	private BluetoothServerSocket serverSocket;
	private BluetoothSocket socket;

	private Ymodem ymodem;


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
		ymodem = new Ymodem();
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_data = (Button) findViewById(R.id.btn_data);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		result = (TextView) findViewById(R.id.result);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(receiver, filter);

		btn_clear.setOnClickListener(this);
		btn_data.setOnClickListener(this);
		//开启服务监听
		acceptThread = new AcceptThread();
		acceptThread.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_clear:
				result.setText("");
				break;
			case R.id.btn_data:
				if (os != null) {
					try {
						os.write(("JLZ=VOL:220." + new Random().nextInt(10) + "V,COR:16.3A,ELC:3.41Kwh,TIME:23MIN,STATE:0").getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				break;
		}
	}

	/**
	 * 服务端监听客户端的线程类
	 */
	private class AcceptThread extends Thread {

		public AcceptThread() {
			try {
				serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} catch (Exception e) {
			}
		}

		public void run() {
			try {
				socket = serverSocket.accept();
				serverSocket.close();
				//服务端收到的流
				is = socket.getInputStream();
				//服务端发出的流
				os = socket.getOutputStream();
				while (true) {
					byte[] buffer = new byte[1029];
					is.read(buffer);
					byte head = buffer[0];
					if (head == Ymodem.STX || head == Ymodem.EOT) {
						boolean isCurrect = ymodem.reciveSTX(buffer);
						if (isCurrect) {//数据包正确
							runOnUiThread(new UpdataUI(1, String.format("head %s   block %s   _block %s  crc_L %s   crc_H %s"
									, buffer[0], buffer[1], buffer[2], buffer[1027], buffer[1028]) + "\n"));
						}
					}else if(head==Ymodem.SOH){
						if(ymodem.reciveSOH(buffer)){
							runOnUiThread(new UpdataUI(1, "SOH包"+new String(buffer)+"\n"));
						}
					} else {
						String tips = new String(buffer, 0, buffer.length, "utf-8");
						if (tips.startsWith("+UPDATA")) {//收到通知升级，就发送C启动接收
							new Thread(new Runnable() {
								@Override
								public void run() {
									if (is != null && os != null) {
										ymodem.setClientOutputStream(os);
										//启动标志C 第一个C
										ymodem.requestFirstFrame();
									} else {
										Message msg = new Message();
										msg.obj = "is或os为空";
										handler.sendMessage(msg);
									}
								}
							}).start();
						} else if (tips.startsWith("+") || tips.startsWith("-") || tips.startsWith("客")) {
							runOnUiThread(new UpdataUI(0, tips.trim()));
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}


	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(getApplicationContext(), String.valueOf(msg.obj),
					Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}
	};

	/**
	 * 更新ui
	 */
	class UpdataUI implements Runnable {
		private String s;
		private static final int TYPE_TOAST = 0;
		private static final int TYPE_TEXT_VIEW = 1;
		private int type;

		public UpdataUI(int type, String s) {
			this.s = s;
			this.type = type;
		}

		@Override
		public void run() {
			switch (type) {
				case TYPE_TEXT_VIEW:
					result.append(s);
					break;
				case TYPE_TOAST:
					Toast.makeText(MainActivityServeice.this, s, Toast.LENGTH_SHORT).show();
					break;
			}
		}
	}

	/**
	 * 定义广播接收器
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			switch (action) {
				case BluetoothDevice.ACTION_ACL_DISCONNECTED:
					//对方强停
					Toast.makeText(context, "连接已断开", Toast.LENGTH_SHORT).show();
					refreshBluetooth();
					break;
			}
		}
	};

	/**
	 * 刷新设备
	 */
	private void refreshBluetooth() {
		//重置监听
		acceptThread = new AcceptThread();
		acceptThread.start();

	}
}
