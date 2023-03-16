package net.azureaaron.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface MouseEvent {
	Event<MouseEvent> EVENT = EventFactory.createArrayBacked(MouseEvent.class,
			(listeners) -> (button, action, mods) -> {
				for(MouseEvent listener : listeners) {
					ActionResult result = listener.onMouse(button, action, mods);
					if(result != ActionResult.PASS) return result;
				}
				return ActionResult.PASS;
			});
	ActionResult onMouse(int button, int action, int mods);
}
