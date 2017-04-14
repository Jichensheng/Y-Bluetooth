package com.heshun.hslibrary.common.http;

import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.ParseError;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 标准post请求request类
 * 
 * @author huangxz
 * 
 */
public class JsonObjectRequestByPost extends Request<JSONObject> {
	private Map<String, String> params;

	public JsonObjectRequestByPost(String url, Map<String, String> params, Listener<JSONObject> listener) {
		super(Method.POST, url, listener);
		this.params = params;
	}

	@Override
	public Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, "UTF-8");
			return Response.success(new JSONObject(jsonString), response);
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

}
