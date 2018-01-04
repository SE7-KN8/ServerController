package se7kn8.servercontroller.gui.dialog;

import se7kn8.servercontroller.api.gui.dialog.AlertDialog;
import se7kn8.servercontroller.util.I18N;
import se7kn8.servercontroller.util.settings.Settings;

import org.controlsfx.control.PropertySheet;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/**
 * Created by se7kn8 on 22.02.2017.
 * The settings dialog
 */
public class SettingsDialog extends AlertDialog {

	public SettingsDialog() {
		super(I18N.translate("dialog_settings"), I18N.translate("dialog_settings"), "", Alert.AlertType.NONE, ButtonType.FINISH, ButtonType.CANCEL);
	}

	@Override
	public DialogPane createDialog(DialogPane dialogPane) {
		PropertySheet sheet = new PropertySheet(Settings.getItems());

		dialogPane.setContent(sheet);
		dialogPane.setPrefHeight(500);

		return dialogPane;
	}
}
