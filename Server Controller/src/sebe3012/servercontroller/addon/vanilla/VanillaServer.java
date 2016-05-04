package sebe3012.servercontroller.addon.vanilla;

import com.google.common.eventbus.Subscribe;

import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.ServerTypes;

public class VanillaServer extends BasicServer implements IEventHandler {

	private static final long serialVersionUID = -4550222081010489472L;

	private String propertiesFile;

	public VanillaServer(String name, String jarFilePath, String properties, String args) {
		super(name, jarFilePath, args);
		this.propertiesFile = properties;
		EventHandler.EVENT_BUS.registerEventListener(this);
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	@Override
	public ServerTypes getServerType() {
		return ServerTypes.VANILLA;
	}

	@Subscribe
	public void serverStoped(ServerStopEvent event) {
		if (event.getServer() == this) {
			System.out.println("[" + getName() + "] Stopped with code: " + event.getStopCode());
		}
	}

	@Override
	public String getPluginName() {
		return VanillaAddon.ADDON_NAME;
	}

	@Override
	public BasicServer createNew() {
		return new VanillaServer(name, jarFile.getAbsolutePath(), propertiesFile, args);
	}

}