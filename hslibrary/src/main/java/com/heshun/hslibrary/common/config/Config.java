package com.heshun.hslibrary.common.config;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 基本配置信息
 * 
 * @author huangxz
 *
 */
public class Config {
	/**
	 * 应用名称
	 */
//	public final String APP_NAME = "EDriveSuzhou";
//
//	public static String WX_APP_ID = "wx23127eb21899529d";
//
//	public static String PUSH_PRE_FIX = "Ddsz_%s";

	// /**
	// * 测试服务器IP地址
	// */
	//
	// public static String SERVER = "http://172.31.59.71:9000/cpm/api/app/";
	//
	// public static String UPLOAD = "http://172.31.59.70:9000/cpm";

	/**
	 * 真实服务器IP地址
	 */

	public static String SERVER = "http://sz.app.jsclp.cn/cpm/api/app/";

	public static String UPLOAD = "http://sz.jsclp.cn/cpm";


	/**
	 * 应用缓存根目录
	 */
	public static final String APP_ROOT_DIR = ".EDriveSuzhou";

	/**
	 * 应用缓存根目录
	 */
	public static final String APP_CACHE_DIR = "cache";

	/**
	 * 应用数据目录
	 */
	public static final String APP_DATA_DIR = "data";

	/**
	 * 临时文件夹
	 */
	public static final Object APP_TEMP_DIR = "temp";
	/**
	 * 图片缓存目录
	 */
	public static final String IMAGE_CACHE_DIR = "image";

	/**
	 * 系统日志目录
	 */
	public static final String APP_LOG_DIR = "log";

	/**
	 * 系统崩溃日志目录
	 */
	public static final String APP_LOG_CRASH_DIR = "crash";
	/**
	 * APK安装包下载地址
	 */
	public static final String APP_UPGRADE_DIR = "upgrade";

	/**
	 * 下载目录
	 */
	public static final String APP_DOWNLOAD_DIR = "download";

	/**
	 * 应用列表下载时的存放目录
	 */
	public static final String APP_DOWNLOAD_SUB_DIR = "apps";

	/**
	 * 视频录制路径
	 */

	public static int THREAD_COUNT = 3;

	public static final String NETROID_CACHE_DIR = "netroid_cache";

	public static final int NETROID_CACHE_SIZE = 2 * 1024 * 1024;

	// 客户端变量
	private static int serverVersionCode = 1;
	private static String serverVersionName;
	private static int localVersionCode = 1;
	private static String localVersionName;
	private static String serverApkDownloadUrl;
	private static String serverApkSize;
	private static String deviceName;
	private static String deviceMac;
	private static long orgId = 50;

	public static int getLocalVersionCode() {
		return localVersionCode;
	}

	public static void setLocalVersionCode(int localVersionCode) {
		Config.localVersionCode = localVersionCode;
	}

	public static int getServerVersionCode() {
		return serverVersionCode;
	}

	public static void setServerVersionCode(int serverVersionCode) {
		Config.serverVersionCode = serverVersionCode;
	}

	public static String getServerVersionName() {
		return serverVersionName;
	}

	public static void setServerVersionName(String serverVersionName) {
		Config.serverVersionName = serverVersionName;
	}

	public static String getLocalVersionName() {
		return localVersionName;
	}

	public static void setLocalVersionName(String localVersionName) {
		Config.localVersionName = localVersionName;
	}

	public static String getServerApkDownloadUrl() {
		return serverApkDownloadUrl;
	}

	public static void setServerApkDownloadUrl(String serverApkDownloadUrl) {
		Config.serverApkDownloadUrl = serverApkDownloadUrl;
	}

	public static String getServerApkSize() {
		return serverApkSize;
	}

	public static void setServerApkSize(String serverApkSize) {
		Config.serverApkSize = serverApkSize;
	}

	public static String getDeviceName() {
		return deviceName;
	}

	public static void setDeviceName(String deviceName) {
		Config.deviceName = deviceName;
	}

	public static String getDeviceMac() {
		return deviceMac;
	}

	public static void setDeviceMac(String deviceMac) {
		Config.deviceMac = deviceMac;
	}

	/**
	 * 获取系统异常退出日志名称
	 * 
	 * @return String生成唯一日志名称
	 */
	public static String getCrashLogName() {
		return String.format("%1$s_%2$s_%3$d.log", getDeviceName(),
				new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), ((int) (Math.random() * 1000)) + 1000);
	}

	/**
	 * 获取cpu核心数
	 * 
	 * @return
	 */
	public static int getNumCores() {
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new CpuFilter());
			Log.d("OnlineStudy", "CPU Count: " + files.length);
			return files.length;
		} catch (Exception e) {
			Log.d("OnlineStudy", "CPU Count: Failed.");
			e.printStackTrace();
			return 1;
		}
	}

	public static long getOrgId() {
		return orgId;
	}

	public static void setOrgId(long orgId) {
		Config.orgId = orgId;
	}
}
