package net.azureaaron.mod.features;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.WorldRenderExtractionCallback;
import net.azureaaron.mod.utils.Cache;
import net.azureaaron.mod.utils.Functions;
import net.azureaaron.mod.utils.render.primitive.PrimitiveCollector;
import net.minecraft.core.BlockPos;

public class M7Waypoints {
	private static final float ALPHA = 0.5f;

	// Shoot Spots
	private static final BlockPos BLUE_SHOOT = new BlockPos(85, 20, 94);
	private static final BlockPos GREEN_SHOOT = new BlockPos(27, 20, 94);
	private static final BlockPos ORANGE_SHOOT = new BlockPos(84, 20, 56);
	private static final BlockPos PURPLE_SHOOT = new BlockPos(56, 20, 124);
	private static final BlockPos RED_SHOOT = new BlockPos(27, 20, 59);

	//Stack Spots
	private static final BlockPos ORANGE_STACK = new BlockPos(48, 4, 80); // 45, 4, 82 or 48, 4, 80
	private static final BlockPos BLUE_STACK = new BlockPos(41, 5, 108); // 41, 5, 108
	private static final BlockPos PURPLE_STACK = new BlockPos(24, 5, 92); // 24, 5, 92
	private static final BlockPos PURPLE_STACK_ALT = new BlockPos(88, 5, 92); // 88, 5, 92
	private static final BlockPos GREEN_STACK = new BlockPos(62, 5, 109); // 66, 5, 109 or 62, 5, 109
	private static final BlockPos RED_STACK = new BlockPos(16, 5, 89); //16, 5, 89

	@Init
	public static void init() {
		WorldRenderExtractionCallback.EVENT.register(M7Waypoints::extractRendering);
	}

	private static void extractRendering(PrimitiveCollector collector) {
		if (Functions.isOnHypixel() && Cache.inM7Phase5) {
			if (AaronModConfigManager.get().skyblock.m7.dragonAimWaypoints) {
				collector.submitFilledBox(BLUE_SHOOT, Dragons.ICE.colourComponents, ALPHA, false);
				collector.submitFilledBox(GREEN_SHOOT, Dragons.APEX.colourComponents, ALPHA, false);
				collector.submitFilledBox(ORANGE_SHOOT, Dragons.FLAME.colourComponents, ALPHA, false);
				collector.submitFilledBox(PURPLE_SHOOT, Dragons.SOUL.colourComponents, ALPHA, false);
				collector.submitFilledBox(RED_SHOOT, Dragons.POWER.colourComponents, ALPHA, false);
			}

			if (AaronModConfigManager.get().skyblock.m7.arrowStackWaypoints) {
				collector.submitFilledBox(ORANGE_STACK, Dragons.FLAME.colourComponents, ALPHA, false);
				collector.submitFilledBox(BLUE_STACK, Dragons.ICE.colourComponents, ALPHA, false);
				collector.submitFilledBox(PURPLE_STACK, Dragons.SOUL.colourComponents, ALPHA, false);
				collector.submitFilledBox(PURPLE_STACK_ALT, Dragons.SOUL.colourComponents, ALPHA, false);
				collector.submitFilledBox(GREEN_STACK, Dragons.APEX.colourComponents, ALPHA, false);
				collector.submitFilledBox(RED_STACK, Dragons.POWER.colourComponents, ALPHA, false);
			}
		}
	}
}
