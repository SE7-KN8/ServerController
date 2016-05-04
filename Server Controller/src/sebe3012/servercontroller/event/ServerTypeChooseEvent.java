package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;

public class ServerTypeChooseEvent implements IEvent {

	private String serverType;

	public ServerTypeChooseEvent(String servertype) {
		this.serverType = servertype;
	}

	public String getServerType() {
		return serverType;
	}

}
