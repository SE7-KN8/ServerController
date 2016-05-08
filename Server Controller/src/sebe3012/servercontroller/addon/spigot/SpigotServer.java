package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitServer;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.ServerTypes;

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

	@Override
	public String getPluginName() {
		return SpigotAddon.ADDON_NAME;
	}

	@Override
	public ServerTypes getServerType() {
		return ServerTypes.SPIGOT;
	}

	@Override
	public BasicServer createNew() {
		return new SpigotServer(name, jarFile.getAbsolutePath(), super.getPropertiesFile(), spigotConfig,
				super.getBukkitConfig(), spigotConfig);
	}

}
