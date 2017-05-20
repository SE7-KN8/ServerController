package sebe3012.servercontroller.server;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.event.ChangeControlsEvent;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.gui.tree.ServerTreeEntry;
import sebe3012.servercontroller.gui.tree.TreeEntry;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.FileUtil;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Servers {
	public static ObservableList<TreeEntry<BasicServer>> serversList = FXCollections.observableArrayList();
	private static Map<BasicServer, Map<String, StringProperty>> serverProperties = new HashMap<>();


	private static Logger log = LogManager.getLogger();

	static {
		log.info("Server-tree was initialized");

		Servers.serversList.addListener((ListChangeListener.Change<? extends TreeEntry<BasicServer>> change) -> {

			//if (change.next()) {
			//List<? extends TreeEntry<BasicServer>> added = change.getAddedSubList();
			//added.forEach(item -> FrameHandler.rootItem.getChildren().add(new TreeItem<>(item)));
			//}TODO

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
		} else {
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void stopCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onStopClicked();
		} else {
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void restartCurrentServer() {
		TabServerHandler server = Tabs.getCurrentServerHandler();
		if (server != null) {
			server.onRestartClicked();
		} else {
			DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.translate("dialog_no_server_selected"));
		}
	}

	public static void editCurrentServer() {
		BasicServer server = Tabs.getCurrentServer();
		if (server != null) {
			if (!server.isRunning()) {
				AddonUtil.loadServerCreateDialog(server.getAddon(), server);
				//EventHandler.EVENT_BUS.post(new ServerEditEvent(server.getAddonName(), server));
			} else {
				DialogUtil.showWaringAlert(I18N.translate("dialog_warning"), "", I18N.translate("dialog_server_must_be_stopped"));
			}
		} else {
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

	public static BasicServer createBasicServer(Map<String, StringProperty> properties, Class<? extends BasicServer> serverClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		log.debug("Creating basic server '{}' with properties '{}'", serverClass.getSimpleName(), properties);

		Constructor<? extends BasicServer> constructor = serverClass.getConstructor(Map.class);

		BasicServer server = constructor.newInstance(properties);

		serverProperties.put(server, properties);

		return server;
	}

	public static Map<String, StringProperty> getServerProperties(BasicServer server) {
		return serverProperties.get(server);
	}

	public static void addServer(BasicServer server, Addon addon) {
		log.debug("Add server {}", server);
		server.setAddon(addon);

		TabContent content = new TabContent();
		ServerTab tab = new ServerTab(server.getName(), content);
		tab.textProperty().bindBidirectional(server.nameProperty());
		tab.setContent(content.getTabContent());

		Platform.runLater(() -> FrameHandler.mainPane.getTabs().add(tab));

		ServerTreeEntry entry = new ServerTreeEntry(server);

		TreeItem<TreeEntry<?>> item = new TreeItem<>(entry);
		FileUtil.searchSubFolders(Paths.get(server.getJarPath()).getParent(), item);

		FrameHandler.rootItem.getChildren().add(item);
		serversList.add(entry);

		FrameHandler.mainPane.getSelectionModel().select(tab);

		EventHandler.EVENT_BUS.post(new ChangeControlsEvent(server.getExtraControls()));


		EventHandler.EVENT_BUS.post(new ServerCreateEvent(server));
	}
}
