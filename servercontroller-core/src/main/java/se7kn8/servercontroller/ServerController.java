package se7kn8.servercontroller;

import se7kn8.servercontroller.addon.Addons;
import se7kn8.servercontroller.gui.Frame;
import se7kn8.servercontroller.server.monitoring.ServerWatcher;
import se7kn8.servercontroller.util.CLIOptions;
import se7kn8.servercontroller.util.I18N;
import se7kn8.servercontroller.util.settings.Settings;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

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
		System.setProperty("logFolder", Paths.get(System.getProperty("user.home"), ".servercontroller").toString());
		System.setProperty("log4j.configurationFile", ClassLoader.getSystemResource("xml/log4j2.xml").toExternalForm());
	}

	public static boolean DEBUG = false;
	public static long START_TIME = 0;

	public static final String VERSION = getVersion();

	private static final Logger log = LogManager.getLogger();

	/**
	 * The main method
	 *
	 * @param args Arguments from the console
	 */
	public static void main(String[] args) {
		START_TIME = System.currentTimeMillis();
		log.info("ServerController ({}) is starting!", ServerController.VERSION);

		CLIOptions.loadOptions(args);

		System.setOut(new ConsoleLog("SYSOUT", System.out, Level.INFO));
		System.setErr(new ConsoleLog("SYSERR", System.err, Level.ERROR));

		I18N.init();

		Frame.load(args);
	}

	public static void stop() {
		log.info("ServerController is stopping");
		Settings.saveSettings();
		Addons.unloadAddons();
		ServerWatcher.running = false;
	}

	private static String getVersion(){
		String version = ServerController.class.getPackage().getImplementationVersion();

		if(version == null){
			version = "INTERNAL_SNAPSHOT";
		}

		return version;
	}

}