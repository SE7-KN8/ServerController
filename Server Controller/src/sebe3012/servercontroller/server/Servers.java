package sebe3012.servercontroller.server;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Servers {
	public static ObservableList<BasicServer> serversList = FXCollections.observableArrayList();
	static {
		System.out.println("Intitialized");

		Servers.serversList.addListener(new ListChangeListener<BasicServer>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends BasicServer> c) {
				System.out.println("Servers changed");

			}
		});

	}
	
}
