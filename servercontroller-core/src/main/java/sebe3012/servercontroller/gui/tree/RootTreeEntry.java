package sebe3012.servercontroller.gui.tree;

import sebe3012.servercontroller.api.gui.tree.TreeEntry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;

/**
 * Created by Sebe3012 on 30.04.2017.
 * The root tree entry
 */
public class RootTreeEntry implements TreeEntry<String> {

	private final String name;

	public RootTreeEntry(String name){
		this.name = name;
	}

	@NotNull
	@Override
	public String getName() {
		return this.name;
	}

	@Nullable
	@Override
	public Node getGraphic() {
		return null;
	}

	@NotNull
	@Override
	public String getItem() {
		return this.name;
	}

	@Override
	public void setItem(@NotNull String item) {
		//Final name
	}
}
