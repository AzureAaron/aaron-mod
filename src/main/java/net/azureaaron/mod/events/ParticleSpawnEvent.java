package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;

@FunctionalInterface
public interface ParticleSpawnEvent {
	Event<ParticleSpawnEvent> EVENT = EventFactory.createArrayBacked(ParticleSpawnEvent.class,
			listeners -> packet -> {
				for (ParticleSpawnEvent listener: listeners) {
					listener.onParticleSpawn(packet);
				}
			});

	void onParticleSpawn(ClientboundLevelParticlesPacket packet);
}
