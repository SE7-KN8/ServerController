package sebe3012.servercontroller.server;

import sebe3012.servercontroller.event.ServerMessageEvent;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.gui.tab.TabServerHandler;
import sebe3012.servercontroller.jna.Kernel32;
import sebe3012.servercontroller.jna.W32API;
import sebe3012.servercontroller.server.monitoring.ServerMonitor;
import sebe3012.servercontroller.util.DialogUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Pointer;

import javafx.scene.control.Control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public abstract class BasicServer {
	protected BufferedReader inputReader;
	protected BufferedWriter outputWriter;
	protected WaitForExit waitForExitThread;
	protected MessageReader messageReaderThread;
	protected ProcessBuilder serverProcessBuilder;
	protected Process serverProcess;
	protected int pid = 0;
	protected File jarFile;
	protected String name;
	protected String args;
	protected String argsAfterJar = "";
	protected TabServerHandler handler;
	private HashMap<String, Object> externalForm;
	private Logger log = LogManager.getLogger();
	private ServerState state = ServerState.STOPPED;
	private ServerMonitor monitor = new ServerMonitor();

	public BasicServer(String name, String jarFilePath, String args) {
		this.name = name;
		this.jarFile = new File(jarFilePath);
		this.args = args;
	}

	public BasicServer(HashMap<String, Object> externalForm) {
		this.externalForm = externalForm;
	}

	public void start() {
		if (getState() == ServerState.STOPPED) {
			try {
				messageReaderThread = new MessageReader(new MessageReader(), this);
				waitForExitThread = new WaitForExit(new WaitForExit(), this);
				messageReaderThread.setName(name + "-Server reader");
				waitForExitThread.setName(name + "-Server stop listener");

				serverProcessBuilder = new ProcessBuilder("java", getArgs(), "-jar", jarFile.getAbsolutePath(),
						getArgsAfterJar() + "nogui");


				StringBuilder builder = new StringBuilder();
				for (String s : serverProcessBuilder.command()) {
					builder.append(s);
					builder.append(" ");
				}
				log.info("[{}]: Start with command: '{}'", getName(), builder.toString());

				serverProcessBuilder.directory(jarFile.getParentFile());
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
			}
		}
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
		return args;
	}

	private final class MessageReader extends Thread implements Serializable {

		private static final long serialVersionUID = -2412119001299121572L;

		private BasicServer server;

		public MessageReader(Runnable runnable, BasicServer server) {
			super(runnable);
			this.server = server;
		}

		public MessageReader() {
		}

		@Override
		public void run() {
			while (!interrupted()) {
				try {
					String line = inputReader.readLine();

					if (line != null) {
						EventHandler.EVENT_BUS.post(new ServerMessageEvent(server, line));
					}
				} catch (Exception e) {
					e.printStackTrace();
					onError(e);
				}
			}
		}
	}

	private final class WaitForExit extends Thread implements Serializable {

		private static final long serialVersionUID = -4874386762623677601L;

		private BasicServer server;

		public WaitForExit(Runnable runnable, BasicServer server) {
			super(runnable);
			this.server = server;
		}

		public WaitForExit() {
		}

		@Override
		public void run() {
			try {
				int code = serverProcess.waitFor();
				EventHandler.EVENT_BUS.post(new ServerStopEvent(this.server, code));

				messageReaderThread.interrupt();

			} catch (Exception e) {
				e.printStackTrace();
				onError(e);
			}
		}
	}

	public String getServerInfo() {
		return "Server-Name: " + name;
	}

	public String getStopCommand() {
		return "stop";
	}

	public void onError(Exception errorMessage) {

		EventHandler.EVENT_BUS.post(new ServerMessageEvent(this, "Error while server run"));

		DialogUtil.showExceptionAlert("Fehler", "Fehler von: " + getName(), "", errorMessage);
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

	public abstract String getPluginName();

	public boolean isRunning() {
		return getState() != ServerState.STOPPED;
	}

	public int getPID() {
		return pid;
	}

	public String getName() {
		return name;
	}

	public File getJarFile() {
		return jarFile;
	}

	public String getArgsAfterJar() {
		return argsAfterJar;
	}

	public void setArgsAfterJar(String argsAfterJar) {
		this.argsAfterJar = argsAfterJar;
	}

	public HashMap<String, Object> toExternalForm() {
		HashMap<String, Object> map = new HashMap<>();

		map.put("name", name);
		map.put("jarfile", jarFile.getAbsolutePath());
		map.put("args", args);

		return map;
	}

	public void fromExternalForm() {

		name = (String) externalForm.get("name");
		jarFile = new File((String) externalForm.get("jarfile"));
		args = (String) externalForm.get("args");

	}

	public abstract BasicServer createNew();

	@Override
	public String toString() {
		return "BasicServer{" +
				", state=" + state + '\'' +
				", jarFile=" + jarFile + '\'' +
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

	public ServerMonitor getMonitor(){
		return this.monitor;
	}

}
