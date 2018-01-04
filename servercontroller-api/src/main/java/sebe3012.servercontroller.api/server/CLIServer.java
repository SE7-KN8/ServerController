package sebe3012.servercontroller.api.server;

import sebe3012.servercontroller.api.server.jna.Kernel32;
import sebe3012.servercontroller.api.server.jna.W32API;
import sebe3012.servercontroller.api.util.ErrorCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.sun.jna.Pointer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public abstract class CLIServer extends NamedServer {

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

						CLIServer.this.getMessageListeners().forEach(listener -> listener.onMessage(line));

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
				log.info("[{}]: Stopped with code {}", CLIServer.this.getName(), code);

				CLIServer.this.destroy();
				CLIServer.this.getStopListeners().forEach(listener -> listener.onStop(code));


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
	private Process serverProcess;
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

				ProcessBuilder serverProcessBuilder = new ProcessBuilder(cmd);
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
				}
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

	protected void onError(@NotNull Exception errorMessage) {
		for (MessageListener listener : getMessageListeners()) {
			StringWriter sw = new StringWriter();
			PrintWriter ps = new PrintWriter(sw);
			errorMessage.printStackTrace(ps);
			listener.onMessage("Error while server run: " + sw.toString());
		}
	}

	@Override
	public void sendCommand(@NotNull String command) {
		try {
			outputWriter.write(command + "\n");
			outputWriter.flush();
		} catch (IOException e) {
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
