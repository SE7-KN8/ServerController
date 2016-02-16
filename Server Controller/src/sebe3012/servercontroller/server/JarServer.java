package sebe3012.servercontroller.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Pointer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import sebe3012.servercontroller.jna.Kernel32;
import sebe3012.servercontroller.jna.W32API;
import javafx.scene.control.ButtonType;

public class JarServer implements Serializable {

	private static final long serialVersionUID = 3225476460145320236L;
	/**
	 * The batch file
	 */
	private File jarFile;
	/**
	 * The properties file
	 */
	private File propertiesFile;
	/**
	 * The processbuild for the serverprocess
	 */
	private ProcessBuilder serverBuild;
	/**
	 * The serverprocess
	 */
	private Process serverProcess;
	/**
	 * The batch output reader
	 */
	private BufferedReader batchOutputReader;
	/**
	 * The batch input writer
	 */
	private BufferedWriter batchInputWriter;
	/**
	 * The list with all server listener
	 */
	private List<ServerListener> listener;
	/**
	 * The read thread
	 */
	private Thread serverReadThread;
	/**
	 * The thread is wait for the server exit
	 */
	private Thread waitForServerExitThread;
	/**
	 * The name of the server
	 */
	private String name;

	/**
	 * The server-properties
	 */

	private PropertiesHandler properties;

	/**
	 * The class for the read thread
	 */
	private class ReadThread extends Thread {
		public ReadThread(Thread serverReadThread) {
			super(serverReadThread);
		}

