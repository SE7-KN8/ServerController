package sebe3012.servercontroller.api.server;

import sebe3012.servercontroller.api.addon.Addon;
import sebe3012.servercontroller.api.server.jna.Kernel32;
import sebe3012.servercontroller.api.server.jna.W32API;
import sebe3012.servercontroller.api.util.ErrorCode;
import sebe3012.servercontroller.api.util.StringPredicates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sun.jna.Pointer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public abstract class BasicServer {

	@FunctionalInterface
	public interface StopListener {
		void onStop(int code);
	}

	@FunctionalInterface
	public interface MessageListener {
		void onMessage(String message);
	}

	private BufferedReader inputReader;
	private BufferedWriter outputWriter;
	private WaitForExit waitForExitThread;
	private MessageReader messageReaderThread;
	private Process serverProcess;
	private int pid = 0;
	private StringProperty jarPath;
	private StringProperty name;
	private StringProperty args;
	private String argsAfterJar = "";
	private Logger log = LogManager.getLogger();
	private ObjectProperty<ServerState> state = new SimpleObjectProperty<>(ServerState.STOPPED);
	private ServerMonitor monitor = new ServerMonitor();
	private Queue<OutputCallback> outputCallbackQueue = new LinkedList<>();
	private boolean isAddonSet = false;
	private Addon addon;
	private List<StopListener> stopListeners = new ArrayList<>();
	private List<MessageListener> messageListeners = new ArrayList<>();
	private Map<String, StringProperty> properties;

	public BasicServer(Map<String, StringProperty> properties, Addon addon) {
		this.properties = properties;
		this.addon = addon;
		name = properties.get("name");
		args = properties.get("args");
		jarPath = properties.get("jarfile");
	}

	public Map<String, StringProperty> getProperties() {
		return properties;
	}

	public void addStopListener(StopListener stopListener) {
		this.stopListeners.add(stopListener);
	}

	public void removeStopListener(StopListener stopListener) {
		this.stopListeners.remove(stopListener);
	}

	public void addMessageListener(MessageListener messageListener) {
		this.messageListeners.add(messageListener);
	}

	public void removeMessageListener(MessageListener messageListener) {
		this.messageListeners.remove(messageListener);
	}

	ErrorCode start() {
		if (getState() == ServerState.STOPPED) {
			try {

				if (!Files.exists(Paths.get(getJarPath()))) {
					return ErrorCode.FILE_NOT_FOUND_ERROR;
				}

				messageReaderThread = new MessageReader();
				waitForExitThread = new WaitForExit();
				messageReaderThread.setName(getName() + "-Server reader");
				waitForExitThread.setName(getName() + "-Server stop listener");

				ProcessBuilder serverProcessBuilder = new ProcessBuilder("java", getArgs(), "-jar", getJarPath(),
						getArgsAfterJar() + "nogui");


				StringBuilder builder = new StringBuilder();
				for (String s : serverProcessBuilder.command()) {
					builder.append(s);

					builder.append(" ");
				}
				log.info("[{}]: Start with command: '{}'", getName(), builder.toString());

				serverProcessBuilder.directory(new File(getJarPath()).getParentFile());
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

				setState(ServerState.STARTING);
				this.monitor.setPid(pid);
			} catch (Exception e) {
				e.printStackTrace();
				onError(e);
				return ErrorCode.ERROR;
			}

			return ErrorCode.SUCCESSFUL;
		}

		return ErrorCode.ERROR;

	}

	void stop() {
		sendCommand(getStopCommand());
		setState(ServerState.STOPPING);
	}

	private void destroy() {
		try {
			this.monitor.setPid(-1);
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

	public String getArgs() {
		return args.get();
	}

	private final class MessageReader extends Thread {
		@Override
		public void run() {
			while (!interrupted()) {
				try {
					String line = inputReader.readLine();

					if (line != null) {
						int size = outputCallbackQueue.size();

						for (int i = 0; i < size; i++) {

							OutputCallback callback = outputCallbackQueue.remove();

							if (!callback.testLine(line)) {
								outputCallbackQueue.add(callback);
							}
						}

						if (BasicServer.this.getState() == ServerState.STARTING && StringPredicates.SERVER_DONE_CHECK.test(line, BasicServer.this)) {
							BasicServer.this.setState(ServerState.RUNNING);
						}

						BasicServer.this.messageListeners.forEach(listener -> listener.onMessage(line));

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
				log.info("[{}]: Stopped with code {}", BasicServer.this.getName(), code);

				BasicServer.this.destroy();
				BasicServer.this.stopListeners.forEach(listener -> listener.onStop(code));


				messageReaderThread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
				onError(e);
			}
		}
	}

	@NotNull
	public List<String> getServerInfos() {
		List<String> infoList = new ArrayList<>();

		infoList.add("Server-Name: " + name);
		infoList.add("Server-Jar-Path: " + getJarPath());
		infoList.add("Stop-Command: " + getStopCommand());
		infoList.add("Start-Args: " + getArgs());
		infoList.add("Addon-Name: " + getAddon().getAddonInfo().getName());
		infoList.add("Done-Regex: " + getDoneRegex());

		return infoList;
	}

	@NotNull
	public String getStopCommand() {
		return "stop";
	}

	private final void onError(@NotNull Exception errorMessage) {
		for (MessageListener listener : messageListeners) {
			StringWriter sw = new StringWriter();
			PrintWriter ps = new PrintWriter(sw);
			errorMessage.printStackTrace(ps);
			listener.onMessage("Error while server run: " + sw.toString());
		}
	}

	public final void sendCommand(@NotNull String command) {
		try {
			outputWriter.write(command + "\n");
			outputWriter.flush();
		} catch (IOException e) {
			onError(e);
			e.printStackTrace();
		}
	}

	@Deprecated
	public final void setAddon(@NotNull Addon addon) {
		if (!isAddonSet) {
			this.addon = addon;
			isAddonSet = true;
		} else {
			throw new RuntimeException("Addon is already set");
		}
	}

	@NotNull
	public final Addon getAddon() {
		return addon;
	}

	public final boolean isRunning() {
		return getState() != ServerState.STOPPED;
	}

	public final int getPID() {
		return pid;
	}

	@NotNull
	public final String getName() {
		return name.get();
	}

	public final void setName(@NotNull String name) {
		this.name.set(name);
	}

	@NotNull
	public final StringProperty nameProperty() {
		return name;
	}

	@NotNull
	public final String getJarPath() {
		return jarPath.get();
	}

	public final void setJarPath(@NotNull String jarPath) {
		this.jarPath.set(jarPath);
	}

	@NotNull
	public final String getArgsAfterJar() {
		return argsAfterJar;
	}

	public final void setArgsAfterJar(@NotNull String argsAfterJar) {
		this.argsAfterJar = argsAfterJar;
	}

	@Override
	public String toString() {
		return "BasicServer{" +
				", state=" + state + '\'' +
				", jarPath=" + jarPath + '\'' +
				", name='" + name + '\'' +
				", args='" + args + '\'' +
				", argsAfterJar='" + argsAfterJar + '\'' +
				'}';
	}

	@NotNull
	public List<Control> getExtraControls() {
		return new ArrayList<>();
	}

	public abstract int getSaveVersion();

	@Nullable
	public String getDoneRegex() {
		return null;
	}

	public void setState(@NotNull ServerState state) {
		log.info("[{}]: Set state from {} to {}", getName(), getState(), state);
		this.state.set(state);
	}

	@NotNull
	public final ServerState getState() {
		return state.get();
	}

	@NotNull
	public final ObjectProperty<ServerState> stateProperty() {
		return state;
	}

	@NotNull
	@Deprecated //FIXME too many bugs
	public ServerMonitor getMonitor() {
		return this.monitor;
	}

	@NotNull
	public final OutputCallback waitForCommandResponse(String regex, Consumer<String> callback) {
		OutputCallback outputCallback = new OutputCallback(regex, callback, this);

		this.outputCallbackQueue.add(outputCallback);
		return outputCallback;
	}

	public final void removeCallback(@NotNull OutputCallback callback) {
		this.outputCallbackQueue.remove(callback);
	}

}
