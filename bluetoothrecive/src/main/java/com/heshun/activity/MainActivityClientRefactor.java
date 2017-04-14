package com.heshun.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heshun.adapter.EPAdapter;
import com.heshun.bluetoothrecive.R;
import com.heshun.blutoothUtils.BluetoothClient;
import com.heshun.entity.ElectricityParameter;
import com.heshun.tools.FileUtils;

import java.util.List;

/**
 * author：Jics
 * 2017/3/13 09:26
 */
public class MainActivityClientRefactor extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, BluetoothClient.OnDataChangeListener {

	private BluetoothClient bluetoothClient;
	private ListView lvDevices;
	private ArrayAdapter<String> arrayAdapter;

	private Button btn;
	private Button btn_get;
	private Button btn_add;
	private Button btn_sub;
	private Button btn_start;
	private Button btn_stop;
	private Button btn_send;
	private TextView textView;
	private TextView tv_debug;
	//	private TextView tv_data;
	private TextView tv_filepath;
	private String filePath = "";
	private RecyclerView recyclerView;
	private EPAdapter adapter;
	private List<ElectricityParameter> eps;

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
		bluetoothClient = new BluetoothClient(this);
		bluetoothClient.setOnDataChangeListener(this);

		recyclerView = (RecyclerView) findViewById(R.id.rv_eplist);
		eps = FileUtils.analysisWords2entity("JLZ=VOL:0V,COR:0A,ELC:0Kwh,TIME:0MIN,STATE:0=JLZ");
		btn = (Button) findViewById(R.id.btn_search);
		btn_get = (Button) findViewById(R.id.btn_get);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_send = (Button) findViewById(R.id.btn_send);
		textView = (TextView) findViewById(R.id.tv2);
		tv_debug = (TextView) findViewById(R.id.tv_debug);
		tv_filepath = (TextView) findViewById(R.id.tv_filepath);
		lvDevices = (ListView) findViewById(R.id.lv_devices);

		//初始化list
		arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, bluetoothClient.getBluetoothDevices());
		lvDevices.setAdapter(arrayAdapter);
		lvDevices.setOnItemClickListener(this);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter = new EPAdapter(this, eps);
		recyclerView.setAdapter(adapter);

		btn.setOnClickListener(this);
		btn_get.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		btn_sub.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		textView.setOnClickListener(this);
		tv_debug.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tv_debug.setText("");
			}
		});

	}

	@Override
	public void onStateChange(final String state) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView.setText(state);
			}
		});
	}

	@Override
	public void onReceiveChange( List<ElectricityParameter> eps) {
		this.eps.clear();
		for(ElectricityParameter ep:eps){
			this.eps.add(ep);
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onToast(final String toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivityClientRefactor.this, toast, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onArrayAdapter() {
		arrayAdapter.notifyDataSetChanged();//更新适配器
	}

	@Override
	public void onProgress(final float progress) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btn_send.setText(String.format("%s %s", "UPDATA", (int) progress + "%"));
			}
		});
	}

	@Override
	public void onDebug(final String s) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv_debug.append(s);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		String s = arrayAdapter.getItem(position);
		String address = s.substring(s.indexOf(":") + 1).trim();//把地址解析出来
		//异步连接
		bluetoothClient.new ConnectTask().execute(address);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_search:
				bluetoothClient.deviceSearch();
				break;
			case R.id.btn_get:
				FileUtils.showFileChooser(this);
				break;
			case R.id.btn_add:
				bluetoothClient.sendCommend("+VOICE");
				break;
			case R.id.btn_sub:
				bluetoothClient.sendCommend("-VOICE");
				break;
			case R.id.btn_start:
				bluetoothClient.sendCommend("+START");
				break;
			case R.id.btn_stop:
				bluetoothClient.sendCommend("+STOP");
				break;
			case R.id.btn_send://发文件
				bluetoothClient.updata();
				break;
			case R.id.tv2:
				if (textView.getText().toString().contains("点击断开")) {
					if (bluetoothClient != null) {
						bluetoothClient.onDestory();
						bluetoothClient = new BluetoothClient(this);
						bluetoothClient.setOnDataChangeListener(this);
						textView.setText("正在断开连接……");
					}

				}
				break;

		}
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
					bluetoothClient.new FileTask().execute(filePath);//打开流并发送
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		bluetoothClient.onDestory();
	}

	private long pressTime;

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - pressTime > 2000) {
			Toast.makeText(MainActivityClientRefactor.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
			pressTime = currentTime;
		} else {
			System.exit(0);
		}
	}
}
