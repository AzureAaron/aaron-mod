package net.azureaaron.mod.utils.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Renderer {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final BufferAllocator ALLOCATOR = new BufferAllocator(1536);

	public static void renderBox(WorldRenderContext context, Box box, float red, float green, float blue, float alpha) {
		renderBox(context, box, 3f, red, green, blue, alpha);
	}

	public static void renderBox(WorldRenderContext context, Box box, float lineWidth, float red, float green, float blue, float alpha) {
		MatrixStack matrices = context.matrixStack();
		Vec3d camera = context.camera().getPos();

		matrices.push();
		matrices.translate(-camera.x, -camera.y, -camera.z);

		VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
		RenderLayer layer = AaronModRenderLayers.getLines(lineWidth);
		VertexConsumer buffer = consumers.getBuffer(layer);

		VertexRendering.drawBox(matrices, buffer, box, red / 255f, green / 255f, blue / 255f, alpha);
		consumers.draw(layer);

		matrices.pop();
	}

	public static void renderFilledBox(WorldRenderContext context, Vec3d pos, float red, float green, float blue, float alpha) {
		MatrixStack matrices = context.matrixStack();
		Vec3d camera = context.camera().getPos();

		matrices.push();
		matrices.translate(-camera.x, -camera.y, -camera.z);

		VertexConsumerProvider.Immediate consumers = (VertexConsumerProvider.Immediate) context.consumers();
		VertexConsumer buffer = consumers.getBuffer(AaronModRenderLayers.FILLED_BOX);

		VertexRendering.drawFilledBox(matrices, buffer, pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1, red / 255f, green / 255f, blue / 255f, alpha);
		consumers.draw(AaronModRenderLayers.FILLED_BOX);

		matrices.pop();
	}

	public static void renderText(WorldRenderContext context, Vec3d pos, OrderedText text, boolean seeThrough) {
		renderText(context, pos, text, seeThrough, 8);
	}

	public static void renderText(WorldRenderContext context, Vec3d pos, OrderedText text, boolean seeThrough, float scale) {
		Matrix4f positionMatrix = new Matrix4f();
		Camera camera = context.camera();
		TextRenderer textRenderer = CLIENT.textRenderer;

		scale *= 0.025f;

		Vec3d cameraPos = camera.getPos();

		positionMatrix
		.translate((float) (pos.getX() - cameraPos.getX()), (float) (pos.getY() - cameraPos.getY()), (float) (pos.getZ() - cameraPos.getZ()))
		.rotate(camera.getRotation())
		.scale(scale, -scale, scale);

		float xOffset = -textRenderer.getWidth(text) / 2f;

		VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(ALLOCATOR);

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(seeThrough ? GL11.GL_ALWAYS : GL11.GL_LEQUAL);

		textRenderer.draw(text, xOffset, 0, 0xFFFFFFFF, false, positionMatrix, consumers, seeThrough ? TextLayerType.SEE_THROUGH : TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		consumers.draw();

		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableDepthTest();
	}

	public static boolean pointIsInArea(double x, double y, double x1, double y1, double x2, double y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	/**
	 * A version of {@link RenderSystem#assertOnRenderThread()} that allows for a custom error message.
	 */
	public static void assertOnRenderThread(String message) {
		if (!RenderSystem.isOnRenderThread()) throw new IllegalStateException(message);
	}
}
