package sebe3012.servercontroller.server;

import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.gui.tab.TabEntry;
import sebe3012.servercontroller.api.gui.tab.TabHandler;
import sebe3012.servercontroller.api.gui.tree.TreeEntry;
import sebe3012.servercontroller.api.gui.tree.TreeHandler;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.server.BasicServerHandler;
import sebe3012.servercontroller.api.util.FileUtil;
import sebe3012.servercontroller.gui.tab.ServerRootTab;
import sebe3012.servercontroller.gui.tree.PathTreeEntry;
import sebe3012.servercontroller.gui.tree.ServerTreeEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
		log.info("Adding server handler");
		servers.add(handler);

		ServerRootTab tab = ServerRootTab.createRootTab(handler, this);

		Platform.runLater(() -> rootTabHandler.addTab(tab));

		//TODO better way to implement this
		TreeItem<TreeEntry<?>> item = new TreeItem<>(new ServerTreeEntry(handler, this));
		searchSubFolders(Paths.get(handler.getServer().getJarPath()).getParent(), item, tab.getServerTabHandler(), handler);
		rootTreeHandler.addItem(item);
	}

	@Deprecated
	private void searchSubFolders(Path parent, TreeItem<TreeEntry<?>> parentItem, TabHandler<TabEntry<?>> serverHandler, BasicServerHandler handler) {
		try {
			searchFiles(parent, parentItem, serverHandler, handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	private void searchFiles(Path parent, TreeItem<TreeEntry<?>> parentItem, TabHandler<TabEntry<?>> serverHandler, BasicServerHandler handler) throws IOException {
		if (!Files.isDirectory(parent)) {
			return;
		}

		DirectoryStream<Path> paths = Files.newDirectoryStream(parent, FileUtil.IS_DIRECTORY);

		for (Path path : paths) {
			TreeItem<TreeEntry<?>> childItem = new TreeItem<>(new PathTreeEntry(path, serverHandler, handler, this));
			parentItem.getChildren().add(childItem);

			searchFiles(path, childItem, serverHandler, handler);
		}


		paths = Files.newDirectoryStream(parent, FileUtil.IS_FILE);

		for (Path path : paths) {
			TreeItem<TreeEntry<?>> childItem = new TreeItem<>(new PathTreeEntry(path, serverHandler, handler, this));
			parentItem.getChildren().add(childItem);
		}
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

	public void clearServers() {
		stopAllServers();
		rootTabHandler.clearTabs();
		rootTreeHandler.clearItems();
		servers.clear();
	}

	public void selectServer(BasicServerHandler handler) {
		rootTabHandler.getTabEntries().forEach(entry->{
			if(entry.getItem() == handler){
				rootTabHandler.selectEntry(entry);
			}
		});
	}

	public void editSelectedServer(){
		BasicServerHandler handler = rootTabHandler.getSelectedTabEntry().getItem();
		AddonUtil.loadServerCreateDialog(handler.getServer().getAddon(), handler.getServer(), this);
	}

}
