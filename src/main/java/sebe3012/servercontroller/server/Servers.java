package sebe3012.servercontroller.server;

import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.util.DialogUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Servers {
	public static ObservableList<BasicServer> serversList = FXCollections.observableArrayList();

	private static Logger log = LogManager.getLogger();

	static {
		log.info("Server-list was initialized");

		Servers.serversList.addListener((ListChangeListener.Change<? extends BasicServer> change) -> log.debug("Servers-list was changed"));
	}

	public static void startCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onStartClicked();
		}
	}

	public static void stopCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onStopClicked();
		}
	}

	public static void restartCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onRestartClicked();
		}
	}

	public static void editCurrentServer() {
		BasicServer server = Tabs.getCurrentServer();
		if (server != null) {
			if (!server.isRunning()) {
				EventHandler.EVENT_BUS.post(new ServerEditEvent(server.getPluginName(), server));
			} else {
				DialogUtil.showWaringAlert("Warnung", "", "Der Server muß erst gestoppt werden");
			}
		}
	}

	public static void startAllServers() {
		for (BasicServer server : Servers.serversList) {
			server.getServerHandler().onStartClicked();
		}
	}

	public static void restartAllServers() {
		for (BasicServer server : Servers.serversList) {
			server.getServerHandler().onRestartClicked();
		}
	}

	public static void stopAllServers() {
		for (BasicServer server : Servers.serversList) {
			server.getServerHandler().onStopClicked();
		}
	}

}
