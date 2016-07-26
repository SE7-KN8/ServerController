package sebe3012.servercontroller.addon.bungeecord;

import java.util.HashMap;

import com.google.common.eventbus.Subscribe;

import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;

public class BungeeCordServer extends BasicServer implements IEventHandler {

	private static final long serialVersionUID = 5283403326325684583L;

	private String configFile;

	private HashMap<String, Runnable> extraButtons = new HashMap<>();
	private HashMap<String, Object> externalForm;

	public BungeeCordServer(String name, String jarFilePath, String configFile, String args) {
		super(name, jarFilePath, "-Djline.terminal=jline.UnsupportedTerminal" + args);
		this.configFile = configFile;
		EventHandler.EVENT_BUS.registerEventListener(this);
	}

	public BungeeCordServer(HashMap<String, Object> externalForm) {
		super(externalForm);
		this.externalForm = externalForm;
	}

	@Override
	public String getServerInfo() {
		return "";
	}

	public String getConfigFile() {
		return configFile;
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
		return BungeeCordAddon.ADDON_NAME;
	}

	@Override
	public BasicServer createNew() {
		return new BungeeCordServer(name, jarFile.getAbsolutePath(), configFile, args);
	}

	@Override
	public HashMap<String, Runnable> getExtraButtons() {
		return extraButtons;
	}

	@Override
	public String getStopCommand() {
		return "end";
	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		configFile = (String) externalForm.get("bungeecord");

	}

	@Override
	public HashMap<String, Object> toExteralForm() {

		HashMap<String, Object> map = super.toExteralForm();

		map.put("bungeecord", configFile);

		return map;
	}

}