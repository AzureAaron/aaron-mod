package net.azureaaron.mod.features;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.WorldRenderExtractionCallback;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.render.primitive.PrimitiveCollector;

public class BoundingBoxes {
	
	@Init
	public static void init() {
		WorldRenderExtractionCallback.EVENT.register(BoundingBoxes::extractRendering);
	}
	
	private static void extractRendering(PrimitiveCollector collector) {
		if (Functions.isOnHypixel() && AaronModConfigManager.get().skyblock.m7.dragonBoundingBoxes && Cache.inM7Phase5) {
			for (Dragons dragon : Dragons.VALUES) {
				collector.submitOutlinedBox(dragon.box, dragon.colourComponents, 1f, 3f, false);
			}
		}
	}
}
