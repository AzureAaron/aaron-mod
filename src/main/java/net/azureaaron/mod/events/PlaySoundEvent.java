package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.ActionResult;

public interface PlaySoundEvent {
	Event<PlaySoundEvent> EVENT = EventFactory.createArrayBacked(PlaySoundEvent.class,
			(listeners) -> (packet) -> {
				for(PlaySoundEvent listener : listeners) {
					ActionResult result = listener.onPlaySound(packet);
					if(result != ActionResult.PASS) return result;
				}
				return ActionResult.PASS;
			});
	ActionResult onPlaySound(PlaySoundS2CPacket packet);
}
