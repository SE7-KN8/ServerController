package sebe3012.servercontroller.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sebe3012.servercontroller.gui.dialog.BatchServerDialog;
import javafx.scene.Scene;

public class Frame extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = FXMLLoader.load(this.getClass().getResource("BaseFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Batchserver Controller by Sebe3012          Pre-Alpha 0.0.1");
		primaryStage.setMaximized(true);
		primaryStage.show();

		BatchServerDialog d = new BatchServerDialog(new Stage());
		d.hashCode();

	}

	public static void load(String... args) {
		launch(args);
	}

}
