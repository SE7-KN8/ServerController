package sebe3012.servercontroller.api.server;

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
	protected List<MessageListener> getMessageListeners() {
		return messageListeners;
	}

	@NotNull
	protected List<StopListener> getStopListeners() {
		return stopListeners;
	}

	@NotNull
	protected List<StateListener> getStateListeners() {
		return stateListeners;
	}

	protected void setState(@NotNull ServerState state) {
		log.info("[{}] Set state from {} to {}", name, this.state, state);
		getStateListeners().forEach(listener -> listener.onStateChange(state));
		this.state = state;
	}

	protected void onError(@NotNull Exception errorMessage) {
		for (MessageListener listener : getMessageListeners()) {
			StringWriter sw = new StringWriter();
			PrintWriter ps = new PrintWriter(sw);
			errorMessage.printStackTrace(ps);
			listener.onMessage("Error while server run: " + sw.toString());
		}
	}

}
