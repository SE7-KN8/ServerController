package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitServer;
import sebe3012.servercontroller.server.BasicServer;

import java.util.HashMap;

public class SpigotServer extends CraftbukkitServer {
	private String spigotConfig;
	private HashMap<String, Object> externalForm;

	public SpigotServer(String name, String jarFile, String propertiesFile, String args, String bukkitConfig,
			String spigotConfig) {
		super(name, jarFile, propertiesFile, args, bukkitConfig);
		this.spigotConfig = spigotConfig;
	}

	public SpigotServer(HashMap<String, Object> externalForm) {
		super(externalForm);
		this.externalForm = externalForm;
		

	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@Override
	public String getServerInfo() {
		return super.getServerInfo();
	}

	public String getSpigotConfig() {
		return spigotConfig;
	}

	@Override
	public String getPluginName() {
		return SpigotAddon.ADDON_NAME;
	}

	@Override
	public BasicServer createNew() {
		return new SpigotServer(name, jarFile.getAbsolutePath(), super.getPropertiesFile(), super.getArgs(),
				super.getBukkitConfig(), spigotConfig);
	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		spigotConfig = (String) externalForm.get("spigot");

	}

	@Override
	public HashMap<String, Object> toExternalForm() {

		HashMap<String, Object> map = super.toExternalForm();

		map.put("spigot", spigotConfig);

		return map;
	}

}
