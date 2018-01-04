package se7kn8.servercontroller.addon;

import se7kn8.servercontroller.api.addon.Addon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by se7kn8 on 21.03.2017.
 * A util class for all stuff with addons
 */
public class Addons {

	private static AddonLoader loader = new AddonLoader();

	private static Logger log = LogManager.getLogger();

	public static void loadAddons() {
		try {
			loader.loadAddons();
		} catch (Exception e) {
			log.error("Can't load addons because: ", e);
		}
	}

	public static void unloadAddons() {
		try {
			loader.unloadAddons();
		} catch (Exception e) {
			log.error("Can't unload addons because: ", e);
		}
	}

	public static void waitForLoadingComplete() {
		loader.finishLoading();
	}

	public static List<Addon> getParents(Addon child) {
		List<String> dependencies = child.getAddonInfo().getDependencies();

		List<Addon> parents = new ArrayList<>();

		for (String dependency : dependencies) {
			parents.add(addonForID(dependency));
		}

		return parents;

	}

	public static AddonLoader getAddonLoader() {
		return loader;
	}

	public static String nameForID(String addonID) {

		String result = null;

		Addon a = AddonLoader.ADDONS.get(addonID);

		if (a != null) {
			result = a.getAddonInfo().getName();
		}

		return result;
	}

	public static Addon addonForID(String addonID) {
		return AddonLoader.ADDONS.get(addonID);
	}
}
