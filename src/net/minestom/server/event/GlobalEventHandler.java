package net.minestom.server.event;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minestom.server.event.handler.EventHandler;

/**
 * Object containing all the global event listeners.
 */
@SuppressWarnings("rawtypes")
public final class GlobalEventHandler implements EventHandler {

    // Events
    private final Map<Class<? extends Event>, Collection<EventCallback>> eventCallbacks = new ConcurrentHashMap<>();

    @Override
    public Map<Class<? extends Event>, Collection<EventCallback>> getEventCallbacksMap() {
        return eventCallbacks;
    }

	@Override
	public <E extends Event> void runEvent(Collection<EventCallback> eventCallbacks, E event) {
	}
}
