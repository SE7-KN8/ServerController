package sebe3012.servercontroller.gui.tab;

import java.util.HashMap;

public class Tabs {

	public static HashMap<Integer, TabServerHandler> servers = new HashMap<>();
	public static HashMap<Integer, TabContentHandler> contents = new HashMap<>();
	public static HashMap<TabServerHandler, Integer> IDforServers = new HashMap<>();
	public static HashMap<TabContentHandler, Integer> IDforContents = new HashMap<>();

	private static int nextID = 0;
	private static boolean value1 = false;
	private static boolean value2 = false;
	private static boolean value3 = false;

	public static int getNextID() {
		if (!value1) {
			value1 = true;
			return nextID;
		} else if (!value2) {
			value2 = true;
			return nextID;
		} else if (!value3) {
			value3 = true;
			return nextID;
		} else {
			value1 = false;
			value2 = false;
			value3 = false;
			return nextID++;
		}
	}

	public static int getID() {
		return nextID;
	}
}
