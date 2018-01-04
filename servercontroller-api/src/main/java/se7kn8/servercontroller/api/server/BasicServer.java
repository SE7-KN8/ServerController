package se7kn8.servercontroller.api.server;

import se7kn8.servercontroller.api.util.ErrorCode;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface BasicServer {

	@FunctionalInterface
	interface StopListener {
		void onStop(int code);
	}

	@FunctionalInterface
	interface MessageListener {
		void onMessage(String message);
	}

	@FunctionalInterface
	interface StateListener {
		void onStateChange(ServerState newState);
	}

	void initialize(@NotNull Map<String, String> properties);

	@NotNull
	Map<String, String> getProperties();

	void setServerCreatorInfo(@NotNull String addonID, @NotNull String serverCreatorID);

	void addStopListener(@NotNull StopListener listener);

	void removeStopListener(@NotNull StopListener listener);

	void addMessageListener(@NotNull MessageListener listener);

	void removeMessageListener(@NotNull MessageListener listener);

	void addStateListener(@NotNull StateListener listener);

	void removeStateListener(@NotNull StateListener listener);

	@NotNull
	String getAddonID();

	@NotNull
	String getServerCreatorID();

	@NotNull
	ErrorCode start();

	@NotNull
	ErrorCode stop();

	boolean isRunning();

	void sendCommand(@NotNull String command);

	@NotNull
	String getName();

	int getSaveVersion();

	@NotNull
	ServerState getState();

	@NotNull
	default List<String> getServerInformation() {
		//Do nothing by default
		return new ArrayList<>();
	}

	@NotNull
	default List<Node> getControls() {
		//Do nothing by default
		return new ArrayList<>();
	}
}
