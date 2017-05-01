package sebe3012.servercontroller.gui.tree;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Sebe3012 on 01.05.2017.
 * A tree entry for a path
 */
public class PathTreeEntry implements TreeEntry<Path> {

	private Path item;

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
