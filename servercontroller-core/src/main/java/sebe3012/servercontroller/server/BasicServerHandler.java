package sebe3012.servercontroller.server;

import org.jetbrains.annotations.NotNull;

public class BasicServerHandler implements BasicServer.StopListener {

	private BasicServer server;
	private ServerManager serverManager;
	private boolean restartServer = false;


	public BasicServerHandler(@NotNull BasicServer server, ServerManager manager) {
		this.server = server;
		this.server.addStopListener(this);
		this.serverManager = manager;
	}

	public void startServer() {
		if (!server.isRunning()) {
			server.start();
		}
		restartServer = false;
	}

	public void stopServer() {
		if (server.isRunning()) {
			server.stop();
		}
		restartServer = false;
	}

	public void restartServer() {
		if (server.isRunning()) {
			server.stop();
		}
		restartServer = true;
	}

	@Override
	public void onStop(int code) {
		if (restartServer) {
			startServer();
		}
	}

	public void sendCommand(@NotNull String command) {
		if (server.isRunning()) {
			server.sendCommand(command);
		}
	}

	@NotNull
	public BasicServer getServer() {
		return server;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}
}
