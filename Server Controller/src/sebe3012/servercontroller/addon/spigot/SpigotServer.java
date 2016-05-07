package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitServer;

public class SpigotServer extends CraftbukkitServer {

	private static final long serialVersionUID = -2107216290451372139L;
	private String spigotConfig;

	public SpigotServer(String name, String jarFile, String propertiesFile, String args, String bukkitConfig,
			String spigotConfig) {
		super(name, jarFile, propertiesFile, args, bukkitConfig);
		this.spigotConfig = spigotConfig;
	}

	@Override
	public String getServerInfo() {
		return super.getServerInfo();
	}

	public String getSpigotConfig() {
		return spigotConfig;
	}

}
