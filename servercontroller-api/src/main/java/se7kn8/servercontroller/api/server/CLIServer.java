package se7kn8.servercontroller.api.server;

import se7kn8.servercontroller.api.util.ErrorCode;
import se7kn8.servercontroller.api.util.FileUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;
import com.pty4j.PtyProcess;
import com.sun.jna.Platform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class CLIServer extends NamedServer {

	private void setupNativeLibraries() {
		try {
			List<String> nativeLibraries = new ArrayList<>();

			if (Platform.isWindows()) {
				if (Platform.is64Bit()) {
					nativeLibraries.add("libpty/win/x86_64/winpty.dll");
					nativeLibraries.add("libpty/win/x86_64/winpty-agent.exe");
					nativeLibraries.add("libpty/win/x86_64/cyglaunch.exe");
				} else {
					nativeLibraries.add("libpty/win/x86/winpty.dll");
					nativeLibraries.add("libpty/win/x86/winpty-agent.exe");
				}
			} else if (Platform.isLinux()) {
				if (Platform.is64Bit()) {
					nativeLibraries.add("libpty/linux/x86_64/libpty.so");
				} else {
					nativeLibraries.add("libpty/linux/x86/libpty.so");
				}
			} else if (Platform.isMac()) {
				if (Platform.is64Bit()) {
					nativeLibraries.add("libpty/macosx/x86_64/libpty.dylib");
				} else {
					nativeLibraries.add("libpty/macosx/x86/libpty.dylib");
				}
			}

			for (String nativeLibrary : nativeLibraries) {
				Path nativePath = FileUtil.ROOT_PATH.resolve("native").resolve(nativeLibrary);

				if (Files.notExists(nativePath)) {
					Files.createDirectories(nativePath.getParent());
					InputStream inputStream = ClassLoader.getSystemResourceAsStream(nativeLibrary);
					Files.copy(inputStream, nativePath);
					inputStream.close();
				}

			}

			System.setProperty("PTY_LIB_FOLDER", FileUtil.ROOT_PATH.resolve("native").resolve("libpty").toString());
		} catch (Exception e) {
			log.error("Error while creating native libraries: ", e);
		}

	}

	private final class MessageReader extends Thread {
		@Override
		public void run() {
			while (!interrupted()) {
				try {
					String line = inputReader.readLine();

					if (line != null) {
						if (CLIServer.this.getState() == ServerState.STARTING && line.matches(getDoneRegex())) {
							CLIServer.this.setState(ServerState.RUNNING);
						}
						CLIServer.this.sendLine(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
					onError(e);
				}
			}
		}
	}

	private final class WaitForExit extends Thread {
		@Override
		public void run() {
			try {
				int code = serverProcess.waitFor();
				CLIServer.this.destroy();
				CLIServer.this.sendStop(code);

				messageReaderThread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
				onError(e);
			}
		}
	}

	private BufferedReader inputReader;
	private BufferedWriter outputWriter;
	private WaitForExit waitForExitThread;
	private MessageReader messageReaderThread;
	private PtyProcess serverProcess;
	private int pid = 0;
	private String[] args;
	private Logger log = LogManager.getLogger();

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);

		args = properties.get("args").split("");
	}

	@NotNull
	public ErrorCode start() {
		if (getState() == ServerState.STOPPED) {
			try {
				setState(ServerState.STARTING);
				messageReaderThread = new MessageReader();
				waitForExitThread = new WaitForExit();
				messageReaderThread.setName(getName() + "-Server reader");
				waitForExitThread.setName(getName() + "-Server stop listener");

				String[] cmd = new String[getArgsAfterCommand().length + getArgs().length + 1];
				cmd[0] = getBaseCommand();
				for (int i = 0; i < getArgs().length; i++) {
					cmd[i + 1] = getArgs()[i];
				}
				for (int i = 0; i < getArgsAfterCommand().length; i++) {
					cmd[i + getArgs().length + 1] = getArgsAfterCommand()[i];
				}

				log.info("[{}]: Start with command: '{}'; Working directory is '{}'", getName(), Arrays.toString(cmd), getWorkingDirectory().toFile().toString());

				setupNativeLibraries();
				Map<String, String> envs = Maps.newHashMap(System.getenv());
				serverProcess = PtyProcess.exec(cmd, envs, getWorkingDirectory().toString());
				inputReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
				outputWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
				messageReaderThread.start();
				waitForExitThread.start();

				/*ProcessBuilder serverProcessBuilder = new ProcessBuilder(cmd);
				serverProcessBuilder.redirectErrorStream(true);
				serverProcessBuilder.directory(getWorkingDirectory().toFile());
				serverProcess = serverProcessBuilder.start();
				inputReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
				outputWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
				messageReaderThread.start();
				waitForExitThread.start();
				if (serverProcess.getClass().getCanonicalName().contains("UNIXProcess")) {
					Field id = serverProcess.getClass().getDeclaredField("pid");
					id.setAccessible(true);
					pid = (int) id.getLong(serverProcess);
				} else {
					Field id = serverProcess.getClass().getDeclaredField("handle");
					id.setAccessible(true);
					Kernel32 kernel = Kernel32.INSTANCE;
					W32API.HANDLE handle = new W32API.HANDLE();
					handle.setPointer(Pointer.createConstant(id.getLong(serverProcess)));
					pid = kernel.GetProcessId(handle);
				}*/
			} catch (Exception e) {
				setState(ServerState.STOPPED);
				e.printStackTrace();
				onError(e);
				return ErrorCode.ERROR;
			}

			return ErrorCode.SUCCESSFUL;
		}

		return ErrorCode.ERROR;

	}

	@NotNull
	public ErrorCode stop() {
		sendCommand(getStopCommand());
		setState(ServerState.STOPPING);
		return ErrorCode.SUCCESSFUL;
	}

	private void destroy() {
		try {
			serverProcess.destroy();
			messageReaderThread.interrupt();
			inputReader.close();
			outputWriter.close();
			setState(ServerState.STOPPED);
		} catch (IOException e) {
			onError(e);
			e.printStackTrace();
		}
	}

	@NotNull
	protected abstract String getBaseCommand();

	@NotNull
	protected abstract String[] getArgsAfterCommand();

	@NotNull
	protected abstract Path getWorkingDirectory();

	public String[] getArgs() {
		return args;
	}

	@Override
	public void sendCommand(@NotNull String command) {
		try {
			outputWriter.write(command + "\n");
			outputWriter.flush();
		} catch (Exception e) {
			onError(e);
			e.printStackTrace();
		}
	}

	public final int getPID() {
		return pid;
	}

	@NotNull
	public abstract String getStopCommand();

	@NotNull
	public abstract String getDoneRegex();
}
