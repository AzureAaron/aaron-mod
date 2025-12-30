package net.azureaaron.mod.utils.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.ScissorState;
import net.minecraft.client.Minecraft;

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
		if (BLUR_SCISSOR_STATE.enabled()) {
			Window window = Minecraft.getInstance().getWindow();
			int framebufferHeight = window.getHeight();
			double scaleFactor = window.getGuiScale();

			double x = BLUR_SCISSOR_STATE.x() * scaleFactor;
			double y = framebufferHeight - (BLUR_SCISSOR_STATE.y() + BLUR_SCISSOR_STATE.height()) * scaleFactor;
			double width = BLUR_SCISSOR_STATE.width() * scaleFactor;
			double height = BLUR_SCISSOR_STATE.height() * scaleFactor;

			renderPass.enableScissor((int) x, (int) y, Math.max(0, (int) width), Math.max(0, (int) height));
		}
	}
}
