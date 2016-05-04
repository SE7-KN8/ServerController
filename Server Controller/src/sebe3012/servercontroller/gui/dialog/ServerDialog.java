package sebe3012.servercontroller.gui.dialog;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.event.ServerTypeChooseEvent;
import sebe3012.servercontroller.eventbus.EventHandler;

public class ServerDialog {

	public static void loadDialog() {

		Alert dialog = new Alert(AlertType.NONE);
		DialogPane rootPane = new DialogPane();
		rootPane.getStylesheets().add(ServerDialog.class.getResource("style.css").toExternalForm());

		GridPane root = null;

		ComboBox<String> box = new ComboBox<>();
		box.setItems(FXCollections.observableArrayList(ServerController.serverAddon));
		box.setPrefWidth(300);
		box.setPrefHeight(50);
		box.setStyle("-fx-font: 30px \"Serif\";");

		Button b = new Button("Fertig");
		b.setOnAction(event -> {
			if (box.getSelectionModel().getSelectedItem() != null) {
				dialog.close();
				EventHandler.EVENT_BUS.post(new ServerTypeChooseEvent(box.getSelectionModel().getSelectedItem()));
			}
		});
		b.setPrefWidth(300);
		b.setPrefHeight(75);

		root = new GridPane();
		root.add(box, 0, 0);
		root.add(b, 0, 1);
		rootPane.getButtonTypes().add(ButtonType.CLOSE);

		rootPane.setContent(root);
		dialog.setDialogPane(rootPane);
		dialog.setTitle("Servertyp auswählen");
		dialog.showAndWait();

	}

}
