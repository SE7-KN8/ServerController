package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;
import sebe3012.servercontroller.server.BasicServer;

public class ServerMessageEvent implements IEvent{

	private BasicServer server;
	private String message;

	public ServerMessageEvent(BasicServer server, String message) {
		this.server = server;
		this.message = message;
	}

	public BasicServer getServer() {
		return server;
	}

	public String getMessage() {
		return message;
	}

}
