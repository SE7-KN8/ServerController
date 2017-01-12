package sebe3012.servercontroller.addon;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import sebe3012.servercontroller.event.ChangeControlsEvent;
import sebe3012.servercontroller.event.ServerCreateEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabContent;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;

public class AddonUtil {

	public static void addServer(BasicServer server, boolean isEdit) {

		int index = -1;

		TabContent content = new TabContent();
		ServerTab tab = new ServerTab(server.getName(), content);
		tab.setContent(content.getTabContent());
		if (isEdit) {
			index = FrameHandler.mainPane.getSelectionModel().getSelectedIndex();
			Tabs.removeCurrentTab();
			FrameHandler.mainPane.getTabs().add(index, tab);
		} else {
			FrameHandler.mainPane.getTabs().add(tab);
		}

		FrameHandler.mainPane.getSelectionModel().select(tab);

		if (!isEdit) {
			EventHandler.EVENT_BUS.post(new ChangeControlsEvent(server.getExtraControls()));
		}
		EventHandler.EVENT_BUS.post(new ServerCreateEvent(server, isEdit, index));
	}

	public static String openFileChooser(String fileType, String fileName) {

		FileChooser fc = new FileChooser();

		//fc.setInitialDirectory(new File(ServerControllerPreferences
		//		.loadSetting(ServerControllerPreferences.Constants.FILE_ADDON_UTIL, System.getProperty("user.home"))));

		fc.getExtensionFilters().add(new ExtensionFilter(fileName, fileType));

		File f = fc.showOpenDialog(null);

		if (f != null) {

			//ServerControllerPreferences.saveSetting(ServerControllerPreferences.Constants.FILE_ADDON_UTIL,
			//		f.getAbsolutePath());

			return f.getAbsolutePath();
		}

		return "";

	}

	public static boolean checkUserInput(String input) {
		if (input != null) {
			return input.trim().length() < 1 ? false : true;
		}
		return false;
	}

}
