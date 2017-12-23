package sebe3012.servercontroller.gui.tree;

import sebe3012.servercontroller.addon.api.AddonUtil;
import sebe3012.servercontroller.addon.api.filetype.FileEditorManager;
import sebe3012.servercontroller.api.gui.tab.TabEntry;
import sebe3012.servercontroller.api.gui.tab.TabHandler;
import sebe3012.servercontroller.api.gui.tree.TreeEntry;
import sebe3012.servercontroller.api.server.BasicServerHandler;
import sebe3012.servercontroller.server.ServerManager;
import sebe3012.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Sebe3012 on 01.05.2017.
 * A tree entry for a path
 */
public class PathTreeEntry implements TreeEntry<Path> {

	private Path item;
	private TabHandler<TabEntry<?>> serverTabHandler;
	private BasicServerHandler handler;
	private ServerManager manager;

	private static final Image FOLDER_ICON = new Image(ClassLoader.getSystemResource("png/treeview/folder.png").toExternalForm());

	public PathTreeEntry(@NotNull Path path, TabHandler<TabEntry<?>> serverTabHandler, BasicServerHandler handler, ServerManager manager) {
		this.item = path;
		this.serverTabHandler = serverTabHandler;
		this.handler = handler;
		this.manager = manager;
	}

	@Override
	public boolean onDoubleClick() {
		if (!Files.isDirectory(item)) {
			AddonUtil.openFileEditor(serverTabHandler, item);
			manager.selectServer(handler);
		}
		return true;
	}

	@Nullable
	@Override
	public Node getGraphic() {
		if (item != null) {
			if (Files.isDirectory(item)) {
				return new ImageView(PathTreeEntry.FOLDER_ICON);
			}

		}
		return null;
	}


	@Nullable
	@Override
	public ContextMenu getContextMenu() {
		Menu openWith = new Menu(I18N.translate("context_menu_open_with"));

		for (FileEditorManager.FileEditorEntry entry : AddonUtil.getEditorsForType(item)) {
			MenuItem item = new MenuItem(entry.getLocalizedName());
			item.setOnAction(e -> {
				AddonUtil.loadFileEditor(entry.getEditorClass().getName(), serverTabHandler, PathTreeEntry.this.item);
				manager.selectServer(handler);
			});
			item.setGraphic(entry.getFileGraphic());
			openWith.getItems().add(item);
		}

		MenuItem openWithSystem = new MenuItem(I18N.translate("context_menu_open_in_system"));
		openWithSystem.setOnAction(e -> {
			try {
				Desktop.getDesktop().open(item.toFile());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		openWithSystem.setGraphic(new ImageView(PathTreeEntry.FOLDER_ICON));
		openWith.getItems().add(openWithSystem);

		ContextMenu menu = new ContextMenu();
		menu.getItems().add(openWith);

		return menu;
	}

	@NotNull
	@Override
	public Path getItem() {
		return item;
	}

	@Override
	public void setItem(@NotNull Path item) {
		this.item = item;
	}

	@NotNull
	@Override
	public String getName() {
		return item.getFileName().toString();
	}
}
