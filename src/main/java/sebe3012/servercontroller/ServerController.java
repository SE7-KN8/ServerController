package sebe3012.servercontroller;

import sebe3012.servercontroller.addon.bungeecord.BungeeCordAddon;
import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitAddon;
import sebe3012.servercontroller.addon.spigot.SpigotAddon;
import sebe3012.servercontroller.addon.vanilla.dialog.VanillaAddon;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.server.BasicServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * ServerController is a program with which you can control
 * Minecraft-Server(e.g. Vanilla,Spigot)
 * <p>
 * This class is the main class of the program
 *
 * @author Sebastian Knackstedt
 */
public class ServerController {

	/**
	 * A list where all addons of the servercontroller listed
	 */
	public static HashMap<String, Class<? extends BasicServer>> serverAddon = new HashMap<>();

	public static boolean DEBUG = false;

	public static final String VERSION = "Alpha 0.2.8.8";

	/**
	 * The main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.setOut(new ServerControllerOutput(System.out));

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-debug")) {
				ServerController.DEBUG = true;
			}
			System.out.println("Start-Argument-" + i + " :" + args[i]);
		}

		EventHandler.EVENT_BUS.loadEventbus("servercontroller");

		VanillaAddon.loadAddon();
		CraftbukkitAddon.loadAddon();
		SpigotAddon.loadAddon();
		BungeeCordAddon.loadAddon();

		System.out.println("ServerController started!");
		Frame.load(args);
	}

	public static String loadStringContent(String path) {
		System.out.println("Load content: " + path);
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
		String r = result.toString();
		return r;
	}
}