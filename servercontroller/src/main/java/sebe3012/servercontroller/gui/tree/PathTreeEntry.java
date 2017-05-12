package sebe3012.servercontroller.gui.tree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Sebe3012 on 01.05.2017.
 * A tree entry for a path
 */
public class PathTreeEntry implements TreeEntry<Path> {

	private Path item;

	public static final Image FOLDER_TEXTURE = new Image(ClassLoader.getSystemResource("png/treeview/folder.png").toExternalForm());

	public PathTreeEntry(Path path) {
		this.item = path;
	}

	@Override
	public boolean onDoubleClick() {

		try {
			Desktop.getDesktop().open(item.toFile());
			return true;
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
				return new ImageView(FOLDER_TEXTURE);
			}

		}
		return null;
	}

	@NotNull
	@Override
	public Path getItem() {
		return item;
	}

	@Override
	public void setItem(Path item) {
		this.item = item;
	}

	@NotNull
	@Override
	public String getName() {
		return item.getFileName().toString();
	}
}
