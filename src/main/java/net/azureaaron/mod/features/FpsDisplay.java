package net.azureaaron.mod.features;

import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class FpsDisplay {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	@SuppressWarnings("unused")
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Text.of("100 fps"),
			AaronModConfigManager.get().fpsDisplayX,
			AaronModConfigManager.get().fpsDisplayY,
			AaronModConfigManager.get().fpsDisplayScale,
			x -> AaronModConfigManager.get().fpsDisplayX = x,
			y -> AaronModConfigManager.get().fpsDisplayY = y,
			scale -> AaronModConfigManager.get().fpsDisplayScale = scale,
			2,
			2);

	@Init
	public static void init() {}

	public static void render(DrawContext context) {
		if (!CLIENT.getDebugHud().shouldShowDebugHud()) {
			int x = AaronModConfigManager.get().fpsDisplayX;
			int y = AaronModConfigManager.get().fpsDisplayY;
			float scale = AaronModConfigManager.get().fpsDisplayScale;
			MatrixStack matrices = context.getMatrices();

			matrices.push();
			matrices.scale(scale, scale, 0);

			context.drawText(CLIENT.textRenderer, CLIENT.getCurrentFps() + " fps", (int) (x / scale), (int) (y / scale), Colors.WHITE, false);

			matrices.pop();
		}
	}
}
