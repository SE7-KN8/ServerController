package sebe3012.servercontroller.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BatchServer {
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
	 * The server-port
	 */
	private int port;
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
						serverListener.serverReturnMessage(line);
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

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
			} catch (InterruptedException e) {
				e.printStackTrace();
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
	 *
	 * Return the server-port
	 *
	 * @return The server-port
	 */
	public int getPort() {
		return port;
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
		System.out.println(listener);
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
	public void start() throws IOException {
		serverReadThread = new ReadThread(serverReadThread);
		waitForServerExitThread = new WaitForExitThread(waitForServerExitThread);
		Scanner s = new Scanner(propertiesFile);
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.contains("server-port")) {
				port = Integer.valueOf(line.split("=")[1]);
			}
		}
		s.close();
		serverBuild = new ProcessBuilder("cmd", "/c", batchFile.getName());
		serverBuild.directory(batchFile.getParentFile());
		serverProcess = serverBuild.start();
		batchOutputReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
		batchInputWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
		serverReadThread.start();
		waitForServerExitThread.start();
	}

}
