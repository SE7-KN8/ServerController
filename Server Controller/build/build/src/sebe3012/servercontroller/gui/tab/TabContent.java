package sebe3012.servercontroller.gui.tab;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

public class TabContent {

	private GridPane root;

	public TabContent() {
		try {
			root = FXMLLoader.load(this.getClass().getResource("TabGUI.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GridPane getTabContent() {
		return root;
	}

}
