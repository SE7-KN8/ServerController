package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

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
				FrameHandler.list.setItems(null);

				Servers.serversList.remove(index);

				FrameHandler.list.setItems(Servers.serversList);
				FrameHandler.list.getSelectionModel().select(index);

			}
		}
	}

	private static void showServerIsRunningDialog() {
		Alert dialog = new Alert(AlertType.WARNING, "Der Server mu� erst gestoppt werden", ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(FrameHandler.class.getResource("style.css").toExternalForm());
		dialog.setTitle("Fehler");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}

}
