package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitServer;

import javafx.beans.property.StringProperty;

import java.util.Map;

public class SpigotServer extends CraftbukkitServer {
	private StringProperty spigotConfig;

	public SpigotServer(Map<String, StringProperty> properties){
		super(properties);

		spigotConfig = properties.get("spigot");
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
		return spigotConfig.get();
	}
}
