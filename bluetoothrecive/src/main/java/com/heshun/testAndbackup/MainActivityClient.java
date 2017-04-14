package com.heshun.testAndbackup;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heshun.YModem.Ymodem;
import com.heshun.bluetoothrecive.R;
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
 * 2017/3/13 09:26
 */
public class MainActivityClient extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

	private static final String TAG = "BluetoothConnect";
	private ListView lvDevices;
	private BluetoothAdapter mBluetoothAdapter;
	private List<String> bluetoothDevices = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口模块

	//作为客户端（发送）
	private BluetoothSocket clientSocket;
	private BluetoothDevice device;
	private OutputStream os;//全局输出流，作为客户端发送数据使用
	private InputStream is;//全局输入流，服务监听使用
	private Button btn;
	private Button btn_get;
	private Button btn_add;
	private Button btn_sub;
	private Button btn_start;
	private Button btn_stop;
	private Button btn_send;
	private TextView textView;
//	private TextView tv_data;
	private TextView tv_filepath;
	private String filePath="";


	private Thread receiceThread;

	private ProgressDialog waitingDialog;
	private Ymodem ymodem;

	private List<byte[]> packageFrames;
	private boolean isFirstFrame = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initDevice();
	}

	/**
	 * 初始化控件和设备
	 */
	private void initDevice() {
		ymodem = new Ymodem();

		btn = (Button) findViewById(R.id.btn_search);
		btn_get = (Button) findViewById(R.id.btn_get);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_send = (Button) findViewById(R.id.btn_send);
		textView = (TextView) findViewById(R.id.tv2);
		tv_filepath = (TextView) findViewById(R.id.tv_filepath);
//		tv_data = (TextView) findViewById(R.id.tv_data);
		lvDevices = (ListView) findViewById(R.id.lv_devices);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


		//获取已经配对的蓝牙设备
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				bluetoothDevices.add(device.getName() + ":" + device.getAddress());
			}
		}
		//初始化list
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, bluetoothDevices);
		lvDevices.setAdapter(arrayAdapter);
		lvDevices.setOnItemClickListener(this);

		//每搜索到一个设备就会发送一个该广播
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		//当全部搜索完后发送该广播
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(receiver, filter);


		btn.setOnClickListener(this);
		btn_get.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		btn_sub.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_send.setOnClickListener(this);

		receiceThread=new Thread(new ReceiveSrvData());

	}

	/**
	 * 客户端收到服务端数据的线程
	 */
	class ReceiveSrvData implements Runnable{

		@Override
		public void run() {
			try {
				while (true) {
					byte[] temp = new byte[1029];
					is.read(temp);
					byte flag = temp[0];
					String charlization = new String(temp, 0, temp.length, "utf-8").trim();//用于判断"C"或者"JLZ="

					Log.e("------receive----", Arrays.toString(temp) );
//					Log.e("----------- ----", "run: "+charlization+ Arrays.toString(temp) );
					if (flag == Ymodem.NAK || flag == Ymodem.CAN || flag == Ymodem.ACK || flag == Ymodem.CHAR_C) {
						if (packageFrames != null) {//包不空
							if (charlization.length() == 1 && flag == Ymodem.CHAR_C && isFirstFrame) {//接到接收端的请求（第一帧）
								int state = ymodem.send(packageFrames, Ymodem.CHAR_C);
								deelState(state);
							} else {//接下来的帧
								int state = ymodem.send(packageFrames, flag);
								deelState(state);
							}
						} else {
							Message msg = new Message();
							msg.obj = "文件为空";
							handler.sendMessage(msg);
						}

					} else if (charlization.startsWith("JLZ=")) {
						final String data = new String(temp, 0, temp.length).trim();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
//								tv_data.setText(data);
							}
						});
					}else{
//						Log.e("----------- ----", "run: "+charlization+ Arrays.toString(temp) );
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								Toast.makeText(MainActivityClient.this, "收到但是未解析", Toast.LENGTH_SHORT).show();
//							}
//						});
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
				handler.sendMessage(msg);
				break;
			case Ymodem.STATE_FORCE_STOP:
				msg.obj = "传输被强停";
				handler.sendMessage(msg);
				isFirstFrame = true;
				break;
			case Ymodem.STATE_MORE_RETRY:
				msg.obj = "重试次数太多被强停";
				handler.sendMessage(msg);
				isFirstFrame = true;
				break;
			case Ymodem.STATE_NEXT:
				//下一个包
				isFirstFrame = false;
				break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		String s = arrayAdapter.getItem(position);
		String address = s.substring(s.indexOf(":") + 1).trim();//把地址解析出来
		//异步连接
		new ConnectTask().execute(address);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_search:
				//如果当前在搜索，就先取消搜索
				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}
				if (waitingDialog == null) {
					waitingDialog = showWaitingDialog();
				}
				//开启搜索
				mBluetoothAdapter.startDiscovery();
				break;
			case R.id.btn_get:
				FileUtils.showFileChooser(this);
				break;
			case R.id.btn_add:
				sendCommend("+VOICE");
				break;
			case R.id.btn_sub:
				sendCommend("-VOICE");
				break;
			case R.id.btn_start:
				sendCommend("+START");
				break;
			case R.id.btn_stop:
				sendCommend("+STOP");
				break;
			case R.id.btn_send://发文件

				new FileTask().execute(filePath);//打开流并发送
//				refreshBluetooth();
				break;

		}
	}

	/**
	 * 字符命令
	 *
	 * @param com
	 */
	private void sendCommend(String com) {
		if (os != null) {
			try {
				os.write(com.getBytes("utf-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Toast.makeText(this, "还未连接设备", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 异步加载文件
	 */
	private class FileTask extends AsyncTask<String, Void, Integer> {
		 static final int ERROR_PIPE = 0;
		 static final int ERROR_FILE = 1;
		 static final int SUCC = 2;


		@Override
		protected Integer doInBackground(String... strings) {
			if (is != null && os != null) {
				ymodem.setClientOutputStream(os);
			} else {
				return ERROR_PIPE;
			}
			try {
				File file = new File(strings[0]);
				InputStream inputStream = new FileInputStream(file);
				packageFrames = Ymodem.getPackage(inputStream,file.getName(),FileUtils.getFileOrFilesSize(strings[0],FileUtils.SIZETYPE_B));
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
				case ERROR_PIPE:
					Toast.makeText(MainActivityClient.this, "监听不到接收方", Toast.LENGTH_SHORT).show();
					break;
				case ERROR_FILE:
					Toast.makeText(MainActivityClient.this, "文件打开失败", Toast.LENGTH_SHORT).show();
					break;
				case SUCC:
					try {
						os.write("+UPDATA".getBytes());//通知接收器发送第一条命令
						Ymodem.setInitiativeStart(true);//防止板子不经允许就自动发C升级
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
			}
		}
	}

	/**
	 * 连接到远程监听端的异步任务
	 */
	private class ConnectTask extends AsyncTask<String, Void, Integer> {

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
			textView.setText("连接中……");
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
						is=clientSocket.getInputStream();
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
					textView.setText("执行连接成功");
					break;
				case DEVICE_HAS_CONNECT:
					textView.setText("已经连接此设备了");
					break;
				case CLIENTSOCKET_ALIVE:
					textView.setText("已连接");
					break;
				case DEVICE_ERROR:
					textView.setText("DEVICE_ERROR：获取远程连接失败" + "\n请检测\n1、配对设备是否开启蓝牙\n" +
							"2、是否被占用" + "\n然后尝试重连");
					break;
				case DEVICE_SAME:
					textView.setText("DEVICE_SAME");
					break;
				case DEVICE_SINGLE_WARNING:
					textView.setText("DEVICE_SINGLE_WARNING:建议不要切换");
					break;
				case OS_NULL:
					textView.setText("OS_NULL");
					break;
				case UNKNOWN_ERROR:
					textView.setText("UNKNOWN_ERROR:可能是socket pipe中断" + "\n请检测\n1、配对设备是否开启蓝牙\n" +
							"2、是否被占用" + "\n然后尝试重连");
					break;
				default:
					textView.setText("执行连接成功");

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
						arrayAdapter.notifyDataSetChanged();//更新适配器
					}
					break;
				case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
					//已搜索完成
					waitingDialog.dismiss();
					waitingDialog = null;
					break;
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
		if (clientSocket != null && clientSocket.isConnected()) {
			try {
				clientSocket.close();
				//重置RfCOMM通道便于下次与服务连接
				clientSocket = null;
				textView.setText("未连接");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 等待dialog
	 *
	 * @return
	 */
	private ProgressDialog showWaitingDialog() {
		ProgressDialog waitingDialog = new ProgressDialog(MainActivityClient.this);
		waitingDialog.setTitle("请稍等");
		waitingDialog.setMessage("搜索中...");
		waitingDialog.setIndeterminate(true);
		waitingDialog.setCancelable(false);
		waitingDialog.show();
		return waitingDialog;
	}

	/**
	 * 文件选择器回调
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case FileUtils.FILE_SELECT_CODE:
				if (resultCode == RESULT_OK) {
					Uri uri = data.getData();
					filePath = FileUtils.getPath(this, uri);
					btn_send.setVisibility(View.VISIBLE);
					tv_filepath.setText(filePath);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
