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

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class BatchServer implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -1629588005985214485L;
	/**
	 * The batch file
	 */
	private File batchFile;
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
	 *
	 * The constructer for the batch server
	 *
	 * @param batch
	 *            The location of the batch file
	 * @param properties
	 *            The location of the properties file
	 * @param name
	 *            The name of the server
	 */
	public BatchServer(String batch, String properties, String name) {
		this.batchFile = new File(batch);
		this.propertiesFile = new File(properties);
		this.name = name;
		listener = new ArrayList<ServerListener>();
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
	public File getBatchFile() {
		return batchFile;
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
			properties = new PropertiesHandler(propertiesFile);
			properties.readProperties();
			serverBuild = new ProcessBuilder("cmd", "/c", batchFile.getName());
			serverBuild.directory(batchFile.getParentFile());
			serverProcess = serverBuild.start();
			batchOutputReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
			batchInputWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
			serverReadThread.start();
			waitForServerExitThread.start();
			isRunning = true;

			Field id = serverProcess.getClass().getDeclaredField("handle");
			id.setAccessible(true);
			System.out.println("[" + name + "] Handle: " + id.getLong(serverProcess));
		} catch (Exception e) {
			onError(e);
			e.printStackTrace();
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchFile == null) ? 0 : batchFile.hashCode());
		result = prime * result + (isRunning ? 1231 : 1237);
		result = prime * result + ((listener == null) ? 0 : listener.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((propertiesFile == null) ? 0 : propertiesFile.hashCode());
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
		BatchServer other = (BatchServer) obj;
		if (batchFile == null) {
			if (other.batchFile != null)
				return false;
		} else if (!batchFile.equals(other.batchFile))
			return false;
		if (isRunning != other.isRunning)
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
		if (propertiesFile == null) {
			if (other.propertiesFile != null)
				return false;
		} else if (!propertiesFile.equals(other.propertiesFile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BatchServer [batchFile=" + batchFile + ", propertiesFile=" + propertiesFile + ", serverBuild="
				+ serverBuild + ", serverProcess=" + serverProcess + ", batchOutputReader=" + batchOutputReader
				+ ", batchInputWriter=" + batchInputWriter + ", listener=" + listener + ", serverReadThread="
				+ serverReadThread + ", waitForServerExitThread=" + waitForServerExitThread + ", name=" + name
				+ ", isRunning=" + isRunning + "]";
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void onError(Exception errorMessage) {

		StringBuilder sb = new StringBuilder();
		sb.append(errorMessage.getLocalizedMessage() + "\n");

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
