package sebe3012.servercontroller.addon.vanilla;

import sebe3012.servercontroller.addon.vanilla.dialog.ops.OpsDialog;
import sebe3012.servercontroller.addon.vanilla.dialog.ops.OpsHandler;
import sebe3012.servercontroller.addon.vanilla.dialog.properties.PropertiesDialog;
import sebe3012.servercontroller.addon.vanilla.dialog.properties.PropertiesHandler;
import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.server.BasicServer;

import org.jetbrains.annotations.NotNull;

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

public class VanillaServer extends BasicServer {
	private StringProperty propertiesFile;
	private PropertiesHandler propertiesHandler;
	private OpsHandler opsHandler;

	private List<Control> extraControls = new ArrayList<>();


	public VanillaServer(Map<String, StringProperty> properties, Addon addon) {
		super(properties, addon);
		propertiesFile = properties.get("properties");

		propertiesHandler = new PropertiesHandler(new File(getPropertiesFile()));
		opsHandler = new OpsHandler(new File(new File(super.getJarPath()).getParentFile(), "ops.json").getAbsolutePath());
		try {
			propertiesHandler.readProperties();
			opsHandler.readOps();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Button propertiesButton = new Button(VanillaAddon.bundle.getString("addon_vanilla_properties"));
		propertiesButton.setOnAction(e -> new PropertiesDialog(new Stage(StageStyle.UTILITY), propertiesHandler, VanillaServer.this));

		Button opsButtons = new Button(VanillaAddon.bundle.getString("addon_vanilla_operators"));
		opsButtons.setOnAction(e -> new OpsDialog(new Stage(StageStyle.UTILITY), opsHandler, VanillaServer.this));

		extraControls.add(propertiesButton);
		extraControls.add(opsButtons);
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@NotNull
	@Override
	public List<String> getServerInfos() {
		List<String> serverInfoList = new ArrayList<>();
		serverInfoList.add("Difficulty: " + propertiesHandler.getDifficulty());
		serverInfoList.add("Gamemode: " + propertiesHandler.getGamemode());
		serverInfoList.add("Port: " + propertiesHandler.getServerPort());
		serverInfoList.add("Max-Players: " + propertiesHandler.getMaxPlayers());
		serverInfoList.add("MOTD: " + propertiesHandler.getMotd());
		serverInfoList.add("View-Distance: " + propertiesHandler.getViewDistance());
		return serverInfoList;
	}

	public String getPropertiesFile() {
		return propertiesFile.get();
	}

	@NotNull
	@Override
	public List<Control> getExtraControls() {
		return extraControls;
	}

	@Override
	public String getDoneRegex() {
		return ".*Done \\(\\d*,\\d*s\\)! For help, type \"help\" or \"\\?\"";
	}
}