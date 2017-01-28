package sebe3012.servercontroller.save;

import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;
import sebe3012.servercontroller.util.DialogUtil;

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

	public static void saveServerController(String path) throws IOException {

		log.info("Start saving");

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

			log.debug("Addon name from server {} is {}", server.getName(), server.getPluginName());
			serverElement.setAttribute("addon", server.getPluginName());
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

		DialogUtil.showInformationAlert("Information", "", "Speichern war erfolgreich");
	}

	public static void loadServerController(String path) throws JDOMException, IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		log.info("Start loading");

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

			Class<? extends BasicServer> serverClass = ServerController.serverAddon.get(pluginName);

			if (serverClass == null) {
				log.warn("No plugin found with name: {}", pluginName);
				DialogUtil.showErrorAlert("Fehler", "", "Kein Plugin mit der ID '" + pluginName + "' gefunden!");
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

		DialogUtil.showInformationAlert("Information", "", "Laden war erfolgreich");

		log.info("Finished loading");

	}

	private static void showServerIsRunningDialog() {
		DialogUtil.showWaringAlert("Warnung", "", "Es müssen erst alle Server beendet werden");
	}
}