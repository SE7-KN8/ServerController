package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitServer;
import sebe3012.servercontroller.api.addon.Addon;

import javafx.beans.property.StringProperty;

import java.util.Map;

public class SpigotServer extends CraftbukkitServer {
	private StringProperty spigotConfig;

	public SpigotServer(Map<String, StringProperty> properties, Addon addon){
		super(properties, addon);

		spigotConfig = properties.get("spigot");
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	public String getSpigotConfig() {
		return spigotConfig.get();
	}
}
