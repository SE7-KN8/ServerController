package sebe3012.servercontroller.gui.tree;

import javafx.scene.control.TreeCell;

/**
 * Created by Sebe3012 on 21.04.2017.
 * A cell for the structure view
 */
public class StructureCell extends TreeCell<TreeEntry<?>>{

	@Override
	protected void updateItem(TreeEntry<?> item, boolean empty) {
		super.updateItem(item, empty);

		if(item == null || empty){
			setText("");
			setGraphic(null);
			setContextMenu(null);
		}else{
			setText(item.getName());
			setGraphic(item.getGraphic());
			setContextMenu(item.getContextMenu());
		}

	}
}
