package se7kn8.servercontroller.api.rest;

import java.io.Serializable;

public class ServerControllerError implements Serializable {
	private String errorMessage;

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
