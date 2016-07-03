package sebe3012.servercontroller.addon;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.server.BasicServer;

public class AddonUtil {

	public AddonUtil() {
	}

	public static void addServer(BasicServer server) {
		TabContent content = new TabContent();
		ServerTab tab = new ServerTab(server.getName(), content);
		tab.setContent(content.getTabContent());
		FrameHandler.mainPane.getTabs().add(tab);

		EventHandler.EVENT_BUS.post(new ServerCreateEvent(server));
	}

	public static void openAlert(String header, String content, AlertType type) {

		Platform.runLater(() -> {
			Alert a = new Alert(type);
			a.getDialogPane().getStylesheets().add(FrameHandler.class.getResource("style.css").toExternalForm());			a.setHeaderText(header);
			a.setContentText(content);
			a.show();
		});

	}

	public static String openFileChooser(String fileType, String fileName) {

		FileChooser fc = new FileChooser();
		
		fc.getExtensionFilters().add(new ExtensionFilter(fileName, fileType));

		File f = fc.showOpenDialog(null);

		if (f != null) {
			return f.getAbsolutePath();
		}

		return "";

	}

	public static boolean checkUserInput(String input) {
		return input.trim().length() < 1 ? false : true;
	}

}
