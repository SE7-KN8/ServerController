package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.event.ServerTypeChooseEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;

public class ServerDialog {

	private static Logger log = LogManager.getLogger();

	public static void loadDialog() {
		log.debug("Load ServerDialog");
		Alert dialog = new Alert(AlertType.NONE);
		DialogPane rootPane = new DialogPane();
		rootPane.getStylesheets().add(FrameHandler.currentDesign);

		VBox root = new VBox(10);
		
		ComboBox<String> box = new ComboBox<>();
		box.setItems(FXCollections.observableArrayList(ServerController.serverAddon.keySet()));
		box.setPrefWidth(300);
		box.setPrefHeight(50);
		box.setStyle("-fx-font: 30px \"Arial\";");

		Button b = new Button("Fertig");
		b.setOnAction(event -> {
			if (box.getSelectionModel().getSelectedItem() != null) {
				dialog.close();
				log.debug("Load addon for name {}", box.getSelectionModel().getSelectedItem());
				EventHandler.EVENT_BUS.post(new ServerTypeChooseEvent(box.getSelectionModel().getSelectedItem()));
			}
		});
		b.setPrefWidth(300);
		b.setPrefHeight(75);
		
		root.getChildren().add(box);
		root.getChildren().add(b);
		
		rootPane.getButtonTypes().add(ButtonType.CLOSE);

		rootPane.setContent(root);
		dialog.setDialogPane(rootPane);
		dialog.setTitle("Servertyp auswählen");
		dialog.showAndWait();

	}

}
