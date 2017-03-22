package sebe3012.servercontroller.addon.vanilla;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.addon.api.DialogRow;
import sebe3012.servercontroller.addon.api.StringPredicates;
import sebe3012.servercontroller.event.ServerEditEvent;
import sebe3012.servercontroller.event.ServerTypeChooseEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class VanillaAddon extends Addon implements IEventHandler {

	static final String ADDON_ID = "vanilla";

	@Override
	public void load() {
		AddonUtil.registerServerType(VanillaAddon.ADDON_ID, VanillaServer.class);
		EventHandler.EVENT_BUS.registerEventListener(this);
	}

	@Override
	public void unload(){
		EventHandler.EVENT_BUS.unregisterEventListener(this);
	}

	@Subscribe
	public void serverTypeChoose(ServerTypeChooseEvent event) {
		if (event.getServerType().equals(VanillaAddon.ADDON_ID)) {
			loadDialog(false, null, null, null);
		}
	}

	@Subscribe
	public void serverEdit(ServerEditEvent event) {
		if (event.getServerType().equals(VanillaAddon.ADDON_ID)) {

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

		AddonUtil.openCreateDialog(ADDON_ID, rows, server, map -> {
			String name = map.get("name").get();
			String jarPath = map.get("jar").get();
			String propertiesPath = map.get("properties").get();
			String args = map.get("args").get();
			AddonUtil.addServer(new VanillaServer(name, jarPath, propertiesPath, args), edit);

		});

	}
}