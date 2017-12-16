package sebe3012.servercontroller.gui.tree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.List;

public class TreeHandler<T extends TreeEntry<?>> {

	private static Logger log = LogManager.getLogger();

	private class CustomCell extends TreeCell<T> {

		@Override
		protected void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty) {
				setText("");
				setGraphic(null);
				setContextMenu(null);
			} else {
				setText(item.getName());
				setGraphic(item.getGraphic());
				setContextMenu(item.getContextMenu());
			}

		}
	}

	private TreeView<T> rootTreeView;
	private boolean updateSelection = true;

	public TreeHandler(@NotNull TreeView<T> rootTreeView, @NotNull TreeItem<T> rootItem, boolean showRootItem) {
		this.rootTreeView = rootTreeView;
		this.rootTreeView.setCellFactory(e -> new CustomCell());
		this.rootTreeView.setRoot(rootItem);
		this.rootTreeView.setShowRoot(showRootItem);
		this.rootTreeView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
				if (!getSelectedTreeEntry().onDoubleClick()) {
					event.consume();
				}
			}
		});
		this.rootTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSelectedEntry());
	}

	@NotNull
	public TreeItem<T> getRootItem() {
		return rootTreeView.getRoot();
	}

	@NotNull
	public T getRootEntry() {
		return getRootItem().getValue();
	}

	@NotNull
	public List<TreeItem<T>> getItems() {
		return new ArrayList<>(getRootItem().getChildren());
	}

	@NotNull
	public List<T> getEntries() {
		List<T> items = new ArrayList<>();

		getItems().forEach(e -> items.add(e.getValue()));

		return items;
	}

	public void addEntry(@NotNull T entry) {
		getRootItem().getChildren().add(new TreeItem<>(entry));
	}

	@NotNull
	public TreeView<T> getTreeView() {
		return rootTreeView;
	}

	public void addItem(@NotNull TreeItem<T> item) {
		getRootItem().getChildren().add(item);
	}

	@NotNull
	public T getSelectedTreeEntry() {
		return getSelectedTreeItem().getValue();
	}

	@NotNull
	public TreeItem<T> getSelectedTreeItem() {
		return this.rootTreeView.getSelectionModel().getSelectedItem();
	}

	public void refresh() {
		this.rootTreeView.refresh();
	}

	public void removeSelectedItem() {
		TreeItem<T> item = getSelectedTreeItem();
		getRootItem().getChildren().remove(item);
	}

	public void clearItems() {
		getRootItem().getChildren().clear();
	}

	public void updateSelectedEntry() {
		if (updateSelection) {
			TreeItem<T> item = this.getTreeView().getSelectionModel().getSelectedItem();
			if(item != null){
				item.getValue().onSelect();
			}else{
				log.info("Selected entry is null!");
			}
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
