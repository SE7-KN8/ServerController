package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;

public class Tabs {

	public static ServerTab getCurrentTab() {
		if (FrameHandler.mainPane.getSelectionModel().getSelectedItem() != null) {
			if (FrameHandler.mainPane.getSelectionModel().getSelectedItem() instanceof ServerTab) {
				return (ServerTab) FrameHandler.mainPane.getSelectionModel().getSelectedItem();
			}
		}
		return null;
	}

	public static BasicServer getCurrentServer() {
		if (getCurrentTab() != null) {
			if (getCurrentTab().getTabContent().getContentHandler().getServerHandler().hasServer()) {
				return getCurrentTab().getTabContent().getContentHandler().getServerHandler().getServer();
			}
		}
		
		return null;

	}
}
