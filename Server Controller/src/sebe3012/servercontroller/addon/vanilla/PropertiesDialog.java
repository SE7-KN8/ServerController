package sebe3012.servercontroller.addon.vanilla;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PropertiesDialog {

	public PropertiesDialog(Stage stage, PropertiesHandler handler) {
		try {

			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("PropertiesDialog.fxml"));
			loader.setController(new PropertiesDialogHandler(handler));

			GridPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
