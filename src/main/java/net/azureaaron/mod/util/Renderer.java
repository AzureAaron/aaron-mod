package net.azureaaron.mod.util;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Renderer {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	
	public static void renderBox(WorldRenderContext wrc, Box box, float red, float green, float blue, float alpha) {
		renderBox(wrc, box, 3f, red, green, blue, alpha);
	}
	
	public static void renderBox(WorldRenderContext wrc, Box box, float lineWidth, float red, float green, float blue, float alpha) {
		MatrixStack matrices = MatrixTransformer.CAMERA_RELATIVE.transform(wrc, null);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder buffer = tessellator.getBuffer();
		
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.lineWidth(lineWidth);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		
		buffer.begin(DrawMode.LINES, VertexFormats.LINES);
		WorldRenderer.drawBox(matrices, buffer, box, red / 255f, green / 255f, blue / 255f, alpha);
		tessellator.draw();
		
		matrices.pop();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableCull();
		RenderSystem.disableDepthTest();
	}
	
	public static void renderFilledBox(WorldRenderContext wrc, Vec3d pos, float red, float green, float blue, float alpha) {
		MatrixStack matrices = MatrixTransformer.CAMERA_RELATIVE.transform(wrc, null);		
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
		WorldRenderer.renderFilledBox(matrices, buffer, pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1, red / 255f, green / 255f, blue / 255f, alpha);
		tessellator.draw();
		
		matrices.pop();
		RenderSystem.polygonOffset(0f, 0f);
		RenderSystem.disablePolygonOffset();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
	}
	
	public static void renderHalfFilledBox(WorldRenderContext wrc, Vec3d pos, float red, float green, float blue, float alpha, boolean secondHalf) {
		double x = pos.x;
		double y = pos.y;
		double z = pos.z;
		
		if (secondHalf) z += 0.5f;
		
		MatrixStack matrices = MatrixTransformer.CAMERA_RELATIVE.transform(wrc, null);
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
	
	public static void renderText(WorldRenderContext wrc, Vec3d pos, OrderedText text, boolean seeThrough) {
		renderText(wrc, pos, text, seeThrough, 8);
	}

	/**
	 * Call from {@link net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents#END} if the text should be rendered infront
	 * of entities - {@code GL_ALWAYS} isn't enough for some reason
	 */
	public static void renderText(WorldRenderContext wrc, Vec3d pos, OrderedText text, boolean seeThrough, float scale) {
		MatrixStack matrices = MatrixTransformer.POSITION_RELATIVE.transform(wrc, pos);
		TextRenderer textRenderer = CLIENT.textRenderer;
		
		scale *= 0.025f;
		
		matrices.peek().getPositionMatrix().mul(RenderSystem.getModelViewMatrix());
		matrices.multiply(wrc.camera().getRotation());
		matrices.scale(-scale, -scale, scale);
		
		Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
		float xOffset = -textRenderer.getWidth(text) / 2f;
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder buffer = tessellator.getBuffer();
		VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(buffer);
		
		RenderSystem.depthFunc(seeThrough ? GL11.GL_ALWAYS : GL11.GL_LEQUAL);
		
		textRenderer.draw(text, xOffset, 0, 0xFFFFFFFF, false, positionMatrix, consumers, TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		consumers.draw();
		
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		matrices.pop();
	}
	
	/**
	 * Matrix Transformation Utility
	 * 
	 * @implNote Each default operation automatically pushes a copy of the top entry onto the stack
	 */
	@FunctionalInterface
	interface MatrixTransformer {
		/**
		 * Transforms the matrices to be relative to the camera
		 * 
		 * @implNote The {@code pos} is unused and should be left as null
		 */
		MatrixTransformer CAMERA_RELATIVE = (wrc, pos) -> {
			Vec3d camera = wrc.camera().getPos();
			MatrixStack matrices = wrc.matrixStack();
			
			matrices.push();
			matrices.translate(-camera.x, -camera.y, -camera.z);
			
			return matrices;
		};
		
		/**
		 * Transforms the matrices relative to the {@code pos} and camera position
		 * 
		 * @implNote The {@code pos} cannot be null
		 */
		MatrixTransformer POSITION_RELATIVE = (wrc, pos) -> {
			Vec3d camera = wrc.camera().getPos();
			MatrixStack matrices = wrc.matrixStack();
			Vec3d transformedPosition = pos.subtract(camera);
			
			matrices.push();
			matrices.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);
			
			return matrices;
		};
		
		MatrixStack transform(WorldRenderContext wrc, @Nullable Vec3d pos);
	}
}
