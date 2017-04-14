package com.heshun.hslibrary.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.heshun.hslibrary.common.config.BaseApplication;


/**
 * 网络工具类
 *
 * @author chenw
 */
public class NetUtil {
	public static final int NETWORN_NONE = 0;
	public static final int NETWORN_WIFI = 1;
	public static final int NETWORN_MOBILE = 2;
	public static boolean checkEnable(Context paramContext) {
		boolean i = false;
		NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
			return true;
		return false;
	}

	/**
	 * 将ip的整数形式转换成ip形式
	 *
	 * @param ipInt
	 * @return
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * 获取当前ip地址
	 *
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			return int2ip(i);
		} catch (Exception ex) {
			return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
		}
	}

	/**
	 * 判断网络是否可用
	 */
	public static boolean isAvailable() {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) BaseApplication.getContextInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		return flag;
	}

	/**
	 * 判断wifi是否可用
	 */
	public static boolean isWiFiAvailable() {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) BaseApplication.getContextInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断手机移动网络是否可用
	 *
	 * @return boolean
	 */
	public static boolean isCellNetworkAvailable() {
		TelephonyManager telephoneManager = (TelephonyManager) BaseApplication.getContextInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		ConnectivityManager manager = (ConnectivityManager) BaseApplication.getContextInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final int networkType = telephoneManager.getNetworkType();
		switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_UMTS: {
				NetworkInfo networkInfo = manager.getNetworkInfo(networkType);
				return null != networkInfo && networkInfo.isAvailable();
			}
			default:
				return false;
		}
	}

	public static String getNetworkType() {
		String type = "未知";
		if (isWiFiAvailable()) {
			type = "Wifi";
		} else {
			ConnectivityManager manager = (ConnectivityManager) BaseApplication.getContextInstance()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info != null) {
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {
					type = "Wifi";
				} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					switch (info.getSubtype()) {
						case TelephonyManager.NETWORK_TYPE_GPRS:
						case TelephonyManager.NETWORK_TYPE_EDGE:
						case TelephonyManager.NETWORK_TYPE_CDMA:
						case TelephonyManager.NETWORK_TYPE_1xRTT:
						case TelephonyManager.NETWORK_TYPE_IDEN:
							type = "2G";
							return getOperatorName().concat(type);
						case TelephonyManager.NETWORK_TYPE_UMTS:
						case TelephonyManager.NETWORK_TYPE_EVDO_0:
						case TelephonyManager.NETWORK_TYPE_EVDO_A:
						case TelephonyManager.NETWORK_TYPE_HSDPA:
						case TelephonyManager.NETWORK_TYPE_HSUPA:
						case TelephonyManager.NETWORK_TYPE_HSPA:
						case TelephonyManager.NETWORK_TYPE_EVDO_B:
						case TelephonyManager.NETWORK_TYPE_EHRPD:
						case TelephonyManager.NETWORK_TYPE_HSPAP:
							type = "3G";
							return getOperatorName().concat(type);
						case TelephonyManager.NETWORK_TYPE_LTE:
							type = "4G";
							return getOperatorName().concat(type);
						default:
							break;

					}
				}
			}
		}
		return type;
	}

	private static String getOperatorName() {
		TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getContextInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telephonyManager.getSimOperator();
		String operatorName = "";
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")) {
				operatorName = "移动";
			} else if (operator.equals("46001")) {
				operatorName = "联通";
			} else if (operator.equals("46003")) {
				operatorName = "电信";
				;
			}
		}
		return operatorName;
	}

	/**
	 * 判断网络状态
	 * @param context
	 * @return
	 */
	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_WIFI;
		}

		// 3G
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_MOBILE;
		}
		return NETWORN_NONE;
	}

}
