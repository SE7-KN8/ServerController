package se7kn8.servercontroller.server;

import se7kn8.servercontroller.addon.AddonRegistryHelper;
import se7kn8.servercontroller.addon.AddonUtil;
import se7kn8.servercontroller.api.gui.tab.TabEntry;
import se7kn8.servercontroller.api.gui.tab.TabHandler;
import se7kn8.servercontroller.api.gui.tree.TreeEntry;
import se7kn8.servercontroller.api.gui.tree.TreeHandler;
import se7kn8.servercontroller.api.server.BasicServer;
import se7kn8.servercontroller.api.server.BasicServerHandler;
import se7kn8.servercontroller.api.util.FileUtil;
import se7kn8.servercontroller.gui.tab.ServerRootTab;
import se7kn8.servercontroller.gui.tree.PathTreeEntry;
import se7kn8.servercontroller.gui.tree.ServerTreeEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerManager {

	private List<BasicServerHandler> servers;
	private TabHandler<TabEntry<BasicServerHandler>> rootTabHandler;
	private TreeHandler<TreeEntry<?>> rootTreeHandler;
	private Logger log = LogManager.getLogger();
	private AddonRegistryHelper registryHelper;

	public ServerManager(@NotNull TabHandler<TabEntry<BasicServerHandler>> rootTabHandler, @NotNull TreeHandler<TreeEntry<?>> rootTreeHandler, @NotNull AddonRegistryHelper helper) {
		this.rootTabHandler = rootTabHandler;
		this.rootTreeHandler = rootTreeHandler;
		this.registryHelper = helper;
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
		rootTabHandler.getSelectedTabEntry().ifPresent(tabEntry -> tabEntry.getItem().startServer());
	}

	public void restartSelectedServer() {
		rootTabHandler.getSelectedTabEntry().ifPresent(tabEntry -> tabEntry.getItem().restartServer());
	}

	public void stopSelectedServer() {
		rootTabHandler.getSelectedTabEntry().ifPresent(tabEntry -> tabEntry.getItem().stopServer());
	}

	@NotNull
	public BasicServerHandler createServerHandler(@NotNull Map<String, String> properties, @NotNull Class<? extends BasicServer> serverClass, boolean addServer, @NotNull String addonID, @NotNull String serverCreatorID) throws NoSuchMethodException, InstantiationException, InvocationTargetException, IllegalAccessException {
		log.info("Creating server '{}' with properties: '{}'", serverClass.getSimpleName(), properties);

		Constructor<? extends BasicServer> constructor = serverClass.getConstructor();
		BasicServer server = constructor.newInstance();
		server.initialize(properties);
		server.setServerCreatorInfo(addonID, serverCreatorID);
		BasicServerHandler handler = new BasicServerHandler(server);

		if (addServer) {
			addServerHandler(handler);
		}

		return handler;
	}

	public void addServerHandler(@NotNull BasicServerHandler handler) {
		log.info("Adding server handler");

		if (getServerIds().contains(handler.getServer().getName())) {
			throw new IllegalArgumentException("ID '" + handler.getServer().getName() + "' is already present!");
		}

		servers.add(handler);
		ServerRootTab tab = ServerRootTab.createRootTab(handler, this);

		Platform.runLater(() -> {
			rootTabHandler.addTab(tab);
			rootTabHandler.selectEntry(tab);
		});

		TreeItem<TreeEntry<?>> item = new TreeItem<>(new ServerTreeEntry(handler, this));

		String path = handler.getServer().getProperties().get("jarPath");

		if (path != null) {
			//TODO better way to implement this
			searchSubFolders(Paths.get(path).getParent(), item, tab.getServerTabHandler(), handler);
		}

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

			Optional<TabEntry<BasicServerHandler>> basicServerHandlerTabEntry = rootTabHandler.getSelectedTabEntry();

			if (basicServerHandlerTabEntry.isPresent()) {
				BasicServerHandler handler = basicServerHandlerTabEntry.get().getItem();
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
	}

	@NotNull
	public TabHandler<TabEntry<BasicServerHandler>> getTabHandler() {
		return rootTabHandler;
	}

	@NotNull
	public TreeHandler<TreeEntry<?>> getTreeHandler() {
		return rootTreeHandler;
	}

	@NotNull
	public AddonRegistryHelper getRegistryHelper() {
		return registryHelper;
	}

	public void clearServers() {
		stopAllServers();
		rootTabHandler.clearTabs();
		rootTreeHandler.clearItems();
		servers.clear();
	}

	public void selectServer(@NotNull BasicServerHandler handler) {
		rootTabHandler.getTabEntries().forEach(entry -> {
			if (entry.getItem() == handler) {
				rootTabHandler.selectEntry(entry);
			}
		});
	}

	public void editSelectedServer() {
		rootTabHandler.getSelectedTabEntry().ifPresent(tabEntry -> AddonUtil.loadServerCreateDialog(tabEntry.getItem().getServer().getAddonID(), tabEntry.getItem().getServer().getServerCreatorID(), tabEntry.getItem().getServer(), this));
	}

	@NotNull
	public List<String> getServerIds() {
		return servers.stream().map(basicServerHandler -> basicServerHandler.getServer().getName()).collect(Collectors.toList());
	}

	@NotNull
	public Optional<BasicServerHandler> findServerByID(String id) {
		if (id != null) {
			return servers.stream().filter(basicServerHandler -> basicServerHandler.getServer().getName().equals(id)).findFirst();
		} else {
			return Optional.empty();
		}
	}

}
