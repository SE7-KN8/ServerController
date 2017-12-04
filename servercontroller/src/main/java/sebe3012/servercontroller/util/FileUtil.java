package sebe3012.servercontroller.util;

import sebe3012.servercontroller.gui.tab.TabEntry;
import sebe3012.servercontroller.gui.tab.TabHandler;
import sebe3012.servercontroller.gui.tree.PathTreeEntry;
import sebe3012.servercontroller.gui.tree.TreeEntry;
import sebe3012.servercontroller.preferences.PreferencesConstants;
import sebe3012.servercontroller.preferences.ServerControllerPreferences;
import sebe3012.servercontroller.server.BasicServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebe3012 on 19.04.2017.
 * A util class for all things with files
 */
public class FileUtil {

	private static DirectoryStream.Filter<Path> isDirectory = p -> (Files.isDirectory(p));
	private static DirectoryStream.Filter<Path> isFile = p -> (!Files.isDirectory(p));

	private static Logger log = LogManager.getLogger();

	public static final Path ROOT_PATH = Paths.get(System.getProperty("user.home"), ".servercontroller");

	public static List<Path> searchDirectory(Path directory, @Nullable PathMatcher allowedExtensions){
		List<Path> foundPaths = new ArrayList<>();

		try {
			for (Path path : Files.newDirectoryStream(directory)) {
				if(allowedExtensions != null){
					if(allowedExtensions.matches(path)){
						foundPaths.add(path);
					}
				}else{
					foundPaths.add(path);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return foundPaths;
	}

	public static void searchSubFolders(Path parent, TreeItem<TreeEntry<?>> parentItem, TabHandler<TabEntry<?>> serverHandler) {
		try {
			searchFiles(parent, parentItem, serverHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void searchFiles(Path parent, TreeItem<TreeEntry<?>> parentItem, TabHandler<TabEntry<?>> serverHandler) throws IOException {
		if (!Files.isDirectory(parent)) {
			return;
		}

		DirectoryStream<Path> paths = Files.newDirectoryStream(parent, isDirectory);

		for (Path path : paths) {
			TreeItem<TreeEntry<?>> childItem = new TreeItem<>(new PathTreeEntry(path, serverHandler));
			parentItem.getChildren().add(childItem);

			searchFiles(path, childItem, serverHandler);
		}


		paths = Files.newDirectoryStream(parent, isFile);

		for (Path path : paths) {
			TreeItem<TreeEntry<?>> childItem = new TreeItem<>(new PathTreeEntry(path, serverHandler));
			parentItem.getChildren().add(childItem);
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

		if (save) {
			f = fc.showSaveDialog(null);
		} else {
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


	@NotNull
	public static String loadStringContent(@NotNull String path) {
		log.info("Load content: " + path);
		StringBuilder result = new StringBuilder();
		try {

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ClassLoader.getSystemResourceAsStream(path)));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer);
				result.append('\n');
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	@NotNull
	public static Path createRelativePath(@NotNull String path) {
		return ROOT_PATH.resolve(path);
	}

	@NotNull
	public static Path loadServerFile(@NotNull BasicServer server, @NotNull String path) {
		return Paths.get(server.getJarPath()).resolve(path);
	}

}
