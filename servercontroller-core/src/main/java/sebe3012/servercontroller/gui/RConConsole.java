package sebe3012.servercontroller.gui;

import sebe3012.servercontroller.api.util.DialogUtil;
import sebe3012.servercontroller.api.util.design.Designs;
import sebe3012.servercontroller.rcon.RCon;
import sebe3012.servercontroller.util.I18N;

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
			DialogUtil.showExceptionAlert(I18N.translate("dialog_rcon_error"),
					I18N.translate("dialog_rcon_server_not_found"), e);
		} catch (IOException e) {
			DialogUtil.showExceptionAlert(I18N.translate("dialog_rcon_error"), "", e);
		}

		Stage stage = new Stage();
		stage.setTitle(I18N.format("dialog_rcon_title", ip, port));

		FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemClassLoader().getResource("fxml/RConConsole.fxml"), I18N.getDefaultBundle());

		loader.setController(new RConConsoleController(rcon));

		SplitPane root;
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			Designs.applyCurrentDesign(scene);

			stage.setOnCloseRequest(windowEvent -> {
				try {
					rcon.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				DialogUtil.showExceptionAlert("", I18N.translate("dialog_rcon_error"), e);
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
