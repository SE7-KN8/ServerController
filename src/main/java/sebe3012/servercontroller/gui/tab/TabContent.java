package sebe3012.servercontroller.gui.tab;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

public class TabContent {

	private GridPane root;
	private TabContentHandler handler;

	public TabContent() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("TabGUI.fxml"));
			this.handler = new TabContentHandler(this);
			loader.setController(handler);
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GridPane getTabContent() {
		return root;
	}

	public TabContentHandler getContentHandler() {
		return handler;
	}

}
