package se7kn8.servercontroller.api.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class NamedServer implements BasicServer {

	private static Logger log = LogManager.getLogger();
	private String name;
	private String addonID;
	private String serverCreatorID;
	private Map<String, String> properties;
	private ServerState state = ServerState.STOPPED;
	private List<MessageListener> messageListeners = new ArrayList<>();
	private List<StopListener> stopListeners = new ArrayList<>();
	private List<StateListener> stateListeners = new ArrayList<>();

	private static final int LATEST_LOG_SIZE = 100; //TODO make it changeable
	private List<String> latestLog = new ArrayList<>();

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		this.properties = properties;
		name = properties.get("name");
	}

	@NotNull
	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	@NotNull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setServerCreatorInfo(@NotNull String addonID, @NotNull String serverCreatorID) {
		log.info("[{}] Set server creator info to addon '{}' and server creator '{}'", name, addonID, serverCreatorID);
		this.addonID = addonID;
		this.serverCreatorID = serverCreatorID;
	}

	@NotNull
	@Override
	public String getServerCreatorID() {
		return serverCreatorID;
	}

	@NotNull
	@Override
	public String getAddonID() {
		return addonID;
	}

	@NotNull
	@Override
	public ServerState getState() {
		return state;
	}

	@Override
	public boolean isRunning() {
		return state != ServerState.STOPPED;
	}

	@Override
	public void addMessageListener(@NotNull MessageListener listener) {
		messageListeners.add(listener);
	}

	@Override
	public void addStopListener(@NotNull StopListener listener) {
		stopListeners.add(listener);
	}

	@Override
	public void removeMessageListener(@NotNull MessageListener listener) {
		messageListeners.remove(listener);
	}

	@Override
	public void removeStopListener(@NotNull StopListener listener) {
		stopListeners.remove(listener);
	}

	@Override
	public void addStateListener(@NotNull StateListener listener) {
		stateListeners.add(listener);
	}

	@Override
	public void removeStateListener(@NotNull StateListener listener) {
		stateListeners.remove(listener);
	}

	@NotNull
	@Override
	public List<String> getLatestLog() {
		return latestLog;
	}

	protected void sendStop(int code) {
		log.info("[{}] Stopped with code: {}", name, code);
		stopListeners.forEach(listener -> listener.onStop(code));
	}

	protected void sendLine(@NotNull String message) {
		if (latestLog.size() >= NamedServer.LATEST_LOG_SIZE) {
			latestLog.remove(latestLog.size() - 1);
		}
		latestLog.add(0, message);
		messageListeners.forEach(listener -> listener.onMessage(message));
	}

	protected void setState(@NotNull ServerState state) {
		log.info("[{}] Set state from {} to {}", name, this.state, state);
		stateListeners.forEach(listener -> listener.onStateChange(state));
		this.state = state;
	}

	protected void onError(@NotNull Exception errorMessage) {
		for (MessageListener listener : messageListeners) {
			StringWriter sw = new StringWriter();
			PrintWriter ps = new PrintWriter(sw);
			errorMessage.printStackTrace(ps);
			listener.onMessage("Error while server run: " + sw.toString());
		}
	}

}
