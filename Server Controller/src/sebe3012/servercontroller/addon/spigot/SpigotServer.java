package sebe3012.servercontroller.addon.spigot;

import java.util.HashMap;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitServer;
import sebe3012.servercontroller.server.BasicServer;

public class SpigotServer extends CraftbukkitServer {

	private static final long serialVersionUID = -2107216290451372139L;
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
		return new SpigotServer(name, jarFile.getAbsolutePath(), super.getPropertiesFile(), spigotConfig,
				super.getBukkitConfig(), spigotConfig);
	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		spigotConfig = (String) externalForm.get("spigot");

	}

	@Override
	public HashMap<String, Object> toExteralForm() {

		HashMap<String, Object> map = super.toExteralForm();

		map.put("spigot", spigotConfig);

		return map;
	}

}
