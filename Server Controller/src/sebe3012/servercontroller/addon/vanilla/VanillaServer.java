package sebe3012.servercontroller.addon.vanilla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;

import com.google.common.eventbus.Subscribe;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;

public class VanillaServer extends BasicServer implements IEventHandler {

	private static final long serialVersionUID = 3205212775811812781L;

	private String propertiesFile;
	private PropertiesHandler handler;

	private HashMap<String, Runnable> extraButtons = new HashMap<>();
	private HashMap<String, Object> externalForm;

	public VanillaServer(String name, String jarFilePath, String properties, String args) {
		super(name, jarFilePath, args);
		this.propertiesFile = properties;
		this.init(true);
	}

	public VanillaServer(HashMap<String, Object> externalForm) {
		super(externalForm);

		this.externalForm = externalForm;

		this.init(false);
	}

	private void init(boolean flag) {
		EventHandler.EVENT_BUS.registerEventListener(this);
		if (flag) {
			handler = new PropertiesHandler(new File(propertiesFile));
			try {
				handler.readProperties();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			handler = new PropertiesHandler();
		}

		extraButtons.put("Properties", (Runnable & Serializable) () -> {
			new PropertiesDialog(new Stage(StageStyle.UTILITY), handler, VanillaServer.this);
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

	@Override
	public HashMap<String, Object> toExteralForm() {
		HashMap<String, Object> map = super.toExteralForm();

		map.put("properties", propertiesFile);

		return map;

	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		propertiesFile = (String) externalForm.get("properties");
		handler.setProperitesFile(new File(propertiesFile));
		try {
			handler.readProperties();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}