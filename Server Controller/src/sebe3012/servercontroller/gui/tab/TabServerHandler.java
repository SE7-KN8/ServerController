package sebe3012.servercontroller.gui.tab;

import java.io.Serializable;

import com.google.common.eventbus.Subscribe;

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
	private TabContentHandler handler;

	public TabServerHandler(TabContentHandler handler) {
		this.id = Tabs.getNextID();
		this.handler = handler;
		EventHandler.EVENT_BUS.registerEventListener(this);
		Tabs.servers.put(id, this);
		Tabs.IDforServers.put(this, Tabs.getNextID());
	}

	public void sendCommand(String command) {
		if (server != null) {
			if (server.isRunning()) {
				server.sendCommand(command);
			} else {
				showServerNotRunningDialog();
			}
		} else {
			showNoServerDialog();
		}
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
				server.sendCommand(server.getStopCommand());
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
				server.sendCommand(server.getStopCommand());
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
		handler.showErrorAlert("Fehler", "", "Kein Server ausgew‰hlt");
	}

	private void showServerNotRunningDialog() {
		handler.showErrorAlert("Fehler", "", "Der Server muﬂ erst gestartet werden");
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

			handler.addTextToOutput("[" + serverName + "] " + event.getMessage());

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

			handler.addTextToOutput(
					"[" + serverName + "] " + "------------------------------------------------------------");

			restartServer = false;
		}

	}

}