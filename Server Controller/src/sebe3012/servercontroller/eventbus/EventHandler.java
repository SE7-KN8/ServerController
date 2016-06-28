package sebe3012.servercontroller.eventbus;

import com.google.common.eventbus.EventBus;

import sebe3012.servercontroller.ServerController;

public class EventHandler {

	private static EventBus eventBus;

	public static final EventHandler EVENT_BUS = new EventHandler();

	public void loadEventbus(String busName) {
		eventBus = new EventBus(busName);
	}

	public void registerEventListener(IEventHandler handler) {
		if (ServerController.DEBUG) {
			System.out.println("Register: " + handler.getClass().getSimpleName());
		}
		eventBus.register(handler);
	}

	public void unregisterEventListener(IEventHandler handler) {
		if (ServerController.DEBUG) {
			System.out.println("Unregister: " + handler.getClass().getSimpleName());
		}
		eventBus.unregister(handler);
	}

	public void post(IEvent event) {
		if (ServerController.DEBUG) {
			System.out.println("Post: " + event.getClass().getSimpleName());
		}
		eventBus.post(event);
	}

}
