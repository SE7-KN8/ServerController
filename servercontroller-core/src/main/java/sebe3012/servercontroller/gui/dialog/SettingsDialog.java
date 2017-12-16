package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.settings.Settings;

import org.controlsfx.control.PropertySheet;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/**
 * Created by Sebe3012 on 22.02.2017.
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
