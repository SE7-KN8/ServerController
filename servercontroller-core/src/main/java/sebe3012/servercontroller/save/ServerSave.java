package sebe3012.servercontroller.save;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.AddonUtil;
import sebe3012.servercontroller.api.gui.server.ServerCreator;
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
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerSave {

	private static final String SAVE_KEY = "last_servers";

	public static void saveServerController(ServerManager manager) {
		String file = FileUtil.openFileChooser("*.xml", ".xml", true);
		ServerSave.saveServerController(file, true, manager);
	}

	private static Logger log = LogManager.getLogger();

	private static void saveServerController(String path, boolean showDialog, ServerManager serverManager) {

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

					log.debug("Addon id from server {} is {}", server.getName(), server.getAddonID());
					log.debug("Creator id from {} is {}", server.getName(), server.getServerCreatorID());
					serverElement.setAttribute("serverCreatorInfo", server.getAddonID() + ":" + server.getServerCreatorID());
					serverElement.setAttribute("saveVersion", String.valueOf(server.getSaveVersion()));
					log.debug("Save version from Server {} is {}", server.getName(), server.getSaveVersion());

					server.getProperties().forEach((key, value) -> {
						log.debug("Save entry from server {} is '{}' with value '{}'", server.getName(), key, value);
						Element keyElement = new Element(key);
						keyElement.setText(value);
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
		saveTask.setOnFailed(event -> {
			log.error("Can't save servers", saveTask.getException());
			showSaveErrorDialog();
		});
		new Thread(saveTask).start();
	}

	public static void loadServerController(ServerManager serverManager) {
		String file = FileUtil.openFileChooser("*.xml", ".xml");

		ServerSave.loadServerController(file, true, serverManager);

	}

	private static void loadServerController(String path, boolean showDialog, ServerManager serverManager) {

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
					String[] ids = serverElement.getAttributeValue("serverCreatorInfo").split(":");
					String addonID = ids[0];
					String creatorID = ids[1];
					log.debug("Plugin is {}", addonID);

					int saveVersion = Integer.valueOf(serverElement.getAttributeValue("saveVersion"));

					Optional<ServerCreator> serverCreatorOptional = AddonUtil.findServerCreator(serverManager, addonID, creatorID);

					if (serverCreatorOptional.isPresent()) {
						Map<String, String> map = new HashMap<>();

						for (Element e : serverElement.getChildren()) {
							log.debug("Load server information '{}' with value '{}'", e.getName(), e.getValue());
							map.put(e.getName(), e.getValue());
						}

						BasicServerHandler serverHandler = serverManager.createServerHandler(map, serverCreatorOptional.get().getServerClass(), false, addonID, creatorID);

						log.info("Create server");
						if (serverHandler.getServer().getSaveVersion() > saveVersion) {
							showSaveStateErrorDialog();
							continue;
						}

						serverManager.addServerHandler(serverHandler);

					} else {
						log.warn("No plugin found with name: {}", addonID);
						Platform.runLater(() -> DialogUtil.showErrorAlert("", I18N.format("dialog_save_no_plugin", addonID)));
					}

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
		loadTask.setOnFailed(event -> {
			log.error("Can't load servers", loadTask.getException());
			showSaveErrorDialog();
		});
		new Thread(loadTask).start();
	}

	public static void loadServerControllerFromLastFile(ServerManager serverManager) {
		if ((boolean) Settings.readSetting(Settings.Constants.AUTO_LOAD_SERVERS)) {
			ServerSave.loadServerController(ServerControllerPreferences.loadSetting(ServerSave.SAVE_KEY, null), false, serverManager);
		}
	}

	private static void showServerIsRunningDialog() {
		Platform.runLater(() -> DialogUtil.showWaringAlert("", I18N.translate("dialog_save_servers_running")));
	}

	private static void showSaveErrorDialog() {
		Platform.runLater(() -> DialogUtil.showErrorAlert("", I18N.translate("dialog_save_error")));
	}

	private static void showSaveStateErrorDialog() {
		Platform.runLater(() -> DialogUtil.showErrorAlert("", I18N.translate("dialog_wrong_save_version")));
	}

}