package se7kn8.servercontroller.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by se7kn8 on 04.02.2017.
 * A util class for translation
 */
@Deprecated
public class I18N {

	private static ResourceBundle bundle;
	private static List<ResourceBundle> bundleExtensions;
	private static Logger log = LogManager.getLogger();

	@Deprecated
	public static void init() {
		bundle = ResourceBundle.getBundle("lang/lang", Locale.getDefault());
		bundleExtensions = new ArrayList<>();
	}

	@Deprecated
	public static void addBundle(String location) {
		bundleExtensions.add(ResourceBundle.getBundle(location, Locale.getDefault()));
	}

	@NotNull
	@Deprecated
	public static String translate(String key) {
		if (bundle.containsKey(key)) {
			return bundle.getString(key);
		} else {
			for (ResourceBundle bundleExtension : bundleExtensions) {
				if (bundleExtension.containsKey(key)) {
					return bundleExtension.getString(key);
				}
			}
		}

		log.warn("No bundle found to translate '{}'", key);

		return "%" + key + "%";
	}

	@NotNull
	@Deprecated
	public static String format(String key, Object... args) {
		return String.format(translate(key), args);
	}

	@NotNull
	@Deprecated
	public static ResourceBundle getDefaultBundle() {
		return bundle;
	}

	@NotNull
	@Deprecated
	public static List<ResourceBundle> getBundleExtensions() {
		return bundleExtensions;
	}
}
