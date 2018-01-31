package se7kn8.servercontroller.rest.authentication;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.rest.authentication.permission.Permission;

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
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiKeyManager {

	private static class ApiKeyManagerSerializer implements JsonSerializer<ApiKeyManager>, JsonDeserializer<ApiKeyManager> {

		@Override
		public JsonElement serialize(ApiKeyManager src, Type typeOfSrc, JsonSerializationContext context) {

			JsonObject object = new JsonObject();
			JsonArray keysArray = new JsonArray();

			src.keys.forEach((key, permissions) -> {
				JsonObject keyObject = new JsonObject();
				keyObject.add("key", new JsonPrimitive(key));
				JsonArray permissionsArray = new JsonArray();

				for (Permission permission : permissions) {
					JsonElement permissionElement = Permission.toJson(permission);
					permissionsArray.add(permissionElement);
				}

				keyObject.add("permissions", permissionsArray);
				keysArray.add(keyObject);
			});

			object.add("keys", keysArray);

			return object;
		}

		@Override
		public ApiKeyManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			ApiKeyManager manager = new ApiKeyManager();

			JsonObject root = json.getAsJsonObject();
			JsonArray keysArray = root.getAsJsonArray("keys");
			keysArray.forEach(element -> {
				JsonObject keyObject = element.getAsJsonObject();
				String key = keyObject.get("key").getAsString();
				List<Permission> permissions = new ArrayList<>();
				JsonArray permissionsArray = keyObject.get("permissions").getAsJsonArray();
				permissionsArray.forEach(permission -> permissions.add(Permission.fromJson(permission)));
				manager.keys.put(key, permissions);
			});

			return manager;
		}
	}

	private static Logger log = LogManager.getLogger();

	public static final int KEY_LENGTH = 32;
	private SecureRandom secureRandom;

	private Map<String, List<Permission>> keys;

	public ApiKeyManager() {
		log.info("Created ApiKeyManager");
		this.secureRandom = new SecureRandom();
		this.keys = new HashMap<>();
	}

	public boolean canExecute(@NotNull String apiKey, @NotNull String permissionToExecute) {
		List<Permission> permissions = getPermissions(apiKey);

		for (Permission permission : permissions) {
			if (permission.isPermissionValid(permissionToExecute)) {
				return true;
			}
		}

		return false;
	}

	@NotNull
	public List<Permission> getPermissions(@NotNull String apiKey) {
		return keys.computeIfAbsent(apiKey, key -> new ArrayList<>());
	}

	public void deleteApiKey(@NotNull String apiKey) {
		if (ServerController.DEBUG) {
			log.info("Deleted api-key: {}", apiKey);
		} else {
			log.info("Deleted api-key: __");
		}
		keys.remove(apiKey);
	}

	@NotNull
	public String generateApiKey(@NotNull List<Permission> permissions) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < ApiKeyManager.KEY_LENGTH; i++) {
			stringBuilder.append(Integer.toString(secureRandom.nextInt(Character.MAX_RADIX), Character.MAX_RADIX));
		}

		String apiKey = stringBuilder.toString();

		keys.put(apiKey, permissions);
		if (ServerController.DEBUG) {
			log.info("Generated api-key: {}", apiKey);
		} else {
			log.info("Generated api-key: __");
		}
		return apiKey;
	}

	public static void saveToFile(ApiKeyManager manager, Path path) {
		if (Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path isn't a file!");
		}
		try {
			Files.createDirectories(path.getParent());
			Gson gson = new GsonBuilder().registerTypeAdapter(ApiKeyManager.class, new ApiKeyManagerSerializer()).setPrettyPrinting().create();
			Writer writer = Files.newBufferedWriter(path);
			gson.toJson(manager, writer);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static ApiKeyManager loadFromFile(Path path) {
		if (Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path isn't a file!");
		}

		if(!Files.exists(path)){
			return new ApiKeyManager();
		}

		try {
			Gson gson = new GsonBuilder().registerTypeAdapter(ApiKeyManager.class, new ApiKeyManagerSerializer()).create();
			return gson.fromJson(Files.newBufferedReader(path), ApiKeyManager.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ApiKeyManager();
	}

}
