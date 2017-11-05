package sebe3012.servercontroller.gui.tree;

import sebe3012.servercontroller.server.BasicServer;
import sebe3012.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Circle;

/**
 * Created by Sebe3012 on 30.04.2017.
 * A tree entry for all basic server
 */
public class ServerTreeEntry implements TreeEntry<BasicServer> {

	private BasicServer item;

	public ServerTreeEntry(BasicServer item) {
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

	@Nullable
	@Override
	public ContextMenu getContextMenu() {

		MenuItem start = new MenuItem(I18N.translate("tooltip_start_server"));
		start.setOnAction(e -> item.getServerHandler().onStartClicked());

		MenuItem restart = new MenuItem(I18N.translate("tooltip_restart_server"));
		restart.setOnAction(e -> item.getServerHandler().onRestartClicked());

		MenuItem stop = new MenuItem(I18N.translate("tooltip_stop_server"));
		stop.setOnAction(e -> item.getServerHandler().onStopClicked());

		ContextMenu menu = new ContextMenu();
		menu.getItems().add(start);
		menu.getItems().add(restart);
		menu.getItems().add(stop);

		return menu;
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
