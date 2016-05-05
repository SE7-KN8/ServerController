package sebe3012.servercontroller.event;

import java.util.HashMap;

import sebe3012.servercontroller.eventbus.IEvent;

public class ChangeButtonsEvent implements IEvent {

	private HashMap<String, Runnable> newButtons;

	public ChangeButtonsEvent(HashMap<String, Runnable> newButtons) {
		this.newButtons = newButtons;
	}

	public HashMap<String, Runnable> getNewButtons() {
		return newButtons;
	}

}
