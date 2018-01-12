package se7kn8.servercontroller.rest;

import io.javalin.Javalin;
import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.addon.AddonLoader;
import se7kn8.servercontroller.api.rest.ServerControllerAddons;
import se7kn8.servercontroller.api.rest.ServerControllerVersion;
import se7kn8.servercontroller.server.ServerManager;

import java.util.ArrayList;
import java.util.List;

public class RestServer implements Runnable {

	private Javalin javalin;
	private int port;
	private String basePath;
	private ServerManager manager;

	public RestServer(int port, String basePath, ServerManager manager) {
		this.port = port;
		this.basePath = basePath;
		this.manager = manager;
	}

	public void start() {
		Thread javalinInitializer = new Thread(this);
		javalinInitializer.setName("Javalin-Initializer-Thread");
		javalinInitializer.start();
	}

	public void stop() {
		javalin.stop();
	}

	@Override
	public void run() {
		javalin = Javalin.create();
		javalin.port(port);
		javalin.start();
		createVersionEndpoint();
		createAddonsEndpoint();
	}

	public void createVersionEndpoint() {
		ServerControllerVersion version = new ServerControllerVersion();
		version.setVersion(ServerController.VERSION);
		javalin.get(basePath + "/version", ctx -> ctx.json(version));
	}

	public void createAddonsEndpoint() {
		List<ServerControllerAddons.ServerControllerAddonInfo> addonInfoList = new ArrayList<>();
		AddonLoader.ADDONS.forEach((id, addon) -> {
			ServerControllerAddons.ServerControllerAddonInfo info = new ServerControllerAddons.ServerControllerAddonInfo();
			info.setId(addon.getAddonInfo().getId());
			info.setName(addon.getAddonInfo().getName());
			info.setAuthors(addon.getAddonInfo().getAuthors());
			info.setVersion(addon.getAddonInfo().getVersion().toString());
			addonInfoList.add(info);
		});
		javalin.get(basePath + "/addons", ctx -> ctx.json(addonInfoList));
	}

}
