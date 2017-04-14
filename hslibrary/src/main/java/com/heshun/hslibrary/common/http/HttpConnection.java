package com.heshun.hslibrary.common.http;

import android.os.Build;

import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.request.FileDownloadRequest;
import com.duowan.mobile.netroid.request.JsonObjectRequest;
import com.duowan.mobile.netroid.stack.HttpClientStack;
import com.duowan.mobile.netroid.stack.HttpStack;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.FileDownloader;
import com.heshun.hslibrary.common.config.Config;
import com.heshun.hslibrary.common.util.FileUtil;
import com.heshun.hslibrary.common.util.NetUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http请求工具合集
 *
 * @author huangxz
 */
public class HttpConnection {
	public final static String JSON_RESULT_DATA_NODE_HEAD = "head";

	public final static String JSON_RESULT_DATA_NODE_BODY = "body";

	public final static String JSON_NODE_SUCC_DATA = "succ";

	public final static String JSON_NODE_ERROR_DATA = "msg";
	//实现网络请求,若要实现https需用HttpStack接口的实现类HurlStack配合SSLSocketFactory
	private HttpStack stackInstance;

	// 请求状态
	public final static int REQUEST_SUCCESS = 10001;
	public static final int REQUEST_FAILED = 10000;
	public static final int REQUEST_ERROR_SERVER = -10001;
	public static final int REQUEST_ERROR_NET = -10002;
	public static final int REQUEST_TIMEOUT = -10003;

	private static HttpConnection instance;
	/**
	 * 单个实例的请求队列
	 */
	private RequestQueue mRqueue;
	/**
	 * 线程池大小初始值
	 */
	private final int THREAD_POOL_SIZE = Config.THREAD_COUNT;

	private DiskCache mDiskCache;

	private final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 浏览器标识
	 */
	private final String USER_AGENT = "netroid/0";

