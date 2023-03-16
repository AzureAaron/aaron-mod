package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.util.ActionResult;

public interface TeamUpdateEvent {
	Event<TeamUpdateEvent> EVENT = EventFactory.createArrayBacked(TeamUpdateEvent.class,
			(listeners) -> (packet) -> {
				for(TeamUpdateEvent listener : listeners) {
					ActionResult result = listener.onTeamUpdate(packet);
					if(result != ActionResult.PASS) return result;
				}
				return ActionResult.PASS;
			});
	ActionResult onTeamUpdate(TeamS2CPacket packet);
}
