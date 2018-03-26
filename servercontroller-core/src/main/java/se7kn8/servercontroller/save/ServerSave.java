package se7kn8.servercontroller.save;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.addon.AddonUtil;
import se7kn8.servercontroller.api.gui.server.ServerCreator;
import se7kn8.servercontroller.api.gui.tab.TabEntry;
import se7kn8.servercontroller.api.preferences.ServerControllerPreferences;
import se7kn8.servercontroller.api.server.BasicServer;
import se7kn8.servercontroller.api.server.BasicServerHandler;
import se7kn8.servercontroller.api.util.DialogUtil;
import se7kn8.servercontroller.api.util.FileUtil;
import se7kn8.servercontroller.gui.FrameHandler;
import se7kn8.servercontroller.server.ServerManager;
import se7kn8.servercontroller.util.I18N;
import se7kn8.servercontroller.util.settings.Settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerSave {

	private static class ServerSerializer implements JsonSerializer<ServerManager> {

		@Override
		public JsonElement serialize(ServerManager src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject rootElement = new JsonObject();
			rootElement.addProperty("version", ServerController.VERSION);
			JsonArray serverArray = new JsonArray();

			for (BasicServerHandler handler : src.getServerList()) {
				BasicServer server = handler.getServer();
				log.info("Start saving server {}", server.getName());
				JsonObject serverObject = new JsonObject();
				log.info("serverCreatorInfo from server {} is {}:{}", server.getName(), server.getAddonID(), server.getServerCreatorID());
				serverObject.addProperty("serverCreatorInfo", server.getAddonID() + ":" + server.getServerCreatorID());
				log.info("saveVersion from server {} is {}", server.getName(), server.getSaveVersion());
				serverObject.addProperty("saveVersion", server.getSaveVersion());
				JsonArray properties = new JsonArray();

				server.getProperties().forEach((key, value) -> {
					JsonObject propertiesElement = new JsonObject();
					propertiesElement.addProperty(key, value);
					log.info("Saved property {} from server {} with value: {}", key, server.getName(), value);
					properties.add(propertiesElement);
				});

				serverObject.add("properties", properties);
				log.info("Server {} was saved", server.getName());
				serverArray.add(serverObject);
			}
			rootElement.add("servers", serverArray);
			return rootElement;
		}

	}

	private static class ServerDeserializer implements JsonDeserializer<ServerManager> {

		private ServerManager manager;

		public ServerDeserializer(@NotNull ServerManager manager) {
			this.manager = manager;
		}

		@Override
		public ServerManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonObject()) {
				JsonObject rootObject = json.getAsJsonObject();
				log.info("Loading save file from servercontroller-version: ", rootObject.get("version").getAsString());
				JsonArray serverArray = rootObject.getAsJsonArray("servers");
				for (JsonElement element : serverArray) {
					JsonObject serverObject = element.getAsJsonObject();
					String[] ids = serverObject.get("serverCreatorInfo").getAsString().split(":");
					String addonID = ids[0];
					String creatorID = ids[1];
					int saveVersion = serverObject.get("saveVersion").getAsInt();

					Optional<ServerCreator> creator = AddonUtil.findServerCreator(manager, addonID, creatorID);

					if (creator.isPresent()) {
						Map<String, String> map = new HashMap<>();

						for (JsonElement propertiesElement : serverObject.getAsJsonArray("properties")) {
							JsonObject propertiesObject = propertiesElement.getAsJsonObject();
							propertiesObject.entrySet().forEach(entry -> {
								log.debug("Load server information '{}' with value '{}'", entry.getKey(), entry.getValue().getAsString());
								map.put(entry.getKey(), entry.getValue().getAsString());
							});
						}

						try {
							BasicServerHandler serverHandler = manager.createServerHandler(map, creator.get().getServerClass(), false, addonID, creatorID);

							log.info("Create server");
							if (serverHandler.getServer().getSaveVersion() > saveVersion) {
								showSaveStateErrorDialog();
								continue;
							}

							manager.addServerHandler(serverHandler);
						} catch (Exception e) {
							throw new JsonParseException(e);
						}
					} else {
						//TODO better warning
						log.warn("No plugin found with name: {}", addonID);
						Platform.runLater(() -> DialogUtil.showWaringAlert("", I18N.format("dialog_save_no_plugin", addonID)));
					}
				}

			}

			return manager;
		}
	}

	private static final String SAVE_KEY = "last_servers";

	public static void saveServerController(@NotNull ServerManager manager) {
		FileUtil.openFileChooser("*.json", ".json", true).ifPresent(file -> ServerSave.saveServerController(file, true, manager));
	}

	private static Logger log = LogManager.getLogger();

	private static void saveServerController(@NotNull String path, boolean showDialog, @NotNull ServerManager serverManager) {

		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				log.info("Start saving");

				ServerControllerPreferences.saveSetting(ServerSave.SAVE_KEY, path);

				Gson gson = new GsonBuilder().registerTypeAdapter(ServerManager.class, new ServerSerializer()).setPrettyPrinting().create();
				FileWriter writer = new FileWriter(new File(path));
				gson.toJson(serverManager, writer);
				writer.close();

				log.info("Finished saving");

				if (showDialog) {
					Platform.runLater(() -> DialogUtil.showInformationAlert("", I18N.translate("dialog_save_successful")));
				}
				return null;
			}
		};

		saveTask.setOnFailed(event -> {
			log.error("Can't save servers", saveTask.getException());
			showSaveErrorDialog();
		});
		new Thread(saveTask).start();
	}

	public static void loadServerController(@NotNull ServerManager serverManager) {
		FileUtil.openFileChooser("*.json", ".json").ifPresent(file -> ServerSave.loadServerController(file, true, serverManager));
	}

	private static void loadServerController(@NotNull String path, boolean showDialog, @NotNull ServerManager serverManager) {

		Task<Void> loadTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				FrameHandler.showBar();

				log.info("Start loading");

				ServerControllerPreferences.saveSetting(ServerSave.SAVE_KEY, path);

				for (TabEntry<BasicServerHandler> entry : serverManager.getTabHandler().getTabEntries()) {
					if (entry.getItem().getServer().isRunning()) {
						log.warn("Can't load servers while a server is running!");
						showServerIsRunningDialog();
						return null;
					}
				}

				Platform.runLater(serverManager::clearServers);

				Gson gson = new GsonBuilder().registerTypeAdapter(ServerManager.class, new ServerDeserializer(serverManager)).create();
				FileReader reader = new FileReader(new File(path));
				gson.fromJson(reader, ServerManager.class);

				if (showDialog) {
					Platform.runLater(() -> DialogUtil.showInformationAlert("", I18N.translate("dialog_load_successful")));
				}

				log.info("Finished loading");

				FrameHandler.hideBar();
				return null;
			}
		};

		FrameHandler.currentProgress.progressProperty().bind(loadTask.progressProperty());
		loadTask.setOnFailed(event -> {
			log.error("Can't load servers", loadTask.getException());
			showSaveErrorDialog();
		});
		new Thread(loadTask).start();
	}

	public static void loadServerControllerFromLastFile(@NotNull ServerManager serverManager) {
		if ((boolean) Settings.readSetting(Settings.Constants.AUTO_LOAD_SERVERS)) {
			ServerSave.loadServerController(ServerControllerPreferences.loadSetting(ServerSave.SAVE_KEY, null), false, serverManager);
		}
	}

	private static void showServerIsRunningDialog() {
		Platform.runLater(() -> DialogUtil.showWaringAlert("", I18N.translate("dialog_save_servers_running")));
	}

	private static void showSaveErrorDialog() {
		Platform.runLater(() -> DialogUtil.showErrorAlert("", I18N.translate("dialog_save_error")));
	}

	private static void showSaveStateErrorDialog() {
		Platform.runLater(() -> DialogUtil.showErrorAlert("", I18N.translate("dialog_wrong_save_version")));
	}

}