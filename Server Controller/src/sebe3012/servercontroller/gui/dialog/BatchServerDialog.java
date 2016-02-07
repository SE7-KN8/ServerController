package sebe3012.servercontroller.gui.dialog;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BatchServerDialog {

	public static Stage stage;
	public static String batchPath;
	public static String propertiesPath;
	public static String name;
	public static boolean useDefault = true;

	public BatchServerDialog(Stage stage) {

		BatchServerDialog.stage = stage;

		try {
			Pane root = FXMLLoader.load(this.getClass().getResource("Dialog.fxml"));
			root.getStyleClass().add("pane");
			Scene scene = new Scene(root);
			scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Server erstellen");
			stage.setResizable(false);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BatchServerDialog(Stage stage, String batch, String properties, String name) {
		BatchServerDialog.useDefault = false;
		BatchServerDialog.batchPath = batch;
		BatchServerDialog.propertiesPath = properties;
		BatchServerDialog.name = name;
		new BatchServerDialog(stage);
	}

}
