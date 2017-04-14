package com.heshun.hslibrary.common.http;


import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkError;
import com.duowan.mobile.netroid.TimeoutError;
import com.heshun.hslibrary.R;
import com.heshun.hslibrary.common.config.BaseApplication;
import com.heshun.hslibrary.common.util.UiUtil;

import org.json.JSONObject;

/**
 * http回调的默认实现
 * 
 * @author huangxz
 */
public class DefaultRequestListener extends Listener<JSONObject> {
	private final String JSON_NODE_RESULT_DATA = "data";
	private final String JSON_NODE_SUCC_DATA = "succ";
	private final String JSON_NODE_ERROR_DATA = "msg";
	private final String JSON_NODE_RESP_CODE = "statusCode";
	private final int CODE_RESPON_LOAD_ALL = -1;
	private ResultHandler mHandler;
	private RequestPolicy mOption;

	public DefaultRequestListener(ResultHandler handler, RequestPolicy rp) {
		this.mHandler = handler;
		this.mOption = rp;
	}

	@Override
	public void onPreExecute() {
		mHandler.onStart();
	}

	@Override
	public void onFinish() {
		mHandler.onFinish();
	}

	@Override
	public void onRetry() {
		super.onRetry();
		mHandler.onRetry();
	}

	@Override
	public void onSuccess(JSONObject response) {
		if (null != response) {
			if (!mOption.isDataOnly()) {
				mHandler.onSuccess(response.toString());
			} else {
				parseResult(response);
			}
		} else {
			String msg = BaseApplication.getContextInstance().getString(R.string.content_is_null);
			mHandler.onFailure(BaseApplication.getContextInstance().getString(R.string.content_is_null));
			if (mHandler.toast()) {
				UiUtil.toast(msg);
			}
		}
	}

	@Override
	public void onError(NetroidError error) {
		if (error instanceof TimeoutError) {
			mHandler.onTimeOut();
		}
		if (error instanceof NetworkError) {
			mHandler.onFailure("服务器异常");
		}
	}

	@Override
	public void onUsedCache() {
		super.onUsedCache();
	}

	@Override
	public void onCancel() {
	}

	private void parseResult(JSONObject response) {

		boolean isSucceed = false;
		try {
			isSucceed = response.getBoolean(JSON_NODE_SUCC_DATA);
			if (isSucceed) {// 请求成功回调

				String data = response.getString(JSON_NODE_RESULT_DATA);
				mHandler.onSuccess(data);
			} else {// 请求结果失败
				String msg = response.getString(JSON_NODE_ERROR_DATA);
				int code = response.getInt(JSON_NODE_RESP_CODE);
				// 在失败结果中有一类错误为数据加载完毕，即CODE=-1;则应该回调onFullLoad()
				if (CODE_RESPON_LOAD_ALL == code) {
					mHandler.onFullLoad(msg);
				} else {
					mHandler.onFailure(msg);
				}
				if (mHandler.toast()) {
					UiUtil.toast(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = BaseApplication.getContextInstance().getString(R.string.json_parse_ex);
			if (isSucceed) {
				mHandler.onSuccess("");
			} else {
				mHandler.onFailure(msg + response.toString());
			}
			if (mHandler.toast()) {
				UiUtil.toast(msg);
			}
		}
	}
}
