package sebe3012.servercontroller.gui.tree;

import sebe3012.servercontroller.server.BasicServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

/**
 * Created by Sebe3012 on 30.04.2017.
 * A tree entry for all basic server
 */
public class ServerTreeEntry implements TreeEntry<BasicServer> {

	private BasicServer item;

	public ServerTreeEntry(BasicServer item){
		this.item = item;
	}

	@NotNull
	@Override
	public String getName() {
		return item.getName();
	}

	@Nullable
	@Override
	public Node getGraphic() {
		return new Circle(8, item.getState().getColor());
	}

	@NotNull
	@Override
	public BasicServer getItem() {
		return item;
	}

	@Override
	public void setItem(@NotNull BasicServer item) {
		this.item = item;
	}
}