	/**
	 * 默认的请求超时及重试机制 5秒超时，不重试
	 */
	private final DefaultRetryPolicy REQUEST_POLICY = new DefaultRetryPolicy(5000, 0,
			DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

	/**
	 * 单例的下载模块
	 */
	private FileDownloader mDownloader;

	/**
	 * 在此处配置请求，缓存目录等
	 */
	private HttpConnection() {
		initStackInstance();
		BasicNetwork netWork = new BasicNetwork(stackInstance, DEFAULT_CHARSET);
		mDiskCache = new DiskCache(FileUtil.getNetroidCachePath(), Config.NETROID_CACHE_SIZE);
		mRqueue = new RequestQueue(netWork, THREAD_POOL_SIZE, mDiskCache);
		mDownloader = new FileDownloader(mRqueue, 1) {
			@Override
			public FileDownloadRequest buildRequest(String storeFilePath, String url) {
				FileDownloadRequest fdq = new FileDownloadRequest(storeFilePath, url) {
					@Override
					public void prepare() {
						addHeader("Accept-Encoding", "identity");
						addHeader("Accept-Ranges", "bytes");
						// 父类的prepare()方法做了Range计算，不要忘记调用
						super.prepare();
					}
				};
				fdq.setRetryPolicy(new DefaultRetryPolicy(10000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
				return fdq;
			}
		};
		mRqueue.start();
	}

	/**
	 * 获取请求helper实例
	 */
	public static HttpConnection getConnection() {
		synchronized (HttpConnection.class) {
			if (null == instance) {
				instance = new HttpConnection();
			}
			return instance;
		}
	}

	private void initStackInstance() {
		if (stackInstance == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				stackInstance = new HurlStack(USER_AGENT);
			} else {
				stackInstance = new HttpClientStack(USER_AGENT);
			}
		}
	}

	/**
	 * 下载文件
	 */
	private FileDownloader.DownloadController download(String filepath, String url, Listener<Void> mListener) {
		return mDownloader.add(filepath, url, mListener);
	}

	public FileDownloader.DownloadController download(File dir, String fileName, String url,
													  final ResultHandler mHandler) {
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return download(dir.getAbsolutePath() + "/" + fileName, url, new Listener<Void>() {
			@Override
			public void onPreExecute() {
				super.onPreExecute();
				mHandler.onStart();
			}

			@Override
			public void onSuccess(Void arg0) {
				mHandler.onSuccess("");
			}

			@Override
			public void onError(NetroidError error) {
				super.onError(error);
				mHandler.onFailure(error.getMessage());
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mHandler.onFinish();
			}

			@Override
			public void onProgressChange(long fileSize, long downloadedSize) {
				super.onProgressChange(fileSize, downloadedSize);
				mHandler.onProcess(downloadedSize, fileSize);
			}

		});

	}

	/**
	 * 在规则类中可以配置缓存、重试等机制
	 */
	public JsonObjectRequest httpGet(String action, final ResultHandler mHandler, RequestPolicy option,
									 String... params) {
		String url = makeUrlWithToken(action, params);
/*		String newUrl="";
		try {
			newUrl= URLDecoder.decode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpGet(newUrl, mHandler, option);*/

		return httpGet(url, mHandler, option);
	}

	/**
	 * get请求
	 */
	public JsonObjectRequest httpGet(String action, final ResultHandler mHandler, String... params) {
		String url = makeUrlWithToken(action, params);
		return httpGet(url, mHandler, RequestPolicy.getDefaultPolicy());
	}

	/**
	 * @param url
	 * @param mHandler ?调用者只关注data字段内的数据:调用者自行解析json串
	 * @return
	 */
	private JsonObjectRequest httpGet(String url, final ResultHandler mHandler, final RequestPolicy option) {
		if (!NetUtil.isAvailable()) {
			mHandler.onFinish();
			mHandler.onNetUnavailable();
			return null;
		}
		DefaultRequestListener listener = new DefaultRequestListener(mHandler, option);

		JsonObjectRequest request = new JsonObjectRequest(url, null, listener);
		// 请求的策略
		DefaultRetryPolicy policy = new DefaultRetryPolicy(option.getOutTime(), option.getRetryTime(),
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		request.setRetryPolicy(policy);

		if (option.isCache()) {
			request.setCacheExpireTime(TimeUnit.MINUTES, option.getExpireTime());
		}
		request.setForceUpdate(option.isForceUpdate());
		mRqueue.add(request);
		return request;
	}

	/**
	 * 通过表单提交post请求
	 * <p>
	 * 请求的方法名
	 *
	 * @param dataOnly ?调用者只关注data字段内的数据:调用者自行解析json串
	 * @param params   需要传递的参数
	 * @return
	 */
	public JsonObjectRequestByPostForm postViaForm(String url, Map<String, Object> params, final ResultHandler mHandler,
												   final boolean dataOnly) {

		if (!NetUtil.isAvailable()) {
			mHandler.onNetUnavailable();
			return null;
		}
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = Config.SERVER + url;
		}
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		DefaultRequestListener listener = new DefaultRequestListener(mHandler,
				RequestPolicy.getDefaultPolicy().setDataOnly(dataOnly));
		JsonObjectRequestByPostForm formRequest = new JsonObjectRequestByPostForm(url, params, listener);

		formRequest.setRetryPolicy(REQUEST_POLICY);
		mRqueue.add(formRequest);
		return formRequest;
	}

	public JsonObjectRequestByPostJson httpPostViaJson(String action, com.alibaba.fastjson.JSONObject params,
													   ResultHandler mHandler) {
		return httpPostViaJson(action, params, mHandler, RequestPolicy.getDefaultPolicy());
	}

	public JsonObjectRequestByPostJson httpPostViaJson(String action, com.alibaba.fastjson.JSONObject params,
													   ResultHandler mHandler, RequestPolicy option) {
		if (!NetUtil.isAvailable()) {
			mHandler.onNetUnavailable();
			return null;
		}

		DefaultRequestListener listener = new DefaultRequestListener(mHandler, option);
		String url = Config.SERVER + action;
		JsonObjectRequestByPostJson postRequest = new JsonObjectRequestByPostJson(url, params, listener);

		//
		DefaultRetryPolicy policy = new DefaultRetryPolicy(option.getOutTime(), option.getRetryTime(),
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

		postRequest.setRetryPolicy(policy);
		if (option.isCache()) {
			postRequest.setCacheExpireTime(TimeUnit.MINUTES, option.getExpireTime());
		}
		if (option.isForceUpdate()) {
			postRequest.setForceUpdate(true);
		}
		postRequest.setRetryPolicy(REQUEST_POLICY);
		mRqueue.add(postRequest);
		return postRequest;
	}

	/**
	 * 生成包含token的请求路径
	 */
	private String makeUrlWithToken(String action, String[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append(Config.SERVER).append(action);
		if (params != null) {
			for (String s : params) {
				sb.append("/").append(s);
			}
		}
		return sb.toString();
	}

	/**
	 * 用于第三方接口的get请求，直接传入完整请求url
	 */
	@Deprecated
	public JsonObjectRequest get(String url, final ResultHandler mHandler) {
		return httpGet(url, mHandler, RequestPolicy.getDefaultPolicy());
	}

	/**
	 * 用于第三方接口的get请求，直接传入完成请求url
	 */
	@Deprecated
	public JsonObjectRequest get(String url, final ResultHandler mHandler, RequestPolicy option) {
		return httpGet(url, mHandler, option);
	}
}
