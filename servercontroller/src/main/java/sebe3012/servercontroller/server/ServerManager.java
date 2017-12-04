package sebe3012.servercontroller.server;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.gui.tab.ServerRootTab;
import sebe3012.servercontroller.gui.tab.TabEntry;
import sebe3012.servercontroller.gui.tab.TabHandler;
import sebe3012.servercontroller.gui.tree.ServerTreeEntry;
import sebe3012.servercontroller.gui.tree.TreeEntry;
import sebe3012.servercontroller.gui.tree.TreeHandler;
import sebe3012.servercontroller.util.FileUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServerManager {

	private List<BasicServerHandler> servers;
	private TabHandler<TabEntry<BasicServerHandler>> rootTabHandler;
	private TreeHandler<TreeEntry<?>> rootTreeHandler;
	private Logger log = LogManager.getLogger();

	public ServerManager(@NotNull TabHandler<TabEntry<BasicServerHandler>> rootTabHandler, @NotNull TreeHandler<TreeEntry<?>> rootTreeHandler) {
		this.rootTabHandler = rootTabHandler;
		this.rootTreeHandler = rootTreeHandler;
		servers = new ArrayList<>();
	}

	@NotNull
	public List<BasicServerHandler> getServerList() {
		return Collections.unmodifiableList(servers);
	}

	public void startAllServers() {
		servers.forEach(BasicServerHandler::startServer);
	}

	public void restartAllServers() {
		servers.forEach(BasicServerHandler::restartServer);
	}

	public void stopAllServers() {
		servers.forEach(BasicServerHandler::stopServer);
	}

	public void startSelectedServer() {
		rootTabHandler.getSelectedTabEntry().getItem().startServer();
	}

	public void restartSelectedServer() {
		rootTabHandler.getSelectedTabEntry().getItem().restartServer();
	}

	public void stopSelectedServer() {
		rootTabHandler.getSelectedTabEntry().getItem().stopServer();
	}

	@NotNull
	public BasicServerHandler createServerHandler(@NotNull Map<String, StringProperty> properties, @NotNull Class<? extends BasicServer> serverClass, @NotNull Addon addon, boolean addServer) throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
		log.info("Creating server '{}' with properties: '{}'", serverClass.getSimpleName(), properties);

		Constructor<? extends BasicServer> constructor = serverClass.getConstructor(Map.class, Addon.class);
		BasicServer server = constructor.newInstance(properties, addon);
		BasicServerHandler handler = new BasicServerHandler(server);

		if (addServer) {
			addServerHandler(handler);
		}

		return handler;
	}

	public void addServerHandler(@NotNull BasicServerHandler handler) {
		Platform.runLater(()->{
			log.info("Adding server handler");
			servers.add(handler);

			ServerRootTab tab = ServerRootTab.createRootTab(handler, this);

			//TODO better way to implement this
			TreeItem<TreeEntry<?>> item = new TreeItem<>(new ServerTreeEntry(handler, this));
			FileUtil.searchSubFolders(Paths.get(handler.getServer().getJarPath()).getParent(), item, tab.getServerTabHandler());
			rootTreeHandler.addItem(item);
		});
	}

	public void removeSelectedServer() {
		if (servers.size() > 0) {
			BasicServerHandler handler = rootTabHandler.getSelectedTabEntry().getItem();
			servers.remove(handler);
			rootTreeHandler.disableUpdateEntry();
			rootTabHandler.disableUpdateEntry();
			rootTabHandler.closeSelectedTab();
			rootTreeHandler.removeSelectedItem();
			rootTabHandler.enableUpdateEntry();
			rootTreeHandler.enableUpdateEntry();
			rootTreeHandler.updateSelectedEntry();
			rootTabHandler.updateSelectedEntry();
		}
	}

	@NotNull
	public TabHandler<TabEntry<BasicServerHandler>> getTabHandler() {
		return rootTabHandler;
	}

	@NotNull
	public TreeHandler<TreeEntry<?>> getTreeHandler() {
		return rootTreeHandler;
	}

	public void clearServers(){
		//TODO
	}

}
