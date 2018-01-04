package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.addon.vanilla.VanillaServer;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CraftBukkitServer extends VanillaServer {
	private String craftBukkitConfig;

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);
		craftBukkitConfig = properties.get("craftBukkitConfig");
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@NotNull
	protected String getCraftbukkitConfig() {
		return craftBukkitConfig;
	}
}
