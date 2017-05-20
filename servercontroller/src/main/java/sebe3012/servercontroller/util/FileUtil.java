package sebe3012.servercontroller.util;

import sebe3012.servercontroller.gui.tree.PathTreeEntry;
import sebe3012.servercontroller.gui.tree.TreeEntry;
import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Sebe3012 on 19.04.2017.
 * A util class for all things with files
 */
public class FileUtil {

	private static DirectoryStream.Filter<Path> isDirectory = p -> (Files.isDirectory(p));
	private static DirectoryStream.Filter<Path> isFile = p -> (!Files.isDirectory(p));

	private static Logger log = LogManager.getLogger();

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

	/**
	 * @param fileType The file regex
	 * @param fileName The file name
	 * @return the path to the file or null if the user canceled the operation
	 */
	@Nullable
	public static String openFileChooser(String fileType, String fileName, boolean save) {

		FileChooser fc = new FileChooser();

		log.debug("Path: {}", ServerControllerPreferences.loadSetting(PreferencesConstants.FILE_ADDON_UTIL, "null"));

		File path = new File(ServerControllerPreferences.loadSetting(PreferencesConstants.FILE_ADDON_UTIL, System.getProperty("user.home")));

		if (path.exists()) {
			fc.setInitialDirectory(path);
		}

		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileName, fileType));

		File f;

		if(save){
			f = fc.showSaveDialog(null);
		}else{
			f = fc.showOpenDialog(null);
		}



		if (f != null) {
			ServerControllerPreferences.saveSetting(PreferencesConstants.FILE_ADDON_UTIL, f.getParent());

			return f.getAbsolutePath();
		}

		return null;

	}

	/**
	 * @param fileType The file regex
	 * @param fileName The file name
	 * @return the path to the file or null if the user canceled the operation
	 */
	@Nullable
	public static String openFileChooser(String fileType, String fileName) {
		return openFileChooser(fileType, fileName, false);
	}
}
