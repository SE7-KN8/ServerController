package sebe3012.servercontroller.addon.vanilla.dialog;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.vanilla.VanillaServer;
import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.event.ServerTypeChooseEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.gui.FrameHandler;

import java.io.IOException;
import java.util.HashMap;

public class VanillaAddon implements IEventHandler {

	public static final String ADDON_NAME = "Vanilla";

	public static void loadAddon() {

		ServerController.serverAddon.put(VanillaAddon.ADDON_NAME, VanillaServer.class);
		EventHandler.EVENT_BUS.registerEventListener(new VanillaAddon());

	}

	@Subscribe
	public void serverTypeChoose(ServerTypeChooseEvent event) {
		if (event.getServerType().equals(VanillaAddon.ADDON_NAME)) {
			loadDialog(null);
		}
	}
	
	@Subscribe
	public void serverEdit(ServerEditEvent event){
		if(event.getServerType().equals(VanillaAddon.ADDON_NAME)){
			loadDialog(event.getServer().toExternalForm());
		}
	}

	private void loadDialog(HashMap<String, Object> extraValues) {
		Platform.runLater(() -> {
			Alert dialog = new Alert(AlertType.NONE);

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("/fxml/VanillaServerDialog.fxml"));
			loader.setController(new VanillaDialogController(dialog, extraValues));

			try {
				GridPane root = loader.load();

				dialog.getDialogPane().setContent(root);
				dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
				dialog.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
				dialog.setTitle("Vanilla-Server erstellen");
				dialog.show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

}