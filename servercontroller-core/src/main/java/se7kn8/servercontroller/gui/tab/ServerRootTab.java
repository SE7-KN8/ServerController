package se7kn8.servercontroller.gui.tab;

import se7kn8.servercontroller.api.gui.tab.TabEntry;
import se7kn8.servercontroller.api.gui.tab.TabHandler;
import se7kn8.servercontroller.api.gui.tree.TreeEntry;
import se7kn8.servercontroller.api.server.BasicServerHandler;
import se7kn8.servercontroller.gui.FrameHandler;
import se7kn8.servercontroller.server.ServerManager;

import org.jetbrains.annotations.NotNull;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ServerRootTab implements TabEntry<BasicServerHandler> {
	private BasicServerHandler serverHandler;
	private TabHandler<TabEntry<?>> serverTabsHandler;
	private ServerManager serverManager;

	private ServerRootTab(@NotNull BasicServerHandler serverHandler, @NotNull ServerManager serverManager) {
		this.serverHandler = serverHandler;
		this.serverManager = serverManager;
	}

	@FXML
	private VBox rootContent;

	@FXML
	private GridPane lblPane;

	@FXML
	private TabPane serverTabPane;

	@FXML
	private void initialize() {
		serverTabsHandler = new TabHandler<>(getTitle() + "-TabHandler", serverTabPane);
		serverTabsHandler.addTab(ServerConsoleTab.createServerConsoleTab(serverHandler));
		loadServerInfo();
	}

	private void loadServerInfo() {
		lblPane.getChildren().clear();
		int index = 0;

		for (int i = 0; i < lblPane.getRowConstraints().size(); i++) {
			for (int k = 0; k < lblPane.getColumnConstraints().size(); k++) {
				List<String> serverInfo = serverHandler.getServer().getServerInformation();

				if (serverInfo.size() > index) {
					String labelText = serverInfo.get(index);
					Label label = new Label(labelText);
					GridPane.setColumnIndex(label, k);
					GridPane.setRowIndex(label, i);
					lblPane.getChildren().add(label);
				}

				index++;
			}
		}
	}

	@Override
	public void refresh() {
		loadServerInfo();
	}

	@Override
	public boolean isCloseable() {
		return false;
	}

	@NotNull
	@Override
	public String getTitle() {
		return serverHandler.getServer().getName();
	}

	@NotNull
	@Override
	public Node getContent() {
		return rootContent;
	}

	@Override
	public void setItem(@NotNull BasicServerHandler item) {
		this.serverHandler = item;
	}

	@Override
	public boolean onClose() {
		serverHandler.stopServer();
		serverTabsHandler.getTabEntries().forEach(TabEntry::onClose);
		return true;
	}

	@NotNull
	@Override
	public BasicServerHandler getItem() {
		return this.serverHandler;
	}

	@Override
	public void onSelect() {
		for (TreeItem<TreeEntry<?>> item : serverManager.getTreeHandler().getItems()) {
			if (item.getValue().getItem() instanceof BasicServerHandler && item.getValue().getItem() == this.serverHandler) {
				serverManager.getTreeHandler().getTreeView().getSelectionModel().select(item);
			}
		}

		FrameHandler.buttonList.getChildren().clear();
		serverHandler.getServer().getControls().forEach(node -> FrameHandler.buttonList.getChildren().add(node));
	}

	public TabHandler<TabEntry<?>> getServerTabHandler() {
		return serverTabsHandler;
	}

	public static ServerRootTab createRootTab(BasicServerHandler serverHandler, ServerManager serverManager) {
		ServerRootTab rootTab;

		try {
			rootTab = new ServerRootTab(serverHandler, serverManager);
			FXMLLoader loader = new FXMLLoader();
			loader.setController(rootTab);
			loader.setLocation(ClassLoader.getSystemResource("fxml/tab/ServerRootTab.fxml"));
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return rootTab;
	}

}

