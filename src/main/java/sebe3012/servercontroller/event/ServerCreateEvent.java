package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;
import sebe3012.servercontroller.server.BasicServer;

public class ServerCreateEvent implements IEvent {

	private BasicServer server;

	public ServerCreateEvent(BasicServer server) {
		this.server = server;
	}

	public BasicServer getServer() {
		return server;
	}

}
