package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.settings.SettingsRow;
import sebe3012.servercontroller.util.I18N;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by Sebe3012 on 22.02.2017.
 * The settings dialog
 */
public class SettingsDialog {

	private List<SettingsRow> settings;
	private List<HBox> settingsRows;
	private HashMap<String, BooleanProperty> dialogMap;

	public SettingsDialog() {
		settings = new ArrayList<>(ServerController.settings.values());
		settingsRows = new ArrayList<>();
		dialogMap = new HashMap<>();
		loadSettings();
	}

	private void loadSettings() {

		for (SettingsRow row : settings) {

			Label label = new Label("   " + row.getLocalizedName());
			CheckBox box = new CheckBox();

			BooleanProperty property = new SimpleBooleanProperty();
			box.selectedProperty().bindBidirectional(property);
			box.setSelected(row.getValue());

			dialogMap.put(row.getId(), property);

			HBox hBox = new HBox();
			hBox.getChildren().addAll(box, label);

			settingsRows.add(hBox);
		}
	}

	public void show() {
		Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.FINISH, ButtonType.CANCEL);
		dialog.setHeaderText(I18N.translate("dialog_settings"));
		dialog.setTitle(I18N.translate("dialog_settings"));

		dialog.getDialogPane().getStylesheets().add(FrameHandler.currentDesign.getStylesheet());
		VBox rows = new VBox();
		rows.setPrefWidth(500);
		rows.getChildren().addAll(settingsRows);
		dialog.getDialogPane().setContent(rows);

		Optional<ButtonType> result = dialog.showAndWait();

		result.ifPresent(type -> {
			if (type == ButtonType.FINISH) {
				dialogMap.forEach((id, property) -> {
					SettingsRow row = ServerController.settings.get(id);
					row.setValue(property.getValue());
					row.save();
				});
			}
		});
	}

}
