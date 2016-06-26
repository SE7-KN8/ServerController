package sebe3012.servercontroller.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.sun.jna.Pointer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import sebe3012.servercontroller.event.ServerMessageEvent;
import sebe3012.servercontroller.event.ServerStopEvent;
import sebe3012.servercontroller.eventbus.EventHandler;
import sebe3012.servercontroller.jna.Kernel32;
import sebe3012.servercontroller.jna.W32API;

public abstract class BasicServer implements Serializable {
	private static final long serialVersionUID = -6581065154916341314L;
	protected boolean running = false;
	private boolean started = false;
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

	public BasicServer(String name, String jarFilePath, String args) {
		this.name = name;
		this.jarFile = new File(jarFilePath);
		this.messageReaderThread = new MessageReader(new MessageReader(), this);
		this.waitForExitThread = new WaitForExit(new WaitForExit(), this);
		this.messageReaderThread.setName(name + "-Server reader");
		this.waitForExitThread.setName(name + "-Server stop listener");
		this.args = args;
	}

	public void start() {
		if (!started) {
			try {

				serverProcessBuilder = new ProcessBuilder("java", getArgs(), "-jar", jarFile.getAbsolutePath(),
						"nogui");

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
				running = true;
				started = true;
			} catch (Exception e) {
				e.printStackTrace();
				onError(e);
			}
		}
	}

	public void stop() {
		try {
			serverProcess.destroy();
			messageReaderThread.interrupt();
			inputReader.close();
			outputWriter.close();
			running = false;
			started = false;
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
				running = false;
				started = false;

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

		StringBuilder sb = new StringBuilder();
		sb.append("\n" + errorMessage.toString() + "\n");

		int counter = 12;

		if (errorMessage.getStackTrace().length < 10) {
			counter = errorMessage.getStackTrace().length;
		}
		for (int i = 0; i < counter; i++) {
			sb.append("at ");
			sb.append(errorMessage.getStackTrace()[i]);
			sb.append("\n");
		}

		Platform.runLater(() -> {
			Alert error = new Alert(AlertType.ERROR, "Es ist ein Fehler aufgetreten.\n" + "Fehler: " + sb.toString(),
					ButtonType.OK);
			error.getDialogPane().setPrefSize(800, 400);
			error.getDialogPane().getStylesheets().add(this.getClass().getClassLoader()
					.getResource("sebe3012/servercontroller/gui/style.css").toExternalForm());
			error.showAndWait();
		});
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

	public abstract String getPluginName();

	public boolean isRunning() {
		return running;
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

	public abstract BasicServer createNew();

	public abstract HashMap<String, Runnable> getExtraButtons();

}
