package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

@FunctionalInterface
public interface PlaySoundEvent {
	Event<PlaySoundEvent> EVENT = EventFactory.createArrayBacked(PlaySoundEvent.class,
			listeners -> packet -> {
				for (PlaySoundEvent listener : listeners) {
					listener.onPlaySound(packet);
				}
			});

	void onPlaySound(PlaySoundS2CPacket packet);
}
