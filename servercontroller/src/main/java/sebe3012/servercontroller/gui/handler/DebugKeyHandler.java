package sebe3012.servercontroller.gui.handler;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.util.DialogUtil;

import org.controlsfx.control.Notifications;
import org.scenicview.ScenicView;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;


public class DebugKeyHandler implements EventHandler<KeyEvent> {
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
					Notifications.create().darkStyle().hideAfter(Duration.seconds(10)).owner(Frame.primaryStage).title("Test notification").onAction(e -> DialogUtil.showInformationAlert("Some Dialog", "", "Some information")).text("Some text with some information").showInformation();
					break;
				case F12:
					ScenicView.show(Frame.primaryStage.getScene());
					break;
			}

		}
	}
}
