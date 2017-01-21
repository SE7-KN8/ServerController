package sebe3012.servercontroller.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Servers {
	public static ObservableList<BasicServer> serversList = FXCollections.observableArrayList();

	private static Logger log = LogManager.getLogger();

	static {
		log.info("Server-list was initialized");

		Servers.serversList.addListener((ListChangeListener.Change<? extends BasicServer> change) -> log.debug("Servers-list was changed"));
	}
}
