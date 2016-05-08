package sebe3012.servercontroller.addon.vanilla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;

import com.google.common.eventbus.Subscribe;

import javafx.stage.Stage;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.ServerTypes;

public class VanillaServer extends BasicServer implements IEventHandler {

	private static final long serialVersionUID = 3205212775811812781L;

	private String propertiesFile;
	private PropertiesHandler handler;

	private HashMap<String, Runnable> extraButtons = new HashMap<>();

	public VanillaServer(String name, String jarFilePath, String properties, String args) {
		super(name, jarFilePath, args);
		this.propertiesFile = properties;
		EventHandler.EVENT_BUS.registerEventListener(this);
		handler = new PropertiesHandler(new File(properties));
		try {
			handler.readProperties();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		extraButtons.put("Properties", (Runnable & Serializable) () -> {
			new PropertiesDialog(new Stage(), handler);
		});

	}

	@Override
	public String getServerInfo() {
		return "Port: " + handler.getServerPort() + " World-Name: " + handler.getLevelName() + "\nDifficulty: "
				+ handler.getDifficulty() + " Seed: " + handler.getLevelSeed();
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
		EventHandler.EVENT_BUS.unregisterEventListener(this);
	}

	@Override
	public String getPluginName() {
		return VanillaAddon.ADDON_NAME;
	}

	@Override
	public BasicServer createNew() {
		return new VanillaServer(name, jarFile.getAbsolutePath(), propertiesFile, args);
	}

	@Override
	public HashMap<String, Runnable> getExtraButtons() {
		return extraButtons;
	}

}