package sebe3012.servercontroller.util;

import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.util.design.Designs;

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

	public static Optional<ButtonType> showErrorAlert(String title, String header, String content){
		return showErrorAlert(title, header, content, true);
	}

	public static Optional<ButtonType> showErrorAlert(String title, String header, String content, boolean wait) {
		return showAlert(title, header, content, AlertType.ERROR, wait);
	}


	public static Optional<ButtonType> showWaringAlert(String title, String header, String content){
		return showWaringAlert(title, header, content, true);
	}

	public static Optional<ButtonType> showWaringAlert(String title, String header, String content, boolean wait) {
		return showAlert(title, header, content, AlertType.WARNING);
	}


	public static Optional<ButtonType> showInformationAlert(String title, String header, String content){
		return showInformationAlert(title, header, content, true);
	}

	public static Optional<ButtonType> showInformationAlert(String title, String header, String content, boolean wait) {
		return showAlert(title, header, content, AlertType.INFORMATION, wait);
	}


	public static Optional<ButtonType> showConformationAlert(String title, String header, String content){
		return showConformationAlert(title, header, content, true);
	}

	public static Optional<ButtonType> showConformationAlert(String title, String header, String content, boolean wait) {
		return showAlert(title, header, content, AlertType.CONFIRMATION, wait);
	}


	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type){
		return showAlert(title, header, content, type, true);
	}

	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type, boolean wait) {
		return showAlert(title, header, content, type, wait, ButtonType.OK);
	}

	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type, boolean wait, ButtonType... types) {
		Alert a = new Alert(type, content, types);
		a.setTitle(title);
		a.setHeaderText(header);
		Designs.applyCurrentDesign(a);
		a.initModality(Modality.APPLICATION_MODAL);
		a.initOwner(Frame.primaryStage);

		if(wait){
			return a.showAndWait();
		}

		a.show();

		return Optional.empty();
	}

	public static Optional<Boolean> showRequestAlert(String title, String header ,String content){
		Optional<ButtonType> result = showAlert(title, header, content, AlertType.CONFIRMATION, true, ButtonType.YES, ButtonType.NO);

		if(result.isPresent() && result.get().equals(ButtonType.YES)){
			return Optional.of(true);
		}else{
			return Optional.of(false);
		}
	}

	public static Optional<ButtonType> showExceptionAlert(String title, String header, String content, Exception exception){
		return showExceptionAlert(title, header, content, exception, true);
	}

	public static Optional<ButtonType> showExceptionAlert(String title, String header, String content, Exception exception, boolean wait) {
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

		if(wait){
			return a.showAndWait();
		}

		a.show();

		return Optional.empty();
	}
}
