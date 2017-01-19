package sebe3012.servercontroller.addon.vanilla.dialog;

import com.google.common.eventbus.Subscribe;
import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.addon.api.DialogRow;
import sebe3012.servercontroller.addon.api.StringPredicates;
import sebe3012.servercontroller.addon.vanilla.VanillaServer;
import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.event.ServerTypeChooseEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;

import java.util.ArrayList;
import java.util.List;

public class VanillaAddon implements IEventHandler {

	public static final String ADDON_NAME = "Vanilla";

	public static void loadAddon() {

		ServerController.serverAddon.put(VanillaAddon.ADDON_NAME, VanillaServer.class);
		EventHandler.EVENT_BUS.registerEventListener(new VanillaAddon());

	}

	@Subscribe
	public void serverTypeChoose(ServerTypeChooseEvent event) {
		if (event.getServerType().equals(VanillaAddon.ADDON_NAME)) {
			loadDialog(false, null, null, null);
		}
	}

	@Subscribe
	public void serverEdit(ServerEditEvent event) {
		if (event.getServerType().equals(VanillaAddon.ADDON_NAME)) {

			if (event.getServer() instanceof VanillaServer) {

				VanillaServer server = (VanillaServer) event.getServer();

				loadDialog(true, server.getJarFile().getAbsolutePath(), server.getPropertiesFile(), server);


			}

		}
	}

	private void loadDialog(boolean edit, String jar, String properties, BasicServer server) {
		DialogRow jarRow = new DialogRow()
				.setName("Jar-Pfad")
				.setUsingFileChooser(true)
				.setFileExtension("*.jar")
				.setFileType("JAR-ARCHIVE")
				.setPropertyName("jar")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);
		DialogRow propertiesRow = new DialogRow()
				.setName("Properties-Pfad")
				.setUsingFileChooser(true)
				.setFileExtension("*.properties")
				.setFileType("PROPERTIES")
				.setPropertyName("properties")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (edit) {
			jarRow.setDefaultValue(jar);
			propertiesRow.setDefaultValue(properties);
		}

		List<DialogRow> rows = new ArrayList<>();
		rows.add(jarRow);
		rows.add(propertiesRow);

		AddonUtil.openCreateDialog(ADDON_NAME, rows, server, map -> {
			String name = map.get("name").get();
			String jarPath = map.get("jar").get();
			String propertiesPath = map.get("properties").get();
			String args = map.get("args").get();
			AddonUtil.addServer(new VanillaServer(name, jarPath, propertiesPath, args), edit);

		});

	}
}