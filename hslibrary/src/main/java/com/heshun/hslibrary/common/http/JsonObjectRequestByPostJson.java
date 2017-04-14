package com.heshun.hslibrary.common.http;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.ParseError;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 表单提交请求类,用于调用本地服务器的接口
 * 
 * @author GYH
 * 
 */
public class JsonObjectRequestByPostJson extends Request<JSONObject> {
	private Object params;

	public JsonObjectRequestByPostJson(String url, Object object, Listener<JSONObject> listener) {
		super(Method.POST, url, listener);
		this.params = object;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, "UTF-8");
			return Response.success(new JSONObject(jsonString), response);
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		return JSON.toJSONString(params).getBytes();
	}

	@Override
	public String getBodyContentType() {
		return "application/json";
	}

}
