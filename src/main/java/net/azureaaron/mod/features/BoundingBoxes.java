package net.azureaaron.mod.features;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.azureaaron.mod.util.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BoundingBoxes {
	
	public enum Dragons {
		POWER(new BlockPos(13, 5, 45), new BlockPos(41, 34, 72), 0xe02b2b),
		FLAME(new BlockPos(71, 5, 45), new BlockPos(102, 34, 72), 0xe87c46),
		APEX(new BlockPos(13, 5, 80), new BlockPos(41, 34, 107), 0x168a16),
		ICE(new BlockPos(71, 5, 80), new BlockPos(102, 34, 107), 0x18d2db),
		SOUL(new BlockPos(41, 5, 112), new BlockPos(71, 34, 145), 0x8d18db);
		
		public final BlockPos pos1;
		public final BlockPos pos2;
		public final Box box;
		
		public final int colour;
		public final float red;
		public final float green;
		public final float blue;
		
		private Dragons(BlockPos pos1, BlockPos pos2, int colour) {
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.box = Box.enclosing(pos1, pos2);
			
			this.colour = colour;
			this.red = (colour >> 16) & 0xFF;
			this.green = (colour >> 8) & 0xFF;
			this.blue = colour & 0xFF;
		}
	}
	
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
