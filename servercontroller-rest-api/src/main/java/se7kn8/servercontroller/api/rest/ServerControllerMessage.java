package se7kn8.servercontroller.api.rest;

import java.io.Serializable;

public class ServerControllerMessage implements Serializable {

	private String message;

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
