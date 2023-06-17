package net.azureaaron.mod.features;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BoundingBoxes {
	private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
	
	public enum Dragons {
		POWER(new BlockPos(13, 5, 45), new BlockPos(41, 34, 72), 224f, 43f, 43f),
		FLAME(new BlockPos(71, 5, 45), new BlockPos(102, 34, 72), 232f, 124f, 70f),
		APEX(new BlockPos(13, 5, 80), new BlockPos(41, 34, 107), 22f, 138f, 22f),
		ICE(new BlockPos(71, 5, 80), new BlockPos(102, 34, 107), 24f, 210f, 219f),
		SOUL(new BlockPos(41, 5, 112), new BlockPos(71, 34, 145), 141f, 24f, 219f);
		
		public final BlockPos pos1;
		public final BlockPos pos2;
		public final float red;
		public final float green;
		public final float blue;
		
		private Dragons(BlockPos pos1, BlockPos pos2, float red, float green, float blue) {
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.red = red * 255f;
			this.green = green * 255f;
			this.blue = blue * 255f;
		}
	}
	
	public static void renderBoxes(WorldRenderContext wrc) {
		if(Functions.isOnHypixel() && Config.masterModeF7DragonBoxes && Cache.inM7Phase5) {
			for(Dragons dragon : Dragons.values()) {
				Box box = new Box(dragon.pos1, dragon.pos2);
				Vec3d camera = minecraftClient.getCameraEntity().getCameraPosVec(wrc.tickDelta());
				MatrixStack matrices = wrc.matrixStack();
				
				matrices.push();
				matrices.translate(-camera.x, -camera.y, -camera.z);
				
				Tessellator tessellator = RenderSystem.renderThreadTesselator();
				BufferBuilder buffer = tessellator.getBuffer();
				
				RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				RenderSystem.lineWidth(3f);
				RenderSystem.disableCull();
				
				buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
				WorldRenderer.drawBox(matrices, buffer, box, dragon.red, dragon.green, dragon.blue, 1f);
				tessellator.draw();
				
				matrices.pop();
				RenderSystem.lineWidth(1f);
				RenderSystem.enableCull();
			}
		}
	}
}
