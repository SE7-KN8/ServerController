package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.addon.vanilla.VanillaServer;
import sebe3012.servercontroller.server.BasicServer;

public class CraftbukkitServer extends VanillaServer {

	private static final long serialVersionUID = -2409037301934942928L;
	private String bukkitConfig;

	public CraftbukkitServer(String name, String jarFile, String propertiesFile, String args, String bukkitConfig) {
		super(name, jarFile, propertiesFile, args);
		this.bukkitConfig = bukkitConfig;
	}

	@Override
	public String getServerInfo() {
		return super.getServerInfo();
	}

	public String getBukkitConfig() {
		return bukkitConfig;
	}

	@Override
	public String getPluginName() {
		return CraftbukkitAddon.ADDON_NAME;
	}

	@Override
	public BasicServer createNew() {
		return new CraftbukkitServer(name, super.getJarFile().getAbsolutePath(), super.getPropertiesFile(), super.args,
				bukkitConfig);
	}

}
