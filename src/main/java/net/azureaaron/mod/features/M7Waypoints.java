package net.azureaaron.mod.features;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.BoundingBoxes.Dragons;
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
import net.minecraft.util.math.Vec3d;

public class M7Waypoints {
	//Gyro Spots
	/*private static final BlockPos BLUE_GYRO = new BlockPos(83, 5, 102);
	private static final BlockPos GREEN_GYRO = new BlockPos(24, 5, 82);*/
	
	// Shoot Spots
	private static final BlockPos BLUE_SHOOT = new BlockPos(85, 20, 94);
	private static final BlockPos GREEN_SHOOT = new BlockPos(27, 20, 94);
	private static final BlockPos ORANGE_SHOOT = new BlockPos(84, 20, 56);
	private static final BlockPos PURPLE_SHOOT = new BlockPos(56, 20, 124);
	private static final BlockPos RED_SHOOT = new BlockPos(27, 20, 59);
	
	//Stack Spots
	private static final BlockPos ORANGE_STACK = new BlockPos(57, 4, 78);
	private static final BlockPos BLUE_STACK = new BlockPos(57, 4, 78);
	private static final BlockPos BLUE_STACK_ALT = new BlockPos(61, 6, 118);
	private static final BlockPos PURPLE_STACK = new BlockPos(27, 5, 94);
	private static final BlockPos PURPLE_STACK_ALT = new BlockPos(86, 5, 96);
	private static final BlockPos GREEN_STACK = new BlockPos(55, 6, 116);
	private static final BlockPos GREEN_STACK_ALT = new BlockPos(28, 5, 44);
	private static final BlockPos RED_STACK = new BlockPos(20, 5, 86);
	private static final BlockPos RED_STACK_ALT = new BlockPos(12, 6, 90);
	
	public static void renderWaypoints(WorldRenderContext wrc) {
		if (Functions.isOnHypixel() && Cache.inM7Phase5) {
			/*if (Config.m7GyroWaypoints) {
				renderFilled(wrc, BLUE_GYRO, 252f * 255f, 211f * 255f, 3 * 255f);
				renderFilled(wrc, GREEN_GYRO, 252f * 255f, 211f * 255f, 3 * 255f);
			}*/
			
			if (Config.m7ShootWaypoints) {
				renderFilled(wrc, BLUE_SHOOT, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue);
				renderFilled(wrc, GREEN_SHOOT, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue);
				renderFilled(wrc, ORANGE_SHOOT, Dragons.FLAME.red, Dragons.FLAME.green, Dragons.FLAME.blue);
				renderFilled(wrc, PURPLE_SHOOT, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue);
				renderFilled(wrc, RED_SHOOT, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue);
			}
			
			if (Config.m7StackWaypoints) {
				renderHalfFilled(wrc, ORANGE_STACK, Dragons.FLAME.red, Dragons.FLAME.green, Dragons.FLAME.blue, false);
				renderHalfFilled(wrc, BLUE_STACK, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue, true);
				renderFilled(wrc, BLUE_STACK_ALT, Dragons.ICE.red, Dragons.ICE.green, Dragons.ICE.blue);
				renderFilled(wrc, PURPLE_STACK, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue);
				renderFilled(wrc, PURPLE_STACK_ALT, Dragons.SOUL.red, Dragons.SOUL.green, Dragons.SOUL.blue);
				renderFilled(wrc, GREEN_STACK, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue);
				renderFilled(wrc, GREEN_STACK_ALT, Dragons.APEX.red, Dragons.APEX.green, Dragons.APEX.blue);
				renderFilled(wrc, RED_STACK, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue);
				renderFilled(wrc, RED_STACK_ALT, Dragons.POWER.red, Dragons.POWER.green, Dragons.POWER.blue);
			}
		}
	}
	
	private static void renderFilled(WorldRenderContext wrc, BlockPos location, float red, float green, float blue) {
		Vec3d camera = wrc.camera().getPos();
		MatrixStack matrices = wrc.matrixStack();
		
		float x = location.getX();
		float y = location.getY();
		float z = location.getZ();
		
		matrices.push();
		matrices.translate(-camera.x, -camera.y, -camera.z);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder buffer = tessellator.getBuffer();
		
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.polygonOffset(-1f, -10f);
		RenderSystem.enablePolygonOffset();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableCull();
		
		buffer.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
		WorldRenderer.renderFilledBox(matrices, buffer, x, y, z, x + 1, y + 1, z + 1, red / 255f, green / 255f, blue / 255f, 0.5f);
		tessellator.draw();
		
		matrices.pop();
		RenderSystem.polygonOffset(0f, 0f);
		RenderSystem.disablePolygonOffset();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
	}
	
	private static void renderHalfFilled(WorldRenderContext wrc, BlockPos location, float red, float green, float blue, boolean secondHalf) {
		Vec3d camera = wrc.camera().getPos();
		MatrixStack matrices = wrc.matrixStack();
		
		float x = location.getX();
		float y = location.getY();
		float z = location.getZ();
		
		if (secondHalf) {
			z += 0.5f;
		}
		
		matrices.push();
		matrices.translate(-camera.x, -camera.y, -camera.z);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder buffer = tessellator.getBuffer();
		
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.polygonOffset(-1f, -10f);
		RenderSystem.enablePolygonOffset();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableCull();
		
		buffer.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
		WorldRenderer.renderFilledBox(matrices, buffer, x, y, z, x + 1f, y + 1f, z + 0.5f, red / 255f, green / 255f, blue / 255f, 0.5f);
		tessellator.draw();
		
		matrices.pop();
		RenderSystem.polygonOffset(0f, 0f);
		RenderSystem.disablePolygonOffset();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
	}
}
