package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

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
	public Label lblInfo;

	@FXML
	void onSendClicked(ActionEvent event) {
		sendCommandToServer(cInput.getText());
	}

	@FXML
	void onEnterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			sendCommandToServer(cInput.getText());
		}
	}

	public void refreshListState(){
		FrameHandler.list.refresh();
	}

	public TabContentHandler(TabContent content) {
		this.content = content;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		server = new TabServerHandler(this);
	}

	protected void addTextToOutput(String text) {
		Platform.runLater(() -> cOutput.appendText(text + "\n"));
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
