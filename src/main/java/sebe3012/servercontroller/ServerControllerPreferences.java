package sebe3012.servercontroller;

import java.util.prefs.Preferences;

public class ServerControllerPreferences {

	private static Preferences servercontrollerPrefernces;

	public static class Constants {

		public static final String KEY_DESIGN = "design";
		public static final String FILE_ADDON_UTIL = "lastest_folder";

	}

	static {
		servercontrollerPrefernces = Preferences.userNodeForPackage(ServerControllerPreferences.class);
	}

	public static void saveSetting(String name, String value) {
		servercontrollerPrefernces.put(name, value);
	}

	public static void saveIntSetting(String name, int value) {
		servercontrollerPrefernces.putInt(name, value);
	}

	public static void saveBooleanSetting(String name, boolean value) {
		servercontrollerPrefernces.putBoolean(name, value);
	}

	public static void saveFloatSetting(String name, float value) {
		servercontrollerPrefernces.putFloat(name, value);
	}

	public static String loadSetting(String name, String defaultValue) {
		return servercontrollerPrefernces.get(name, defaultValue);
	}

	public static int loadIntSetting(String name, int defaultValue) {
		return servercontrollerPrefernces.getInt(name, defaultValue);
	}

	public static boolean loadBooleanSetting(String name, boolean defaultValue) {
		return servercontrollerPrefernces.getBoolean(name, defaultValue);
	}

	public static float loadFloatSetting(String name, float defaultValue) {
		return servercontrollerPrefernces.getFloat(name, defaultValue);
	}

}
