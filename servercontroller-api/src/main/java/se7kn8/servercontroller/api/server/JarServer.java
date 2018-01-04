package se7kn8.servercontroller.api.server;

import se7kn8.servercontroller.api.util.ErrorCode;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public abstract class JarServer extends CLIServer {

	private Path jarPath;

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);
		jarPath = Paths.get(properties.get("jarPath"));
	}

	@NotNull
	@Override
	public ErrorCode start() {
		if (Files.exists(jarPath)) {
			return super.start();
		} else {
			return ErrorCode.FILE_NOT_FOUND_ERROR;
		}
	}

	@NotNull
	protected Path getJarPath() {
		return jarPath;
	}

	@NotNull
	protected abstract String getArgsAfterJar();

	@NotNull
	@Override
	protected String getBaseCommand() {
		return "java";
	}

	@NotNull
	@Override
	protected String[] getArgsAfterCommand() {
		return new String[]{"-jar", "\"" + getJarPath().toString() + "\"", getArgsAfterJar()};
	}

	@NotNull
	@Override
	protected Path getWorkingDirectory() {
		return jarPath.getParent();
	}
}
