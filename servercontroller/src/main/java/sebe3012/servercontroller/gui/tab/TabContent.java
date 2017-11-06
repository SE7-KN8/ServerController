package sebe3012.servercontroller.gui.tab;

import sebe3012.servercontroller.util.I18N;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
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

			CodeArea area = new CodeArea();
			area.setId("cOutput");
			area.setPrefHeight(400);
			area.setPrefWidth(800);
			VirtualizedScrollPane<CodeArea> pane = new VirtualizedScrollPane<>(area, ScrollPane.ScrollBarPolicy.AS_NEEDED, ScrollPane.ScrollBarPolicy.ALWAYS);
			GridPane.setRowIndex(pane, 1);

			root.getChildren().add(pane);
			handler.initCodeArea(area);
			//<CodeArea fx:id="cOutput" editable="false" prefHeight="414.0" prefWidth="823.0" GridPane.rowIndex="1" />


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
