package sebe3012.servercontroller;

import java.util.ArrayList;
import java.util.List;

import sebe3012.servercontroller.addon.bungeecord.BungeeCordAddon;
import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitAddon;
import sebe3012.servercontroller.addon.spigot.SpigotAddon;
import sebe3012.servercontroller.addon.vanilla.VanillaAddon;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.Frame;

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
	public static List<String> serverAddon = new ArrayList<>();

	/**
	 * 
	 * The main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		System.setOut(new ServerControllerOutput(System.out));

		EventHandler.EVENT_BUS.loadEventbus("servercontroller");

		VanillaAddon.loadAddon();
		CraftbukkitAddon.loadAddon();
		SpigotAddon.loadAddon();
		BungeeCordAddon.loadAddon();

		System.out.println("ServerController started!");
		Frame.load(args);
	}
}