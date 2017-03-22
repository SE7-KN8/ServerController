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

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VanillaServer extends BasicServer implements IEventHandler {
	private String propertiesFile;
	private PropertiesHandler propertiesHandler;
	private OpsHandler opsHandler;

	private List<Control> extraControls = new ArrayList<>();
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

	@Override
	public int getSaveVersion() {
		return 1;
	}

	private void init(boolean isNew) {
		EventHandler.EVENT_BUS.registerEventListener(this);
		if (isNew) {
			propertiesHandler = new PropertiesHandler(new File(propertiesFile));
			opsHandler = new OpsHandler(new File(super.getJarFile().getParentFile(), "ops.json").getAbsolutePath());
			try {
				propertiesHandler.readProperties();
				opsHandler.readOps();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			propertiesHandler = new PropertiesHandler();
			opsHandler = new OpsHandler();
		}

		Button propertiesButton = new Button(I18N.translate("addon_vanilla_properties"));
		propertiesButton.setOnAction(e -> new PropertiesDialog(new Stage(StageStyle.UTILITY), propertiesHandler, VanillaServer.this));

		Button opsButtons = new Button(I18N.translate("addon_vanilla_operators"));
		opsButtons.setOnAction(e -> new OpsDialog(new Stage(StageStyle.UTILITY), opsHandler, VanillaServer.this));

		extraControls.add(propertiesButton);
		extraControls.add(opsButtons);
	}

	@Override
	public String getServerInfo() {
		return I18N.format("addon_vanilla_server_description", propertiesHandler.getServerPort(), propertiesHandler.getLevelName(), propertiesHandler.getDifficulty(), propertiesHandler.getLevelSeed());
	}

	public String getPropertiesFile() {
		return propertiesFile;
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
		return VanillaAddon.ADDON_ID;
	}

	@Override
	public BasicServer createNew() {
		return new VanillaServer(name, jarFile.getAbsolutePath(), propertiesFile, args);
	}

	@Override
	public List<Control> getExtraControls() {
		return extraControls;
	}

	@Override
	public HashMap<String, Object> toExternalForm() {
		HashMap<String, Object> map = super.toExternalForm();

		map.put("properties", propertiesFile);

		return map;

	}

	@Override
	public String getDoneRegex() {
		return ".*Done \\(\\d*,\\d*s\\)! For help, type \"help\" or \"\\?\"";
	}

	@Override
	public void fromExternalForm() {
		super.fromExternalForm();
		propertiesFile = (String) externalForm.get("properties");
		propertiesHandler.setProperitesFile(new File(propertiesFile));
		opsHandler.setPath(new File(super.getJarFile().getParentFile(), "ops.json").getAbsolutePath());
		try {
			propertiesHandler.readProperties();
			opsHandler.readOps();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}