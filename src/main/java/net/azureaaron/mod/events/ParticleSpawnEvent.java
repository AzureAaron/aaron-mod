package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

@FunctionalInterface
public interface ParticleSpawnEvent {
	Event<ParticleSpawnEvent> EVENT = EventFactory.createArrayBacked(ParticleSpawnEvent.class,
			listeners -> packet -> {
				for (ParticleSpawnEvent listener: listeners) {
					listener.onParticleSpawn(packet);
				}
			});
	
	void onParticleSpawn(ParticleS2CPacket packet);
}
