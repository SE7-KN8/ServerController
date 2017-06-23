package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;

import org.fxmisc.richtext.CodeArea;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.Closeable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TabContentHandler implements Initializable, Closeable {

	private TabServerHandler server;
	private TabContent content;
	private List<String> commandList;
	private int maxCommands = 30;
	private int currentListCounter;

	@FXML
	public ResourceBundle resources;

	@FXML
	public URL location;

	@FXML
	public CodeArea cOutput;

	@FXML
	public TextField cInput;

	@FXML
	public Button btnSend;

	@FXML
	public Label lblInfo;

	private OutputFormatter formatter;

	@FXML
	void onSendClicked(ActionEvent event) {
		sendCommandToServer(cInput.getText());
	}

	@FXML
	void onEnterPressed(KeyEvent event) {
		switch (event.getCode()) {
			case ENTER:
				sendCommandToServer(cInput.getText());
				break;
			case UP:
				if (currentListCounter < commandList.size() - 1) {
					currentListCounter++;
				}
				cInput.setText(commandList.get(currentListCounter));
				break;
			case DOWN:
				if (currentListCounter > 0) {
					currentListCounter--;
					cInput.setText(commandList.get(currentListCounter));
				} else {
					cInput.setText("");
				}
				break;
		}
	}

	public void refreshListState() {
		FrameHandler.tree.refresh();
	}

	public TabContentHandler(TabContent content) {
		this.content = content;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		server = new TabServerHandler(this);
		cOutput.getStylesheets().clear();
		formatter = new OutputFormatter();
		formatter.start(cOutput);
		commandList = new ArrayList<>();
		currentListCounter = 0;
	}

	protected void addTextToOutput(String text) {
		Platform.runLater(() -> cOutput.appendText(text + "\n"));
	}

	private void sendCommandToServer(String command) {
		if (command.trim().length() >= 1) {
			server.sendCommand(command.trim());
			cInput.setText("");
			if (commandList.size() > maxCommands) {
				commandList.remove(commandList.size() - 1);
			}

			if (!(commandList.size() >= 1 && commandList.get(0).equals(command))) {
				commandList.add(0, command);
			}
			currentListCounter = -1;
		}
	}

	public TabServerHandler getServerHandler() {
		return server;
	}

	public TabContent getContent() {
		return content;
	}

	@Override
	public void close() {
		formatter.close();
	}

}
