package com.heshun.blutoothUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.heshun.YModem.Ymodem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * author：Jics
 * 2017/3/23 08:56
 */
public class BluetoothService {
	private BluetoothAdapter mBluetoothAdapter;
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口模块

	private OutputStream os;//全局输出流，给客户端发数据
	private InputStream is;//全局输入流，服务监听使用

	private Context context;

	//作为服务端（接收）
	private AcceptThread acceptThread;
	private final String NAME = "Bluetooth_Socket";
	private BluetoothServerSocket serverSocket;
	private BluetoothSocket socket;

	private Ymodem ymodem;
	private OnServerDataChangeListener onServerDataChangeListener;

	public BluetoothService(Context context) {
		this.context = context;
		initDevice();
	}

	/**
	 * 初始化控件和设备
	 */
	private void initDevice() {
		ymodem = new Ymodem();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		context.registerReceiver(receiver, filter);

		//开启服务监听
		acceptThread = new AcceptThread();
		acceptThread.start();
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
							onServerDataChangeListener.onTextChange(String.format("head %s   block %s   _block %s  crc_H %s   crc_L %s"
									, buffer[0], buffer[1], buffer[2], buffer[1027], buffer[1028]) + "\n");
						}
					} else if (head == Ymodem.SOH) {
						if (ymodem.reciveSOH(buffer)) {
							if (buffer[131] == 0 && buffer[132] == 0) {
								onServerDataChangeListener.onTextChange("SOH包   " + "全0数据包" + "\n");
							} else {

								onServerDataChangeListener.onTextChange("SOH包   " + getNameAndSize(buffer)+"\n");
							}
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
										onServerDataChangeListener.onToastChange("is或os为空");
									}
								}
							}).start();
						} else if (tips.startsWith("+") || tips.startsWith("-") || tips.startsWith("Hell")) {
							onServerDataChangeListener.onToastChange(tips.trim());
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}

	public void sendToClient(String s) {
		if (os != null) {
			try {
				os.write(s.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getNameAndSize(byte[] data) {
		int indexStart_1 = 3;
		int indexEnd_1 = -1;
		int indexStart_2 = -1;
		int indexEnd_2 = -1;
		String s="";
		for (int i = indexStart_1; i < data.length; i++) {
			if (data[i] == 0 && indexEnd_1 == -1) {
				indexEnd_1 = i - 1;
				indexStart_2=i+1;
				continue;
			}
			if (data[i] == 0 && indexEnd_1 != -1) {
				indexEnd_2 = i - 1;
				break;
			}
		}
		if (indexEnd_1*indexStart_2*indexEnd_2>=0&&indexEnd_1>=indexStart_1&&indexEnd_2>=indexStart_2) {
			byte[] name=new byte[indexEnd_1-indexStart_1+1];
			byte[] size=new byte[indexEnd_2-indexStart_2+1];
			System.arraycopy(data,indexStart_1,name,0,name.length);
			System.arraycopy(data,indexStart_2,size,0,size.length);
			s= new String(name)+"  "+new String(size)+"B";
		}
		return s;
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
					onServerDataChangeListener.onToastChange("连接已断开");
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

	public interface OnServerDataChangeListener {
		void onTextChange(String data);

		void onToastChange(String data);
	}

	public void setOnServerDataChangeListener(OnServerDataChangeListener onServerDataChangeListener) {
		this.onServerDataChangeListener = onServerDataChangeListener;
	}

	/**
	 * 注销广播
	 */
	public void unregBroadcast() {
		context.unregisterReceiver(receiver);
	}

}
