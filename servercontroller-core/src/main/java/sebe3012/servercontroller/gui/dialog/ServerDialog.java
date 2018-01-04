package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.addon.AddonUtil;
import sebe3012.servercontroller.api.gui.dialog.Dialog;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
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
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ServerDialog implements Dialog {

	private Logger log = LogManager.getLogger();
	private ServerManager serverManager;

	public ServerDialog(ServerManager manager) {
		this.serverManager = manager;
	}

	public void showDialog() {
		log.debug("Load ServerDialog");
		Alert dialog = new Alert(AlertType.NONE);
		Designs.applyCurrentDesign(dialog.getDialogPane());

		VBox root = new VBox(10);

		List<String> serverCreators = new ArrayList<>();

		serverManager.getRegistryHelper().getServerCreatorRegistry().getData().forEach((addon, serverCreatorsList) -> serverCreatorsList.stream().filter(ServerCreator::isStandaloneServer).forEach(serverCreator -> serverCreators.add(addon.getAddonInfo().getId() + ":" + serverCreator.getID())));

		ComboBox<String> box = new ComboBox<>();
		box.setItems(FXCollections.observableArrayList(serverCreators));
		box.setPrefWidth(300);
		box.setPrefHeight(50);
		box.setStyle("-fx-font: 30px \"Arial\";");

		Button b = new Button(I18N.translate("dialog_finish"));
		b.setOnAction(event -> {
			if (box.getSelectionModel().getSelectedItem() != null) {
				dialog.close();
				log.debug("Load ServerCreator for id {}", box.getSelectionModel().getSelectedItem());
				String[] ids = box.getSelectionModel().getSelectedItem().split(":");
				AddonUtil.loadServerCreateDialog(ids[0], ids[1], null, serverManager);
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
