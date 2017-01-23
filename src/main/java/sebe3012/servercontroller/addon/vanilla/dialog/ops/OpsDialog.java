package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class OpsDialog {

	public OpsDialog(Stage stage, OpsHandler handler, BasicServer server) {
		try {
			stage.setTitle("Operatoren von: " + server.getName());

			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/OpsDialog.fxml"));
			loader.setController(new OpsDialogController(handler, server));

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
