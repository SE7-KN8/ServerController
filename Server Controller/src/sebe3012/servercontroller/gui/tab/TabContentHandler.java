package sebe3012.servercontroller.gui.tab;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import sebe3012.servercontroller.gui.FrameHandler;

public class TabContentHandler implements Initializable {

	private TabServerHandler server;
	private TabContent content;

	@FXML
	public ResourceBundle resources;

	@FXML
	public URL location;

	@FXML
	public TextArea cOutput;

	@FXML
	public TextField cInput;

	@FXML
	public Button btnSend;

	@FXML
	public Button btnStart;

	@FXML
	public Button btnEnd;

	@FXML
	public Label lblInfo;

	@FXML
	public Button btnRestart;

	@FXML
	public Button btnPro;

	@FXML
	void onEndClicked(ActionEvent event) {
		server.onEndClicked();
	}

	@FXML
	void onSendClicked(ActionEvent event) {
		sendCommandToServer(cInput.getText());
	}

	@FXML
	void onStartClicked(ActionEvent event) {
		server.onStartClicked();
	}

	@FXML
	void onEnterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			sendCommandToServer(cInput.getText());
		}
	}

	@FXML
	void onRestartClicked(ActionEvent event) {
		server.onRestartClicked();
	}

	public TabContentHandler(TabContent content) {
		this.content = content;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		server = new TabServerHandler(this);
	}

	protected void showErrorAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.ERROR);
	}

	protected void showWaringAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.WARNING);
	}

	protected void showInformationAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.INFORMATION);
	}

	protected void showConformationAlert(String title, String header, String content) {
		showAlert(title, header, content, AlertType.CONFIRMATION);
	}

	private void showAlert(String title, String header, String content, AlertType type) {
		Alert a = new Alert(AlertType.ERROR, content, ButtonType.OK);
		a.setTitle(title);
		a.setHeaderText(header);
		a.getDialogPane().getStylesheets().add(FrameHandler.currentDesign);
		a.showAndWait();
	}

	protected void addTextToOutput(String text) {
		Platform.runLater(() -> {
			cOutput.appendText(text + "\n");
		});
	}

	private void sendCommandToServer(String command) {
		if (command.trim().length() >= 1) {
			server.sendCommand(command.trim());
			cInput.setText("");
		}
	}

	public TabServerHandler getServerHandler() {
		return server;
	}

	public TabContent getContent() {
		return content;
	}

}
