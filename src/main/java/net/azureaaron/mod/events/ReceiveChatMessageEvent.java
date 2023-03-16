package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ReceiveChatMessageEvent {
	Event<ReceiveChatMessageEvent> EVENT = EventFactory.createArrayBacked(ReceiveChatMessageEvent.class,
			(listeners) -> (message, stringForm) -> {
				for(ReceiveChatMessageEvent listener : listeners) {
					ActionResult result = listener.onMessage(message, stringForm);
					if(result != ActionResult.PASS) return result;
				}
				return ActionResult.PASS;
			});
	ActionResult onMessage(Text message, String stringForm);
}
