package sebe3012.servercontroller.gui.tab;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;

public class TabHandler<T extends TabEntry<?>> {

	private class CustomTab extends Tab {

		private ObjectProperty<T> tabEntry;

		public CustomTab(@NotNull final T entry) {
			super();
			tabEntry = new SimpleObjectProperty<>(entry);
			createTab(entry);
			setOnCloseRequest(e -> {
				log.debug("Try to closing tab: {}", getText());
				boolean close = entry.onClose();
				if (!close) {
					e.consume();
				}
			});
		}

		public void refresh(){
			//TODO use properties
			createTab(getTabEntry());
		}

		private void createTab(T value){
			setText(value.getTitle());
			setClosable(value.isCloseable());
			setContent(value.getContent());
			setContextMenu(value.getContextMenu());
			setGraphic(value.getGraphic());
		}

		public void setTabEntry(@NotNull T tabEntry) {
			this.tabEntry.set(tabEntry);
		}

		@NotNull
		public T getTabEntry() {
			return tabEntry.get();
		}

		@NotNull
		public ObjectProperty<T> tabEntryProperty() {
			return tabEntry;
		}
	}

	private static final Logger log = LogManager.getLogger();

	private TabPane rootPane;
	private String name;
	private boolean updateSelection = true;

	public TabHandler(@NotNull String name, @NotNull TabPane rootPane) {

		log.info("Creating tab handler with name: {}", name);
		this.rootPane = rootPane;
		this.name = name;

		this.rootPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		this.rootPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateSelectedEntry();
			}
		});
	}

	public void addTab(@NotNull T tabEntry) {
		log.info("[{}] Adding tab with name: {}", name, tabEntry.getTitle());

		CustomTab tab = new CustomTab(tabEntry);

		rootPane.getTabs().add(tab);
		rootPane.getSelectionModel().select(tab);
	}

	public boolean closeSelectedTab() {
		T entry = getSelectedTabEntry();
		if (entry.onClose()) {
			rootPane.getTabs().remove(rootPane.getSelectionModel().getSelectedIndex());
			return true;
		}
		return false;
	}

	public void clearTabs() {
		/*for (Tab t : this.rootPane.getTabs()) {
			T tab = castTab(t);
			if (tab.onClose()) {
				rootPane.getTabs().remove(t);
			} else {
				return false;
			}
		}*/

		this.rootPane.getTabs().clear();

		//return true;
	}

	@NotNull
	public T getSelectedTabEntry() {
		return getEntryFromTab(rootPane.getSelectionModel().getSelectedItem());
	}

	@NotNull
	public List<T> getTabEntries() {
		ObservableList<Tab> tabs = rootPane.getTabs();
		List<T> entries = new ArrayList<>();

		tabs.forEach(tab -> entries.add(getEntryFromTab(tab)));

		return entries;
	}

	@NotNull
	private T getEntryFromTab(@NotNull Tab tab) {
		return castTab(tab).getTabEntry();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public CustomTab castTab(@NotNull Tab tab){
		try{
			return (CustomTab) tab;
		}catch (ClassCastException e){
			//Should ever happen
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public TabPane getTabPane() {
		return rootPane;
	}

	public void updateSelectedEntry() {
		if (updateSelection) {//TODO refactor this very dirty code
			getEntryFromTab(this.getTabPane().getSelectionModel().getSelectedItem()).onSelect();
		}
	}

	public void selectEntry(@NotNull T entry) {
		rootPane.getTabs().forEach(tab -> {
			if (getEntryFromTab(tab) == entry) {
				rootPane.getSelectionModel().select(tab);
			}
		});
	}

	public void refresh(){
		getTabPane().getTabs().forEach(tab->{
			getEntryFromTab(tab).refresh();
			castTab(tab).refresh();
		});
	}

	/**
	 * Very dirty
	 *
	 * @deprecated for removal
	 */
	@Deprecated
	public void enableUpdateEntry() {
		this.updateSelection = true;
	}

	/**
	 * Very dirty
	 *
	 * @deprecated for removal
	 */
	@Deprecated
	public void disableUpdateEntry() {
		this.updateSelection = false;
	}

}
