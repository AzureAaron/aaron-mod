package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface PingResultEvent {
	Event<PingResultEvent> EVENT = EventFactory.createArrayBacked(PingResultEvent.class,
			listeners -> ping -> {
				for (PingResultEvent listener : listeners) {
					listener.onPingResult(ping);
				}
			});
			
	void onPingResult(long ping);
}
