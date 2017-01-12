package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;

public class OpsDialog {

	public OpsDialog(Stage stage, OpsHandler handler, BasicServer server) {
		try {
			stage.setTitle("Operatoren von: " + server.getName());

			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("OpsDialog.fxml"));
			loader.setController(new OpsDialogController(handler, server));

			GridPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(FrameHandler.class.getResource("style.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
