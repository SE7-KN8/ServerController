package sebe3012.servercontroller.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import sebe3012.servercontroller.gui.FrameHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

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

	public static void showExceptionAlert(String title, String header, String content, Exception exception) {
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(content);
		a.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label l = new Label("Fehlercode: ");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(l, 0, 0);
		expContent.add(textArea, 0, 1);

		a.getDialogPane().setExpandableContent(expContent);

		a.showAndWait();
	}

}
