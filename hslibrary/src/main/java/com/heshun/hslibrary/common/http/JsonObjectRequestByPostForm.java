package com.heshun.hslibrary.common.http;

import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.ParseError;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.Response;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 表单提交请求类
 * 
 * @author huangxz
 * 
 */
public class JsonObjectRequestByPostForm extends Request<JSONObject> {

	private MultipartEntity entity = new MultipartEntity();

	private Map<String, Object> params;

	public JsonObjectRequestByPostForm(String url, Map<String, Object> params, Listener<JSONObject> listener) {
		super(Method.POST, url, listener);
		this.params = params;
		buildMultipartEntity();
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

	private void buildMultipartEntity() {
		for (String key : params.keySet()) {
			Object obj = params.get(key);
			if (obj instanceof File) {
				// FileBody fb = new FileBody((File)obj);
				// FileInputStream fis = null;
				// try {
				// fis = new FileInputStream((File) obj);
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// }

				FileBody fb = new FileBody((File) obj);
				// InputStreamBody fb = new InputStreamBody(fis, "image/png",
				// ((File) obj).getName());
				// BodyPart bp = new BodyPart();
				// bp.setContentTransferEncoding("8bit");
				// fb.setParent(bp);
				entity.addPart(key, fb);
			} else {
				try {
					entity.addPart(key, new StringBody(obj.toString()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

}
