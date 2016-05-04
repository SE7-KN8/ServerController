package sebe3012.servercontroller.eventbus;

import com.google.common.eventbus.EventBus;

public class EventHandler {

	private static EventBus eventBus;

	public static final EventHandler EVENT_BUS = new EventHandler();

	public void loadEventbus(String busName) {
		eventBus = new EventBus(busName);
	}

	public void registerEventListener(IEventHandler handler) {
		System.out.println("[EventHandler] Register" + handler.getClass().getSimpleName());
		eventBus.register(handler);
	}

	public void unregisterEventListener(IEventHandler handler) {
		System.out.println("[EventHandler] Unregister" + handler.getClass().getSimpleName());
		eventBus.unregister(handler);
	}

	public void post(IEvent event) {
		System.out.println("[EventHandler] Post " + event.getClass().getSimpleName());
		eventBus.post(event);
	}

}
