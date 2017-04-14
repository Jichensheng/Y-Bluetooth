/**
 * 2010(c) Copyright Oceansoft Information System Co.,LTD. All rights reserved.
 * <p>
 * Compile: JDK 1.6+
 * <p>
 * 版权所有(C)：江苏欧索软件有限公司
 * <p>
 * 公司名称：江苏欧索软件有限公司
 * <p>
 * 公司地址：中国苏州科技城青山路1号
 * <p>
 * 网址: http://www.oceansoft.com.cn
 * <p>
 * 作者: 090922(陈伟)
 * <p>
 * 文件名: com.os.sztv.common.http.ResultHandler.java
 * <p>
 * 类产生时间: 13-12-7 下午4:07
 * <p>
 * 负责人: 090922(陈伟)
 * <p>
 * Email:javacspring@gmail.com
 * <p>
 * 所在组 : 掌上公安应用平台
 * <p>
 * 所在部门: 开发部--手持技术部
 * <p>
 * <p>
 */
package com.heshun.hslibrary.common.http;


import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.config.BaseApplication;
import com.heshun.hslibrary.common.util.UiUtil;

/**
 * 处理结果Handler
 * 
 * @author: chenw
 * @time: 13-12-7 下午4:07
 */
public class ResultHandler {

	private boolean showToast = false;

	public ResultHandler() {
	}

	public ResultHandler(boolean showToast) {
		this.showToast = showToast;
	}

	/**
	 * 开始调用异步请求时回调
	 */
	protected void onStart() {
	}

	protected void onRetry() {

	}

	/**
	 * 成功返回结果信息时回调函数<br>
	 * 此处成功是指业务逻辑数据返回值isSuccess=true</br>
	 * 
	 * @param response
	 *            成功返回结果JSONObject数据
	 */
	protected void onSuccess(String response) {
	}

	/**
	 * 处理进度回调
	 * 
	 * @param writeByte
	 *            已写入（处理）字节数
	 * @param total
	 *            总字节数
	 */
	protected void onProcess(long writeByte, long total) {
	}

	/**
	 * 数据全部加载完成回调函数<br/>
	 * 
	 * @param message
	 *            提示信息
	 */
	protected void onFullLoad(String message) {
	}

	/**
	 * 失败返回结果数据
	 * 
	 * @param message
	 *            调用失败提示信息
	 */
	protected void onFailure(String message) {
	}

	/**
	 * 网络不可用
	 * 
	 *            调用失败提示信息
	 */
	protected void onNetUnavailable() {
		UiUtil.toast(BaseApplication.getContextInstance().getString(R.string.netunavaible));
	}

	/**
	 * 网络连接超时
	 */
	protected void onTimeOut() {
		UiUtil.toast(BaseApplication.getContextInstance().getString(R.string.neterror));
	}

	/**
	 * 请求结束事件回调
	 */
	protected void onFinish() {
	}

	public boolean toast() {
		return this.showToast;
	}
}
