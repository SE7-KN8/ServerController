package sebe3012.servercontroller.event;

import java.util.List;

import javafx.scene.control.Control;

import sebe3012.servercontroller.eventbus.IEvent;

public class ChangeControlsEvent implements IEvent {

	private List<Control> newButtons;

	public ChangeControlsEvent(List<Control> newButtons) {
		this.newButtons = newButtons;
	}

	public List<Control> getNewControls() {
		return newButtons;
	}

}
