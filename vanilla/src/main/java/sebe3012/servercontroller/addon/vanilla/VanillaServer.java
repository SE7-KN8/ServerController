package sebe3012.servercontroller.addon.vanilla;

import sebe3012.servercontroller.addon.vanilla.dialog.ops.OpsDialog;
import sebe3012.servercontroller.addon.vanilla.dialog.ops.OpsHandler;
import sebe3012.servercontroller.addon.vanilla.dialog.properties.PropertiesDialog;
import sebe3012.servercontroller.addon.vanilla.dialog.properties.PropertiesHandler;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.eventbus.IEventHandler;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VanillaServer extends BasicServer implements IEventHandler {
	private StringProperty propertiesFile;
	private PropertiesHandler propertiesHandler;
	private OpsHandler opsHandler;

	private List<Control> extraControls = new ArrayList<>();


	public VanillaServer(Map<String, StringProperty> properties) {
		super(properties);
		propertiesFile = properties.get("properties");

		EventHandler.EVENT_BUS.registerEventListener(this);


		propertiesHandler = new PropertiesHandler(new File(getPropertiesFile()));
		opsHandler = new OpsHandler(new File(new File(super.getJarPath()).getParentFile(), "ops.json").getAbsolutePath());
		try {
			propertiesHandler.readProperties();
			opsHandler.readOps();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Button propertiesButton = new Button(I18N.translate("addon_vanilla_properties"));
		propertiesButton.setOnAction(e -> new PropertiesDialog(new Stage(StageStyle.UTILITY), propertiesHandler, VanillaServer.this));

		Button opsButtons = new Button(I18N.translate("addon_vanilla_operators"));
		opsButtons.setOnAction(e -> new OpsDialog(new Stage(StageStyle.UTILITY), opsHandler, VanillaServer.this));

		extraControls.add(propertiesButton);
		extraControls.add(opsButtons);
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@Override
	public String getServerInfo() {
		return I18N.format("addon_vanilla_server_description", propertiesHandler.getServerPort(), propertiesHandler.getLevelName(), propertiesHandler.getDifficulty(), propertiesHandler.getLevelSeed());
	}

	public String getPropertiesFile() {
		return propertiesFile.get();
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
	public String getDoneRegex() {
		return ".*Done \\(\\d*,\\d*s\\)! For help, type \"help\" or \"\\?\"";
	}


}