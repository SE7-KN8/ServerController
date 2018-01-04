package se7kn8.servercontroller.util.settings;

import se7kn8.servercontroller.api.util.FileUtil;
import se7kn8.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PropertySheet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by se7kn8 on 25.06.2017.
 * A util class for all settings
 */
public class Settings {

	private static final Logger log = LogManager.getLogger();

	public static class Constants {

		public static final String AUTO_LOAD_SERVERS = "auto_load_servers";
		public static final String MAX_COMMAND_HISTORY = "max_command_history";

	}

	public enum SettingsType {
		NUMBER,
		POINT_NUMBER,
		STRING,
		BOOLEAN,
		OBJECT
	}

	static {
		settings = new HashMap<>();
		loadSettings();
		addSetting(Constants.AUTO_LOAD_SERVERS, false, SettingsType.BOOLEAN);
		addSetting(Constants.MAX_COMMAND_HISTORY, 30, SettingsType.NUMBER);
	}

	private static Map<String, MapEntry> settings;

	public static class MapEntry {

		private SettingsType type;
		private Object value;

		public MapEntry create(Object value, SettingsType type) {
			this.value = value;
			this.type = type;
			return this;
		}

		public SettingsType getType() {
			return type;
		}

		public Class<?> getClassType() {
			return value.getClass();
		}
	}

	private static class MapEntryDeserializer implements JsonDeserializer<MapEntry> {

		@Override
		public MapEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			JsonElement value = obj.get("value");

			switch (obj.get("type").getAsString()) {
				case "NUMBER":
					return new MapEntry().create(value.getAsInt(), SettingsType.NUMBER);
				case "POINT_NUMBER":
					return new MapEntry().create(value.getAsDouble(), SettingsType.POINT_NUMBER);
				case "STRING":
					return new MapEntry().create(value.getAsString(), SettingsType.STRING);
				case "BOOLEAN":
					return new MapEntry().create(value.getAsBoolean(), SettingsType.BOOLEAN);
				case "OBJECT":
					return new MapEntry().create(context.deserialize(json, typeOfT), SettingsType.OBJECT);
			}

			throw new JsonParseException("Cannot map type " + obj.get("type").getAsString());
		}
	}

	private static class Setting implements PropertySheet.Item {

		private String key;
		private MapEntry entry;

		Setting(String key, MapEntry entry) {
			this.key = key;
			this.entry = entry;
		}

		@Override
		public Class<?> getType() {
			return entry.getClassType();
		}

		@Override
		public String getCategory() {
			return I18N.translate(key + "_category");
		}

		@Override
		public String getName() {
			return I18N.translate(key);
		}

		@Override
		public String getDescription() {
			return I18N.translate(key + "_desc");
		}

		@Override
		public Object getValue() {
			return entry.value;
		}

		@Override
		public void setValue(Object value) {
			entry.value = value;
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.empty();
		}
	}

	public static void addSetting(String key, Object value, SettingsType type) {
		if (!settings.containsKey(key)) {
			settings.put(key, new MapEntry().create(value, type));
		}
	}

	public static Object readSetting(String key) {
		return settings.get(key).value;
	}

	public static ObservableList<PropertySheet.Item> getItems() {
		ObservableList<PropertySheet.Item> settingItems = FXCollections.observableArrayList();

		settings.forEach((k, v) -> settingItems.add(new Setting(k, v)));

		return settingItems;
	}

	public static void saveSettings() {
		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Path savePath = FileUtil.createRelativePath("data/settings");
					Files.createDirectories(savePath);
					File saveFile = new File(savePath.toFile(), "settings.json");
					FileWriter writer = new FileWriter(saveFile);
					new GsonBuilder().setPrettyPrinting().create().toJson(settings, writer);
					writer.close();
					log.info("Saving settings");
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		saveTask.setOnFailed(e -> log.error("Failed to save settings", saveTask.getException()));
		new Thread(saveTask).start();
	}

	public static void loadSettings() {

		Map<String, MapEntry> map = null;

		try {

			Path loadPath = FileUtil.createRelativePath("data/settings");
			Files.createDirectories(loadPath);
			File loadFile = new File(loadPath.toFile(), "settings.json");
			if (loadFile.exists()) {
				Reader reader = new FileReader(loadFile);
				log.info("Loading settings");
				map = new GsonBuilder().registerTypeAdapter(MapEntry.class, new MapEntryDeserializer()).create().fromJson(reader, new TypeToken<Map<String, MapEntry>>() {
				}.getType());
			}
		} catch (Exception e) {
			log.error("Failed to load settings", e);
		}

		if (map != null) {
			settings = map;
		}
	}

}
