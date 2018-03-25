package se7kn8.servercontroller.rest;

import io.javalin.embeddedserver.jetty.websocket.WebSocketHandler;
import io.javalin.embeddedserver.jetty.websocket.WsSession;
import se7kn8.servercontroller.api.server.BasicServer;
import se7kn8.servercontroller.api.server.BasicServerHandler;
import se7kn8.servercontroller.rest.authentication.ApiKeyManager;
import se7kn8.servercontroller.server.ServerManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerLogWebSocket {

	private final ConcurrentMap<String, ServerLogListener> map = new ConcurrentHashMap<>();

	private class ServerLogListener implements BasicServer.MessageListener {

		private WsSession session;

		public ServerLogListener(@NotNull WsSession session) {
			this.session = session;
		}

		@Override
		public void onMessage(String message) {
			try {
				session.getRemote().sendString(message);
			} catch (Exception e) {
				log.error("Unexpected error", e);
			}
		}
	}

	private Logger log = LogManager.getLogger();
	private ApiKeyManager apiKeyManager;
	private ServerManager manager;

	public ServerLogWebSocket(@NotNull ApiKeyManager apiKeyManager, @NotNull ServerManager manager, @NotNull WebSocketHandler handler) {
		this.apiKeyManager = apiKeyManager;
		this.manager = manager;
		handler.onConnect(this::handleConnect);
		handler.onMessage(this::handleMessage);
		handler.onClose(this::handleClose);
		handler.onError(this::handleException);
	}

	private void handleConnect(WsSession session) throws Exception {
		Optional<BasicServerHandler> optionalHandler = manager.findServerByID(session.param("id"));
		if (!optionalHandler.isPresent()) {
			session.close(1001, "Server not found");
		} else {
			//TODO replace this with websocket-access-manager in future javalin version
			if(apiKeyManager.canExecute(session.header("token"), "servercontroller.server.log")){
				ServerLogListener listener = new ServerLogListener(session);
				optionalHandler.get().getServer().addMessageListener(listener);
				map.put(session.getId(), listener);
			}
		}
	}

	private void handleMessage(WsSession session, String msg) throws Exception {
		//For future use
	}

	private void handleClose(WsSession session, int statusCode, String reason) throws Exception {
		Optional<BasicServerHandler> optionalHandler = manager.findServerByID(session.param("id"));
		if (!optionalHandler.isPresent()) {
			session.close(1001, "Server not found");
		} else {
			ServerLogListener listener = map.get(session.getId());
			optionalHandler.get().getServer().removeMessageListener(listener);
		}
	}

	private void handleException(WsSession session, Throwable throwable) throws Exception {
		//For future use
	}

}
