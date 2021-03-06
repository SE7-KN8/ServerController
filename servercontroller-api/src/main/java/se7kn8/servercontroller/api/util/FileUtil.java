package se7kn8.servercontroller.api.util;

import se7kn8.servercontroller.api.preferences.ServerControllerPreferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.util.Optional;

/**
 * Created by se7kn8 on 19.04.2017.
 * A util class for all things with files
 */
public class FileUtil {

	public static final DirectoryStream.Filter<Path> IS_DIRECTORY = p -> (Files.isDirectory(p));
	public static final DirectoryStream.Filter<Path> IS_FILE = p -> (!Files.isDirectory(p));

	private static Logger log = LogManager.getLogger();
	private static final String SAVE_KEY_LAST_LOCATION = "lastLocation";

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

	/**
	 * @param fileType The file regex
	 * @param fileName The file name
	 * @return the path to the file or null if the user canceled the operation
	 */
	@NotNull
	public static Optional<String> openFileChooser(String fileType, String fileName, boolean save) {

		FileChooser fc = new FileChooser();

		log.debug("Path: {}", ServerControllerPreferences.loadSetting(FileUtil.SAVE_KEY_LAST_LOCATION, "null"));

		File path = new File(ServerControllerPreferences.loadSetting(FileUtil.SAVE_KEY_LAST_LOCATION, System.getProperty("user.home")));

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
			ServerControllerPreferences.saveSetting(FileUtil.SAVE_KEY_LAST_LOCATION, f.getParent());

			return Optional.of(f.getAbsolutePath());
		}

		return Optional.empty();

	}

	/**
	 * @param fileType The file regex
	 * @param fileName The file name
	 * @return the path to the file or null if the user canceled the operation
	 */
	@NotNull
	public static Optional<String> openFileChooser(String fileType, String fileName) {
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
}
