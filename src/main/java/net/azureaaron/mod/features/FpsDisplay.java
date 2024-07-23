package net.azureaaron.mod.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class FpsDisplay {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static void render(DrawContext context) {
		if (!CLIENT.getDebugHud().shouldShowDebugHud()) {
			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.scale(1, 1, 0);
			context.drawText(CLIENT.textRenderer, CLIENT.getCurrentFps() + " fps", 2, 2, 0xffffffff, false);
			matrices.pop();
		}
	}
}
