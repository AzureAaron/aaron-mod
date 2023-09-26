package net.azureaaron.mod.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class FpsDisplay {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	
	public static void render(DrawContext context) {
		if(!client.getDebugHud().shouldShowDebugHud()) {
			MatrixStack matrices = context.getMatrices();
	        matrices.push();
	        matrices.translate(2, 2, 0);
	        matrices.scale(1, 1, 0);
	        matrices.translate(-2, -2, 0);
			context.drawText(client.textRenderer, String.valueOf(client.getCurrentFps()) + " fps", 2, 2, 0xffffff, false);
	        matrices.pop();
		}
	}
}
