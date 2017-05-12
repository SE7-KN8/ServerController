package sebe3012.servercontroller.eventbus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;

public class EventHandler {

	private static EventBus eventBus;

	public static final EventHandler EVENT_BUS = new EventHandler();

	private static final Logger log = LogManager.getLogger();

	public void loadEventbus(String busName) {
		eventBus = new EventBus(busName);
	}

	public void registerEventListener(IEventHandler handler) {
		log.trace("Register: {}", handler.getClass().getSimpleName());
		eventBus.register(handler);
	}

	public void unregisterEventListener(IEventHandler handler) {
		log.trace("Unregister: {}", handler.getClass().getSimpleName());
		eventBus.unregister(handler);
	}

	public void post(IEvent event) {
		log.trace("Post: {}", event.getClass().getSimpleName());
		eventBus.post(event);
	}
}
