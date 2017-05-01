package sebe3012.servercontroller.util;

import sebe3012.servercontroller.gui.tree.PathTreeEntry;
import sebe3012.servercontroller.gui.tree.TreeEntry;

import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Sebe3012 on 19.04.2017.
 * A util class for all files
 */
public class FileUtil {

	private static DirectoryStream.Filter<Path> isDirectory = p -> (Files.isDirectory(p));
	private static DirectoryStream.Filter<Path> isFile = p -> (!Files.isDirectory(p));

	public static void searchSubFolders(Path parent, TreeItem<TreeEntry<?>> parentItem) {
		try {
			searchFiles(parent, parentItem);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void searchFiles(Path parent, TreeItem<TreeEntry<?>> parentItem) throws IOException {
		if(!Files.isDirectory(parent)){
			return;
		}

		DirectoryStream<Path> paths = Files.newDirectoryStream(parent, isDirectory);

		for (Path path : paths) {

			TreeItem<TreeEntry<?>> childItem = new TreeItem<>(new PathTreeEntry(path));
			parentItem.getChildren().add(childItem);

			searchFiles(path, childItem);
		}


		paths = Files.newDirectoryStream(parent, isFile);

		for (Path path : paths) {

			TreeItem<TreeEntry<?>> childItem = new TreeItem<>(new PathTreeEntry(path));
			parentItem.getChildren().add(childItem);

			searchFiles(path, childItem);
		}


	}
}
