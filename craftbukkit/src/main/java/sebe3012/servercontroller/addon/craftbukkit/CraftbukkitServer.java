package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.addon.vanilla.VanillaServer;

import javafx.beans.property.StringProperty;

import java.util.Map;

public class CraftbukkitServer extends VanillaServer {
	private StringProperty bukkitConfig;

	public CraftbukkitServer(Map<String, StringProperty> properties){
		super(properties);

		bukkitConfig = properties.get("bukkit");
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	public String getBukkitConfig() {
		return bukkitConfig.get();
	}

}
