package sebe3012.servercontroller.addon.craftbukkit;

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

public class CraftbukkitAddon extends Addon implements IEventHandler {

	public static final String ADDON_ID = "craftbukkit";

	@Override
	public void load() {

		AddonUtil.registerServerType(CraftbukkitAddon.ADDON_ID, CraftbukkitServer.class);
		EventHandler.EVENT_BUS.registerEventListener(this);

	}

	@Override
	public void unload() {
		EventHandler.EVENT_BUS.unregisterEventListener(this);
	}

	@Subscribe
	public void serverTypeChoose(ServerTypeChooseEvent event) {
		if (event.getServerType().equals(CraftbukkitAddon.ADDON_ID)) {
			loadDialog(false, null, null, null, null);
		}
	}

	@Subscribe
	public void serverEdit(ServerEditEvent event) {
		if (event.getServerType().equals(CraftbukkitAddon.ADDON_ID)) {

			if (event.getServer() instanceof CraftbukkitServer) {
				CraftbukkitServer server = (CraftbukkitServer) event.getServer();
				loadDialog(true, server.getJarFile().getAbsolutePath(), server.getPropertiesFile(), server.getBukkitConfig(), server);
			}

		}
	}

	private void loadDialog(boolean edit, String jar, String properties, String bukkitConfig, BasicServer server) {
		DialogRow jarRow = new DialogRow()
				.setName("Jar")
				.setUsingFileChooser(true)
				.setFileExtension("*.jar")
				.setFileType("JAR-ARCHIVE")
				.setPropertyName("jar")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);
		DialogRow propertiesRow = new DialogRow()
				.setName("Properties")
				.setUsingFileChooser(true)
				.setFileExtension("*.properties")
				.setFileType("PROPERTIES")
				.setPropertyName("properties")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);
		DialogRow bukkitConfigRow = new DialogRow()
				.setName("Bukkit-Config")
				.setUsingFileChooser(true)
				.setFileExtension("*.yml")
				.setFileType("YML")
				.setPropertyName("bukkitYML")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (edit) {
			jarRow.setDefaultValue(jar);
			propertiesRow.setDefaultValue(properties);
			bukkitConfigRow.setDefaultValue(bukkitConfig);
		}

		List<DialogRow> rows = new ArrayList<>();
		rows.add(jarRow);
		rows.add(propertiesRow);
		rows.add(bukkitConfigRow);

		AddonUtil.openCreateDialog(ADDON_ID, rows, server, map -> {
			String name = map.get("name").get();
			String jarPath = map.get("jar").get();
			String propertiesPath = map.get("properties").get();
			String args = map.get("args").get();
			String bukkitYML = map.get("bukkitYML").get();
			AddonUtil.addServer(new CraftbukkitServer(name, jarPath, propertiesPath, args, bukkitYML), edit);
		});
	}
}