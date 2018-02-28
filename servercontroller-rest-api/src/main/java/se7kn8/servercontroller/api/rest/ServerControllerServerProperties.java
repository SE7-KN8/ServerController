package se7kn8.servercontroller.api.rest;

import java.io.Serializable;
import java.util.Map;

public class ServerControllerServerProperties implements Serializable {

	private Map<String, String> properties;

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
