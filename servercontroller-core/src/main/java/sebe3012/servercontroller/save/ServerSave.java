package sebe3012.servercontroller.save;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.Addons;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.api.server.BasicServer;
import sebe3012.servercontroller.api.server.BasicServerHandler;
import sebe3012.servercontroller.api.util.DialogUtil;
import sebe3012.servercontroller.api.util.FileUtil;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.server.ServerManager;
import sebe3012.servercontroller.util.I18N;
import sebe3012.servercontroller.util.settings.Settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSave {

	private static final String SAVE_KEY = "last_servers";

	public static void saveServerController(ServerManager manager) {
		String file = FileUtil.openFileChooser("*.xml", ".xml", true);

		try {
			ServerSave.saveServerController(file, true, manager);
		} catch (IOException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}
	}

	private static Logger log = LogManager.getLogger();

	private static void saveServerController(String path, boolean showDialog, ServerManager serverManager) throws IOException {

		Task<Void> saveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				FrameHandler.showBar();
				log.info("Start saving");

				if (path != null) {
					ServerControllerPreferences.saveSetting(ServerSave.SAVE_KEY, path);
				}

				FileOutputStream fos = new FileOutputStream(new File(path));

				final Element rootElement = new Element("servercontroller");
				rootElement.setAttribute("servercontroller", ServerController.VERSION);

				Document xml = new Document(rootElement);

				int max = serverManager.getServerList().size();

				for (int i = 0; i < max; i++) {
					updateProgress(i, max);

					BasicServerHandler item = serverManager.getServerList().get(i);

					BasicServer server = item.getServer();

					log.info("Start saving server {}", server.getName());
					final Element serverElement = new Element("server");

					log.debug("Addon id from server {} is {}", server.getName(), server.getAddon().getAddonInfo().getId());
					serverElement.setAttribute("addon", server.getAddon().getAddonInfo().getId());
					serverElement.setAttribute("addonVersion", String.valueOf(server.getSaveVersion()));
					log.debug("Save version from Server {} is {}", server.getName(), server.getSaveVersion());

					server.getProperties().forEach((key, value) -> {
						log.debug("Save entry from server {} is '{}' with value '{}'", server.getName(), key, value);
						Element keyElement = new Element(key);
						keyElement.setText(value.get());
						serverElement.addContent(keyElement);
					});

					rootElement.addContent(serverElement);
					log.info("Finished saving server {}", server.getName());

				}

				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

				out.output(xml, fos);

				fos.close();

				log.info("Finished saving");

				if (showDialog) {
					Platform.runLater(() -> DialogUtil.showInformationAlert("", I18N.translate("dialog_save_successful")));
				}

				FrameHandler.hideBar();
				return null;
			}
		};

		FrameHandler.currentProgress.progressProperty().bind(saveTask.progressProperty());
		//TODO show dialog
		saveTask.setOnFailed(event -> log.error("Can't save servers", saveTask.getException()));
		new Thread(saveTask).start();
	}

	public static void loadServerController(ServerManager serverManager) {
		String file = FileUtil.openFileChooser("*.xml", ".xml");

		try {
			ServerSave.loadServerController(file, true, serverManager);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			showSaveStateErrorDialog();
		} catch (JDOMException | IOException | IllegalArgumentException | ReflectiveOperationException e) {
			e.printStackTrace();
			showSaveErrorDialog();
		}
	}

	private static void loadServerController(String path, boolean showDialog, ServerManager serverManager) throws JDOMException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Task<Void> loadTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				FrameHandler.showBar();

				log.info("Start loading");

				if (path == null || !Files.exists(Paths.get(path))) {
					log.info("Can't load servers, because xml path is invalid");
					return null;
				}

				ServerControllerPreferences.saveSetting(ServerSave.SAVE_KEY, path);

				serverManager.getTabHandler().getTabEntries().forEach(e -> {
					if (e.getItem().getServer().isRunning()) {
						log.warn("Can't load servers while a server is running!");
						showServerIsRunningDialog();
						return;
					}
				});


				//TODO use new system
				//Platform.runLater(_Tabs::removeAllTabs);
				Platform.runLater(serverManager::clearServers);

				FileInputStream fis = new FileInputStream(new File(path));

				Document xml = new SAXBuilder().build(fis);

				Element serverController = xml.getRootElement();

				List<Element> elementList = serverController.getChildren("server");

				int counter = 0;
				int max = elementList.size();

				for (Element serverElement : elementList) {
					counter++;

					updateProgress(counter, max);
					log.info("Start loading {}", serverElement.getName());
					String addonId = serverElement.getAttributeValue("addon");
					log.debug("Plugin is {}", addonId);
					long saveVersion = Long.valueOf(serverElement.getAttributeValue("addonVersion"));

					Addon serverAddon = Addons.addonForID(addonId);

					Class<? extends BasicServer> serverClass = AddonUtil.getServerTypes().get(serverAddon);

					if (serverClass == null) {
						log.warn("No plugin found with name: {}", addonId);
						Platform.runLater(() -> DialogUtil.showErrorAlert("", I18N.format("dialog_save_no_plugin", addonId)));
					}

					Map<String, StringProperty> map = new HashMap<>();

					for (Element e : serverElement.getChildren()) {
						log.debug("Load server information '{}' with value '{}'", e.getName(), e.getValue());
						map.put(e.getName(), new SimpleStringProperty(e.getValue()));
					}

					if (serverClass == null) {
						log.warn("Server-class is null. Can't load server");
						continue;
					}

					BasicServerHandler serverHandler = serverManager.createServerHandler(map, serverClass, serverAddon, false);

					log.info("Create server");
					if (serverHandler.getServer().getSaveVersion() != saveVersion) {
						throw new IllegalStateException("The save type of the server has been changed");
					}

					serverManager.addServerHandler(serverHandler);

				}

				fis.close();

				if (showDialog) {
					Platform.runLater(() -> DialogUtil.showInformationAlert("", I18N.translate("dialog_load_successful")));
				}

				log.info("Finished loading");

				FrameHandler.hideBar();
				return null;
			}
		};

		FrameHandler.currentProgress.progressProperty().bind(loadTask.progressProperty());
		loadTask.setOnFailed(event -> log.error("Can't load servers", loadTask.getException()));
		new Thread(loadTask).start();


	}

	public static void loadServerControllerFromLastFile(ServerManager serverManager) {
		if ((boolean) Settings.readSetting(Settings.Constants.AUTO_LOAD_SERVERS)) {
			try {
				ServerSave.loadServerController(ServerControllerPreferences.loadSetting(ServerSave.SAVE_KEY, null), false, serverManager);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				showSaveStateErrorDialog();
			} catch (JDOMException | IOException | IllegalArgumentException | ReflectiveOperationException e) {
				e.printStackTrace();
				showSaveErrorDialog();
			}
		}
	}

	private static void showServerIsRunningDialog() {
		Platform.runLater(() -> DialogUtil.showWaringAlert("", "dialog_save_servers_running"));
	}

	private static void showSaveErrorDialog() {
		DialogUtil.showErrorAlert("", I18N.translate("dialog_save_error"));
	}

	private static void showSaveStateErrorDialog() {
		DialogUtil.showErrorAlert("", I18N.translate("dialog_wrong_save_version"));
	}

}