package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;

@FunctionalInterface
public interface TeamUpdateEvent {
	Event<TeamUpdateEvent> EVENT = EventFactory.createArrayBacked(TeamUpdateEvent.class,
			listeners -> packet -> {
				for (TeamUpdateEvent listener : listeners) {
					listener.onTeamUpdate(packet);
				}
			});
	
	void onTeamUpdate(TeamS2CPacket packet);
}
