package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.addon.api.StringPredicates;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.event.ServerMessageEvent;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.gui.tree.TreeEntry;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.ServerState;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;

public class TabServerHandler implements IEventHandler {
	private BasicServer server;
	private boolean restartServer = false;
	private TabContentHandler handler;

	public TabServerHandler(TabContentHandler handler) {
		this.handler = handler;
		EventHandler.EVENT_BUS.registerEventListener(this);
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
		if (server != null) {
			if (!server.isRunning()) {
				startServer();
			} else {
				showServerAlreadyRunningDialog();
			}
		} else {
			showNoServerDialog();
		}
	}

	public void onStopClicked() {
		restartServer = false;
		if (server != null) {
			if (server.isRunning()) {
				server.sendCommand(server.getStopCommand());
				server.setState(ServerState.STOPPING);
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
				server.setState(ServerState.STOPPING);
			} else {
				showServerNotRunningDialog();
			}
		} else {
			showNoServerDialog();
		}
	}

	public boolean hasServer() {
		return server != null;
	}

	private String serverName;

	private void startServer() {
		if (server != null) {
			server.start();
			handler.addTextToOutput("[" + serverName + "] " + "Server \"" + serverName + "\" starts\n");
		} else {
			showNoServerDialog();
		}

	}

	public TabContentHandler getContentHandler() {
		return handler;
	}

	public BasicServer getServer() {
		return server;
	}

	private void showNoServerDialog() {
		DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
	}

	private void showServerNotRunningDialog() {
		DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_server_must_be_started"));
	}

	private void showServerAlreadyRunningDialog() {
		DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_server_already_started"));
	}

	public void refreshListState() {
		handler.refreshListState();
	}

	@Subscribe
	public void serverCreateEvent(ServerCreateEvent event) {
		if (!this.hasServer()) {
			this.server = event.getServer();
			this.serverName = server.getName();

			if (!this.server.hasServerHandler()) {
				server.setServerHandler(this);
			}

			Platform.runLater(() -> getContentHandler().lblInfo.setText(this.server.getServerInfo()));
		}
	}

	@Subscribe
	public void serverReturnMessage(ServerMessageEvent event) {
		if (event.getServer() == server) {
			handler.addTextToOutput("[" + serverName + "] " + event.getMessage());
			if (server.getState() == ServerState.STARTING && StringPredicates.SERVER_DONE_CHECK.test(event.getMessage(), event.getServer())) {
				server.setState(ServerState.RUNNING);
			}
		}
	}

	@Subscribe
	public void serverStopped(ServerStopEvent event) {
		if (event.getServer() == server) {

			TreeEntry<BasicServer> entry = Servers.findEntry(server);

			server.stop();

			if (entry != null) {
				entry.setItem(server);
			} else {
				throw new RuntimeException("Can't find entry for server: " + server.getName());
			}

			handler.addTextToOutput("[" + serverName + "] ------------------------------------------------------------");

			if (restartServer) {
				server.start();
			}


			restartServer = false;
		}

	}

}