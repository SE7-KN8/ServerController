package sebe3012.servercontroller.save;
/*
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.gui.tab.Tabs;*/

public class ServerSave {

	public static void saveServerController(String path)/* throws IOException */{
		/*boolean canContinue = true;
		for (int i = 0; i < Tabs.servers.size(); i++) {
			if (Tabs.servers.get(i).getServer().isRunning()) {
				showServerIsRunningDialog();
				canContinue = false;
				break;
			}
		}
		if (canContinue) {
			FileOutputStream fos = new FileOutputStream(new File(path));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(Tabs.servers);
			oos.close();
		}*/
	}

	//private static boolean init = false;

	public static void loadServerController(String path) /*throws IOException, ClassNotFoundException */{
		/*FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object map = null;
		try {
			map = ois.readObject();
		} catch (InvalidClassException | ClassNotFoundException e) {
			Platform.runLater(() -> {
				Alert wrongVersion = new Alert(AlertType.ERROR,
						"Diese Speicher-Datei ist nicht für diese Version des ServerControllers geeignet. Bitte nehme die neuste Version",
						ButtonType.OK);
				wrongVersion.getDialogPane().getStylesheets()
						.add(FrameHandler.class.getResource("style.css").toExternalForm());
				wrongVersion.showAndWait();
			});
		}
		ois.close();
		if (map instanceof HashMap<?, ?>) {
			HashMap<?, ?> servers = (HashMap<?, ?>) map;

			FrameHandler.mainPane.getTabs().clear();
			FrameHandler.list.getItems().clear();
			Tabs.contents.clear();
			Tabs.servers.clear();
			Tabs.IDforContents.clear();
			Tabs.IDforServers.clear();
			Tabs.resetID();

			servers.forEach((id, server) -> {
				if (server instanceof TabServerHandler) {
					TabServerHandler tsh = (TabServerHandler) server;
					Platform.runLater(() -> {
						TabContent content = new TabContent();
						ServerTab tab = new ServerTab(tsh.getServer().getName(), content);
						tab.setContent(content.getTabContent());
						tab.setClosable(false);
						FrameHandler.mainPane.getTabs().add(tab);
						init = false;
						Tabs.servers.forEach((id2, server2) -> {
							if (!init) {
								if (!server2.hasServer()) {
									EventHandler.EVENT_BUS.post(new ServerCreateEvent(tsh.getServer().createNew()));
									init = true;
								}
							}
						});
					});
				}
			});
		}*/
	}
/*
	private static void showServerIsRunningDialog() {
		Alert dialog = new Alert(AlertType.WARNING, "Es müssen erst alle Server beendet werden", ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(FrameHandler.class.getResource("style.css").toExternalForm());
		dialog.setTitle("Warnung");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}*/
}