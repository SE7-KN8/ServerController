package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;

public class Tabs {

	public static ServerTab getCurrentTab() {
		return (ServerTab) (FrameHandler.mainPane.getSelectionModel().getSelectedItem());
	}

	public static BasicServer getCurrentServer() {
		if (getCurrentTab().getTabContent().getContentHandler().getServerHandler().hasServer()) {
			return getCurrentTab().getTabContent().getContentHandler().getServerHandler().getServer();
		}

		return null;

	}
}
