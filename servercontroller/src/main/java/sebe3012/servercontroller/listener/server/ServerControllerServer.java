package sebe3012.servercontroller.listener.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerControllerServer {

	public class ClientHandler implements Runnable {

		private BufferedReader reader;

		public ClientHandler(Socket client) {
			try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				String message = reader.readLine();
				if (message == null) {
					return;
				}
				if (message.split(";")[0].equals("Restart Server")) {
					restartServer(message.split(";")[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class WaitForClients implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Socket client = server.accept();
					new Thread(new ClientHandler(client)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private ServerSocket server;
	private int port;

	public ServerControllerServer(int port) throws IOException {
		this.port = port;
		server = new ServerSocket(port);
		Thread waitForClients = new Thread(new WaitForClients());
		waitForClients.setName("");
	}

	public int getPort() {
		return port;
	}

	public void closeServer() {
		try {
			this.server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void restartServer(String serverID) {
		/*Tabs.servers.forEach((id, server) -> {
			if (server.hasServer()) {
				if (server.getServer().getName().equalsIgnoreCase(serverID)) {
					server.onRestartClicked();
				}
			}
		});*/

	}

}
