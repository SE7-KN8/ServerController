package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;

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

		if (index == FrameHandler.tree.getSelectionModel().getSelectedIndex()) {
			return index;
		}
		throw new IllegalStateException("Tab index != List index");

	}

	public static void removeAllTabs() {
		Servers.serversList.clear();

		FrameHandler.tree.getRoot().getChildren().clear();
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

				FrameHandler.tree.getSelectionModel().select(index);
			}
		}
	}

	private static void showServerIsRunningDialog() {
		DialogUtil.showWaringAlert(I18N.translate("dialog_warning"), "", I18N.translate("dialog_server_must_be_stopped"));
	}

}
