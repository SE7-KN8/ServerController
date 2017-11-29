package sebe3012.servercontroller.gui.tree;

import sebe3012.servercontroller.util.I18N;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
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

	private static final Image FOLDER_ICON = new Image(ClassLoader.getSystemResource("png/treeview/folder.png").toExternalForm());

	public PathTreeEntry(@NotNull Path path) {
		this.item = path;
	}

	@Override
	public boolean onDoubleClick() {
		try {

			if(!Files.isDirectory(item)){
				Desktop.getDesktop().open(item.toFile());
				return true;
			}

			return false;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

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

		MenuItem openWithSystem = new MenuItem(I18N.translate("context_menu_open_in_system"));
		openWithSystem.setOnAction(e->{
			try{
				Desktop.getDesktop().open(item.toFile());
			}catch (IOException ex){
				ex.printStackTrace();
			}
		});
		openWithSystem.setGraphic(new ImageView(PathTreeEntry.FOLDER_ICON));

		ContextMenu menu = new ContextMenu();
		menu.getItems().add(openWithSystem);

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
