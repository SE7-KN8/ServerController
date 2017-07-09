package sebe3012.servercontroller.gui.dialog;

import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.design.Designs;
import sebe3012.servercontroller.util.settings.Settings;

import org.controlsfx.control.PropertySheet;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by Sebe3012 on 22.02.2017.
 * The settings dialog
 */
public class SettingsDialog {

	public void show() {     
		Alert dialog = new Alert(Alert.AlertType.NONE, "", ButtonType.FINISH, ButtonType.CANCEL);
		dialog.setHeaderText(I18N.translate("dialog_settings"));
		dialog.setTitle(I18N.translate("dialog_settings"));

		Designs.applyCurrentDesign(dialog);

		PropertySheet sheet = new PropertySheet(Settings.getItems());

		dialog.getDialogPane().setContent(sheet);
		dialog.getDialogPane().setPrefHeight(500);
		dialog.setResizable(true);

		dialog.showAndWait();
	}


}
