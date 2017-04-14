package com.heshun.hslibrary.common.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.config.Config;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 本地文件管理 User: chenw Date: 13-10-17 Time: 上午11:25
 */
public class LocalFileManager {

	private static final String TAG = "LocalFileManager";
	private static Context mAppContext;
	private static LocalFileManager instance = new LocalFileManager();

	private LocalFileManager() {
	}

	public static void initContext(Context context) {
		mAppContext = context;
	}

	public static LocalFileManager getInstance() {
		return instance;
	}

	/**
	 * 获取某个目录下的文件数，不包括嵌套的目录
	 * 
	 * @param dir
	 *            指定统计目录
	 * @return int指定目录下文件个数
	 */
	public final int countDirFiles(File dir) {
		int count = 0;
		File[] subFiles = dir.listFiles();
		if (null != subFiles && subFiles.length > 0) {
			for (File file : subFiles) {
				if (file.isFile()) {
					count++;
				} else if (file.isDirectory()) {
					count += countDirFiles(file);
				}
			}
		}
		return count;
	}

	/**
	 * 检索指定目录下的所有文件
	 * 
	 * @param dir
	 *            指定的目录
	 * @return List<File>
	 */
	public final List<File> listFiles(File dir) {
		List<File> fileList = null;
		if (null != dir && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (null != files) {
				fileList = Arrays.asList(files);
			}
		}
		if (null == fileList) {
			fileList = Collections.emptyList();
		}
		return fileList;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 *            指定文件路径
	 */
	public final void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			// file.delete();
		} else {
			System.out.println("所删除的文件不存在！" + '\n');
		}
	}

	/**
	 * 获取本应用安装包名称
	 * 
	 * @return 安装包名称
	 */
	public final File getPackageName() {
		return new File(getSdCardDir(), mAppContext.getPackageName());
	}

	/**
	 * 删除指定目录下全部文件
	 * 
	 * @param dir
	 *            指定目录路径
	 */
	public final void deleteAllFiles(File dir) {
		boolean exceptionOccurred = false;
		if (null != dir && dir.isDirectory()) {
			File[] subFiles = dir.listFiles();
			if (null != subFiles) {
				for (File file : subFiles) {
					try {
						deleteFile(file);
					} catch (Exception e) {
						e.printStackTrace();
						exceptionOccurred |= true;
					}
				}
			}
		}
		if (exceptionOccurred) {
			UiUtil.toast(mAppContext.getString(R.string.partial_delete_failure));
			Log.e("LocalFileManager", mAppContext.getString(R.string.partial_delete_failure));
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 *            File
	 */
	public final void createFile(File file) {
		synchronized (this) {
			if (null != file && file.isFile()) {
				try {
					file.createNewFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param dir
	 *            目录名称
	 */
	public final void createDir(File dir) {
		synchronized (this) {
			if (null != dir && !dir.exists()) {
				dir.mkdirs();
				grantPermission(dir);
			}
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param dir
	 */
	public final File createAppDir(String dir) {
		synchronized (this) {
			String _path = String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, dir);
			File _dir = new File(_path);
			if (null != dir && !_dir.exists()) {
				boolean rt = _dir.mkdirs();
				grantPermission(_dir);
				return _dir;
			}
			return null;
		}
	}

	/**
	 * 在外部存储卡中创建文件
	 * 
	 * @param filePath
	 *            创建文件路径名称
	 * @return File
	 */
	public final File createExtFile(String filePath) {
		File baseFile = new File(
				String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, Config.APP_TEMP_DIR));
		if (!baseFile.exists()) {
			baseFile.mkdirs();
		}
		File file = new File(baseFile, filePath);
		if (file.exists()) {
			file.delete();
		}
		try {
			boolean rt = file.createNewFile();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 在外部存储卡中创建目录
	 * 
	 * @param subDir
	 *            目录地址
	 * @return File
	 */
	public final File createExtDir(String subDir) {
		synchronized (this) {
			File _dir = new File(String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, subDir));
			if (null != subDir && !_dir.exists()) {
				boolean rt = _dir.mkdirs();
				grantPermission(_dir);
				return _dir;
			}
			return null;
		}
	}

	/**
	 * 获取缓存目录
	 * 
	 * @return File
	 */
	public final File getDataDir(String subdir) {
		File parent = new File(
				String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, Config.APP_DATA_DIR));
		File child = new File(parent, subdir);
		if (!child.exists()) {
			if (!child.mkdirs()) {
				Log.e("", "Unable to create external cache directory");
				return null;
			}
			try {
				new File(child, ".nomedia").createNewFile();// 避免被外部程序扫描
			} catch (IOException e) {
				Log.e("",
						"Can't create \".nomedia\" file in com.oceansoft.webview.cache.application external cache directory");
			}
		}
		return child;
	}

	/**
	 * 获取缓存目录
	 * 
	 * @return File
	 */
	public final File getCacheDir(String subdir) {
		File parent = new File(
				String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, Config.APP_CACHE_DIR));
		File child = new File(parent, subdir);
		if (!child.exists()) {
			if (!child.mkdirs()) {
				Log.e("", "Unable to create external cache directory");
				return null;
			}
			try {
				new File(child, ".nomedia").createNewFile();// 避免被外部程序扫描
			} catch (IOException e) {
				Log.e("",
						"Can't create \".nomedia\" file in com.oceansoft.webview.cache.application external cache directory");
			}
		}
		return child;
	}

	/**
	 * 创建系统日志目录
	 * 
	 * @return File
	 */
	public final File getLogDir() {
		File dir = new File(
				String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, Config.APP_LOG_DIR));
		if (dir.isDirectory() && !dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e("", "Unable to create external cache directory");
				return null;
			}
			try {
				new File(dir, ".nomedia").createNewFile();// 避免被外部程序扫描
			} catch (IOException e) {
				Log.e("",
						"Can't create \".nomedia\" file in com.oceansoft.webview.cache.application external cache directory");
			}
		}
		return dir;
	}

	/**
	 * 创建异常日志目录
	 * 
	 * @return File
	 */
	public final File getCrashLogDir() {
		File parent = new File(
				String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, Config.APP_LOG_DIR));
		File child = new File(parent, Config.APP_LOG_CRASH_DIR);
		if (!child.exists()) {
			if (!child.mkdirs()) {
				Log.e("", "Unable to create external log directory");
				return null;
			}
			try {
				new File(child, ".nomedia").createNewFile();// 避免被外部程序扫描
			} catch (IOException e) {
				Log.e("",
						"Can't create \".nomedia\" file in com.oceansoft.webview.cache.application external log directory");
			}
		}
		return child;
	}

	/**
	 * 获取 应用列表下载存放目录
	 * 
	 * @return
	 */
	public final File getAppDownloadDir() {
		File parent = new File(
				String.format("%1$s/%2$s/%3$s", getSdCardRootPath(), Config.APP_ROOT_DIR, Config.APP_DOWNLOAD_DIR));
		File child = new File(parent, Config.APP_DOWNLOAD_SUB_DIR);
		if (!child.exists()) {
			if (!child.mkdirs()) {
				Log.e("", "Unable to create external log directory");
				return null;
			}
			grantPermission(child);
		}
		return child;
	}

	/**
	 * 清除缓存
	 */
	public final void cleanCache() {
		deleteAllFiles(FileUtil.getCacheRootFile());
		ImageLoader.getInstance().clearDiscCache();
		ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * 获取缓存目录大小
	 * 
	 * @return String
	 */
	public final String getCacheSize() {
		return getPathSize(FileUtil.getCacheRootFile().getAbsolutePath());
	}

	/**
	 * <获取文件夹或者文件大小>
	 * 
	 * @param path
	 *            指定目录路径或者文件
	 * @return String 文件的大小，以BKMG来计量
	 */
	public static String getPathSize(String path) {
		String flieSizesString = "";
		File file = new File(path.trim());
		long fileSizes = 0;
		if (null != file && file.exists()) {
			if (file.isDirectory()) { // 如果路径是文件夹的时候
				fileSizes = getFolderTotalSize(file);
			} else if (file.isFile()) {
				fileSizes = file.length();
			}
		}
		flieSizesString = formatFileSizeToString(fileSizes);
		return flieSizesString;
	}

	private static long getFolderTotalSize(File fileDir) {
		long totalSize = 0;
		File fileList[] = fileDir.listFiles();
		for (File aFileList : fileList) {
			if (aFileList.isDirectory()) {
				totalSize = totalSize + getFolderTotalSize(aFileList);
			} else {
				totalSize = totalSize + aFileList.length();
			}
		}
		return totalSize;
	}

	private static String formatFileSizeToString(long fileSize) {// 转换文件大小
		String fileSizeString = "";
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		if (fileSize == 0) {
			fileSizeString = "0.0K";
		} else if (fileSize < 1024) {
			fileSizeString = decimalFormat.format((double) fileSize) + "B";
		} else if (fileSize < (1 * 1024 * 1024)) {
			fileSizeString = decimalFormat.format((double) fileSize / 1024) + "K";
		} else if (fileSize < (1 * 1024 * 1024 * 1024)) {
			fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024 * 1024)) + "M";
		} else {
			fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024 * 1024 * 1024)) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 查找指定目录匹配文件
	 * 
	 * @param dir
	 *            指定目录
	 * @param filenamePattern
	 *            文件名称匹配串
	 * @return List<File>
	 */
	public final List<File> findMatchedFiles(File dir, String filenamePattern) {
		if (null == dir || !dir.exists() || !dir.isDirectory()) {
			return Collections.<File> emptyList();
		}
		List<File> matchedFiles = new ArrayList<File>(10);
		internalFindMatchedFiles(matchedFiles, dir, filenamePattern);
		return matchedFiles;
	}

	/**
	 * 文件匹配查找
	 * 
	 * @param matchedFileList
	 *            匹配文件列表
	 * @param dir
	 *            目录名称
	 * @param filenamePattern
	 *            文件名称通配符
	 */
	private void internalFindMatchedFiles(List<File> matchedFileList, File dir, String filenamePattern) {
		File[] subFiles = dir.listFiles();
		if (null != subFiles) {
			for (File file : subFiles) {
				if (file.isFile()) {
					if (file.getName().replaceFirst("^(.*)(?:\\.\\w+$)", "$1").contains(filenamePattern)) {
						matchedFileList.add(file);
					}
				} else {
					internalFindMatchedFiles(matchedFileList, file, filenamePattern);
				}
			}
		}
	}

	/**
	 * 获取文件权限(755)
	 * 
	 * @param file
	 *            指定目录名称
	 */
	public final void grantPermission(File file) {
		try {
			Runtime.getRuntime().exec("chmod 755 ".concat(file.getName()));
		} catch (IOException e) {
			Log.e(TAG, "GRANT permission 755 to file [".concat(file.getName()).concat("] failed!"));
		}
	}

	/**
	 * 判断当前SdCard是否已经挂载
	 * 
	 * @return true 正常 false 未挂载F
	 */
	public final boolean isSdCardMounted() {
		String _status = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(_status);
	}

	/**
	 * 检测系统SdCard状态
	 */
	public boolean checkSdCard() {
		String sdCardState = Environment.getExternalStorageState();
		if (sdCardState.equals(Environment.MEDIA_REMOVED)) {// SdCard已移动
			Toast.makeText(mAppContext, mAppContext.getString(R.string.sdcard_removed), Toast.LENGTH_SHORT).show();
			return false;
		} else if (sdCardState.equals(Environment.MEDIA_CHECKING)) {
			Toast.makeText(mAppContext, mAppContext.getString(R.string.sdcard_checking), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!sdCardState.equals(Environment.MEDIA_MOUNTED)) {// SdCard未挂载
			Toast.makeText(mAppContext, mAppContext.getString(R.string.sdcard_unmounted), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 获取外部存储卡根目录
	 * 
	 * @return File
	 */
	public final File getSdCardDir() {
		try {
			return Environment.getExternalStorageDirectory();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取外部存储卡目录
	 * 
	 * @param dirName
	 *            外部目录名称
	 * @return 绝对路径
	 */
	public final File getExtDir(String dirName) {
		return new File(String.format("%1$s/edsz/%2$s", getSdCardRootPath(), dirName));
	}

	/**
	 * 获取SdCard根目录
	 * 
	 * @return SdCard根目录路径
	 */
	public String getSdCardRootPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 创建临时文件
	 * 
	 * @param fileName
	 *            临时文件名称
	 * @return File
	 */
	public File createTempFile(String fileName) {
		return createExtFile(fileName);
	}

	public File getTempAvatar() {
		return getExtDir("avatar.jpg");
	}

	public File getTempPic(String fileName) {
		return getExtDir(fileName);
	}
}
