package sebe3012.servercontroller.gui.tab;

import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import sebe3012.servercontroller.server.BatchServer;
import sebe3012.servercontroller.server.ServerListener;

@SuppressWarnings("unused")
public class TabServerHandler {

	public class ServerHandler implements ServerListener {
		@Override
		public void serverReturnMessage(String message) {
			output.appendText(message + "\n");
		}

		@Override
		public void serverStoped(int code) {
			output.appendText("Server stopped with code: " + code);
		}
	}

	private BatchServer server;

	private TextArea output;
	private TextField input;
	private Button start;
	private Button stop;
	private Button send;
	private Label info;

	public TabServerHandler(TextArea output, TextField input, Button start, Button stop, Button send, Label info) {
		this.output = output;
		this.input = input;
		this.start = start;
		this.stop = stop;
		this.send = send;
		this.info = info;
		Tabs.servers.put(Tabs.getNextID(), this);
		Tabs.IDforServers.put(this, Tabs.getNextID());
	}

	public void onStartClicked() {
		startServer();
	}

	public void onEndClicked() {
		if (server != null) {
			server.sendCommand("stop");
		} else {
			Alert a = new Alert(AlertType.ERROR, "Kein Server ausgewählt", ButtonType.OK);
			a.getDialogPane().getStylesheets().add(TabServerHandler.class.getResource("style.css").toExternalForm());
			a.showAndWait();
		}
	}

	public void onSendClicked() {
		if (server != null) {
			server.sendCommand(input.getText());
			input.setText("");
		} else {
			Alert a = new Alert(AlertType.ERROR, "Kein Server ausgewählt", ButtonType.OK);
			a.getDialogPane().getStylesheets().add(TabServerHandler.class.getResource("style.css").toExternalForm());
			a.showAndWait();
		}
	}

	public boolean hasServer() {
		if (server == null) {
			return false;
		} else {
			return true;
		}
	}

	public void initServer(String batch, String properties, String id) {
		server = new BatchServer(batch, properties, id);
		server.registerListener(new ServerHandler());
	}

	public void startServer() {
		try {
			if (server != null) {
				server.start();
			} else {
				Alert a = new Alert(AlertType.ERROR, "Kein Server ausgewählt", ButtonType.OK);
				a.getDialogPane().getStylesheets()
						.add(TabServerHandler.class.getResource("style.css").toExternalForm());
				a.showAndWait();
			}
		} catch (IOException e) {

		}
	}

}
