package sebe3012.servercontroller.gui.tab;

import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import sebe3012.servercontroller.server.BatchServer;
import sebe3012.servercontroller.server.ServerListener;
import sebe3012.servercontroller.server.Servers;

@SuppressWarnings("unused")
public class TabServerHandler {

	public class ServerHandler implements ServerListener {
		@Override
		public void serverReturnMessage(String message) {
			Platform.runLater(() -> {
				output.appendText(message + "\n");
			});

		}

		@Override
		public void serverStoped(int code) {
			System.out.println(restartServer);
			server.stop();
			server = null;
			if (restartServer) {
				server = new BatchServer(batchPath, propertiesPath, serverName);
				server.registerListener(new ServerHandler());
				try {
					server.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				server = new BatchServer(batchPath, propertiesPath, serverName);
				server.registerListener(new ServerHandler());
			}
			Platform.runLater(() -> {
				output.appendText("Server stopped with code: " + code + "\n");
				output.appendText("-----------------------------------------\n");
			});
			System.out.println(server);

		}
	}

	private BatchServer server;

	private TextArea output;
	private TextField input;
	private Button start;
	private Button stop;
	private Button send;
	private Label info;
	private Button restart;
	private boolean restartServer = false;

	public TabServerHandler(TextArea output, TextField input, Button start, Button stop, Button send, Label info,
			Button btnRestart) {
		this.output = output;
		this.input = input;
		this.start = start;
		this.stop = stop;
		this.send = send;
		this.info = info;
		this.restart = btnRestart;
		Tabs.servers.put(Tabs.getNextID(), this);
		Tabs.IDforServers.put(this, Tabs.getNextID());
	}

	public void onStartClicked() {
		restartServer = false;
		startServer();
		info.setText("Server: " + server.getName() + "\nPort: " + server.getPort());
	}

	public void onEndClicked() {
		restartServer = false;
		if (server != null) {
			if(server.isRunning()){
				server.sendCommand("stop");
			}
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

	public void onRestartClicked() {
		restartServer = true;
		server.sendCommand("stop");
	}

	public boolean hasServer() {
		if (server == null) {
			return false;
		} else {
			return true;
		}
	}

	private String batchPath;
	private String propertiesPath;
	private String serverName;

	public void initServer(String batch, String properties, String id) {
		this.batchPath = batch;
		this.propertiesPath = properties;
		this.serverName = id;
		this.server = new BatchServer(batch, properties, id);
		this.server.registerListener(new ServerHandler());
		Servers.servers.add(server);
	}

	public void startServer() {
		try {
			if (server != null) {
				server.start();
				System.out.println(Servers.servers);
			} else {
				Alert a = new Alert(AlertType.ERROR, "Kein Server ausgewählt", ButtonType.OK);
				a.getDialogPane().getStylesheets()
						.add(TabServerHandler.class.getResource("style.css").toExternalForm());
				a.showAndWait();
			}
		} catch (IOException e) {

		}
	}

	public BatchServer getServer() {
		return server;
	}

}
