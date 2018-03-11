package se7kn8.servercontroller.api.rest;

import java.io.Serializable;

public class ServerControllerServerCommand implements Serializable {
	private String command;

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
}
