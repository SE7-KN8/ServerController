package sebe3012.servercontroller.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import sebe3012.servercontroller.gui.FrameHandler;

public class DialogUtil {

	public static void showErrorAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.ERROR);
	}

	public static void showWaringAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.WARNING);
	}

	public static void showInformationAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.INFORMATION);
	}

	public static void showConformationAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.CONFIRMATION);
	}

	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert a = new Alert(AlertType.ERROR, content, ButtonType.OK);
		a.setTitle(title);
		a.setHeaderText(header);
		a.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
		a.showAndWait();
	}

}
