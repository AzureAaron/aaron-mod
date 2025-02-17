package net.azureaaron.mod.utils.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Renderer {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final BufferAllocator ALLOCATOR = new BufferAllocator(1536);

	public static void renderBox(WorldRenderContext wrc, Box box, float red, float green, float blue, float alpha) {
		renderBox(wrc, box, 3f, red, green, blue, alpha);
	}

	public static void renderBox(WorldRenderContext wrc, Box box, float lineWidth, float red, float green, float blue, float alpha) {
		MatrixStack matrices = MatrixTransformer.CAMERA_RELATIVE.transform(wrc, null);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();

		RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.lineWidth(lineWidth);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);

		BufferBuilder buffer = tessellator.begin(DrawMode.LINES, VertexFormats.LINES);
		VertexRendering.drawBox(matrices, buffer, box, red / 255f, green / 255f, blue / 255f, alpha);
		BufferRenderer.drawWithGlobalProgram(buffer.end());

		matrices.pop();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableCull();
		RenderSystem.disableDepthTest();
	}

	public static void renderFilledBox(WorldRenderContext wrc, Vec3d pos, float red, float green, float blue, float alpha) {
		MatrixStack matrices = MatrixTransformer.CAMERA_RELATIVE.transform(wrc, null);		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();

		RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.polygonOffset(-1f, -10f);
		RenderSystem.enablePolygonOffset();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableCull();

		BufferBuilder buffer = tessellator.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
		VertexRendering.drawFilledBox(matrices, buffer, pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1, red / 255f, green / 255f, blue / 255f, alpha);
		BufferRenderer.drawWithGlobalProgram(buffer.end());

		matrices.pop();
		RenderSystem.polygonOffset(0f, 0f);
		RenderSystem.disablePolygonOffset();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.disableDepthTest();
	}

	public static void renderText(WorldRenderContext wrc, Vec3d pos, OrderedText text, boolean seeThrough) {
		renderText(wrc, pos, text, seeThrough, 8);
	}

	public static void renderText(WorldRenderContext wrc, Vec3d pos, OrderedText text, boolean seeThrough, float scale) {
		Matrix4f positionMatrix = new Matrix4f();
		Camera camera = wrc.camera();
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

			matrices.push();
			matrices.translate(pos.x - camera.x, pos.y - camera.y, pos.z - camera.z);

			return matrices;
		};

		MatrixStack transform(WorldRenderContext wrc, @Nullable Vec3d pos);
	}
}
