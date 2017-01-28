package sebe3012.servercontroller.addon.bungeecord;

import sebe3012.servercontroller.ServerController;
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

public class BungeeCordAddon implements IEventHandler {

	public static final String ADDON_NAME = "BungeeCord";

	public static void loadAddon() {

		ServerController.serverAddon.put(BungeeCordAddon.ADDON_NAME, BungeeCordServer.class);
		EventHandler.EVENT_BUS.registerEventListener(new BungeeCordAddon());

	}

	@Subscribe
	public void serverTypeChoose(ServerTypeChooseEvent event) {
		if (event.getServerType().equals(BungeeCordAddon.ADDON_NAME)) {
			loadDialog(false, null, null, null);
		}
	}

	@Subscribe
	public void serverEdit(ServerEditEvent event) {
		if (event.getServerType().equals(BungeeCordAddon.ADDON_NAME)) {
			if(event.getServer() instanceof  BungeeCordServer){
				BungeeCordServer server = (BungeeCordServer) event.getServer();
				loadDialog(true, server.getJarFile().getAbsolutePath(), server.getConfigFile(), server);
			}
		}
	}

	private void loadDialog(boolean edit, String jar, String config, BasicServer server) {
		DialogRow jarRow = new DialogRow()
				.setName("Jar-Pfad")
				.setUsingFileChooser(true)
				.setFileExtension("*.jar")
				.setFileType("JAR-ARCHIVE")
				.setPropertyName("jar")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);
		DialogRow configRow = new DialogRow()
				.setName("Config-Pfad")
				.setUsingFileChooser(true)
				.setFileExtension("*.yml")
				.setFileType("YML")
				.setPropertyName("bungeeConfig")
				.setStringPredicate(StringPredicates.DEFAULT_CHECK);

		if (edit) {
			jarRow.setDefaultValue(jar);
			configRow.setDefaultValue(config);
		}

		List<DialogRow> rows = new ArrayList<>();
		rows.add(jarRow);
		rows.add(configRow);

		AddonUtil.openCreateDialog(ADDON_NAME, rows, server, map -> {
			String name = map.get("name").get();
			String jarPath = map.get("jar").get();
			String configPath = map.get("bungeeConfig").get();
			String args = map.get("args").get();

			System.out.println(args);
			args = args.replace("-Djline.terminal=jline.UnsupportedTerminal", "");
			System.out.println(args);

			AddonUtil.addServer(new BungeeCordServer(name, jarPath, configPath, args), edit);

		});

	}

}