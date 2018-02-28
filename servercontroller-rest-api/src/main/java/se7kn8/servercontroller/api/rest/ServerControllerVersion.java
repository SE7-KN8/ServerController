package se7kn8.servercontroller.api.rest;

import java.io.Serializable;

public class ServerControllerVersion implements Serializable {

	private String version;
	private String apiVersion;

	public void setVersion(String version) {
		this.version = version;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getVersion() {
		return version;
	}

	public String getApiVersion() {
		return apiVersion;
	}
}
