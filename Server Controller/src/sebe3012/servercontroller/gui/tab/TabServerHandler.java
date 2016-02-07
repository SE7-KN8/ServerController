package sebe3012.servercontroller.gui.tab;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import sebe3012.servercontroller.gui.dialog.properties.PropertiesDialog;
import sebe3012.servercontroller.server.BatchServer;
import sebe3012.servercontroller.server.ServerListener;
import sebe3012.servercontroller.server.Servers;

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
			server.stop();
			server = new BatchServer(batchPath, propertiesPath, serverName);
			server.registerListener(new ServerHandler());
			if (restartServer) {
				server.start();
			}
			Platform.runLater(() -> {
				output.appendText("Server stopped with code: " + code + "\n");
				output.appendText("------------------------------------------------------------\n");
			});

		}
	}

	private BatchServer server;

	private TextArea output;
	private TextField input;
	private Label info;
	private boolean restartServer = false;

	public TabServerHandler(TextArea output, TextField input, Label info) {
		this.output = output;
		this.input = input;
		this.info = info;
		Tabs.servers.put(Tabs.getNextID(), this);
		Tabs.IDforServers.put(this, Tabs.getNextID());
	}

	public void onStartClicked() {
		restartServer = false;
		startServer();
		info.setText("Server: " + server.getName() + "\nPort: " + server.getServerProperties().getServerPort()
				+ "\nMaximale Spieler: " + server.getServerProperties().getMaxPlayers());
	}

	public void onEndClicked() {
		restartServer = false;
		if (server != null) {
			if (server.isRunning()) {
				server.sendCommand("stop");
			} else {
				showServerNotRunningDialog();
			}
		} else {
			showNoServerDialog();
		}
	}

	public void onSendClicked() {
		if (server != null) {
			if (server.isRunning()) {
				server.sendCommand(input.getText());
				input.setText("");
			} else {
				showServerNotRunningDialog();
			}
		} else {
			showNoServerDialog();
		}
	}

	public void onRestartClicked() {
		if (server != null) {
			if (server.isRunning()) {
				restartServer = true;
				server.sendCommand("stop");
			} else {
				showServerNotRunningDialog();
			}
		} else {
			showNoServerDialog();
		}
	}

	public boolean hasServer() {
		return server == null ? false : true;
	}

	private String batchPath;
	private String propertiesPath;
	private String serverName;

	public void initServer(String batch, String properties, String id, boolean addList) {
		this.batchPath = batch;
		this.propertiesPath = properties;
		this.serverName = id;
		this.server = new BatchServer(batch, properties, id);
		this.server.registerListener(new ServerHandler());
		if (addList) {
			Servers.servers.add(server);
		}
	}

	public void startServer() {
		if (server != null) {
			server.start();
		} else {
			showNoServerDialog();
		}

	}

	public BatchServer getServer() {
		return server;
	}

	private void showNoServerDialog() {
		Alert a = new Alert(AlertType.ERROR, "Kein Server ausgewählt", ButtonType.OK);
		a.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		a.showAndWait();
	}

	private void showServerNotRunningDialog() {
		Alert a = new Alert(AlertType.WARNING, "Der Server läuft noch nicht", ButtonType.OK);
		a.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		a.showAndWait();
	}

	public void onPropertiesClicked() {
		if (server != null) {
			if (server.isRunning()) {
				PropertiesDialog.properties = server.getServerProperties();
				new PropertiesDialog(new Stage());
			} else {
				showServerNotRunningDialog();
			}
		} else {
			showNoServerDialog();
		}

	}

}