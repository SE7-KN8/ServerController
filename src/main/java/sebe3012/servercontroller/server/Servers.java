package sebe3012.servercontroller.server;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Servers {
	public static ObservableList<BasicServer> serversList = FXCollections.observableArrayList();

	private static Logger log = LogManager.getLogger();

	static {
		log.info("Server-list was initialized");

		Servers.serversList.addListener(new ListChangeListener<BasicServer>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends BasicServer> c) {
				log.debug("Servers-list was changed");
			}
		});
	}
}
