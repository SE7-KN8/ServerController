package se7kn8.servercontroller.addon;

import se7kn8.servercontroller.ServerController;
import se7kn8.servercontroller.api.addon.Addon;
import se7kn8.servercontroller.api.addon.AddonInfo;
import se7kn8.servercontroller.api.util.DialogUtil;
import se7kn8.servercontroller.api.util.FileUtil;
import se7kn8.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.concurrent.ExecutionException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by se7kn8 on 01.03.2017.
 * The loader class for all addons
 */
public final class AddonLoader {

	public static class AddonVersionAdapter extends TypeAdapter<AddonInfo.AddonVersion> {
		@Override
		public AddonInfo.AddonVersion read(JsonReader in) throws IOException {
			String value = in.nextString();
			String versionRegex = "([0-9]*)[._]([0-9]*)[._]([0-9]*)[._]([0-9]*)";

			if(value.equals("SERVERCONTROLLER_VERSION")){
				if(ServerController.VERSION.matches(versionRegex)){
					value = ServerController.VERSION;
				}else{
					return new AddonInfo.AddonVersion(0,0,0,0);
				}
			}

			Pattern pattern = Pattern.compile(versionRegex);
			Matcher matcher = pattern.matcher(value);
			matcher.matches();

			return new AddonInfo.AddonVersion(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)), Integer.valueOf(matcher.group(3)), Integer.valueOf(matcher.group(4)));
		}

		@Override
		public void write(JsonWriter out, AddonInfo.AddonVersion value) throws IOException {
			throw new UnsupportedOperationException();
		}
	}

	AddonLoader() {
	}

	public static final Path ADDON_PATH = FileUtil.createRelativePath("addons");
	public static final Path ADDON_TEMP_PATH = FileUtil.createRelativePath("temp/addons");
	public static final PathMatcher JAR_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.jar");
	public static final List<Path> JAR_PATHS = new ArrayList<>();
	public static final Map<String, Addon> ADDONS = new HashMap<>();

	private List<AddonInfo> addonsToLoad = new ArrayList<>();
	private List<AddonInfo> addonsToLoadSorted = new ArrayList<>();

	private AddonRegistryHelper registryHelper = new AddonRegistryHelper();

	private Gson gson;
	private Logger log = LogManager.getLogger();

	private static boolean loadingFlag = false;

	private static Task<Void> loadingTask;

	private void searchAddons() {
		searchJars();
		searchAddonInfo();
	}

	void loadAddons() {
		loadingTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				log.info("Start searching addons");
				searchAddons();

				log.info("Start loading addons");
				if (!loadingFlag) {
					calculateDependencies();

					URL[] urls = new URL[addonsToLoadSorted.size()];

					for (int i = 0; i < urls.length; i++) {
						try {
							urls[i] = addonsToLoadSorted.get(i).getJarPath().toUri().toURL();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					URLClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
					for (AddonInfo info : addonsToLoadSorted) {
						loadAddon(info, loader);
					}

					loadingFlag = true;
				} else {
					throw new Exception("Addons already loaded");
				}
				return null;
			}
		};

		loadingTask.setOnFailed(e -> log.error("Failed to load addons", loadingTask.getException()));

		new Thread(loadingTask).start();
	}

	public void finishLoading() {
		try {
			loadingTask.get();
		} catch (ExecutionException | InterruptedException e) {
			log.error("Can't finish the loading process", e);
		}
	}


	private void loadAddon(AddonInfo info, URLClassLoader loader) {
		try {
			String mainClass = info.getMainClass();

			Class<?> clazz = loader.loadClass(mainClass);
			Addon addon = (Addon) clazz.newInstance();
			addon.setAddonInfo(info);

			if (!addon.isLoaded()) {
				log.info("Start to load addon '{}'", info.getId());
				registryHelper.setCurrentAddon(addon);
				addon.loadAddon(registryHelper);
				log.info("Addon '{}' is loaded", info.getId());
			} else {
				log.warn("Addon '{}' is already loaded", info.getId());
			}

			ADDONS.put(info.getId(), addon);
		} catch (ClassNotFoundException e) {
			Platform.runLater(() -> DialogUtil.showExceptionAlert(I18N.format("dialog_addon_cannot_load", info.getId()), "", e));
			log.error("Can't continue loading addon '" + info.getId() + "', because wrong main-class in addon.json: " + e);
		} catch (InstantiationException | IllegalAccessException e) {
			Platform.runLater(() -> DialogUtil.showExceptionAlert(I18N.format("dialog_addon_cannot_load", info.getId()), "", e));
			log.error("Can't continue loading addon '" + info.getId() + "', because somethings is wrong in the addon main-class: " + e);
		} catch (AbstractMethodError e) {
			Platform.runLater(() -> DialogUtil.showExceptionAlert(I18N.format("dialog_addon_cannot_load", info.getId()), "", e));
			log.error("Can't continue loading addon '" + info.getId() + "', because there are compatibility problems: " + e);
		} catch (Throwable e) {
			Platform.runLater(() -> DialogUtil.showExceptionAlert(I18N.format("dialog_addon_cannot_load", info.getId()), "", e));
			log.error("Can't continue loading addon '" + info.getId() + "', because an exception was thrown", e);
		}
	}

	void unloadAddons() {
		for (Addon addon : ADDONS.values()) {
			registryHelper.setCurrentAddon(addon);
			addon.unloadAddon(registryHelper);
		}
	}

	private void searchJars() {
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

	private void searchAddonInfo() {

		List<AddonInfo> addonInfos = new ArrayList<>();

		gson = new GsonBuilder().registerTypeAdapter(AddonInfo.AddonVersion.class, new AddonVersionAdapter()).create();

		for (Path jarPath : JAR_PATHS) {
			try {

				//Creates JarFile object
				JarFile file = new JarFile(jarPath.toFile());

				//Read addon.json entry
				JarEntry addonInfo = file.getJarEntry("addon.json");

				//Test if addon.json exists
				if (addonInfo == null) {
					log.warn("Can't load '{}', because addon.json was not found. Check your addon", jarPath);
					continue;
				}

				//Parse addon.json to AddonInfo
				AddonInfo info = gson.fromJson(new InputStreamReader(file.getInputStream(addonInfo)), AddonInfo.class);
				info.setJarPath(jarPath);

				addonInfos.add(info);

				/*for (int i = 0; i < addonInfos.size(); i++) {
					AddonInfo oldInfo = addonInfos.get(i);

					if (oldInfo.getId().equals(info.getId())) {
						log.info("Addon '{}({})' was already registered, deciding by newest version.", info.getId(), info.getJarPath());

						AddonInfo.AddonVersion oldVersion = oldInfo.getVersion();
						AddonInfo.AddonVersion currentVersion = info.getVersion();

						int value = oldVersion.compareTo(currentVersion);

						if (value == 0) {
							addonInfos.add(info);
						} else if (value > 0) {
							addonInfos.add(oldInfo);
						} else {
							addonInfos.add(info);
						}

					}else{
						addonInfos.add(info);
					}

				}*///FIXME not working
				log.info("Loading addon info: {}", info.getId());
			} catch (IOException e) {
				log.error("Can't continue searching addon.json in jarfile " + jarPath + ", because: ", e);
			}
		}

		AddonInfo baseInfo = gson.fromJson(new InputStreamReader(ClassLoader.getSystemResourceAsStream("json/addon/base_addon.json")), AddonInfo.class);
		baseInfo.setJarPath(Paths.get(""));
		addonInfos.add(baseInfo);

		addonsToLoad.addAll(addonInfos);
	}

	public AddonRegistryHelper getRegistryHelper() {
		return registryHelper;
	}

	private void calculateDependencies() {

		List<String> resolvedDependencies = new ArrayList<>();

		//Test to find  addons with no dependencies
		for (AddonInfo info : addonsToLoad) {
			if (info.getDependencies().isEmpty()) {
				addonsToLoadSorted.add(info);
				resolvedDependencies.add(info.getId());
			}
		}


		//Removed resolved dependencies
		addonsToLoad.removeAll(addonsToLoadSorted);

		while (true) {

			boolean thingsChanged = false;

			for (AddonInfo info : addonsToLoad) {

				log.debug("Try to resolve dependencies for addon '{}'", info.getId());

				boolean allDependenciesFound = false;

				for (String s : info.getDependencies()) {
					if (resolvedDependencies.contains(s)) {
						allDependenciesFound = true;
					} else {
						allDependenciesFound = false;
						break;
					}
				}

				if (allDependenciesFound) {
					log.debug("Found all dependencies for addon '{}'", info.getId());
					addonsToLoadSorted.add(info);
					resolvedDependencies.add(info.getId());
					thingsChanged = true;
				}
			}

			//Remove addons with dependencies that has found
			addonsToLoad.removeAll(addonsToLoadSorted);

			//If nothing has changed, break
			if (!thingsChanged) {
				break;
			}

		}


		//Search for addons with unresolvable dependencies
		for (AddonInfo info : addonsToLoad) {
			log.warn("Couldn't find all dependencies for addon '{}'. The addon will not load", info.getId());
		}

	}

}
