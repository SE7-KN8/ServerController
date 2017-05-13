package sebe3012.servercontroller.server;

import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.gui.tree.TreeEntry;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

import java.util.List;

public class Servers {
	public static ObservableList<TreeEntry<BasicServer>> serversList = FXCollections.observableArrayList();


	private static Logger log = LogManager.getLogger();

	static {
		log.info("Server-tree was initialized");

		Servers.serversList.addListener((ListChangeListener.Change<? extends TreeEntry<BasicServer>> change) -> {

			if (change.next()) {
				List<? extends TreeEntry<BasicServer>> added = change.getAddedSubList();
				added.forEach(item -> FrameHandler.rootItem.getChildren().add(new TreeItem<>(item)));
			}

			log.debug("Servers-tree was changed");
		});
	}

	public static TreeEntry<BasicServer> findEntry(BasicServer server) {
		for (TreeEntry<BasicServer> item : serversList) {

			if (item.getItem().equals(server)) {
				return item;
			}
		}

		return null;
	}

	public static ServerTab findTab(BasicServer server) {

		for (Tab tab : FrameHandler.mainPane.getTabs()) {
			if (tab instanceof ServerTab) {

				ServerTab serverTab = (ServerTab) tab;

				if (serverTab.getTabContent().getContentHandler().getServerHandler().hasServer() && serverTab.getTabContent().getContentHandler().getServerHandler().getServer().equals(server)) {
					return serverTab;
				}

			}
		}

		return null;
	}

	public static void startCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onStartClicked();
		}else{
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void stopCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onStopClicked();
		}else{
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void restartCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onRestartClicked();
		}else{
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void editCurrentServer() {
		BasicServer server = Tabs.getCurrentServer();
		if (server != null) {
			if (!server.isRunning()) {
				EventHandler.EVENT_BUS.post(new ServerEditEvent(server.getAddonName(), server));
			} else {
				DialogUtil.showWaringAlert(I18N.translate("dialog_warning"), "", I18N.translate("dialog_server_must_be_stopped"));
			}
		}else{
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void startAllServers() {
		for (TreeEntry<BasicServer> item : Servers.serversList) {
			item.getItem().getServerHandler().onStartClicked();
		}
	}

	public static void restartAllServers() {
		for (TreeEntry<BasicServer> item : Servers.serversList) {
			item.getItem().getServerHandler().onRestartClicked();
		}
	}

	public static void stopAllServers() {
		for (TreeEntry<BasicServer> item : Servers.serversList) {
			item.getItem().getServerHandler().onStopClicked();
		}
	}

}
