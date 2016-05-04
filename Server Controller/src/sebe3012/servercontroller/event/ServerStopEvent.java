package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;
import sebe3012.servercontroller.server.BasicServer;

public class ServerStopEvent implements IEvent {

	private int stopCode;
	private BasicServer server;

	public ServerStopEvent(BasicServer server, int code) {
		this.stopCode = code;
		this.server = server;
	}

	public int getStopCode() {
		return stopCode;
	}

	public BasicServer getServer() {
		return server;
	}

}
