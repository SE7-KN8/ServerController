package sebe3012.servercontroller;

import sebe3012.servercontroller.addon.Addons;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.gui.FrameHandler;
import sebe3012.servercontroller.gui.tab.ServerTab;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.server.monitoring.ServerWatcher;
import sebe3012.servercontroller.settings.SettingsConstants;
import sebe3012.servercontroller.settings.SettingsRow;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

/**
 * ServerController is a program with which you can control
 * Minecraft-Server(e.g. Vanilla, Spigot)
 * <p>
 * This class is the main class of the program
 *
 * @author Sebastian Knackstedt
 */
public class ServerController {

	static {
		System.setProperty("log4j.configurationFile", ClassLoader.getSystemResource("xml/log4j2.xml").toExternalForm());
	}

	public static HashMap<String, SettingsRow> settings = new HashMap<>();

	public static boolean DEBUG = false;

	public static final String VERSION = "0.4.12.9_alpha";

	private static final Logger log = LogManager.getLogger();

	/**
	 * The main method
	 *
	 * @param args Arguments from the console
	 */
	public static void main(String[] args) {
		log.info("ServerController is starting!");

		System.setOut(new ConsoleLog("SYSOUT", System.out, Level.INFO));
		System.setErr(new ConsoleLog("SYSERR", System.err, Level.ERROR));

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-debug")) {
				ServerController.DEBUG = true;
			} else if (args[i].startsWith("-lang=")) {
				String lang = args[i].split("=")[1];
				for (Locale locale : Locale.getAvailableLocales()) {
					if (locale.getLanguage().equalsIgnoreCase(lang)) {
						Locale.setDefault(locale);
						break;
					}
				}
				log.info("Set language to {}", Locale.getDefault());
			}
			log.info("Start-Argument-" + i + " : " + args[i]);
		}

		addSetting(SettingsConstants.AUTO_LOAD_SERVERS, false);

		I18N.init();

		EventHandler.EVENT_BUS.loadEventbus("servercontroller");
		Addons.loadAddons();

		Frame.load(args);
	}

	public static void stop() {
		log.info("ServerController is stopping");
		Addons.unloadAddons();
		ServerWatcher.running = false;
		FrameHandler.mainPane.getTabs().forEach(tab -> {
			if (tab instanceof ServerTab) {

				TabServerHandler handler = ((ServerTab) tab).getTabContent().getContentHandler()
						.getServerHandler();

				if (handler.getServer().isRunning()) {
					handler.onStopClicked();
				}

				handler.getContentHandler().close();
			}

		});
	}

	public static void addSetting(String id, boolean defaultValue) {
		log.info("Load settings : {}", id);
		SettingsRow row = new SettingsRow(id, defaultValue);
		row.load();
		settings.put(row.getId(), row);
	}

	public static String loadStringContent(String path) {
		log.info("Load content: " + path);
		StringBuilder result = new StringBuilder();
		try {

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ClassLoader.getSystemResourceAsStream(path)));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer);
				result.append('\n');
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}