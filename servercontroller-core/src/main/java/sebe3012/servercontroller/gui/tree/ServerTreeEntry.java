package sebe3012.servercontroller.gui.tree;

import sebe3012.servercontroller.api.gui.tab.TabEntry;
import sebe3012.servercontroller.api.gui.tree.TreeEntry;
import sebe3012.servercontroller.api.server.BasicServerHandler;
import sebe3012.servercontroller.server.ServerManager;
import sebe3012.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Circle;

import java.util.List;

/**
 * Created by Sebe3012 on 30.04.2017.
 * A tree entry for all basic server
 */
public class ServerTreeEntry implements TreeEntry<BasicServerHandler> {

	private BasicServerHandler item;
	private ServerManager manager;

	public ServerTreeEntry(@NotNull BasicServerHandler item, @NotNull ServerManager manager) {
		this.item = item;
		this.manager = manager;
		this.item.getServer().stateProperty().addListener((observable, oldValue, newValue) -> manager.getTreeHandler().refresh());
	}

	@NotNull
	@Override
	public String getName() {
		return item.getServer().getName();
	}

	@Nullable
	@Override
	public Node getGraphic() {
		return new Circle(8, item.getServer().getState().getColor());
	}

	@Nullable
	@Override
	public ContextMenu getContextMenu() {

		MenuItem start = new MenuItem(I18N.translate("tooltip_start_server"));
		start.setOnAction(e -> item.startServer());

		MenuItem restart = new MenuItem(I18N.translate("tooltip_restart_server"));
		restart.setOnAction(e -> item.restartServer());

		MenuItem stop = new MenuItem(I18N.translate("tooltip_stop_server"));
		stop.setOnAction(e -> item.stopServer());

		ContextMenu menu = new ContextMenu();
		menu.getItems().add(start);
		menu.getItems().add(restart);
		menu.getItems().add(stop);

		return menu;
	}

	@NotNull
	@Override
	public BasicServerHandler getItem() {
		return item;
	}

	@Override
	public void setItem(@NotNull BasicServerHandler item) {
		this.item = item;
	}

	@Override
	public void onSelect() {
		List<TabEntry<BasicServerHandler>> entriesList = manager.getTabHandler().getTabEntries();

		for (int i = 0; i < entriesList.size(); i++) {
			if (entriesList.get(i).getItem() == this.item) {
				manager.getTabHandler().getTabPane().getSelectionModel().select(i);
			}
		}
	}
}
