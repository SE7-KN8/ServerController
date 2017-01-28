package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;

import org.jetbrains.annotations.Nullable;

import javafx.scene.control.Tab;

public class Tabs {

	@Nullable
	public static TabServerHandler getCurrentServerHandler() {
		ServerTab tab = getCurrentTab();

		if(tab != null){
			return tab.getTabContent().getContentHandler().getServerHandler();
		}

		return null;
	}

	@Nullable
	public static ServerTab getCurrentTab() {
		Tab tab = FrameHandler.mainPane.getSelectionModel().getSelectedItem();

		if (tab != null && tab instanceof ServerTab) {
			return (ServerTab) tab;
		}

		return null;
	}

	@Nullable
	public static BasicServer getCurrentServer() {
		TabServerHandler serverHandler = getCurrentServerHandler();

		if(serverHandler != null && serverHandler.hasServer()){
			return serverHandler.getServer();
		}

		return null;
	}

	public static int getCurrentIndex() {
		int index = FrameHandler.mainPane.getSelectionModel().getSelectedIndex();

		if (index == FrameHandler.list.getSelectionModel().getSelectedIndex()) {
			return index;
		}
		throw new IllegalStateException("Tab index != List index");

	}

	public static void removeAllTabs() {
		Servers.serversList.clear();
		FrameHandler.list.setItems(null);
		FrameHandler.list.setItems(Servers.serversList);

		FrameHandler.mainPane.getTabs().clear();

	}

	public static void removeCurrentTab() {
		BasicServer server = Tabs.getCurrentServer();
		if (server != null) {
			if (server.isRunning()) {
				showServerIsRunningDialog();
			} else {

				int index = Tabs.getCurrentIndex();


				FrameHandler.mainPane.getTabs().remove(index);

				Servers.serversList.remove(index);

				FrameHandler.list.getSelectionModel().select(index);
			}
		}
	}

	private static void showServerIsRunningDialog() {
		DialogUtil.showWaringAlert("Fehler", "", "Der Server muß erst gestoppt werden");
	}

}
