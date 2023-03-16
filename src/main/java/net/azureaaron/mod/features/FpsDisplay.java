package net.azureaaron.mod.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class FpsDisplay {
	private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
	
	public static void render(MatrixStack matrices) {
		if(!minecraftClient.options.debugEnabled) {
	        matrices.push();
	        matrices.translate(2, 2, 0);
	        matrices.scale(1, 1, 0);
	        matrices.translate(-2, -2, 0);
			minecraftClient.textRenderer.draw(matrices, String.valueOf(minecraftClient.getCurrentFps()) + " fps", 2, 2, 0xffffff);
	        matrices.pop();
		}
	}
}
