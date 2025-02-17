package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface PingResultCallback {
	Event<PingResultCallback> EVENT = EventFactory.createArrayBacked(PingResultCallback.class,
			listeners -> ping -> {
				for (PingResultCallback listener : listeners) {
					listener.onPingResult(ping);
				}
			});

	void onPingResult(long ping);
}
