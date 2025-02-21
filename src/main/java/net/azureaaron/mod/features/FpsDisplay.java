package net.azureaaron.mod.features;

import net.azureaaron.mod.Main;
import net.azureaaron.mod.annotations.Init;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.utils.render.hud.TextHudElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class FpsDisplay {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Identifier ID = Identifier.of(Main.NAMESPACE, "fps");
	@SuppressWarnings("unused")
	private static final TextHudElement HUD_ELEMENT = new TextHudElement(
			Text.of("100 fps"),
			AaronModConfigManager.get().uiAndVisuals.fpsHud,
			2,
			2);

	@Init
	public static void init() {
		HudLayerRegistrationCallback.EVENT.register(d -> d.attachLayerAfter(IdentifiedLayer.STATUS_EFFECTS, ID, FpsDisplay::render));
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		if (!CLIENT.getDebugHud().shouldShowDebugHud() && AaronModConfigManager.get().uiAndVisuals.fpsHud.enableFpsHud) {
			int x = AaronModConfigManager.get().uiAndVisuals.fpsHud.x;
			int y = AaronModConfigManager.get().uiAndVisuals.fpsHud.y;
			float scale = AaronModConfigManager.get().uiAndVisuals.fpsHud.scale;
			MatrixStack matrices = context.getMatrices();

			matrices.push();
			matrices.scale(scale, scale, 0);

			context.drawText(CLIENT.textRenderer, CLIENT.getCurrentFps() + " fps", (int) (x / scale), (int) (y / scale), Colors.WHITE, false);

			matrices.pop();
		}
	}
}
