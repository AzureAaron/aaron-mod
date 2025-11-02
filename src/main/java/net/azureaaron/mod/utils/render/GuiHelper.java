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
		if (BLUR_SCISSOR_STATE.isEnabled()) {
			Window window = MinecraftClient.getInstance().getWindow();
			int framebufferHeight = window.getFramebufferHeight();
			double scaleFactor = window.getScaleFactor();

			double x = BLUR_SCISSOR_STATE.getX() * scaleFactor;
			double y = framebufferHeight - (BLUR_SCISSOR_STATE.getY() + BLUR_SCISSOR_STATE.getHeight()) * scaleFactor;
			double width = BLUR_SCISSOR_STATE.getWidth() * scaleFactor;
			double height = BLUR_SCISSOR_STATE.getHeight() * scaleFactor;

			renderPass.enableScissor((int) x, (int) y, Math.max(0, (int) width), Math.max(0, (int) height));
		}
	}
}
