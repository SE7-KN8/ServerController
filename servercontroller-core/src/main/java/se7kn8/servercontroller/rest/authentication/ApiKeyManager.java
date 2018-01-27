package se7kn8.servercontroller.rest.authentication;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.rest.authentication.permission.Permission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiKeyManager {

	private static Logger log = LogManager.getLogger();

	public static final int KEY_LENGTH = 32;
	private SecureRandom secureRandom;

	//TODO make this persistent
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

}