		@Override
		public void run() {
			while (!interrupted()) {
				try {
					String line = batchOutputReader.readLine();
					listener.forEach(serverListener -> {
						if (line != null) {
							serverListener.serverReturnMessage(line);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
					onError(e);
					break;
				}
			}
		}
	}

	/**
	 * This boolean is true if the server is running
	 */
	private boolean isRunning = false;

	/**
	 * The the class for the thread is wait for the server exit
	 */
	private class WaitForExitThread extends Thread {

		public WaitForExitThread(Thread waitForServerExitThread) {
			super(waitForServerExitThread);
		}

		@Override
		public void run() {
			try {
				int code = serverProcess.waitFor();
				listener.forEach(serverListener -> {
					serverReadThread.interrupt();
					serverListener.serverStoped(code);
				});
				System.out.println("[" + name + "] Stopped server with code " + code);
				isRunning = false;
			} catch (InterruptedException e) {
				onError(e);
				e.printStackTrace();
				isRunning = false;
			}

		}
	}

	/**
	 * The server ram size
	 */
	private String ram;

	public String getRam() {
		return ram;
	}

	/**
	 *
	 * The constructer for the batch server
	 *
	 * @param batch
	 *            The location of the batch file
	 * @param properties
	 *            The location of the properties file
	 * @param name
	 *            The name of the server
	 * @param ram
	 *            The server ram
	 */
	public JarServer(String batch, String properties, String name, String ram) {
		try {
			this.jarFile = new File(batch);
			this.propertiesFile = new File(properties);
			this.name = name;
			this.ram = ram;
			this.properties = new PropertiesHandler(propertiesFile);
			this.properties.readProperties();
			listener = new ArrayList<ServerListener>();
		} catch (Exception e) {
			onError(e);
			e.printStackTrace();
		}
	}

	/**
	 * Return the server-name
	 *
	 * @return The server-name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the batch-file
	 *
	 * @return The batch file
	 *
	 * @see File
	 */
	public File getJarFile() {
		return jarFile;
	}

	/**
	 * Return the properties-file
	 *
	 * @return The properies-file
	 *
	 * @see File
	 */
	public File getPropertiesFile() {
		return propertiesFile;
	}

	/**
	 * Register a serverlistener on this server
	 *
	 * @param listener
	 *            The serverlistener
	 *
	 * @see ServerListener
	 */
	public void registerListener(ServerListener listener) {
		this.listener.add(listener);
	}

	/**
	 *
	 * Send a command to the server
	 *
	 * @param command
	 *            The commend
	 */
	public void sendCommand(String command) {
		try {
			batchInputWriter.write(command + "\n");
			batchInputWriter.flush();
		} catch (IOException e) {
			onError(e);
			e.printStackTrace();
		}
	}

	private int pid;

	/**
	 *
	 * Starts the server
	 *
	 * @throws IOException
	 *             This exception will be throw if the properties and the batch
	 *             path is incorrect
	 */
	public void start() {
		try {
			serverReadThread = new ReadThread(serverReadThread);
			waitForServerExitThread = new WaitForExitThread(waitForServerExitThread);
			System.out.println();
			serverBuild = new ProcessBuilder("java", "-Xmx" + ram + "M", "-jar", jarFile.getName(), "nogui");
			serverBuild.directory(jarFile.getParentFile());
			serverProcess = serverBuild.start();
			batchOutputReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
			batchInputWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
			serverReadThread.setName(getName() + "-read-thread");
			waitForServerExitThread.setName(getName() + "-wait-for-exit-thread");
			serverReadThread.start();
			waitForServerExitThread.start();
			isRunning = true;

			Field id = serverProcess.getClass().getDeclaredField("handle");
			id.setAccessible(true);

			Kernel32 kernel = Kernel32.INSTANCE;
			W32API.HANDLE handle = new W32API.HANDLE();
			handle.setPointer(Pointer.createConstant(id.getLong(serverProcess)));
			pid = kernel.GetProcessId(handle);
		} catch (Exception e) {
			onError(e);
			e.printStackTrace();
		}
	}

	/**
	 * Return the server-process id
	 * 
	 * @return The PID
	 */
	public int getPID() {
		return pid;
	}

	/**
	 * Stop the server process
	 */
	public void stop() {
		try {
			serverProcess.destroy();
			batchInputWriter.close();
			batchOutputReader.close();
			isRunning = false;
		} catch (IOException e) {
			onError(e);
			e.printStackTrace();
		}
	}

	/**
	 * Return the server-properties
	 */
	public PropertiesHandler getServerProperties() {
		return properties;
	}

	/**
	 * Return the hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchInputWriter == null) ? 0 : batchInputWriter.hashCode());
		result = prime * result + ((batchOutputReader == null) ? 0 : batchOutputReader.hashCode());
		result = prime * result + (isRunning ? 1231 : 1237);
		result = prime * result + ((jarFile == null) ? 0 : jarFile.hashCode());
		result = prime * result + ((listener == null) ? 0 : listener.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + pid;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((propertiesFile == null) ? 0 : propertiesFile.hashCode());
		result = prime * result + ((ram == null) ? 0 : ram.hashCode());
		result = prime * result + ((serverBuild == null) ? 0 : serverBuild.hashCode());
		result = prime * result + ((serverProcess == null) ? 0 : serverProcess.hashCode());
		result = prime * result + ((serverReadThread == null) ? 0 : serverReadThread.hashCode());
		result = prime * result + ((waitForServerExitThread == null) ? 0 : waitForServerExitThread.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JarServer other = (JarServer) obj;
		if (batchInputWriter == null) {
			if (other.batchInputWriter != null)
				return false;
		} else if (!batchInputWriter.equals(other.batchInputWriter))
			return false;
		if (batchOutputReader == null) {
			if (other.batchOutputReader != null)
				return false;
		} else if (!batchOutputReader.equals(other.batchOutputReader))
			return false;
		if (isRunning != other.isRunning)
			return false;
		if (jarFile == null) {
			if (other.jarFile != null)
				return false;
		} else if (!jarFile.equals(other.jarFile))
			return false;
		if (listener == null) {
			if (other.listener != null)
				return false;
		} else if (!listener.equals(other.listener))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pid != other.pid)
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (propertiesFile == null) {
			if (other.propertiesFile != null)
				return false;
		} else if (!propertiesFile.equals(other.propertiesFile))
			return false;
		if (ram == null) {
			if (other.ram != null)
				return false;
		} else if (!ram.equals(other.ram))
			return false;
		if (serverBuild == null) {
			if (other.serverBuild != null)
				return false;
		} else if (!serverBuild.equals(other.serverBuild))
			return false;
		if (serverProcess == null) {
			if (other.serverProcess != null)
				return false;
		} else if (!serverProcess.equals(other.serverProcess))
			return false;
		if (serverReadThread == null) {
			if (other.serverReadThread != null)
				return false;
		} else if (!serverReadThread.equals(other.serverReadThread))
			return false;
		if (waitForServerExitThread == null) {
			if (other.waitForServerExitThread != null)
				return false;
		} else if (!waitForServerExitThread.equals(other.waitForServerExitThread))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BatchServer [batchFile=" + jarFile + ", propertiesFile=" + propertiesFile + ", serverBuild="
				+ serverBuild + ", serverProcess=" + serverProcess + ", batchOutputReader=" + batchOutputReader
				+ ", batchInputWriter=" + batchInputWriter + ", listener=" + listener + ", serverReadThread="
				+ serverReadThread + ", waitForServerExitThread=" + waitForServerExitThread + ", name=" + name
				+ ", isRunning=" + isRunning + "]";
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void onError(Exception errorMessage) {

		listener.forEach(serverListener -> {
			serverListener.serverReturnMessage("Error while server start");
		});

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

}