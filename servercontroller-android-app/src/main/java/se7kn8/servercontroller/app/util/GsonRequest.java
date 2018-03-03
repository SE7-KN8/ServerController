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

public class GsonRequest<T> extends Request<T> {

	private Gson mGson = new Gson();
	private Class<T> mClass;
	private Map<String, String> mHeaders;
	private Response.Listener<T> mListener;

	public GsonRequest(String url, Response.ErrorListener errorListener, Class<T> clazz, String apiKey, Response.Listener<T> listener) {
		super(Method.GET, url, errorListener);
		this.mClass = clazz;
		this.mListener = listener;
		mHeaders = new HashMap<>();
		mHeaders.put("token", apiKey);
	}

	@Override
	public Map<String, String> getHeaders() {
		return mHeaders;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			if (response.statusCode == 200) {
				return Response.success(mGson.fromJson(json, mClass), HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return Response.error(new ServerControllerException(mGson.fromJson(json, ServerControllerError.class)));
			}

		} catch (UnsupportedEncodingException | JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}
