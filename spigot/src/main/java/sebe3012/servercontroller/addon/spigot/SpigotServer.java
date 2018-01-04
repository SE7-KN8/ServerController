package sebe3012.servercontroller.addon.spigot;

import sebe3012.servercontroller.addon.craftbukkit.CraftBukkitServer;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SpigotServer extends CraftBukkitServer {
	private String spigotConfig;

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);

		spigotConfig = properties.get("spigotConfig");
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@NotNull
	public String getSpigotConfig() {
		return spigotConfig;
	}
}
