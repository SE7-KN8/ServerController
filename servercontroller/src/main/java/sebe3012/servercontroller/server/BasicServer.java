package sebe3012.servercontroller.server;

import sebe3012.servercontroller.addon.api.Addon;
import sebe3012.servercontroller.event.ServerMessageEvent;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.jna.Kernel32;
import sebe3012.servercontroller.jna.W32API;
import sebe3012.servercontroller.server.monitoring.ServerMonitor;
import sebe3012.servercontroller.util.DialogUtil;
import sebe3012.servercontroller.util.ErrorCode;
import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Pointer;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public abstract class BasicServer {
	protected BufferedReader inputReader;
	protected BufferedWriter outputWriter;
	protected WaitForExit waitForExitThread;
	protected MessageReader messageReaderThread;
	protected ProcessBuilder serverProcessBuilder;
	protected Process serverProcess;
	protected int pid = 0;
	protected StringProperty jarPath;
	private StringProperty name;
	private StringProperty args;
	private String argsAfterJar = "";
	private TabServerHandler handler;
	private Logger log = LogManager.getLogger();
	private ServerState state = ServerState.STOPPED;
	private ServerMonitor monitor = new ServerMonitor();
	private Queue<OutputCallback> outputCallbackQueue = new LinkedList<>();
	private boolean isAddonSet = false;
	private Addon addon;

	public BasicServer(Map<String, StringProperty> properties) {
		name = properties.get("name");
		args = properties.get("args");
		jarPath = properties.get("jarfile");
	}

	public ErrorCode start() {
		if (getState() == ServerState.STOPPED) {
			try {

				if (!Files.exists(Paths.get(getJarPath()))) {
					DialogUtil.showErrorAlert(I18N.translate("dialog_error"), I18N.translate("dialog_error"), I18N.format("dialog_jarfile_not_found", getJarPath()), false);
					return ErrorCode.FILE_NOT_FOUND_ERROR;
				}

				messageReaderThread = new MessageReader();
				waitForExitThread = new WaitForExit();
				messageReaderThread.setName(getName() + "-Server reader");
				waitForExitThread.setName(getName() + "-Server stop listener");

				serverProcessBuilder = new ProcessBuilder("java", getArgs(), "-jar", getJarPath(),
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

	public void stop() {
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
						EventHandler.EVENT_BUS.post(new ServerMessageEvent(BasicServer.this, line));
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
				EventHandler.EVENT_BUS.post(new ServerStopEvent(BasicServer.this, code));

				messageReaderThread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
				onError(e);
			}
		}
	}

	public String getServerInfo() {
		return I18N.format("server_name", getName());
	}

	public String getStopCommand() {
		return "stop";
	}

	public void onError(Exception errorMessage) {

		EventHandler.EVENT_BUS.post(new ServerMessageEvent(this, "Error while server run"));

		Platform.runLater(() -> DialogUtil.showExceptionAlert(I18N.translate("dialog_error"), I18N.format("dialog_server_error_of", getName()), "", errorMessage));
	}

	public void sendCommand(String command) {
		try {
			outputWriter.write(command + "\n");
			outputWriter.flush();
		} catch (IOException e) {
			onError(e);
			e.printStackTrace();
		}
	}

	public void setServerHandler(TabServerHandler handler) {
		this.handler = handler;
	}

	public TabServerHandler getServerHandler() {
		return handler;
	}

	public boolean hasServerHandler() {
		return handler != null;
	}

	public final void setAddon(Addon addon) {
		if (!isAddonSet) {
			this.addon = addon;
			isAddonSet = true;
		} else {
			throw new RuntimeException("Addon is already set");
		}
	}

	public Addon getAddon() {
		return addon;
	}

	public boolean isRunning() {
		return getState() != ServerState.STOPPED;
	}

	public int getPID() {
		return pid;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getJarPath() {
		return jarPath.get();
	}

	public void setJarPath(String jarPath) {
		this.jarPath.set(jarPath);
	}

	public String getArgsAfterJar() {
		return argsAfterJar;
	}

	public void setArgsAfterJar(String argsAfterJar) {
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

	public abstract List<Control> getExtraControls();

	public abstract int getSaveVersion();

	public String getDoneRegex() {
		return null;
	}

	public ServerState getState() {
		return this.state;
	}

	public void setState(ServerState state) {
		log.debug("[{}]: Set state from {} to {}", getName(), this.state, state);
		this.state = state;
		handler.refreshListState();
	}

	public ServerMonitor getMonitor() {
		return this.monitor;
	}


	public OutputCallback waitForCommandResponse(String regex, Consumer<String> callback) {
		OutputCallback outputCallback = new OutputCallback(regex, callback, this);

		this.outputCallbackQueue.add(outputCallback);
		return outputCallback;
	}

	public void removeCallback(OutputCallback callback) {
		this.outputCallbackQueue.remove(callback);
	}

}
