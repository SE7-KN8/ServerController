package sebe3012.servercontroller.addon;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.addon.api.AddonInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Sebe3012 on 01.03.2017.
 * The loader class for all addons
 */
public class AddonLoader {

	public static final Path ADDON_PATH = Paths.get(System.getProperty("user.home"), ".servercontroller", "addons");
	public static final PathMatcher JAR_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.jar");
	public static final List<Path> JAR_PATHS = new ArrayList<>();
	public static final List<AddonInfo> ADDON_INFO = new ArrayList<>();
	public static final List<Addon> ADDONS = new ArrayList<>();
	public static final Map<String, Addon> ADDON_IDS = new HashMap<>();

	private static Gson gson = new Gson();
	private static Logger log = LogManager.getLogger();

	public static void searchAddons() {
		searchJars();
		searchAddonInfo();
	}

	private static boolean loadingFlag = false;

	public static void loadAddons() {
		if (!loadingFlag) {
			for (AddonInfo info : ADDON_INFO) {

				try {

					if (ADDON_IDS.keySet().contains(info.getId())) {
						throw new RuntimeException("Addon '" + info.getId() + "' is already loaded");
					}

					String mainClass = info.getMainClass();
					Path jarPath = info.getJarPath();

					URLClassLoader loader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()});

					Class<?> clazz = loader.loadClass(mainClass);
					Addon addon = (Addon) clazz.newInstance();
					addon.setAddonInfo(info);

					ADDONS.add(addon);
					ADDON_IDS.put(info.getId(), addon);

					addon.load();

				} catch (MalformedURLException e) {
					log.error("Can't continue loading addon " + info.getId() + ", because: " + e);
				} catch (ClassNotFoundException e) {
					log.error("Can't continue loading addon " + info.getId() + ", because wrong main-class in addon.json: " + e);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("Can't continue loading addon " + info.getId() + ", because somethings is wrong in the addon main-class: " + e);
				} catch (AbstractMethodError e) {
					log.error("Can't continue loading addon " + info.getId() + ", because there are compatibility problems: " + e);
				}
			}

			loadingFlag = true;
		} else {
			throw new RuntimeException("Addons already loaded");
		}
	}

//TODO Unused
//	public static void reloadAddons(){
//
//		for(Addon addon: ADDONS){
//			addon.unload();
//		}
//
//		for(Addon addon: ADDONS){
//			addon.load();
//		}
//	}

	public static void unloadAddons() {
		for (Addon addon : ADDONS) {
			addon.unload();
		}
	}

	private static void searchJars() {
		try {
			Files.createDirectories(ADDON_PATH);

			for (Path path : Files.newDirectoryStream(ADDON_PATH)) {
				if (JAR_FILE_MATCHER.matches(path)) {
					log.info("Found jar at: {}", path);
					JAR_PATHS.add(path);
				}
			}
		} catch (IOException e) {
			log.error("Can't continue searching jars, because: ", e);
		}

	}

	private static void searchAddonInfo() {
		for (Path jarPath : JAR_PATHS) {
			try {
				JarFile file = new JarFile(jarPath.toFile());

				JarEntry addonInfo = file.getJarEntry("addon.json");

				if (addonInfo == null) {
					throw new RuntimeException("addon.json not found! Please check your addon");
				}

				AddonInfo info = gson.fromJson(new InputStreamReader(file.getInputStream(addonInfo)), AddonInfo.class);

				info.setJarPath(jarPath);

				log.info("Loading addon info: {}", info.getId());

				ADDON_INFO.add(info);
			} catch (IOException e) {
				log.error("Can't continue searching addon.json in jarfile " + jarPath + ", because: ", e);
			}
		}
	}

}
