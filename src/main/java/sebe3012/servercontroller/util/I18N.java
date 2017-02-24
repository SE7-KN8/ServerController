package sebe3012.servercontroller.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by Sebe3012 on 04.02.2017.
 * A util class for translation
 */
public class I18N {

	private static ResourceBundle bundle;
	private static Logger log = LogManager.getLogger();

	public static void init() {
		bundle = ResourceBundle.getBundle("lang/lang", Locale.getDefault());
	}

	public static String translate(String key) {
		String result;

		try{
			result = bundle.getString(key);
		}catch (MissingResourceException e){
			result = key;
			log.warn("Can't find translation for {}, using default value", key);
		}

		return result;
	}

	public static String format(String key, Object... args){
		return String.format(translate(key), args);
	}

	public static ResourceBundle getBundle() {
		return bundle;
	}
}
