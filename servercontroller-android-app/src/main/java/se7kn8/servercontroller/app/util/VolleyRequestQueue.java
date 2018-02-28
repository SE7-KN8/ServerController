package se7kn8.servercontroller.app.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyRequestQueue {
	private static VolleyRequestQueue mInstance;
	private RequestQueue mRequestQueue;


	public static synchronized VolleyRequestQueue getInstance() {
		if (mInstance == null) {
			mInstance = new VolleyRequestQueue();
		}
		return mInstance;
	}

	private RequestQueue getRequestQueue(Context ctx) {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, Context context) {
		getRequestQueue(context).add(req);
	}
}
