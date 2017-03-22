package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;
import sebe3012.servercontroller.server.BasicServer;

public class ServerEditEvent implements IEvent{

	private String serverType;
	private BasicServer server;
	
	public ServerEditEvent(String serverType,BasicServer server) {
		this.serverType = serverType;
		this.server = server;
	}
	
	public String getServerType() {
		return serverType;
	}
	
	public BasicServer getServer() {
		return server;
	}

}
