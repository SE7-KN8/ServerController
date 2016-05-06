package sebe3012.servercontroller;

import java.util.ArrayList;
import java.util.List;

import sebe3012.servercontroller.addon.craftbukkit.CraftbukkitAddon;
import sebe3012.servercontroller.addon.vanilla.VanillaAddon;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.Frame;

public class ServerController {

	public static List<String> serverAddon = new ArrayList<>();

	public static void main(String[] args) {

		System.setOut(new ServerControllerOutput(System.out));

		EventHandler.EVENT_BUS.loadEventbus("servercontroller");

		VanillaAddon.loadAddon();
		CraftbukkitAddon.loadAddon();
		System.out.println("ServerController started!");
		Frame.load(args);
	}

}