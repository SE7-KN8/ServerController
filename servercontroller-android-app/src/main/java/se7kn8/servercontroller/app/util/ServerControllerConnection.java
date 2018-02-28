package se7kn8.servercontroller.app.util;

import java.io.Serializable;

public class ServerControllerConnection implements Serializable {

	private String name;
	private String ip;
	private int port;
	private String apiKey;

	public ServerControllerConnection(String name, String ip, int port, String apiKey) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.apiKey = apiKey;
	}

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String toURL() {
		return "http://" + ip + ":" + port+"/servercontroller/";
	}
}
