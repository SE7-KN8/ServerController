package sebe3012.servercontroller.event;

import sebe3012.servercontroller.eventbus.IEvent;

import javafx.scene.control.Control;

import java.util.List;

public class ChangeControlsEvent implements IEvent {

	private List<Control> newButtons;

	public ChangeControlsEvent(List<Control> newButtons) {
		this.newButtons = newButtons;
	}

	public List<Control> getNewControls() {
		return newButtons;
	}

}
