package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.systems.RenderPass;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.util.Window;

public class GuiHelper {
	private static final ScissorState BLUR_SCISSOR_STATE = new ScissorState();

	/**
	 * Allows for applying scissoring to the screen background blur effect.
	 */
	public static void enableBlurScissor(int x, int y, int width, int height) {
		BLUR_SCISSOR_STATE.enable(x, y, width, height);
	}

	public static void disableBlurScissor() {
		BLUR_SCISSOR_STATE.disable();
	}

	public static void applyBlurScissorToRenderPass(RenderPass renderPass) {
		if (BLUR_SCISSOR_STATE.method_72091()) {
			Window window = MinecraftClient.getInstance().getWindow();
			int framebufferHeight = window.getFramebufferHeight();
			double scaleFactor = window.getScaleFactor();

			double x = BLUR_SCISSOR_STATE.method_72092() * scaleFactor;
			double y = framebufferHeight - (BLUR_SCISSOR_STATE.method_72093() + BLUR_SCISSOR_STATE.method_72095()) * scaleFactor;
			double width = BLUR_SCISSOR_STATE.method_72094() * scaleFactor;
			double height = BLUR_SCISSOR_STATE.method_72095() * scaleFactor;

			renderPass.enableScissor((int) x, (int) y, Math.max(0, (int) width), Math.max(0, (int) height));
		}
	}
}
