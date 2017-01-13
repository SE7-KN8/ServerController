package sebe3012.servercontroller.save;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import sebe3012.servercontroller.ServerController;
import sebe3012.servercontroller.addon.AddonUtil;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.Tabs;
import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.server.Servers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ServerSave {

	public static void saveServerController(String path) throws IOException {

		Servers.serversList.forEach(server -> {
			if (server.isRunning()) {
				showServerIsRunningDialog();
				return;
			}
		});

		FileOutputStream fos = new FileOutputStream(new File(path));

		final Element rootElement = new Element("servercontroller");
		rootElement.setAttribute("servercontroller", ServerController.VERSION);

		Document xml = new Document(rootElement);

		Servers.serversList.forEach(server -> {

			final Element serverElement = new Element("server");

			serverElement.setAttribute("addon", server.getPluginName());
			try {
				Field serUID = server.getClass().getDeclaredField("serialVersionUID");
				serUID.setAccessible(true);
				long uid = serUID.getLong(server);
				serverElement.setAttribute("serialVersionUID", String.valueOf(uid));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			server.toExternalForm().forEach((key, value) -> {

				Element keyElement = new Element(key);
				keyElement.setText(value.toString());
				serverElement.addContent(keyElement);

			});

			rootElement.addContent(serverElement);

		});

		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

		out.output(xml, fos);

		fos.close();

	}

	public static void loadServerController(String path) throws JDOMException, IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Servers.serversList.forEach(server -> {
			if (server.isRunning()) {
				showServerIsRunningDialog();
				return;
			}
		});

		Tabs.removeAllTabs();

		FileInputStream fis = new FileInputStream(new File(path));

		Document xml = new SAXBuilder().build(fis);

		Element serverController = xml.getRootElement();

		for (Element serverElement : serverController.getChildren("server")) {

			String pluginName = serverElement.getAttributeValue("addon");
			long xmlUid = Long.valueOf(serverElement.getAttributeValue("serialVersionUID"));

			Class<? extends BasicServer> serverClass = ServerController.serverAddon.get(pluginName);

			HashMap<String, Object> map = new HashMap<>();

			for (Element e : serverElement.getChildren()) {
				map.put(e.getName(), e.getValue());
			}

			Constructor<?> constructor = serverClass.getConstructors()[1];

			Object serverObject = constructor.newInstance(map);

			if (serverObject instanceof BasicServer) {
				BasicServer server = (BasicServer) serverObject;
				server.fromExternalForm();
				Field serUID;
				try {
					serUID = server.getClass().getDeclaredField("serialVersionUID");
					serUID.setAccessible(true);
					long uid = serUID.getLong(server);
					if (uid != xmlUid) {
						throw new IllegalStateException("The save type of the server has been changed");
					}
				} catch (NoSuchFieldException | SecurityException e1) {
					e1.printStackTrace();
				}
				AddonUtil.addServer(server, false);
			}

		}

		fis.close();

	}

	private static void showServerIsRunningDialog() {
		Alert dialog = new Alert(AlertType.WARNING, "Es müssen erst alle Server beendet werden", ButtonType.OK);
		dialog.getDialogPane().getStylesheets().add(FrameHandler.class.getResource("style.css").toExternalForm());
		dialog.setTitle("Warnung");
		dialog.setHeaderText("");
		dialog.showAndWait();
	}
}