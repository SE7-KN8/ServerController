package sebe3012.servercontroller.gui.tab;

import java.io.Serializable;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import sebe3012.servercontroller.event.ChangeButtonsEvent;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.event.ServerMessageEvent;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;

public class TabServerHandler implements Serializable, IEventHandler {

	private static final long serialVersionUID = -3676312859798447613L;
	private BasicServer server;
	private boolean restartServer = false;
	private final int id;

	public TabServerHandler() {
		this.id = Tabs.getNextID();
		EventHandler.EVENT_BUS.registerEventListener(this);
		Tabs.servers.put(id, this);
		Tabs.IDforServers.put(this, Tabs.getNextID());
	}

	public void onStartClicked() {
		restartServer = false;
		startServer();
		Tabs.contents.get(id).cOutput.appendText("[" + serverName + "] " + "Server \"" + serverName + "\" starts\n");
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

	private String serverName;

	public void startServer() {
		if (server != null) {
			server.start();
		} else {
			showNoServerDialog();
		}

	}

	public BasicServer getServer() {
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

	@Subscribe
	public void serverCreateEvent(ServerCreateEvent event) {
		this.server = event.getServer();
		this.serverName = server.getName();
		Servers.servers.add(this.server);
		
		Tabs.contents.get(id).lblInfo.setText(this.server.getServerInfo());
		
		EventHandler.EVENT_BUS.post(new ChangeButtonsEvent(this.server.getExtraButtons()));

	}

	@Subscribe
	public void serverReturnMessage(ServerMessageEvent event) {
		if (event.getServer() == server) {

			Platform.runLater(() -> {

				TextArea output = Tabs.contents.get(id).cOutput;
				output.appendText("[" + serverName + "] " + event.getMessage() + "\n");
			});
		}
	}

	@Subscribe
	public void serverStoped(ServerStopEvent event) {

		if (event.getServer() == server) {

			server.stop();
			server = server.createNew();

			if (restartServer) {
				server.start();
			}
			Platform.runLater(() -> {
				TextArea output = Tabs.contents.get(id).cOutput;
				output.appendText(
						"[" + serverName + "] " + "------------------------------------------------------------\n");
			});
			restartServer = false;
		}

	}

}