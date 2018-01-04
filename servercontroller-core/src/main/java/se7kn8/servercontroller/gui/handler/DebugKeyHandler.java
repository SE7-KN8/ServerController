package se7kn8.servercontroller.gui.handler;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.api.util.DialogUtil;

import org.controlsfx.control.Notifications;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;


public class DebugKeyHandler implements EventHandler<KeyEvent> {

	private Stage primaryStage;

	public DebugKeyHandler(Stage primaryStage){
		this.primaryStage = primaryStage;
	}

	@Override
	public void handle(KeyEvent event) {
		if (ServerController.DEBUG) {
			switch (event.getCode()) {
				case F1:
					break;
				case F2:
					break;
				case F3:
					break;
				case F4:
					break;
				case F5:
					break;
				case F6:
					break;
				case F7:
					break;
				case F8:
					break;
				case F9:
					break;
				case F10:
					break;
				case F11:
					Notifications.create().darkStyle().hideAfter(Duration.seconds(10)).owner(primaryStage).title("Test notification").onAction(e -> DialogUtil.showInformationAlert("", "Some information")).text("Some text with some information").showInformation();
					break;
				case F12:
					break;
			}

		}
	}
}
