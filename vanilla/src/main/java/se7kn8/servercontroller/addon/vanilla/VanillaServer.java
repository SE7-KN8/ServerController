package se7kn8.servercontroller.addon.vanilla;

import se7kn8.servercontroller.addon.vanilla.dialog.ops.OpsDialog;
import se7kn8.servercontroller.addon.vanilla.dialog.ops.OpsHandler;
import se7kn8.servercontroller.addon.vanilla.dialog.properties.PropertiesDialog;
import se7kn8.servercontroller.addon.vanilla.dialog.properties.PropertiesHandler;
import se7kn8.servercontroller.api.server.JarServer;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VanillaServer extends JarServer {
	private String propertiesFile;
	private PropertiesHandler propertiesHandler;
	private OpsHandler opsHandler;

	private List<Node> extraControls = new ArrayList<>();

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);

		propertiesFile = properties.get("propertiesPath");


		propertiesHandler = new PropertiesHandler(new File(getPropertiesFile()));
		opsHandler = new OpsHandler(Paths.get(getJarPath().getParent().toString(), "ops.json").toFile().getAbsolutePath());

		try {
			propertiesHandler.readProperties();
			opsHandler.readOps();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Button propertiesButton = new Button(VanillaAddon.bundle.getString("addon_vanilla_properties"));
		propertiesButton.setOnAction(e -> new PropertiesDialog(new Stage(StageStyle.UTILITY), propertiesHandler, VanillaServer.this));
		propertiesButton.setPrefWidth(1000);

		Button opsButtons = new Button(VanillaAddon.bundle.getString("addon_vanilla_operators"));
		opsButtons.setOnAction(e -> new OpsDialog(new Stage(StageStyle.UTILITY), opsHandler, VanillaServer.this));
		opsButtons.setPrefWidth(1000);

		extraControls.add(propertiesButton);
		extraControls.add(opsButtons);
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@NotNull
	@Override
	public List<String> getServerInformation() {
		List<String> serverInfoList = new ArrayList<>();
		serverInfoList.add("Difficulty: " + propertiesHandler.getDifficulty());
		serverInfoList.add("Gamemode: " + propertiesHandler.getGamemode());
		serverInfoList.add("Port: " + propertiesHandler.getServerPort());
		serverInfoList.add("Max-Players: " + propertiesHandler.getMaxPlayers());
		serverInfoList.add("MOTD: " + propertiesHandler.getMotd());
		serverInfoList.add("View-Distance: " + propertiesHandler.getViewDistance());
		return serverInfoList;
	}

	@NotNull
	protected String getPropertiesFile() {
		return propertiesFile;
	}

	@NotNull
	@Override
	public List<Node> getControls() {
		return extraControls;
	}

	@NotNull
	@Override
	public String getStopCommand() {
		return "stop";
	}

	@NotNull
	@Override
	protected String getArgsAfterJar() {
		return "nogui";
	}

	@Override
	@NotNull
	public String getDoneRegex() {
		return ".*Done \\(\\d*,\\d*s\\)! For help, type \"help\" or \"\\?\"";
	}
}