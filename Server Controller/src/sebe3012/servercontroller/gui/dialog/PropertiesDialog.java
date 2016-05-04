package sebe3012.servercontroller.gui.dialog;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sebe3012.servercontroller.server.PropertiesHandler;

public class PropertiesDialog {

	public static PropertiesHandler properties;
	public static Stage stage;

	public PropertiesDialog(Stage stage) {
		try {
			PropertiesDialog.stage = stage;
			GridPane root = FXMLLoader.load(this.getClass().getResource("PropertiesDialog.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
