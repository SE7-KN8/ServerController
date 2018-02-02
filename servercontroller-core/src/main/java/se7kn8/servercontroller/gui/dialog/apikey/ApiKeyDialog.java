package se7kn8.servercontroller.gui.dialog.apikey;

import se7kn8.servercontroller.api.gui.dialog.StageDialog;
import se7kn8.servercontroller.rest.RestServer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ApiKeyDialog extends StageDialog {

	private RestServer server;

	public ApiKeyDialog(RestServer server) {
		super("ApiKey Manager");//TODO to translate
		this.server = server;
	}

	@Override
	public Scene createDialog(Stage stage) {
		FXMLLoader loader = new FXMLLoader();
		loader.setController(new ApiKeyDialogController(server));
		loader.setLocation(ClassLoader.getSystemResource("fxml/ApiKeyDialog.fxml"));


		try {
			BorderPane pane = loader.load();
			return new Scene(pane);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
