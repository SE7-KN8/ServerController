package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.util.I18N;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class TabContent {

	private GridPane root;
	private TabContentHandler handler;

	public TabContent() {
		try {
			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/TabGUI.fxml"), I18N.getDefaultBundle());
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
