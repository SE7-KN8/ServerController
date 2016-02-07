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

	private TabServerHandler server;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextArea cOutput;

	@FXML
	private TextField cInput;

	@FXML
	private Button btnSend;

	@FXML
	private Button btnStart;

	@FXML
	private Button btnEnd;

	@FXML
	private Label lblInfo;

	@FXML
	private Button btnRestart;

	@FXML
	private Button btnPro;

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
		server = new TabServerHandler(cOutput, cInput, lblInfo);
		Tabs.contents.put(Tabs.getNextID(), this);
		Tabs.IDforContents.put(this, Tabs.getNextID());
	}

}
