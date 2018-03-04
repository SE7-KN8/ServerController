package se7kn8.servercontroller.app.util;

import se7kn8.servercontroller.api.rest.ServerControllerError;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//T = Transmit R = Response
public class GsonPostRequest<T, R> extends Request<R> {

	private Gson mGson = new Gson();
	private Class<R> mResponseClass;
	private Map<String, String> mHeaders;
	private Response.Listener<R> mListener;
	private T mToTransmit;

	public GsonPostRequest(String url, String apiKey, Class<R> responseClass, T toTransmit, Response.Listener<R> listener, Response.ErrorListener errorListener){
		super(Method.POST, url, errorListener);
		this.mResponseClass = responseClass;
		this.mListener = listener;
		this.mToTransmit = toTransmit;
		mHeaders = new HashMap<>();
		mHeaders.put("token", apiKey);
	}

	@Override
	public Map<String, String> getHeaders() {
		return mHeaders;
	}

	@Override
	protected void deliverResponse(R response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<R> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			if (response.statusCode == 200) {
				return Response.success(mGson.fromJson(json, mResponseClass), HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return Response.error(new ServerControllerException(mGson.fromJson(json, ServerControllerError.class)));
			}

		} catch (UnsupportedEncodingException | JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public String getBodyContentType() {
		return "application/json; charset=utf-8";
	}

	@Override
	public byte[] getBody() {
		try{
			String requestBody = mGson.toJson(mToTransmit);
			return requestBody == null ? null : requestBody.getBytes("utf-8");
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
			return null;
		}
	}
}
