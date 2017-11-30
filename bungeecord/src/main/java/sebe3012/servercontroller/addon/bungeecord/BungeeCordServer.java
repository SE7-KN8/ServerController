package sebe3012.servercontroller.addon.bungeecord;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.server.BasicServer;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BungeeCordServer extends BasicServer {
	private StringProperty configFile;

	private List<Control> extraControls = new ArrayList<>();

	public BungeeCordServer(Map<String, StringProperty> properties, Addon addon){
		super(properties, addon);

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