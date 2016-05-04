package sebe3012.servercontroller.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Servers {
	public static ObservableList<BasicServer> servers = FXCollections.observableArrayList();
	static {
		System.out.println("[ServerList] Intitialized");
	}
}
