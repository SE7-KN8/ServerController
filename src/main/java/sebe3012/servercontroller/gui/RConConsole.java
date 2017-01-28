package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.rcon.RCon;
import sebe3012.servercontroller.util.DialogUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class RConConsole {

	public RConConsole(String ip, int port, char[] password) {

		RCon rcon = new RCon(ip, port, password);
		try {
			rcon.loadConnection();
		} catch (UnknownHostException e) {
			DialogUtil.showExceptionAlert("Fehler", "Fehler beim Laden der RCon-Verbindung",
					"Der Server konnte nicht gefunden werden", e);
		} catch (IOException e) {
			DialogUtil.showExceptionAlert("Fehler", "Fehler beim Laden der RCon-Verbindung", "", e);
		}

		Stage stage = new Stage();
		stage.setTitle("RCon-Verbindung zu: " + ip + ":" + port);

		FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemClassLoader().getResource("fxml/RConConsole.fxml"));

		loader.setController(new RConConsoleController(rcon));

		SplitPane root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene scene = new Scene(root);
		scene.getStylesheets().add(FrameHandler.currentDesign);

		stage.setOnCloseRequest(windowEvent -> {
			try {
				rcon.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		stage.setScene(scene);
		stage.show();

	}

	private class RConConsoleController {

		private RCon rcon;

		@FXML
		private ResourceBundle resources;

		@FXML
		private URL location;

		@FXML
		private TextArea area;

		@FXML
		private TextField input;

		@FXML
		private Button send;

		@FXML
		void onKeyTyped(KeyEvent event) {
			if (event.getCode() == KeyCode.ENTER) {
				sendToServer();
			}
		}

		@FXML
		void onSendClicked(ActionEvent event) {
			sendToServer();
		}

		private void sendToServer() {

			String text = input.getText();

			try {
				String payload = rcon.sendCommand(text);
				area.appendText(payload + "\n");
			} catch (Exception e) {
				DialogUtil.showExceptionAlert("Fehler", "", "Fehler bei der RCon-Verbindung", e);
			}

			input.setText("");
		}

		@FXML
		void initialize() {

		}

		public RConConsoleController(RCon rcon) {
			this.rcon = rcon;
		}
	}

}