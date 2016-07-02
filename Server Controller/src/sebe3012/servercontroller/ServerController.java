package sebe3012.servercontroller;

import java.util.HashMap;

import sebe3012.servercontroller.addon.bungeecord.BungeeCordAddon;
import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitAddon;
import sebe3012.servercontroller.addon.spigot.SpigotAddon;
import sebe3012.servercontroller.addon.vanilla.VanillaAddon;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.Frame;
import sebe3012.servercontroller.server.BasicServer;

/**
 * 
 * ServerController is a programm with which you can control
 * Minecraft-Server(e.g. Vanilla,Spigot)
 * 
 * This class is the main class of the programm
 * 
 * @author Sebastian Knackstedt
 * 
 */
public class ServerController {

	/**
	 * A list where all addons of the servercontroller listed
	 */
	public static HashMap<String, Class<? extends BasicServer>> serverAddon = new HashMap<>();

	public static boolean DEBUG = false;

	public static final String VERSION = "Alpha 0.1.6.7";

	/**
	 * 
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
}