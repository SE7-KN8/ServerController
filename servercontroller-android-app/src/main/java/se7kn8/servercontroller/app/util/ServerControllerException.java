package se7kn8.servercontroller.app.util;

import se7kn8.servercontroller.api.rest.ServerControllerError;

import com.android.volley.VolleyError;

public class ServerControllerException extends VolleyError {
	private ServerControllerError mError;

	public ServerControllerException(ServerControllerError error) {
		super(error.getErrorMessage());
		this.mError = error;
	}

	public ServerControllerError getServerControllerError() {
		return mError;
	}
}
