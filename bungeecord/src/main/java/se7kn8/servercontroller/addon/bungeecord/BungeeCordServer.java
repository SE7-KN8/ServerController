package se7kn8.servercontroller.addon.bungeecord;

import se7kn8.servercontroller.api.server.JarServer;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BungeeCordServer extends JarServer {
	private String configFile;

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);

		configFile = properties.get("bungeeCordConfig");
	}

	@Override
	public int getSaveVersion() {
		return 1;
	}

	@NotNull
	public String getConfigFile() {
		return configFile;
	}

	@NotNull
	@Override
	public String getStopCommand() {
		return "end";
	}

	@NotNull
	@Override
	protected String getArgsAfterJar() {
		return "";
	}

	@Override
	public String[] getArgs() {
		String[] oldArgs = super.getArgs();
		String[] newArgs = new String[oldArgs.length + 2];
		for (int i = 0; i < oldArgs.length; i++) {
			newArgs[i] = oldArgs[i];
		}
		newArgs[oldArgs.length] = "-Djline.WindowsTerminal.directConsole=false";
		newArgs[oldArgs.length + 1] = "-Djline.terminal=jline.UnsupportedTerminal";
		return newArgs;
	}

	@NotNull
	@Override
	public String getDoneRegex() {
		return ".*Listening on \\/\\d*.\\d*.\\d*.\\d*:\\d*";
	}

}