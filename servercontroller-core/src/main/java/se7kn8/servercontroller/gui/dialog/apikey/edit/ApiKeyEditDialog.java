package se7kn8.servercontroller.gui.dialog.apikey.edit;

import se7kn8.servercontroller.api.gui.dialog.StageDialog;
import se7kn8.servercontroller.rest.RestServer;
import se7kn8.servercontroller.util.I18N;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ApiKeyEditDialog extends StageDialog {

	private RestServer server;
	private ApiKeyEditDialogController controller;


	public ApiKeyEditDialog(RestServer server) {
		super("ApiKey Permission Manager");//TODO to translate
		this.server = server;
	}

	@Override
	public Scene createDialog(Stage stage) {
		FXMLLoader loader = new FXMLLoader();
		controller = new ApiKeyEditDialogController(server, stage);
		loader.setController(controller);
		loader.setResources(I18N.getDefaultBundle());
		loader.setLocation(ClassLoader.getSystemResource("fxml/ApiKeyEditDialog.fxml"));

		try {
			BorderPane pane = loader.load();
			return new Scene(pane);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ApiKeyEditDialogController getController() {
		return controller;
	}
}
