package sebe3012.servercontroller.gui.tab;

import java.io.Serializable;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import sebe3012.servercontroller.gui.dialog.properties.PropertiesDialog;
import sebe3012.servercontroller.server.JarServer;
import sebe3012.servercontroller.server.ServerListener;
import sebe3012.servercontroller.server.Servers;

public class TabServerHandler implements Serializable {

	private static final long serialVersionUID = -6964977119376149290L;

	public class ServerHandler implements ServerListener, Serializable {

		private static final long serialVersionUID = -7773561411474591661L;

		@Override
		public void serverReturnMessage(String message) {
			Platform.runLater(() -> {
				TextArea output = Tabs.contents.get(id).cOutput;
				output.appendText(message + "\n");
			});

		}

		@Override
		public void serverStoped(int code) {
			server.stop();
			server = new JarServer(jarPath, propertiesPath, serverName, serverRam);
			server.registerListener(new ServerHandler());
			if (restartServer) {
				server.start();
			}
			Platform.runLater(() -> {
				TextArea output = Tabs.contents.get(id).cOutput;
				output.appendText("Server stopped with code: " + code + "\n");
				output.appendText("------------------------------------------------------------\n");
			});
			restartServer = false;

		}
	}

	private JarServer server;
	private boolean restartServer = false;
	private final int id;

	public TabServerHandler() {
		this.id = Tabs.getNextID();
		Tabs.servers.put(id, this);
		Tabs.IDforServers.put(this, Tabs.getNextID());
	}

	public void onStartClicked() {
		restartServer = false;
		startServer();
		Tabs.contents.get(id).lblInfo
				.setText("Server: " + server.getName() + "\nPort: " + server.getServerProperties().getServerPort()
						+ "\nMaximale Spieler: " + server.getServerProperties().getMaxPlayers());
		Tabs.contents.get(id).cOutput.appendText("Server \"" + serverName + "\" starts\n");
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
				TextField input = Tabs.contents.get(id).cInput;
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

	private String jarPath;
	private String propertiesPath;
	private String serverName;
	private String serverRam;

	public void initServer(String jar, String properties, String id, String ram, boolean addList) {
		this.jarPath = jar;
		this.propertiesPath = properties;
		this.serverName = id;
		this.serverRam = ram;
		this.server = new JarServer(jar, properties, id, ram);
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

	public JarServer getServer() {
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