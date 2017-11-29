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
			setText(entry.getTitle());
			setClosable(entry.isCloseable());
			setContent(entry.getContent());
			setContextMenu(entry.getContextMenu());
			setGraphic(entry.getGraphic());
			setOnCloseRequest(e -> {
				log.debug("Try to closing tab: {}", getText());
				boolean close = entry.onClose();
				if (!close) {
					e.consume();
				}
			});
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

	public void closeSelectedTab() {
		T entry = getSelectedTabEntry();
		if (entry.onClose()) {
			rootPane.getTabs().remove(rootPane.getSelectionModel().getSelectedIndex());
		}
	}

	@NotNull
	public T getSelectedTabEntry() {
		return castTab(rootPane.getSelectionModel().getSelectedItem());
	}

	@NotNull
	public List<T> getTabEntries() {
		ObservableList<Tab> tabs = rootPane.getTabs();
		List<T> entries = new ArrayList<>();

		tabs.forEach(tab -> entries.add(castTab(tab)));

		return entries;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private T castTab(@NotNull Tab tab) {
		try {
			return ((CustomTab) tab).getTabEntry();
		} catch (ClassCastException e) {
			//This should never happen
			throw new RuntimeException(e);
		}
	}

	@NotNull
	public TabPane getTabPane() {
		return rootPane;
	}

	public void updateSelectedEntry() {
		if (updateSelection) {//TODO refactor this very dirty code
			castTab(this.getTabPane().getSelectionModel().getSelectedItem()).onSelect();
		}
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
