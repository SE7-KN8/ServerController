package sebe3012.servercontroller.save;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ServerSave {

	private static Logger log = LogManager.getLogger();

	public static void saveServerController(String path, boolean showDialog) throws IOException {
		log.info("Start saving");
		ServerControllerPreferences.saveSetting(PreferencesConstants.LAST_SERVERS, path);

		Servers.serversList.forEach(server -> {
			if (server.isRunning()) {
				log.warn("Can't save while server is running");
				showServerIsRunningDialog();
				return;
			}
		});

		FileOutputStream fos = new FileOutputStream(new File(path));

		final Element rootElement = new Element("servercontroller");
		rootElement.setAttribute("servercontroller", ServerController.VERSION);

		Document xml = new Document(rootElement);

		Servers.serversList.forEach(server -> {
			log.info("Start saving server {}", server.getName());
			final Element serverElement = new Element("server");

			log.debug("Addon name from server {} is {}", server.getName(), server.getAddonName());
			serverElement.setAttribute("addon", server.getAddonName());
			serverElement.setAttribute("addonVersion", String.valueOf(server.getSaveVersion()));
			log.debug("Save version from Server {} is {}", server.getName(), server.getSaveVersion());

			server.toExternalForm().forEach((key, value) -> {
				log.debug("Save entry from server {} is '{}' with value '{}'", server.getName(), key, value);
				Element keyElement = new Element(key);
				keyElement.setText(value.toString());
				serverElement.addContent(keyElement);
			});

			rootElement.addContent(serverElement);
			log.info("Finished saving server {}", server.getName());
		});

		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

		out.output(xml, fos);

		fos.close();

		log.info("Finished saving");

		if (showDialog) {
			DialogUtil.showInformationAlert(I18N.translate("dialog_information"), "", I18N.translate("dialog_save_successful"));
		}
	}

	public static void loadServerController(String path, boolean showDialog) throws JDOMException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		log.info("Start loading");
		ServerControllerPreferences.saveSetting(PreferencesConstants.LAST_SERVERS, path);

		Servers.serversList.forEach(server -> {
			if (server.isRunning()) {
				log.warn("Can't load while server is running");
				showServerIsRunningDialog();
				return;
			}
		});

		Tabs.removeAllTabs();

		FileInputStream fis = new FileInputStream(new File(path));

		Document xml = new SAXBuilder().build(fis);

		Element serverController = xml.getRootElement();

		for (Element serverElement : serverController.getChildren("server")) {
			log.info("Start loading {}" + serverElement);
			String pluginName = serverElement.getAttributeValue("addon");
			log.debug("Plugin is {}", pluginName);
			long saveVersion = Long.valueOf(serverElement.getAttributeValue("addonVersion"));

			Class<? extends BasicServer> serverClass = AddonUtil.getServerTypes().get(pluginName);

			if (serverClass == null) {
				log.warn("No plugin found with name: {}", pluginName);
				DialogUtil.showErrorAlert(I18N.translate("dialog_error"), "", I18N.format("dialog_save_no_plugin", pluginName));
			}

			HashMap<String, Object> map = new HashMap<>();

			for (Element e : serverElement.getChildren()) {
				log.debug("Load server information '{}' with value '{}'", e.getName(), e.getValue());
				map.put(e.getName(), e.getValue());
			}

			Constructor<?> constructor = serverClass.getConstructors()[1];

			Object serverObject = constructor.newInstance(map);

			if (serverObject instanceof BasicServer) {
				BasicServer server = (BasicServer) serverObject;
				log.info("Create server");
				server.fromExternalForm();
				if (server.getSaveVersion() != saveVersion) {
					throw new IllegalStateException("The save type of the server has been changed");
				}
				AddonUtil.addServer(server, false);
			}
		}

		fis.close();

		if (showDialog) {
			DialogUtil.showInformationAlert(I18N.translate("dialog_information"), "", I18N.translate("dialog_load_successful"));
		}

		log.info("Finished loading");

	}

	private static void showServerIsRunningDialog() {
		DialogUtil.showWaringAlert(I18N.translate("dialog_warning"), "", "dialog_save_servers_running");
	}
}