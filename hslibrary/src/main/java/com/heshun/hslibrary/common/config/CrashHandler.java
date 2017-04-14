package com.heshun.hslibrary.common.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSONObject;
import com.heshun.hslibrary.common.http.HttpConnection;
import com.heshun.hslibrary.common.http.ResultHandler;
import com.heshun.hslibrary.common.util.LocalFileManager;
import com.heshun.hslibrary.common.util.NetUtil;
import com.heshun.hslibrary.common.util.UiUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局捕获的异常处理器，用于手机用户错误日志，方便调试
 *
 * @author huangxz
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private Context mContext;

	private UncaughtExceptionHandler mDefaultHandler;

	private CrashLog crashLog = null;

	private int count = 0;

	private static final String CRASHFILENAME = "error.log";


	/**
	 * CrashHandler实例
	 */

	private CrashHandler() {
	}

	private static CrashHandler instance;

	/**
	 * 获取CrashHandler实例 ,单例模式
	 */

	public static CrashHandler getInstance() {
		synchronized (CrashHandler.class) {
			if (instance == null) {
				instance = new CrashHandler();
			}
		}

		return instance;
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		// 如果用户没有处理则让系统默认的异常处理器来处理
		if (!handleException(ex)) {
			if (mDefaultHandler != null) {
				mDefaultHandler.uncaughtException(thread, ex);
			}
		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			BaseApplication.getInstance().exit();
		}
	}

	/**
	 * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 *
	 * @param ex Throwable
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(Throwable ex) {
		crashLog = new CrashLog();
		if (ex == null) {
			return false;
		} else {
			// 收集设备参数信息
			collectDeviceInfo(mContext);
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					UiUtil.toast("很抱歉,程序出现异常序出现异常,即将退出.");
//					Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}.start();
			ex.printStackTrace();
			// 判断当前网络是否可用，如果可用则直接上传至服务器，否则将异常存储在外围设备保存日志文件,让用户自行决定是否提交
			if (NetUtil.isAvailable()) {
				uploadLog(ex);
			}
			saveCrashLog2File(ex);
			return true;
		}
	}

	/**
	 * 收集设备参数信息
	 *
	 * @param context 上下文
	 */
	@SuppressLint("SimpleDateFormat")
	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				crashLog.setVersionName(versionName);
				crashLog.setVersionCode(versionCode);
			}
		} catch (PackageManager.NameNotFoundException e) {
		}
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		Field[] fields = Build.class.getDeclaredFields();
		crashLog.setWidth(String.valueOf(dm.widthPixels));
		crashLog.setHeight(String.valueOf(dm.heightPixels));
		crashLog.setDpi(String.valueOf(dm.densityDpi));
		crashLog.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.getName().equalsIgnoreCase("id")) {
				} else if (field.getName().equalsIgnoreCase("display")) {
					crashLog.setDisplay(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("product")) {
					crashLog.setProduct(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("device")) {
					crashLog.setDevice(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("board")) {
					crashLog.setBoard(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("cpu_abi")) {
					crashLog.setCpuAbi(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("cpu_abi2")) {
					crashLog.setCpuAbi2(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("manufacturer")) {
					crashLog.setManufacturer(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("brand")) {
					crashLog.setBrand(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("model")) {
					crashLog.setModel(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("bootloader")) {
					crashLog.setBootloader(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("radio")) {
					crashLog.setRadio(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("hardware")) {
					crashLog.setHardware(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("serial")) {
					crashLog.setSerial(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("type")) {
					crashLog.setType(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("tags")) {
					crashLog.setTags(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("fingerprint")) {
					crashLog.setFingerprint(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("user")) {
					crashLog.setUser(field.get(null).toString());
				} else if (field.getName().equalsIgnoreCase("host")) {
					crashLog.setHost(field.get(null).toString());
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 *
	 * @param ex Throwable 异常
	 * @return 返回文件名称, 便于将文件传送到服务器
	 */
	private String saveCrashLog2File(Throwable ex) {
		StringBuilder sb = new StringBuilder();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		sb.append(writer.toString());
		if (crashLog != null) {
			crashLog.setError(sb.toString());
		}
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = LocalFileManager.getInstance().getCrashLogDir();
				File file = new File(dir.getPath() + "/" + CRASHFILENAME);
				if (!file.exists()) {
					file.createNewFile();
				}

				fos = new FileOutputStream(file);
				oos = new ObjectOutputStream(fos);
				if (null != crashLog)
					oos.writeObject(crashLog.toString());
			}
			return CRASHFILENAME;
		} catch (Exception e) {
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 上传异常日志
	 *
	 * @param ex 异常日志内容
	 */
	private void uploadLog(Throwable ex) {
		StringBuilder sb = new StringBuilder();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		sb.append(writer.toString());
		if (crashLog != null) {
			crashLog.setError(sb.toString());
		}

		// Map<String, Object> params = new HashMap<String, Object>();
		// params.put("resultData", JSON.toJSONString(crashLog));
		JSONObject _json = (JSONObject) JSONObject.toJSON(crashLog);
		HttpConnection.getConnection().httpPostViaJson("logError/saveLogError", _json, new ResultHandler() {
			@Override
			protected void onSuccess(String response) {
			}
		});

	}
}
