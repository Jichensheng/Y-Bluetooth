package com.heshun.hslibrary.common.http;

/**
 * http请求的一些自定义规则
 * 
 * @author huangxz
 * 
 */
public class RequestPolicy {

	/**
	 * 默认超时时间
	 */
	public static int DEFAULT_OUT_TIME = 5 * 1000;

	/**
	 * 默认重试次数
	 */
	public static int DEFAULT_RETRY_TIME = 0;
	/**
	 * 超时时间
	 */
	private int outTime = DEFAULT_OUT_TIME;
	/**
	 * 请求失败重试次数
	 */
	private int retryTime = DEFAULT_RETRY_TIME;
	/**
	 * 是否仅返回data字段
	 */
	private boolean dataOnly = true;
	/**
	 * 是否缓存
	 */
	private boolean cache = false;
	/**
	 * 过期时间|单位：min
	 */
	private int expireTime = 30;

	private boolean forceUpdate = false;

	public boolean isCache() {
		return cache;
	}

	public RequestPolicy forceUpdate(boolean flag) {
		this.forceUpdate = flag;
		return this;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public static RequestPolicy getDefaultPolicy() {
		return new RequestPolicy();
	}

	public RequestPolicy isCache(boolean cache) {
		this.cache = cache;
		return this;
	}

	public int getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime
	 *            按分钟算
	 * @return
	 */
	public RequestPolicy setExpireMinutes(int expireTime) {
		this.expireTime = expireTime;
		if (expireTime > 0) {
			this.isCache(true);
		} else {
			this.isCache(false);
		}
		return this;
	}

	public int getOutTime() {
		return outTime;
	}

	public RequestPolicy setOutTime(int sec) {
		this.outTime = sec * 1000;
		return this;
	}

	public int getRetryTime() {
		return retryTime;
	}

	public RequestPolicy setRetryTime(int retryTime) {
		this.retryTime = retryTime;
		return this;
	}

	public boolean isDataOnly() {
		return dataOnly;
	}

	public RequestPolicy setDataOnly(boolean dataOnly) {
		this.dataOnly = dataOnly;
		return this;
	}

}
