package sebe3012.servercontroller.addon.craftbukkit;

import sebe3012.servercontroller.addon.vanilla.VanillaServer;
import sebe3012.servercontroller.server.BasicServer;

import java.util.HashMap;

public class CraftbukkitServer extends VanillaServer {
	private String bukkitConfig;
	private HashMap<String, Object> externalForm;
	
	public CraftbukkitServer(String name, String jarFile, String propertiesFile, String args, String bukkitConfig) {
		super(name, jarFile, propertiesFile, args);
		this.bukkitConfig = bukkitConfig;
	}

	public CraftbukkitServer(HashMap<String, Object> externalForm) {
		super(externalForm);
		this.externalForm = externalForm;
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@Override
	public String getServerInfo() {
		return super.getServerInfo();
	}

	public String getBukkitConfig() {
		return bukkitConfig;
	}

	@Override
	public String getAddonName() {
		return CraftbukkitAddon.ADDON_ID;
	}

	@Override
	public BasicServer createNew() {
		return new CraftbukkitServer(name, super.getJarFile().getAbsolutePath(), super.getPropertiesFile(), super.args,
				bukkitConfig);
	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		this.bukkitConfig = (String) externalForm.get("bukkit");
	}

	@Override
	public HashMap<String, Object> toExternalForm() {
		HashMap<String, Object> map = super.toExternalForm();

		map.put("bukkit", bukkitConfig);

		return map;
	}

}
