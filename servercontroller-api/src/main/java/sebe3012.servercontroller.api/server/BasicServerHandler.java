package sebe3012.servercontroller.api.server;

import sebe3012.servercontroller.api.util.DialogUtil;
import sebe3012.servercontroller.api.util.ErrorCode;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

public class BasicServerHandler implements BasicServer.StopListener {

	private BasicServer server;
	private boolean restartServer = false;
	private static ResourceBundle bundle = ResourceBundle.getBundle("lang/basic_server_handler/lang");

	public BasicServerHandler(@NotNull BasicServer server) {
		this.server = server;
		this.server.addStopListener(this);
	}

	public void startServer() {
		if (!server.isRunning()) {
			ErrorCode code = server.start();
			if (code != ErrorCode.SUCCESSFUL) {
				if (code == ErrorCode.FILE_NOT_FOUND_ERROR) {
					DialogUtil.showErrorAlert("", bundle.getString("dialog_file_not_found"), false);
				} else {
					DialogUtil.showErrorAlert("", code.toString(), false);
				}
			}
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
}
