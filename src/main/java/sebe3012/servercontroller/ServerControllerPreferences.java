package sebe3012.servercontroller;

import java.util.prefs.Preferences;

public class ServerControllerPreferences {

	private static Preferences servercontrollerPreferences;

	public static class Constants {

		public static final String KEY_DESIGN = "design";
		public static final String FILE_ADDON_UTIL = "last_folder";

	}

	static {
		servercontrollerPreferences = Preferences.userNodeForPackage(ServerControllerPreferences.class);
	}

	public static void saveSetting(String name, String value) {
		servercontrollerPreferences.put(name, value);
	}

	public static void saveIntSetting(String name, int value) {
		servercontrollerPreferences.putInt(name, value);
	}

	public static void saveBooleanSetting(String name, boolean value) {
		servercontrollerPreferences.putBoolean(name, value);
	}

	public static void saveFloatSetting(String name, float value) {
		servercontrollerPreferences.putFloat(name, value);
	}

	public static String loadSetting(String name, String defaultValue) {
		return servercontrollerPreferences.get(name, defaultValue);
	}

	public static int loadIntSetting(String name, int defaultValue) {
		return servercontrollerPreferences.getInt(name, defaultValue);
	}

	public static boolean loadBooleanSetting(String name, boolean defaultValue) {
		return servercontrollerPreferences.getBoolean(name, defaultValue);
	}

	public static float loadFloatSetting(String name, float defaultValue) {
		return servercontrollerPreferences.getFloat(name, defaultValue);
	}

}
