package com.heshun.hslibrary.common.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.widget.Toast;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.config.BaseApplication;
import com.heshun.hslibrary.common.config.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件操作工具类 User: chenw Date: 13-9-29 Time: 下午4:39
 */
public class FileUtil {

	/**
	 * 获取文件大小工具方法
	 * 
	 * @param size
	 *            文件大小（字节数值）
	 * @return 文件大小文本值
	 */
	public static String getFileSize(final long size) {
		if (size > 1073741824) {
			return String.format("%.2f", size / 1073741824.0) + " GB";
		} else if (size > 1048576) {
			return String.format("%.2f", size / 1048576.0) + " MB";
		} else if (size > 1024) {
			return String.format("%.2f", size / 1024.0) + " KB";
		} else {
			return size + " B";
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 *            目录路径
	 */
	public static void createPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/**
	 * 获取SdCard根目录
	 * 
	 * @param context
	 *            上下文
	 * @return SdCard根目录路径
	 */
	public static String getSdcardRootPath(Context context) {
		if (checkSdCard(context)) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return "";
	}

	/**
	 * 检测系统SdCard
	 */
	public static boolean checkSdCard(Context context) {
		String sdCardState = Environment.getExternalStorageState();
		if (sdCardState.equals(Environment.MEDIA_REMOVED)) {// SdCard已移除
			Toast.makeText(context, context.getString(R.string.sdcard_removed), Toast.LENGTH_SHORT).show();
			return false;
		} else if (sdCardState.equals(Environment.MEDIA_CHECKING)) {
			Toast.makeText(context, context.getString(R.string.sdcard_checking), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!sdCardState.equals(Environment.MEDIA_MOUNTED)) {// SdCard未挂载
			Toast.makeText(context, context.getString(R.string.sdcard_unmounted), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public static String bitmapToBase64(Bitmap bitmap) {
		String string = null;
		try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bStream);
			byte[] bytes = bStream.toByteArray();
			string = Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return string;
	}

	public static void renameFile(String fName, String nName) {
		File file = new File(fName);
		file.renameTo(new File(nName));
		file.delete();
	}

	public static boolean isFileExist(String pathAndFile) {
		File file = new File(pathAndFile);
		if (file.exists() && file.length() > 0)
			return true;
		else
			return false;
	}

	public static long getFileSize(String pathAndFile) {
		File file = new File(pathAndFile);
		return file.length();
	}

	public static void unzipFiles(File zipFile, File upzipDir) throws IOException {
		if (!upzipDir.isDirectory() || !upzipDir.exists()) {
			if (!upzipDir.mkdir())
				throw new IOException("Create Unzip directory failure");
		}
		ZipFile zip = new ZipFile(zipFile);
		try {
			for (@SuppressWarnings("rawtypes")
			Enumeration entries = zip.entries(); entries.hasMoreElements();) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String name = entry.getName();
				InputStream in = zip.getInputStream(entry);
				String outPath = String.format("%s/%s", upzipDir.getPath(), name).replace("\\*", "/");
				// 判断路径是否存在,不存在则创建文件路径
				File file = new File(outPath.substring(0, outPath.lastIndexOf("/")));
				if (!file.exists()) {
					file.mkdir();
				}
				// 判断文件全路径是否为文件夹,如果是文件夹则递归解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
				// 输出文件路径信息
				System.out.println(outPath);
				OutputStream out = new FileOutputStream(outPath);
				byte[] buf1 = new byte[1024];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				in.close();
				out.flush();
				out.close();
			}
		} finally {
			zip.close();
		}
	}

	/**
	 * 获取下载路径
	 * 
	 * @param downDir
	 *            下载的文件夹
	 * @param filename
	 *            下载文件名称
	 * @return
	 */
	public static File getDownloadFile(String downDir, String filename) {
		File f = null;

		File dir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡已经挂载
			dir = Environment.getExternalStorageDirectory();
		} else {
			dir = BaseApplication.getContextInstance().getFilesDir();
		}

		dir = new File(dir, downDir);
		dir.mkdir();

		String[] command = { "chmod", "777", dir.getAbsolutePath() };
		ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		f = new File(dir, filename);
		try {
			f.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * 获取SD卡可用空间 返回的是字节单位B
	 * 
	 * @return
	 */
	public static long getSDcardAvailaleSize() {

		File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径

		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();

		long availableBlocks = stat.getAvailableBlocks();

		return availableBlocks * blockSize;

		// (availableBlocks * blockSize)/1024 KB 单位

		// (availableBlocks * blockSize)/1024 /1024 MB单位
	}

	/**
	 * 返回缓存路径，有SD卡 返回的是/mnt/sdcard/Android/data/...包名/cache 没有SD卡
	 * 返回的是data/data/...包名/cache
	 * 
	 * @return
	 */
	public static File getAndroidCacheFile() {
		File result = null;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡已经挂载
			result = BaseApplication.getContextInstance().getExternalCacheDir();
		} else {
			result = BaseApplication.getContextInstance().getCacheDir();
		}
		return result;
	}

	/**
	 * 获取图片缓存路径 有SDcard放在/mnt/sdcard/Android目录下
	 * 
	 * 没有sdcard 放在 data/data/packagename/file 目录下
	 * 
	 * @return
	 */
	public static File getImageCacheFile() {

		File result = new File(
				getCacheRootFile().getAbsoluteFile() + "/" + Config.APP_CACHE_DIR + "/" + Config.IMAGE_CACHE_DIR);
		if (!result.exists())
			result.mkdirs();

		return result;
	}

	/**
	 * 获取项目缓存总目录
	 * 
	 * @return
	 */
	public static File getCacheRootFile() {
		File result = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡已经挂载
			result = BaseApplication.getContextInstance().getExternalFilesDir(null);
		} else {
			result = BaseApplication.getContextInstance().getFilesDir();
		}
		return result;
	}

	/**
	 * 获取视频录制的文件 文件名以当前时间的毫秒数
	 * 
	 * @return
	 */
	public static File getRecordFile() {
		File result = null;
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡已经挂载
			return result;
		}

		result = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

		result = new File(result.getAbsoluteFile() + "/" + "record_" + System.currentTimeMillis() + ".mp4");
		return result;
	}

	public static void main(String[] args) {
		getRecordFile();
	}

	/**
	 * 获取netroid缓存路径
	 * 
	 * @return
	 */
	public static File getNetroidCachePath() {
		File result = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡已经挂载
			result = BaseApplication.getContextInstance().getExternalFilesDir(null);
		}
		if (result == null)
			result = BaseApplication.getContextInstance().getFilesDir();

		result = new File(result.getAbsoluteFile() + "/" + Config.NETROID_CACHE_DIR);
		if (!result.exists())
			result.mkdir();

		return result;
	}

	/**
	 * 头像的临时文件路径
	 * 
	 * @return
	 */
	public static File getTempAvatarFile() {
		File result = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡已经挂载
			result = BaseApplication.getContextInstance().getExternalFilesDir(null);
		}
		if (result == null)
			result = BaseApplication.getContextInstance().getFilesDir();

		result = new File(result.getAbsoluteFile() + "/avatar.jpg");
		if (!result.exists())
			try {
				result.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		return result;
	}

	/**
	 * 添加到图库
	 * 
	 * @param path
	 *            图片路径 不传路径就是用mCurrentPhotoPath，否则就使用传入的string
	 */
	public static void Add2Gallery(String path) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = null;
		f = new File(path);

		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		BaseApplication.getContextInstance().sendBroadcast(mediaScanIntent);
	}

	public static void saveBitmap(String fileName, Bitmap mBitmap) {
		File f = new File(fileName);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			mBitmap.compress(CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static Uri saveBitmap(Bitmap bm) {
		File fileDir = LocalFileManager.getInstance().getTempPic("crop.jpg");
		FileOutputStream fos;
		try {
			if (!fileDir.exists()) {
				fileDir.createNewFile();
			}
			fos = new FileOutputStream(fileDir);
			bm.compress(CompressFormat.JPEG, 85, fos);
			fos.flush();
			fos.close();
			return Uri.fromFile(fileDir);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
