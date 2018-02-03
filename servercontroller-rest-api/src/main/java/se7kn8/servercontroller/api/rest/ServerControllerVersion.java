package se7kn8.servercontroller.api.rest;

public class ServerControllerVersion {

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
