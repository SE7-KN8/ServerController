package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;

public class ServerTypeChooseEvent implements IEvent {

	private String serverType;

	public ServerTypeChooseEvent(String serverType) {
		this.serverType = serverType;
	}

	public String getServerType() {
		return serverType;
	}

}
