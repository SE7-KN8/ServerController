package sebe3012.servercontroller.addon.bungeecord;

import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BungeeCordServer extends BasicServer implements IEventHandler {
	private StringProperty configFile;

	private List<Control> extraControls = new ArrayList<>();

	public BungeeCordServer(Map<String, StringProperty> properties){
		super(properties);

		configFile = properties.get("bungeecord");
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
		return configFile.get();
	}

	@Subscribe
	public void serverStopped(ServerStopEvent event) {
		if (event.getServer() == this) {
			System.out.println("[" + getName() + "] Stopped with code: " + event.getStopCode());
		}
		EventHandler.EVENT_BUS.unregisterEventListener(this);
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

}