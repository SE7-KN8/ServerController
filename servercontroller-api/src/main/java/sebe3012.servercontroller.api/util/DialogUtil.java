package sebe3012.servercontroller.api.util;

import sebe3012.servercontroller.api.util.design.Designs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.ResourceBundle;

public class DialogUtil {

	private static ResourceBundle dialogBundle = ResourceBundle.getBundle("lang/dialog_util/lang");
	private static Logger log = LogManager.getLogger();

	public static Optional<ButtonType> showErrorAlert(String header, String content){
		return showErrorAlert(header, content, true);
	}

	public static Optional<ButtonType> showErrorAlert(String header, String content, boolean wait) {
		return showAlert(dialogBundle.getString("dialog_error"), header, content, AlertType.ERROR, wait);
	}


	public static Optional<ButtonType> showWaringAlert(String header, String content){
		return showWaringAlert(header, content, true);
	}

	public static Optional<ButtonType> showWaringAlert(String header, String content, boolean wait) {
		return showAlert(dialogBundle.getString("dialog_warning"), header, content, AlertType.WARNING);
	}


	public static Optional<ButtonType> showInformationAlert(String header, String content){
		return showInformationAlert(header, content, true);
	}

	public static Optional<ButtonType> showInformationAlert(String header, String content, boolean wait) {
		return showAlert(dialogBundle.getString("dialog_information"), header, content, AlertType.INFORMATION, wait);
	}


	public static Optional<ButtonType> showConformationAlert(String header, String content){
		return showConformationAlert(header, content, true);
	}

	public static Optional<ButtonType> showConformationAlert(String header, String content, boolean wait) {
		return showAlert(dialogBundle.getString("dialog_confirmation"), header, content, AlertType.CONFIRMATION, wait);
	}


	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type){
		return showAlert(title, header, content, type, true);
	}

	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type, boolean wait) {
		return showAlert(title, header, content, type, wait, ButtonType.OK);
	}

	public static Optional<ButtonType> showAlert(String title, String header, String content, AlertType type, boolean wait, ButtonType... types) {
		log.info("Showing dialog with title: '{}', header: '{}' and type: '{}'", title, header, type);
		Alert a = new Alert(type, content, types);
		a.setTitle(title);
		a.setHeaderText(header);
		Designs.applyCurrentDesign(a);
		a.initModality(Modality.APPLICATION_MODAL);

		if(wait){
			return a.showAndWait();
		}

		a.show();

		return Optional.empty();
	}

	public static Optional<Boolean> showRequestAlert(String header ,String content){
		Optional<ButtonType> result = showAlert(dialogBundle.getString("dialog_confirmation"), header, content, AlertType.CONFIRMATION, true, ButtonType.YES, ButtonType.NO);

		if(result.isPresent() && result.get().equals(ButtonType.YES)){
			return Optional.of(true);
		}else{
			return Optional.of(false);
		}
	}

	public static Optional<ButtonType> showExceptionAlert(String header, String content, Throwable exception){
		return showExceptionAlert(header, content, exception, true);
	}

	public static Optional<ButtonType> showExceptionAlert(String header, String content, Throwable exception, boolean wait) {
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle(dialogBundle.getString("dialog_error"));
		a.setHeaderText(header);
		a.setContentText(content);
		Designs.applyCurrentDesign(a);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label l = new Label(dialogBundle.getString("dialog_error_code"));

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
