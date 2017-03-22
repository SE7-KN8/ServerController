package sebe3012.servercontroller.gui.tab;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class ServerTab extends Tab {

	private ObjectProperty<TabContent> tabContentProperty = new SimpleObjectProperty<>();

	public ServerTab(String name, TabContent contentTab) {
		super(name);
		tabContentProperty.set(contentTab);
	}

	public ServerTab(String name, Node content, TabContent contentTab) {
		super(name, content);
		tabContentProperty.set(contentTab);
	}

	public ObjectProperty<TabContent> getTabContentProperty() {
		return tabContentProperty;
	}

	public void setTabContent(TabContent tabContent) {
		tabContentProperty.set(tabContent);
	}

	public TabContent getTabContent() {
		return tabContentProperty.get();
	}

}
