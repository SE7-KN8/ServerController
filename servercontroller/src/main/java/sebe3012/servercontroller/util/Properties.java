package sebe3012.servercontroller.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sebe3012 on 03.04.2017.
 * A wrapper for java properties
 */
public class Properties {

	public static class Constants{
		//TODO Unused
	}

	private static java.util.Properties properties = new java.util.Properties();
	private static Path propertiesPath = Paths.get(System.getProperty("user.home"), ".servercontroller", "properties");

	private static Logger log = LogManager.getLogger();

	static {
		try{
			Files.createDirectories(propertiesPath);

			save();
		}catch (IOException e){

		}
	}

	public static void save() {

		try{

			properties.store(new FileWriter(new File(propertiesPath.toFile(), "default.properties")), "ServerController properties files");
		}catch (IOException e){
			log.error("Can't save properties file, because: ", e);
		}


	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
		save();
	}

	public static void setProperty(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, short value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, byte value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, long value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, char value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, float value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, double value) {
		setProperty(key, String.valueOf(value));
	}

	public static void setProperty(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	public static String getProperty(String key){
		return properties.getProperty(key);
	}

	public static float getFloatProperty(String key){
		return Float.valueOf(getProperty(key));
	}

	public static double getDoubleProperty(String key){
		return Double.valueOf(getProperty(key));
	}

	public static long getLongProperty(String key){
		return Long.valueOf(getProperty(key));
	}

	public static int getIntProperty(String key){
		return Integer.valueOf(getProperty(key));
	}

	public static short getShortProperty(String key){
		return Short.valueOf(getProperty(key));
	}

	public static byte getByteProperty(String key){
		return Byte.valueOf(getProperty(key));
	}

	public static char getCharProperty(String key){
		return getProperty(key).charAt(0);
	}

	public static boolean getBooleanProperty(String key){
		return Boolean.valueOf(getProperty(key));
	}

}
