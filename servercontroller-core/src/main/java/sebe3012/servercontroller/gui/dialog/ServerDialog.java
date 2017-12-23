package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.gui.dialog.Dialog;
import sebe3012.servercontroller.api.util.design.Designs;
import sebe3012.servercontroller.server.ServerManager;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ServerDialog implements Dialog {

	private Logger log = LogManager.getLogger();
	private ServerManager serverManager;

	public ServerDialog(ServerManager manager){
		this.serverManager = manager;
	}

	public void showDialog() {
		log.debug("Load ServerDialog");
		Alert dialog = new Alert(AlertType.NONE);
		Designs.applyCurrentDesign(dialog.getDialogPane());

		VBox root = new VBox(10);

		Callback<ListView<Addon>, ListCell<Addon>> callback = e -> new ListCell<Addon>() {

			@Override
			protected void updateItem(Addon item, boolean empty) {
				super.updateItem(item, empty);

				if (item != null && !empty) {
					setText(item.getAddonInfo().getName());
				}
			}
		};

		ComboBox<Addon> box = new ComboBox<>();
		box.setButtonCell(callback.call(null));
		box.setCellFactory(callback);
		box.setItems(FXCollections.observableArrayList(AddonUtil.getServerTypes().keySet()));
		box.setPrefWidth(300);
		box.setPrefHeight(50);
		box.setStyle("-fx-font: 30px \"Arial\";");

		Button b = new Button(I18N.translate("dialog_finish"));
		b.setOnAction(event -> {
			if (box.getSelectionModel().getSelectedItem() != null) {
				dialog.close();
				log.debug("Load addon for name {}", box.getSelectionModel().getSelectedItem().getAddonInfo().getId());
				AddonUtil.loadServerCreateDialog(box.getSelectionModel().getSelectedItem(), null, serverManager);
			}
		});
		b.setPrefWidth(300);
		b.setPrefHeight(75);

		root.getChildren().add(box);
		root.getChildren().add(b);

		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

		dialog.getDialogPane().setContent(root);
		dialog.setTitle(I18N.translate("dialog_choose_server_type"));
		dialog.showAndWait();

	}

}
