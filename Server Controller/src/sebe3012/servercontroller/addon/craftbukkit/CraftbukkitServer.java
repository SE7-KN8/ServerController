package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.addon.vanilla.VanillaServer;

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

}
