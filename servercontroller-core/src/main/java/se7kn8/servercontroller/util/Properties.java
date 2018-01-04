package se7kn8.servercontroller.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by se7kn8 on 03.04.2017.
 * A wrapper for java properties
 */
public class Properties {

	public static class Constants{
		public static final String KEY_DESIGN = "design";
		public static final String FILE_ADDON_UTIL = "last_folder";
		public static final String SETTINGS_PREFIX = "setting_";
		public static final String LAST_SERVERS = "last_servers";
	}

	private static java.util.Properties properties = new java.util.Properties();
	private static Path propertiesPath = Paths.get(System.getProperty("user.home"), ".servercontroller", "properties");

	private static Logger log = LogManager.getLogger();

	static {
		try{
			Files.createDirectories(propertiesPath);
		}catch (IOException e){
			log.error("Can't create properties path, because", e);
		}

		save();

	}

	public static void save() {
		try{
			properties.store(new FileWriter(new File(propertiesPath.toFile(), "default.properties")), "ServerController properties files");
		}catch (IOException e){
			log.error("Can't save properties file, because: ", e);
		}
	}

	public static void setProperty(@NotNull String key,@NotNull String value) {
		properties.setProperty(key, value);
		save();
	}

	public static void setProperty(@NotNull String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, short value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, byte value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, long value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, char value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, float value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, double value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(@NotNull String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	@NotNull
	public static String getProperty(@NotNull String key, @NotNull String defaultValue){
		String value = properties.getProperty(key);

		if(value == null){
			value = defaultValue;
		}

		return value;
	}

	public static float getFloatProperty(@NotNull String key, float defaultValue){
		return Float.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public static double getDoubleProperty(@NotNull String key, double defaultValue){
		return Double.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public static long getLongProperty(@NotNull String key, long defaultValue){
		return Long.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public static int getIntProperty(@NotNull String key, int defaultValue){
		return Integer.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public static short getShortProperty(@NotNull String key, short defaultValue){
		return Short.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public static byte getByteProperty(@NotNull String key, byte defaultValue){
		return Byte.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

	public static char getCharProperty(@NotNull String key, char defaultValue){
		return getProperty(key, String.valueOf(defaultValue)).charAt(0);
	}

	public static boolean getBooleanProperty(@NotNull String key, boolean defaultValue){
		return Boolean.valueOf(getProperty(key, String.valueOf(defaultValue)));
	}

}
