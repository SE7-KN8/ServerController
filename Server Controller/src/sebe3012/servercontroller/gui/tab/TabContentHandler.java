package sebe3012.servercontroller.gui.tab;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TabContentHandler implements Initializable {

	public TabServerHandler server;

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
		server.onSendClicked();
	}

	@FXML
	void onStartClicked(ActionEvent event) {
		server.onStartClicked();
	}

	@FXML
	void onEnterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			server.onSendClicked();
		}
	}

	@FXML
	void onRestartClicked(ActionEvent event) {
		server.onRestartClicked();
	}

	@FXML
	void onPropertiesClicked(ActionEvent event) {
		server.onPropertiesClicked();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		server = new TabServerHandler();
		Tabs.contents.put(Tabs.getNextID(), this);
		Tabs.IDforContents.put(this, Tabs.getNextID());
	}

}
