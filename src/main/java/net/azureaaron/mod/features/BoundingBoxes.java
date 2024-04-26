package net.azureaaron.mod.features;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class BoundingBoxes {
	
	public static void init() {
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(BoundingBoxes::renderBoxes);
	}
	
	private static void renderBoxes(WorldRenderContext wrc) {
		if (Functions.isOnHypixel() && AaronModConfigManager.get().masterModeF7DragonBoxes && Cache.inM7Phase5) {
			for (Dragons dragon : Dragons.values()) {
				Renderer.renderBox(wrc, dragon.box, dragon.red, dragon.green, dragon.blue, 1f);
			}
		}
	}
}
