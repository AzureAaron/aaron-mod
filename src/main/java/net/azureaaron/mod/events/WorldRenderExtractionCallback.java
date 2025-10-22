package net.azureaaron.mod.events;

import net.azureaaron.mod.utils.render.primitive.PrimitiveCollector;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface WorldRenderExtractionCallback {
	Event<WorldRenderExtractionCallback> EVENT = EventFactory.createArrayBacked(WorldRenderExtractionCallback.class, callbacks -> collector -> {
		for (WorldRenderExtractionCallback callback : callbacks) {
			callback.onExtract(collector);
		}
	});

	void onExtract(PrimitiveCollector collector);
}
