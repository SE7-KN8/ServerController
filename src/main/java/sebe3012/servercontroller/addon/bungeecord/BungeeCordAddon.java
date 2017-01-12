package sebe3012.servercontroller.addon.bungeecord;

import java.io.IOException;
import java.util.HashMap;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.event.ServerTypeChooseEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.gui.FrameHandler;

public class BungeeCordAddon implements IEventHandler {

	public static final String ADDON_NAME = "BungeeCord";

	public static void loadAddon() {

		ServerController.serverAddon.put(BungeeCordAddon.ADDON_NAME, BungeeCordServer.class);
		EventHandler.EVENT_BUS.registerEventListener(new BungeeCordAddon());

	}

	@Subscribe
	public void serverTypeChoose(ServerTypeChooseEvent event) {
		if (event.getServerType().equals(BungeeCordAddon.ADDON_NAME)) {
			loadDialog(null);
		}
	}

	@Subscribe
	public void serverEdit(ServerEditEvent event) {
		if (event.getServerType().equals(BungeeCordAddon.ADDON_NAME)) {
			loadDialog(event.getServer().toExteralForm());
		}
	}

	private void loadDialog(HashMap<String, Object> extraValues) {
		Platform.runLater(() -> {
			Alert dialog = new Alert(AlertType.NONE);

			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("BungeeCordServerDialog.fxml"));
			loader.setController(new BungeeCordDialogController(dialog, extraValues));

			try {
				GridPane root = loader.load();

				dialog.getDialogPane().setContent(root);
				dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
				dialog.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
				dialog.setTitle("BungeeCord-Server erstellen");
				dialog.show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

}