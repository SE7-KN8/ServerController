package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;
import sebe3012.servercontroller.server.BasicServer;

public class ServerCreateEvent implements IEvent {

	private BasicServer server;
	private boolean hasIndex;
	private int index;

	public ServerCreateEvent(BasicServer server, boolean hasIndex, int index) {
		this.server = server;
		this.hasIndex = hasIndex;
		this.index = index;
	}

	public BasicServer getServer() {
		return server;
	}
	public int getIndex() {
		return index;
	}
	public boolean hasIndex(){
		return hasIndex;
	}

}
