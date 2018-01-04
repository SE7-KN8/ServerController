package se7kn8.servercontroller.api.preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.prefs.Preferences;

public class ServerControllerPreferences {

	private static Preferences servercontrollerPreferences;
	private static Logger log = LogManager.getLogger();

	static {
		servercontrollerPreferences = Preferences.userNodeForPackage(ServerControllerPreferences.class);
		log.info("Load preferences");
	}

	public static void saveSetting(String name, String value) {
		log.info("Saving setting '{}', with value {}", name, value);
		servercontrollerPreferences.put(name, value);
	}

	public static void saveIntSetting(String name, int value) {
		log.info("Saving integer setting '{}', with value {}", name, value);
		servercontrollerPreferences.putInt(name, value);
	}

	public static void saveBooleanSetting(String name, boolean value) {
		log.info("Saving boolean setting '{}', with value {}", name, value);
		servercontrollerPreferences.putBoolean(name, value);
	}

	public static void saveFloatSetting(String name, float value) {
		log.info("Saving float setting '{}', with value {}", name, value);
		servercontrollerPreferences.putFloat(name, value);
	}

	public static String loadSetting(String name, String defaultValue) {
		log.info("Loading setting '{}', with default value {}", name, defaultValue);
		return servercontrollerPreferences.get(name, defaultValue);
	}

	public static int loadIntSetting(String name, int defaultValue) {
		log.info("Loading integer setting '{}', with default value {}", name, defaultValue);
		return servercontrollerPreferences.getInt(name, defaultValue);
	}

	public static boolean loadBooleanSetting(String name, boolean defaultValue) {
		log.info("Loading boolean setting '{}', with default value {}", name, defaultValue);
		return servercontrollerPreferences.getBoolean(name, defaultValue);
	}

	public static float loadFloatSetting(String name, float defaultValue) {
		log.info("Loading float setting '{}', with default value {}", name, defaultValue);
		return servercontrollerPreferences.getFloat(name, defaultValue);
	}

}