package se7kn8.servercontroller.prelaunch;

import se7kn8.servercontroller.addon.AddonLoader;
import se7kn8.servercontroller.api.util.FileUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AddonInstallerTask {

	private Logger log = LogManager.getLogger();
	private Path searchPath;
	private Path copyPath;
	private PathMatcher allowedFileExtensions;

	public AddonInstallerTask(@NotNull Path searchPath, @NotNull Path copyPath, @Nullable PathMatcher allowedFileExtensions) {
		this.searchPath = searchPath;
		this.copyPath = copyPath;
		this.allowedFileExtensions = allowedFileExtensions;
	}

	public void installAddons() {
		try {
			Files.createDirectories(AddonLoader.ADDON_TEMP_PATH);
			Files.createDirectories(AddonLoader.ADDON_PATH);

			List<Path> filesToCopy = FileUtil.searchDirectory(searchPath, allowedFileExtensions);

			for (Path path : filesToCopy) {
				log.info("Copy file '{}' into the addons folders", path);
				Files.copy(path, copyPath.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
				Files.delete(path);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
