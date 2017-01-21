package sebe3012.servercontroller.addon.vanilla.dialog.properties;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PropertiesDialog {

	public PropertiesDialog(Stage stage, PropertiesHandler handler, BasicServer server) {
		try {

			stage.setTitle("Properties von: " + server.getName());

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/PropertiesDialog.fxml"));
			loader.setController(new PropertiesDialogHandler(handler));

			GridPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(FrameHandler.currentDesign);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
