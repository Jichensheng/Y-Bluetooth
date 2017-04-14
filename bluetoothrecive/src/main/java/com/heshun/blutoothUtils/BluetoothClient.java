package com.heshun.blutoothUtils;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.heshun.YModem.Ymodem;
import com.heshun.entity.ElectricityParameter;
import com.heshun.tools.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * author：Jics
 * 2017/3/16 10:18
 */
public class BluetoothClient {

	private Context context;
	private static final String TAG = "BluetoothConnect";
	private BluetoothAdapter mBluetoothAdapter;
	private List<String> bluetoothDevices;
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口模块
	//作为客户端（发送）
	private BluetoothSocket clientSocket;
	private BluetoothDevice device;

	private OutputStream os;//全局输出流，作为客户端发送数据使用
	private InputStream is;//全局输入流，服务监听使用

	private Thread receiceThread;

	private ProgressDialog waitingDialog;
	private Ymodem ymodem;
	private boolean fileSucc = false;//文件是否打开成功

	private List<byte[]> packageFrames;
	private boolean isFirstFrame = true;
	private OnDataChangeListener onDataChangeListener;

	private boolean dataNeedAdd = false;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(context.getApplicationContext(), String.valueOf(msg.obj),
					Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}
	};

	/**
	 * 构造函数里初始化蓝牙、广播、接收线程
	 *
	 * @param context
	 */
	public BluetoothClient(Context context) {
		this.context = context;
		ymodem = new Ymodem();

		bluetoothDevices = new ArrayList<>();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//获取已经配对的蓝牙设备
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				bluetoothDevices.add(device.getName() + ":" + device.getAddress());
			}
		}

		//每搜索到一个设备就会发送一个该广播
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(receiver, filter);

		//当全部搜索完后发送该广播
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		context.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		context.registerReceiver(receiver, filter);

		receiceThread = new Thread(new ReceiveSrvData());

	}

	/**
	 * 搜寻设备
	 */
	public void deviceSearch() {
		//如果当前在搜索，就先取消搜索
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
		if (waitingDialog == null) {
			waitingDialog = showWaitingDialog();
		}
		//开启搜索
		mBluetoothAdapter.startDiscovery();
	}

	/**
	 * 获取已配对蓝牙列表
	 *
	 * @return
	 */
	public List<String> getBluetoothDevices() {
		return bluetoothDevices;
	}

	/**
	 * 定义广播接收器
	 */
	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			switch (action) {
				case BluetoothDevice.ACTION_FOUND:
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						bluetoothDevices.add(device.getName() + ":" + device.getAddress());
						onDataChangeListener.onArrayAdapter();
					}
					break;
				case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
					//已搜索完成
					waitingDialog.dismiss();
					waitingDialog = null;
					break;
				case BluetoothDevice.ACTION_ACL_DISCONNECTED:
					//对方强停
					onDataChangeListener.onToast("连接已断开");
					onDataChangeListener.onStateChange("未连接");
					refreshBluetooth();
					break;
			}
		}
	};

	/**
	 * 字符命令
	 *
	 * @param com
	 */
	public void sendCommend(String com) {
		if (os != null) {
			try {
				os.write(com.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			onDataChangeListener.onToast("还未连接设备");
		}

	}

	/**
	 * 刷新设备
	 */
	private void refreshBluetooth() {
		if (clientSocket != null && clientSocket.isConnected()) {
			try {
				clientSocket.close();
				//重置RfCOMM通道便于下次与服务连接
				clientSocket = null;
				onDataChangeListener.onStateChange("未连接");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 连接到远程监听端的异步任务
	 */
	public class ConnectTask extends AsyncTask<String, Void, Integer> {

		private static final int CONNECT_SUCCESS = 0;
		private static final int DEVICE_HAS_CONNECT = 1;
		private static final int CLIENTSOCKET_ALIVE = 2;
		private static final int DEVICE_ERROR = 3;
		private static final int OS_NULL = 4;
		private static final int UNKNOWN_ERROR = 5;
		private static final int DEVICE_SAME = 6;
		private static final int DEVICE_SINGLE_WARNING = 7;
		private int currentState = CONNECT_SUCCESS;

		@Override
		protected void onPreExecute() {
			onDataChangeListener.onStateChange("连接中……");
		}

		@Override
		protected Integer doInBackground(String... address) {
			//主动连接蓝牙服务端
			try {
				//判断当前是否正在搜索
				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}
				try {

					if (device == null) {
						//获得远程设备
						device = mBluetoothAdapter.getRemoteDevice(address[0]);
					} else if (!device.getAddress().equals(address[0])) {
						return DEVICE_SINGLE_WARNING;
					}
					if (clientSocket == null) {
						//创建客户端蓝牙Socket
						//客户端将在其打开到服务器的 RFCOMM 通道时收到该套接字
						clientSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
						//开始连接蓝牙，如果没有配对则弹出对话框提示我们进行配对
						clientSocket.connect();
						//获得输出流（客户端向服务器端输出的流）
						os = clientSocket.getOutputStream();
						is = clientSocket.getInputStream();
					} else {
						currentState = CLIENTSOCKET_ALIVE;
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "doInBackground: " + e.toString());
					//没连接成功也会初始化clientSocket影响第二次重试，所以重新置空
					clientSocket = null;
					return DEVICE_ERROR;
				}
				//流发出
				if (os != null) {
					os.write("Hello".getBytes());
					receiceThread.start();
				} else {
					return OS_NULL;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "doInBackground: " + e.toString());
				//socket中断后也要置空，便于重连
				clientSocket = null;
				return UNKNOWN_ERROR;
			}
			return currentState;
		}

		@Override
		protected void onPostExecute(Integer state) {
			switch (state) {
				case CONNECT_SUCCESS:
					onDataChangeListener.onStateChange("执行连接成功（点击断开）");
					break;
				case DEVICE_HAS_CONNECT:
					onDataChangeListener.onStateChange("已经连接此设备了");
					break;
				case CLIENTSOCKET_ALIVE:
					onDataChangeListener.onStateChange("已连接");
					break;
				case DEVICE_ERROR:
					onDataChangeListener.onStateChange("DEVICE_ERROR：获取远程连接失败" + "\n请检测\n1、配对设备是否开启蓝牙\n" +
							"2、是否被占用" + "\n然后尝试重连");
					break;
				case DEVICE_SAME:
					onDataChangeListener.onStateChange("DEVICE_SAME");
					break;
				case DEVICE_SINGLE_WARNING:
					onDataChangeListener.onStateChange("点击断开后再切换设备");
					break;
				case OS_NULL:
					onDataChangeListener.onStateChange("OS_NULL");
					break;
				case UNKNOWN_ERROR:
					onDataChangeListener.onStateChange("UNKNOWN_ERROR:可能是socket pipe中断" + "\n请检测\n1、配对设备是否开启蓝牙\n" +
							"2、是否被占用" + "\n然后尝试重连");
					break;
				default:
					onDataChangeListener.onStateChange("执行连接成功");

			}
		}
	}

	/**
	 * 客户端收到服务端数据的线程
	 */
	class ReceiveSrvData implements Runnable {
		String tempData = "";

		@Override
		public void run() {
			try {
				while (true) {
					byte[] temp = new byte[1029];
					is.read(temp);
					byte flag = temp[0];
					byte flag_splicing = temp[1];
					String charlization = new String(temp, 0, temp.length).trim();//用于判断"C"或者"JLZ="
					Log.e("------receive-----", Arrays.toString(temp));
					onDataChangeListener.onDebug(">:"+charlization+"");
					if (flag == Ymodem.NAK || flag == Ymodem.CAN || flag == Ymodem.ACK || flag == Ymodem.CHAR_C) {
						if (flag == Ymodem.ACK && flag_splicing == Ymodem.CHAR_C) {//处理[6,67]粘包问题
							flag = Ymodem.CHAR_C;
						}
						if (packageFrames != null) {//包不空
							if (charlization.length() == 1 && flag == Ymodem.CHAR_C && isFirstFrame) {//接到接收端的请求（第一帧）
								int state = ymodem.send(packageFrames, Ymodem.CHAR_C);
								deelState(state);
							} else {//接下来的帧
								int state = ymodem.send(packageFrames, flag);
								deelState(state);
							}
						} else {
							onDataChangeListener.onToast("文件为空");
						}

					} else if (charlization.startsWith("J") && !dataNeedAdd) {
						tempData += charlization;
						dataNeedAdd = true;
						if (charlization.contains("=JLZ")) {
							onDataChangeListener.onReceiveChange(FileUtils.analysisWords2entity(tempData));
							tempData = "";
							dataNeedAdd = false;
						}

//						Log.e("------JLZ-----", new String(temp, 0, temp.length).trim());
					} else {
						if (dataNeedAdd && charlization.contains("=JLZ")) {
							tempData +=charlization;
							onDataChangeListener.onReceiveChange(FileUtils.analysisWords2entity(tempData));
							tempData = "";
							dataNeedAdd = false;
						}
//						Log.e("------JLZelse-----", new String(temp).trim());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 根据状态做处理
	 *
	 * @param state
	 */
	private void deelState(int state) {
		Message msg = new Message();
		switch (state) {
			case Ymodem.STATE_COMPLETE:
				isFirstFrame = true;
				msg.obj = "传输完成";
				onDataChangeListener.onProgress(100f);
				handler.sendMessage(msg);
				break;
			case Ymodem.STATE_FORCE_STOP:
				msg.obj = "传输被强停";
				onDataChangeListener.onProgress(0f);
				handler.sendMessage(msg);
				isFirstFrame = true;
				break;
			case Ymodem.STATE_MORE_RETRY:
				msg.obj = "重试次数太多被强停";
				onDataChangeListener.onProgress(0f);
				handler.sendMessage(msg);
				isFirstFrame = true;
				break;
			case Ymodem.STATE_NEXT:
				onDataChangeListener.onProgress(((float) (ymodem.getFrameNumber()) / (packageFrames.size())) * 100);
				//下一个包
				isFirstFrame = false;
				break;
		}
	}

	/**
	 * 异步加载文件
	 */
	public class FileTask extends AsyncTask<String, Void, Integer> {
		static final int ERROR_PIPE = 0;
		static final int ERROR_FILE = 1;
		static final int SUCC = 2;


		@Override
		protected Integer doInBackground(String... strings) {

			try {
				File file = new File(strings[0]);
				InputStream inputStream = new FileInputStream(file);
				packageFrames = Ymodem.getPackage(inputStream, file.getName(), FileUtils.getFileOrFilesSize(strings[0], FileUtils.SIZETYPE_B));
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR_FILE;
			}
			return SUCC;
		}

		@Override
		protected void onPostExecute(Integer flag) {
			switch (flag) {
				case ERROR_FILE:
					onDataChangeListener.onToast("文件打开失败");
					fileSucc = false;
					break;
				case SUCC:
					fileSucc = true;
					break;
			}
		}
	}

	public void updata() {
		if (is != null && os != null) {
			ymodem.setClientOutputStream(os);
			ymodem.resetSendState();
			if (fileSucc) {
				try {
					os.write("+UPDATA".getBytes());//通知接收器发送第一条命令
					Ymodem.setInitiativeStart(true);//防止板子不经允许就自动发C升级
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				onDataChangeListener.onToast("请重新打开文件");
			}
		} else {
			onDataChangeListener.onToast("监听不到接收方");
		}

	}

	/**
	 * 注销广播
	 */
	public void onDestory() {
		context.unregisterReceiver(receiver);
		try {
			if (is != null) {
				is.close();
			} else {
				onDataChangeListener.onStateChange("已断开");
			}
			if (os != null) {
				os.close();
			} else {
				onDataChangeListener.onStateChange("已断开");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 等待dialog
	 *
	 * @return
	 */
	private ProgressDialog showWaitingDialog() {
		ProgressDialog waitingDialog = new ProgressDialog(context);
		waitingDialog.setTitle("请稍等");
		waitingDialog.setMessage("搜索中...");
		waitingDialog.setIndeterminate(true);
		waitingDialog.setCancelable(false);
		waitingDialog.show();
		return waitingDialog;
	}

	public interface OnDataChangeListener {
		/**
		 * 连接状态
		 *
		 * @param state
		 */
		void onStateChange(String state);

		void onReceiveChange(List<ElectricityParameter> eps);

		void onToast(String toast);

		/**
		 * 更新ListView适配器
		 */
		void onArrayAdapter();

		void onProgress(float progress);
		void onDebug(String s);
	}

	public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
		this.onDataChangeListener = onDataChangeListener;
	}

}
