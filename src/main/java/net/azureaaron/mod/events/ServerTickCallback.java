package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ServerTickCallback {
	Event<ServerTickCallback> EVENT = EventFactory.createArrayBacked(ServerTickCallback.class,
			listeners -> () -> {
				for (ServerTickCallback listener : listeners) {
					listener.onTick();
				}
			});

	void onTick();
}
