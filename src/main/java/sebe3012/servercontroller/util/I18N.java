package sebe3012.servercontroller.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Sebe3012 on 04.02.2017.
 * A util class for translation
 */
public class I18N {

	private static ResourceBundle bundle;

	public static void init() {
		bundle = ResourceBundle.getBundle("lang/lang", Locale.getDefault());
	}

	public static String translate(String key) {
		return bundle.getString(key);
	}

	public static String format(String key, Object... args){
		return String.format(translate(key), args);
	}

	public static ResourceBundle getBundle() {
		return bundle;
	}
}
