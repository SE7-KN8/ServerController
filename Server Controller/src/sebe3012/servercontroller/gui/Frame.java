package sebe3012.servercontroller.gui;

import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import sebe3012.servercontroller.gui.dialog.BatchServerDialog;
import sebe3012.servercontroller.gui.tab.Tabs;

public class Frame extends Application {

	public static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Frame.primaryStage = primaryStage;
		BorderPane root = FXMLLoader.load(this.getClass().getResource("BaseFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Batchserver Controller by Sebe3012          Pre-Alpha 0.0.1");
		primaryStage.setMaximized(true);
		primaryStage.setOnCloseRequest(event -> {
			Alert dialog = new Alert(AlertType.CONFIRMATION, "Wollen sie wirklich beenden?", ButtonType.OK,
					ButtonType.CANCEL);
			dialog.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
			dialog.setHeaderText("Beenden?");
			dialog.setTitle("");
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent()) {
				if (result.get().equals(ButtonType.OK)) {
					System.out.println("[Main] Stop Servercontroller");
					Tabs.servers.forEach((id, server) -> {
						if (server.getServer().isRunning()) {
							server.onEndClicked();
						}
					});
				} else {
					event.consume();
				}
			}
		});
		primaryStage.show();

		BatchServerDialog d = new BatchServerDialog(new Stage());
		d.hashCode();

	}

	public static void load(String... args) {
		launch(args);
	}

}
