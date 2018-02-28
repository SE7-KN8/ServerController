package se7kn8.servercontroller.api.rest;

import java.io.Serializable;

public class ServerControllerServerState implements Serializable {
	private String state;

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}
