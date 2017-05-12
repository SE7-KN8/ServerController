package sebe3012.servercontroller.util;

import sebe3012.servercontroller.gui.Frame;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class DialogUtil {

	public static Optional<ButtonType> showErrorAlert(String title, String header, String content) {
		return showAlert(title, header, content, AlertType.ERROR);
	}

	public static Optional<ButtonType> showWaringAlert(String title, String header, String content) {
		return showAlert(title, header, content, AlertType.WARNING);
	}

	public static Optional<ButtonType> showInformationAlert(String title, String header, String content) {
		return showAlert(title, header, content, AlertType.INFORMATION);
	}

	public static Optional<ButtonType> showConformationAlert(String title, String header, String content) {
		return showAlert(title, header, content, AlertType.CONFIRMATION);
	}

	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type) {
		return showAlert(title, header, content, type, ButtonType.OK);
	}

	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type, ButtonType... types) {
		Alert a = new Alert(type, content, types);
		a.setTitle(title);
		a.setHeaderText(header);
		Designs.applyCurrentDesign(a);
		a.initModality(Modality.APPLICATION_MODAL);
		a.initOwner(Frame.primaryStage);
		return a.showAndWait();
	}

	public static Optional<ButtonType> showExceptionAlert(String title, String header, String content, Exception exception) {
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(content);
		Designs.applyCurrentDesign(a);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label l = new Label(I18N.translate("dialog_error_code"));

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

		return a.showAndWait();
	}
}
