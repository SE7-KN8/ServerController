package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.server.BasicServer;

public class PropertiesDialog {

	public PropertiesDialog(Stage stage, PropertiesHandler handler, BasicServer server) {
		try {

			stage.setTitle("Properties von: " + server.getName());

			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("PropertiesDialog.fxml"));
			loader.setController(new PropertiesDialogHandler(handler));

			GridPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(Frame.class.getResource("style.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
