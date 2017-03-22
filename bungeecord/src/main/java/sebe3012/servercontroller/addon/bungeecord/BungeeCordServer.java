package sebe3012.servercontroller.addon.bungeecord;

import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;

import com.google.common.eventbus.Subscribe;

import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BungeeCordServer extends BasicServer implements IEventHandler {
	private String configFile;

	private List<Control> extraControls = new ArrayList<>();
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
	public int getSaveVersion() {
		return 1;
	}

	@Override
	public String getServerInfo() {
		return "";
	}

	public String getConfigFile() {
		return configFile;
	}

	@Subscribe
	public void serverStopped(ServerStopEvent event) {
		if (event.getServer() == this) {
			System.out.println("[" + getName() + "] Stopped with code: " + event.getStopCode());
		}
		EventHandler.EVENT_BUS.unregisterEventListener(this);
	}

	@Override
	public String getAddonName() {
		return BungeeCordAddon.ADDON_ID;
	}

	@Override
	public BasicServer createNew() {
		return new BungeeCordServer(name, jarFile.getAbsolutePath(), configFile, args);
	}

	@Override
	public List<Control> getExtraControls() {
		return extraControls;
	}

	@Override
	public String getStopCommand() {
		return "end";
	}

	@Override
	public String getDoneRegex() {
		return ".*Listening on \\/\\d*.\\d*.\\d*.\\d*:\\d*";
	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		configFile = (String) externalForm.get("bungeecord");

	}

	@Override
	public HashMap<String, Object> toExternalForm() {

		HashMap<String, Object> map = super.toExternalForm();

		map.put("bungeecord", configFile);

		return map;
	}

}