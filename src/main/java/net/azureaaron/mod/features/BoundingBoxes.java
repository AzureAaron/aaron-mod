package net.azureaaron.mod.features;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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
			this.box = new Box(pos1, pos2);
			
			this.colour = colour;
			this.red = (colour >> 16) & 0xFF;
			this.green = (colour >> 8) & 0xFF;
			this.blue = colour & 0xFF;
		}
	}
	
	public static void renderBoxes(WorldRenderContext wrc) {
		if (Functions.isOnHypixel() && Config.masterModeF7DragonBoxes && Cache.inM7Phase5) {
			for (Dragons dragon : Dragons.values()) {
				Vec3d camera = wrc.camera().getPos();
				MatrixStack matrices = wrc.matrixStack();
				
				matrices.push();
				matrices.translate(-camera.x, -camera.y, -camera.z);
				
				Tessellator tessellator = RenderSystem.renderThreadTesselator();
				BufferBuilder buffer = tessellator.getBuffer();
				
				RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				RenderSystem.lineWidth(3f);
				RenderSystem.disableCull();
				RenderSystem.enableDepthTest();
				
				buffer.begin(DrawMode.LINES, VertexFormats.LINES);
				WorldRenderer.drawBox(matrices, buffer, dragon.box, dragon.red / 255f, dragon.green / 255f, dragon.blue / 255f, 1f);
				tessellator.draw();
				
				matrices.pop();
				RenderSystem.lineWidth(1f);
				RenderSystem.enableCull();
				RenderSystem.disableDepthTest();
			}
		}
	}
}
